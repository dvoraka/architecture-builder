package dvoraka.archbuilder.prototype.actioncoordinator.action;

import dvoraka.archbuilder.prototype.actioncoordinator.context.ActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAction<ID, D, PD, C extends ActionContext<ID, D, PD>> implements Action<PD> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final C context;


    protected AbstractAction(C context) {
        this.context = context;
    }

    @Override
    public void process() {
        try {
            if (context.isRollback()) {
                log.debug("Rollback ({}): {}", context.getId(), context);
                rollback(context);
            } else {
                log.debug("Applying ({}): {}", context.getId(), context);
                apply(context);
            }
        } catch (Exception e) {
            log.warn("Action failed (" + getContext().getId() + ")", e);
            getContext().actionFailed();
        }
    }

    protected C getContext() {
        return context;
    }

    protected abstract void apply(C context);

    protected abstract void rollback(C context);
}
