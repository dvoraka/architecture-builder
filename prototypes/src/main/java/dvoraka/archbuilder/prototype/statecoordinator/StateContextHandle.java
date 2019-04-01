package dvoraka.archbuilder.prototype.statecoordinator;

import java.time.Instant;
import java.util.function.Predicate;

public interface StateContextHandle<ID, D, PD> {

    /**
     * Cancels operations and revert back everything.
     */
    void cancel();

    /**
     * Returns an context ID.
     *
     * @return the context ID
     */
    ID getId();

    /**
     * @return <t>true</t> if context is done
     */
    boolean isDone();

    /**
     * @return <t>true</t> if context is parked
     */
    boolean isParked();

    /**
     * Resumes from a parking state
     *
     * @param data the data
     */
    void resume(PD data);

    /**
     * Returns a condition to resuming the context
     *
     * @return the condition
     */
    Predicate<PD> getResumeCondition();

    /**
     * Restarts the current state.
     */
    void restartState();

    /**
     * Returns a last update time for a context.
     *
     * @return the last update
     */
    Instant getLastUpdate();

    /**
     * Starts with processing.
     */
    void processState();
}
