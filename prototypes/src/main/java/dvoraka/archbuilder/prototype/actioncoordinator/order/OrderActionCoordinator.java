package dvoraka.archbuilder.prototype.actioncoordinator.order;

import dvoraka.archbuilder.prototype.actioncoordinator.ActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.OrderStatus;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.data.notification.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;


/**
 * Testing action coordinator implementation.
 */
@Service
public class OrderActionCoordinator implements ActionCoordinator<Long, OrderData> {

    private static final Logger log = LoggerFactory.getLogger(OrderActionCoordinator.class);

    private final Map<Long, OrderActionContextHandle> contexts;
    private final Set<Long> parkedContexts;

    private final ConcurrentHashMap<Long, Notification> notifications;


    @Autowired
    public OrderActionCoordinator() {
        contexts = new ConcurrentHashMap<>();
        parkedContexts = ConcurrentHashMap.newKeySet();
        notifications = new ConcurrentHashMap<>();
    }

    @PostConstruct
    public void start() {
        // load not parked contexts

        // subscribe to notifications
//        notificationService.subscribe(this::onNotification);

        startWatchdog();
    }

    private void startWatchdog() {
        Flux
                .interval(Duration.ofSeconds(10), Schedulers.elastic())
                .onBackpressureDrop()
                .doOnNext(l -> watchdog())
                .subscribe();
    }

    private boolean resumeCondition(Notification notification) {
        if (notification.getType() != NotificationType.ORDER_STATUS) {
            return false;
        }

        OrderData orderData = (OrderData) notification.getData().get("orderData");
        OrderStatus status = orderData.getStatus();

        return status == OrderStatus.COMPLETED
                || status == OrderStatus.CANCELLED
                || status == OrderStatus.FAILED;
    }

    @Override
    public void process(OrderData orderData) {
        log.info("Process for: {}", orderData);
        OrderActionContextHandle context = createContext(orderData);
        context.processState();
    }

    @Override
    public void cancel(Long orderId) throws Exception {
        if (contexts.containsKey(orderId)) {
            throw new Exception();
        }

        OrderActionContextHandle context = contexts.get(orderId);
        context.cancel();
    }

    private void watchdog() {
        log.debug("Context count: {}", contexts.size());

        // find ended contexts and remove them
        contexts.entrySet().removeIf(entry -> entry.getValue().isDone());

        // find stuck contexts and restart them
        Predicate<OrderActionContextHandle> predicate = context ->
                context.getLastUpdate().isBefore(Instant.now().minusSeconds(120));
        contexts.values().stream()
                .filter(predicate)
                .forEach(OrderActionContextHandle::restartState);

        // find old notifications and remove them
//        notifications.entrySet().removeIf(entry ->
//                entry.getValue().getTimestamp().isBefore(Instant.now().minusSeconds(60)));

        // find parked states and release them
        contexts.values().stream()
                .filter(OrderActionContextHandle::isParked)
                .peek(context -> log.debug("Parking context: {}", context.getId()))
                .forEach(context -> {
                    Notification notification = notifications.remove(context.getId());
                    if (notification != null && resumeCondition(notification)) {
                        context.resume(notification);
                    } else {
                        parkedContexts.add(context.getId());
                    }
                });
        // remove parked contexts
        contexts.entrySet()
                .removeIf(entry -> parkedContexts.contains(entry.getKey()));

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new context for new data.
     *
     * @param orderData the new data
     * @return the context
     */
    private OrderActionContextHandle createContext(OrderData orderData) {
        // start the context from start
        OrderActionContextHandle context = OrderActionContext.createContext(
                CreateOrderState.INIT,
                null,
                orderData
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
//        OrderStatusEntity orderStatusEntity = repository.findById(orderId)
//                .orElseThrow(RuntimeException::new);

        //TODO
//        OrderData orderData = new DefaultOrderData().setOrderId(60);

        OrderActionContextHandle context = OrderActionContext.createContext(
                null, null, null
        );
        //TODO: check the context ID
        contexts.put(context.getId(), context);

        return context;
    }

    private void onNotification(Notification notification) {
//        if (notification.getType() != NotificationType.ORDER_STATUS
//                || notification.getData() == null) {
//            return;
//        }

        log.debug("On notification: {}", notification);

        long orderId = 1; // notification.getData().getOrderId();
        if (parkedContexts.contains(orderId)) {
            if (resumeCondition(notification)) {
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
