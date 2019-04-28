package dvoraka.archbuilder.prototype.actioncoordinator.context;

import java.time.Instant;
import java.util.function.Predicate;

public interface ActionContextHandle<ID, D, PD> {

    /**
     * Cancels operations and revert back everything.
     */
    void cancel();

    /**
     * Returns a context ID.
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
    boolean isSuspended();

    /**
     * Resumes from a parked action.
     *
     * @param data the data
     */
    void resume(PD data);

    /**
     * Returns a condition to resume the context
     *
     * @return the condition
     */
    Predicate<PD> getResumptionCondition();

    /**
     * Restarts the current action.
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
