package dvoraka.archbuilder.prototype.actioncoordinator.order;

import dvoraka.archbuilder.prototype.actioncoordinator.AbstractActionContext;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.AbstractOrderAction;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.CheckOrderAction;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.CompleteOrderAction;
import dvoraka.archbuilder.prototype.actioncoordinator.action.order.InitOrderAction;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.function.Predicate;

public class OrderActionContext
        extends AbstractActionContext<Long, OrderData, Notification>
        implements OrderActionContextHandle {

    private CreateOrderAction currentAction;
    private CreateOrderAction lastAction;

    private volatile Instant lastUpdate;

    private final EnumMap<CreateOrderAction, AbstractOrderAction> config;


    private OrderActionContext(
            CreateOrderAction initAction,
            CreateOrderAction lastAction,
            OrderData data
    ) {
        super(data.getId(), data);

        this.currentAction = initAction;
        this.lastAction = lastAction;

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
            OrderData data
    ) {
        return new OrderActionContext(
                initState,
                lastState,
                data
        );
    }

    @Override
    public boolean isDone() {
        return getCurrentAction() == CreateOrderAction.END;
    }

    @Override
    public void resume(Notification notification) {
        log.debug("Resume action ({}): {}, last action: {}", getId(), getCurrentAction(), getLastAction());
        setSuspended(false);
        AbstractOrderAction state = config.get(getCurrentAction());
        state.resume(notification);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void processState() {
        if (isDone()) {
            log.debug("Action context {} done in {}", this.getId(),
                    Duration.between(getCreated(), Instant.now()));
        } else {
            log.debug("Process action ({}): {}, last action: {}", getId(), getCurrentAction(), getLastAction());
            AbstractOrderAction state = config.get(getCurrentAction());
            state.process();
        }
    }

    private void processStateAsync() {
        Mono.fromRunnable(this::processState)
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
        this.lastAction = this.currentAction;
        this.currentAction = newState;
    }

    private CreateOrderAction getCurrentAction() {
        return currentAction;
    }

    private CreateOrderAction getLastAction() {
        return lastAction;
    }

    @Override
    public boolean isRollback() {
        return getLastAction() != null && getLastAction().getNext() != getCurrentAction();
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
        log.debug("Action done for ({}): {}", getId(), getCurrentAction());

        saveToDb();

        forward();

        processStateAsync();
    }

    @Override
    public void actionFailed() {
        log.debug("Action failed for ({}): {}", getId(), getCurrentAction());

        saveToDb(); // attempt count

        backward();

        processStateAsync();
    }

    @Override
    public void rollbackDone() {
        log.debug("Rollback done for ({}): {}", getId(), getCurrentAction());

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
        log.debug("Save ({}): {}, {}", getId(), getCurrentAction(), getLastAction());

        // save current status into DB

        update();
    }

    @Override
    public String toString() {
        return "OrderActionContext{" +
                "currentAction=" + currentAction +
                '}';
    }
}
