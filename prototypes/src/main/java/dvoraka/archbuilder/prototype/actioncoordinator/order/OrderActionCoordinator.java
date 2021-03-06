package dvoraka.archbuilder.prototype.actioncoordinator.order;

import dvoraka.archbuilder.prototype.actioncoordinator.coordinator.ActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.exception.StateException;
import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.model.OrderActionStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.net.NotificationService;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderActionRepository;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.data.notification.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;


/**
 * Testing action coordinator implementation.
 */
@Service
public class OrderActionCoordinator implements ActionCoordinator<Long, Order, StateException> {

    private final OrderActionRepository repository;
    private final NotificationService notificationService;

    private static final Logger log = LoggerFactory.getLogger(OrderActionCoordinator.class);

    private static final int WATCHDOG_DELAY_S = 10;

    private final Map<Long, OrderActionContextHandle> contexts;
    private final Set<Long> suspendedContexts;

    private final ConcurrentHashMap<Long, Notification> notifications;


    @Autowired
    public OrderActionCoordinator(OrderActionRepository repository, NotificationService notificationService) {
        this.repository = requireNonNull(repository);
        this.notificationService = requireNonNull(notificationService);

        contexts = new ConcurrentHashMap<>();
        suspendedContexts = ConcurrentHashMap.newKeySet();
        notifications = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void start() {
        // load not suspended contexts
        List<OrderActionStatus> statuses = repository.findAll();
        for (OrderActionStatus status : statuses) {
            if (!status.isSuspended()) {
                loadContext(status.getOrderId());
            }
        }

        // subscribe to notifications
        notificationService.subscribe(this::onNotification);

        startWatchdog();
    }

    private void startWatchdog() {
        Flux
                .interval(Duration.ofSeconds(WATCHDOG_DELAY_S), Schedulers.elastic())
                .onBackpressureDrop()
                .doOnNext(l -> watchdog())
                .subscribe();
    }

    private boolean isResumptionCondition(Notification notification) {
        if (notification.getType() != NotificationType.CHECK) {
            return false;
        }

        //TODO
        return true;

//        Order order = (Order) notification.getData().get("order");
//        OrderStatus status = order.getStatus();

//        return status == OrderStatus.COMPLETED
//                || status == OrderStatus.CANCELLED
//                || status == OrderStatus.FAILED;
    }

    @Override
    public void process(Order order) {
        log.info("Process for: {}", order);

        Mono<Object> createOrderActionStatusMono =
                Mono.fromRunnable(() -> createOrderActionStatus(order));

        Mono<OrderActionContextHandle> createContextMono =
                Mono.fromCallable(() -> createContext(order));

        createOrderActionStatusMono
                .publishOn(Schedulers.parallel())
                .then(createContextMono)
                .doOnNext(OrderActionContextHandle::processState)
                .subscribe();

//        createOrderActionStatus(order);
//        OrderActionContextHandle context = createContext(order);
//        context.processState();
    }

    private void createOrderActionStatus(Order order) {
        log.debug("Creating order action status: {}", order.getId());

        OrderActionStatus status = new OrderActionStatus();
        status.setOrderId(order.getId());
        status.setOrderData(order.toString());

        repository.save(status);
        repository.flush();
    }

    @Override
    public void cancel(Long orderId) throws StateException {
        if (!contexts.containsKey(orderId)) {
            //TODO
            throw new RuntimeException("Data not found.");
        }

        OrderActionContextHandle context = contexts.get(orderId);
        context.cancel();
    }

    @Override
    public long getSize() {
        return contexts.size();
    }

    private void watchdog() {
        log.debug("Context count: {}", contexts.size());

        // find ended contexts and remove them
        contexts.entrySet().removeIf(entry -> {
            boolean done = entry.getValue().isDone();
            if (done) {
                log.debug("Removing done context: {}", entry.getValue());
            }
            return done;
        });

        // find stuck contexts and restart them
        Predicate<OrderActionContextHandle> predicate = context ->
                context.getLastUpdate().isBefore(Instant.now().minusSeconds(120));
        contexts.values().stream()
                .filter(predicate)
                .forEach(OrderActionContextHandle::restartState);

        // find old notifications and remove them
        notifications.entrySet().removeIf(entry ->
                entry.getValue().getTimestamp().isBefore(Instant.now().minusSeconds(60)));

        // find suspended states and release them
        contexts.values().stream()
                .filter(OrderActionContextHandle::isSuspended)
                .peek(context -> log.debug("Suspending context: {}", context.getId()))
                .forEach(context -> {
                    Notification notification = notifications.remove(context.getId());
                    if (notification != null && isResumptionCondition(notification)) {
                        context.resume(notification);
                    } else {
                        suspendedContexts.add(context.getId());
                    }
                });
        // remove suspended contexts
        contexts.entrySet()
                .removeIf(entry -> suspendedContexts.contains(entry.getKey()));

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new context for an order.
     *
     * @param order the order
     * @return the context
     */
    private OrderActionContextHandle createContext(Order order) {
        log.debug("Creating context: {}", order.getId());
        // start the context from start
        OrderActionContextHandle context = OrderActionContext.createContext(
                CreateOrderAction.INIT,
                null,
                order,
                repository
        );
        //TODO: check the context ID
        contexts.put(context.getId(), context);

        return context;
    }

    /**
     * Loads a context for a given order ID.
     *
     * @param orderId the saved order ID for the action
     * @return the context
     */
    private OrderActionContextHandle loadContext(long orderId) {
        OrderActionStatus actionStatus = repository.findByOrderId(orderId)
                .orElseThrow(RuntimeException::new);

        //TODO: get order data for new context
//        Order orderData = new DefaultOrderData().setOrderId(60);
        Order order = new Order();
        order.setId(orderId);

        OrderActionContextHandle context = OrderActionContext.createContext(
                actionStatus.getAction(),
                actionStatus.getPreviousAction(),
                order,
                repository
        );
        //TODO: check the context ID
        if (context.isDone()) {
            log.debug("Context already done: {}", context);
        } else {
            log.debug("Loading context: {}", context);
            contexts.put(context.getId(), context);
        }

        return context;
    }

    private void onNotification(Notification notification) {
        if (notification.getType() != NotificationType.CHECK
                || notification.getData() == null) {
            return;
        }

        log.debug("On notification: {}", notification);

        long orderId = (long) notification.getData().get("orderId");
        if (suspendedContexts.contains(orderId)) {
            if (isResumptionCondition(notification)) {
                log.debug("Waking up the context: {}...", orderId);
                // wake up the context
                OrderActionContextHandle context = loadContext(orderId);
                context.resume(notification);
            }
        } else {
//            notifications.put(notification.getData().getOrderId(), notification);
        }
    }
}
