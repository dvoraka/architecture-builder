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

    private CreateOrderState currentState;
    private CreateOrderState lastState;

    private volatile Instant lastUpdate;

    private final EnumMap<CreateOrderState, AbstractOrderAction> config;


    private OrderActionContext(
            CreateOrderState initState,
            CreateOrderState lastState,
            OrderData data
    ) {
        super(data.getId(), data);

        this.currentState = initState;
        this.lastState = lastState;

        lastUpdate = Instant.now();
        config = getConfig();
    }

    private EnumMap<CreateOrderState, AbstractOrderAction> getConfig() {
        EnumMap<CreateOrderState, AbstractOrderAction> configuration = new EnumMap<>(CreateOrderState.class);
        configuration.put(CreateOrderState.INIT, new InitOrderAction(this));
        configuration.put(CreateOrderState.CHECK, new CheckOrderAction(this));
        configuration.put(CreateOrderState.COMPLETE, new CompleteOrderAction(this));

        return configuration;
    }

    public static OrderActionContextHandle createContext(
            CreateOrderState initState,
            CreateOrderState lastState,
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
        return getCurrentState() == CreateOrderState.END;
    }

    @Override
    public void resume(Notification notification) {
        log.debug("Resume action ({}): {}, last action: {}", getId(), getCurrentState(), getLastState());
        setSuspended(false);
        AbstractOrderAction state = config.get(getCurrentState());
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
            log.debug("Process action ({}): {}, last action: {}", getId(), getCurrentState(), getLastState());
            AbstractOrderAction state = config.get(getCurrentState());
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
        setCurrentState(getCurrentState().getNext());
    }

    private void backward() {
        setCurrentState(getCurrentState().getPrevious());
    }

    private void setCurrentState(CreateOrderState newState) {
        this.lastState = this.currentState;
        this.currentState = newState;
    }

    private CreateOrderState getCurrentState() {
        return currentState;
    }

    private CreateOrderState getLastState() {
        return lastState;
    }

    @Override
    public boolean isRollback() {
        return getLastState() != null && getLastState().getNext() != getCurrentState();
    }

    @Override
    public Instant getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public void restartState() {
        log.debug("Restart action ({}): {}", getId(), getCurrentState());

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
        log.debug("Action done for ({}): {}", getId(), getCurrentState());

        saveToDb();

        forward();

        processStateAsync();
    }

    @Override
    public void actionFailed() {
        log.debug("Action failed for ({}): {}", getId(), getCurrentState());

        saveToDb(); // attempt count

        backward();

        processStateAsync();
    }

    @Override
    public void rollbackDone() {
        log.debug("Rollback done for ({}): {}", getId(), getCurrentState());

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
        log.debug("Save ({}): {}, {}", getId(), getCurrentState(), getLastState());

        // save current status into DB

        update();
    }

    @Override
    public String toString() {
        return "OrderActionContext{" +
                "currentState=" + currentState +
                '}';
    }
}
