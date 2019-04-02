package dvoraka.archbuilder.prototype.statecoordinator;

import java.time.Instant;
import java.util.function.Predicate;

public interface ActionContext<ID, D, PD> extends ActionContextHandle<ID, D, PD> {

    Instant getCreated();

    /**
     * Notifies this context that state is done.
     */
    void stateDone();

    /**
     * Notifies this context that state failed.
     */
    void stateFailed();

    /**
     * Parks the state and release the context.
     */
    void parkState(Predicate<PD> condition);

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
