package dvoraka.archbuilder.prototype.statecoordinator.order;

import dvoraka.archbuilder.prototype.statecoordinator.AbstractStateContext;
import dvoraka.archbuilder.prototype.statecoordinator.state.order.AbstractOrderState;
import dvoraka.archbuilder.prototype.statecoordinator.state.order.CheckOrderState;
import dvoraka.archbuilder.prototype.statecoordinator.state.order.CompleteOrderState;
import dvoraka.archbuilder.prototype.statecoordinator.state.order.InitOrderState;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.function.Predicate;

public class OrderStateContext
        extends AbstractStateContext<Long, OrderData, Notification>
        implements OrderStateContextHandle {

    private CreateOrderState currentState;
    private CreateOrderState lastState;

    private volatile Instant lastUpdate;

    private final EnumMap<CreateOrderState, AbstractOrderState> config;


    private OrderStateContext(
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

    private EnumMap<CreateOrderState, AbstractOrderState> getConfig() {
        EnumMap<CreateOrderState, AbstractOrderState> configuration = new EnumMap<>(CreateOrderState.class);
        configuration.put(CreateOrderState.INIT, new InitOrderState(this));
        configuration.put(CreateOrderState.CHECK, new CheckOrderState(this));
        configuration.put(CreateOrderState.COMPLETE, new CompleteOrderState(this));

        return configuration;
    }

    public static OrderStateContextHandle createContext(
            CreateOrderState initState,
            CreateOrderState lastState,
            OrderData data
    ) {
        return new OrderStateContext(
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
        log.debug("Resume state ({}): {}, last state: {}", getId(), getCurrentState(), getLastState());
        setParked(false);
        AbstractOrderState state = config.get(getCurrentState());
        state.resume(notification);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void processState() {
        if (isDone()) {
            log.debug("State context {} done in {}", this.getId(),
                    Duration.between(getCreated(), Instant.now()));
        } else {
            log.debug("Process state ({}): {}, last state: {}", getId(), getCurrentState(), getLastState());
            AbstractOrderState state = config.get(getCurrentState());
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
        log.debug("Restart state ({}): {}", getId(), getCurrentState());

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
    public void stateDone() {
        log.debug("State done for ({}): {}", getId(), getCurrentState());

        saveToDb();

        forward();

        processStateAsync();
    }

    @Override
    public void stateFailed() {
        log.debug("State failed for ({}): {}", getId(), getCurrentState());

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
    public void parkState(Predicate<Notification> condition) {
        super.parkState(condition);

        saveToDb();
    }

    private void saveToDb() {
        log.debug("Save ({}): {}, {}", getId(), getCurrentState(), getLastState());

        // save current status into DB

        update();
    }

    @Override
    public String toString() {
        return "OrderStateContext{" +
                "currentState=" + currentState +
                '}';
    }
}
