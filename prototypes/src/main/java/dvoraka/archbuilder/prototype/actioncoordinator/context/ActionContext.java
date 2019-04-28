package dvoraka.archbuilder.prototype.actioncoordinator.context;

import java.time.Instant;
import java.util.function.Predicate;

public interface ActionContext<ID, D, PD> extends ActionContextHandle<ID, D, PD> {

    Instant getCreated();

    /**
     * Notifies this context that action is done.
     */
    void actionDone();

    /**
     * Notifies this context that action failed.
     */
    void actionFailed();

    /**
     * Suspends the action and release the context.
     *
     * @param condition the condition for action resuming
     */
    void suspendAction(Predicate<PD> condition);

    /**
     * Notifies this context that rollback is done.
     */
    void rollbackDone();

    /**
     * @return <t>true</t> if rollback is in progress.
     */
    boolean isRollback();

    /**
     * Returns a data.
     *
     * @return the data
     */
    D getData();
}
