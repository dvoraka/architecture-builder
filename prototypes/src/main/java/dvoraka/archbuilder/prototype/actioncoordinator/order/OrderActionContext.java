package dvoraka.archbuilder.prototype.actioncoordinator.order;

import dvoraka.archbuilder.prototype.actioncoordinator.action.order.AbstractOrderAction;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.CheckOrderAction;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.CompleteOrderAction;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.InitOrderAction;
import dvoraka.archbuilder.prototype.actioncoordinator.context.AbstractActionContext;
import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.model.OrderActionStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderActionRepository;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.function.Predicate;

public class OrderActionContext
        extends AbstractActionContext<Long, Order, Notification>
        implements OrderActionContextHandle {

    private final OrderActionRepository repository;

    private CreateOrderAction currentAction;
    private CreateOrderAction previousAction;

    private volatile Instant lastUpdate;

    private final EnumMap<CreateOrderAction, AbstractOrderAction> config;


    private OrderActionContext(
            CreateOrderAction initAction,
            CreateOrderAction previousAction,
            Order data,
            OrderActionRepository repository
    ) {
        super(data.getId(), data);

        this.currentAction = initAction;
        this.previousAction = previousAction;

        this.repository = repository;

        lastUpdate = Instant.now();
        config = getConfig();
    }

    private EnumMap<CreateOrderAction, AbstractOrderAction> getConfig() {
        EnumMap<CreateOrderAction, AbstractOrderAction> configuration = new EnumMap<>(CreateOrderAction.class);
        configuration.put(CreateOrderAction.INIT, new InitOrderAction(this));
        configuration.put(CreateOrderAction.CHECK, new CheckOrderAction(this));
        configuration.put(CreateOrderAction.COMPLETE, new CompleteOrderAction(this));

        return configuration;
    }

    public static OrderActionContextHandle createContext(
            CreateOrderAction initState,
            CreateOrderAction lastState,
            Order data,
            OrderActionRepository repository
    ) {
        return new OrderActionContext(
                initState,
                lastState,
                data,
                repository
        );
    }

    @Override
    public boolean isDone() {
        return getCurrentAction() == CreateOrderAction.END;
    }

    @Override
    public void resume(Notification notification) {
        log.debug("Resume action ({}): {}, previous action: {}", getId(), getCurrentAction(), getPreviousAction());
        setSuspended(false);
        AbstractOrderAction state = config.get(getCurrentAction());
        state.resume(notification);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void processState() {
        log.debug("Process state.");
        processStateAsync();
    }

    private void processStateInt() {
        if (isDone()) {
            log.debug("Action context {} done in {}", this.getId(),
                    Duration.between(getCreated(), Instant.now()));
            saveToDb();
        } else {
            log.debug("Process action ({}): {}, previous: {}", getId(), getCurrentAction(), getPreviousAction());
            AbstractOrderAction state = config.get(getCurrentAction());
            state.process();
        }
    }

    private void processStateAsync() {
        Mono.fromRunnable(this::processStateInt)
                .publishOn(Schedulers.parallel())
                .log()
                .subscribe();
    }

    private void forward() {
        setCurrentAction(getCurrentAction().getNext());
    }

    private void backward() {
        setCurrentAction(getCurrentAction().getPrevious());
    }

    private void setCurrentAction(CreateOrderAction newState) {
        this.previousAction = this.currentAction;
        this.currentAction = newState;
    }

    private CreateOrderAction getCurrentAction() {
        return currentAction;
    }

    private CreateOrderAction getPreviousAction() {
        return previousAction;
    }

    @Override
    public boolean isRollback() {
        return getPreviousAction() != null && getPreviousAction().getNext() != getCurrentAction();
    }

    @Override
    public Instant getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public void restartState() {
        log.debug("Restart action ({}): {}", getId(), getCurrentAction());

        // - it could work but it needs a lot of other logic
        // - for instance, 3 messages with results can come
        // - restart should discard all of previous callbacks
        // - discarding is possible with a correlation ID, we have access
        //   to both messages
//        processStateAsync();
    }

    private void update() {
        lastUpdate = Instant.now();
    }

    @Override
    public void actionDone() {
        log.debug("Action done ({}): {}", getId(), getCurrentAction());

        saveToDb();

        forward();

        processStateAsync();
    }

    @Override
    public void actionFailed() {
        log.debug("Action failed ({}): {}", getId(), getCurrentAction());

        saveToDb(); // attempt count

        backward();

        processStateAsync();
    }

    @Override
    public void rollbackDone() {
        log.debug("Rollback done ({}): {}", getId(), getCurrentAction());

        saveToDb(); // save reverts

        backward();

        processStateAsync();
    }

    @Override
    public void suspendAction(Predicate<Notification> condition) {
        super.suspendAction(condition);

        saveToDb();
    }

    private void saveToDb() {
        log.debug("Save ({}): {}, previous: {}", getId(), getCurrentAction(), getPreviousAction());

        // save action status into DB
        OrderActionStatus statusEntity = repository.findByOrderId(getId())
                .orElseThrow(RuntimeException::new);
        statusEntity.setAction(getCurrentAction());
        statusEntity.setPreviousAction(getPreviousAction());
        statusEntity.setSuspended(isSuspended());

        repository.save(statusEntity);
        repository.flush();

        update();
    }

    @Override
    public String toString() {
        return "OrderActionContext{" +
                "id=" + getId() + ", " +
                "currentAction=" + currentAction +
                '}';
    }
}
