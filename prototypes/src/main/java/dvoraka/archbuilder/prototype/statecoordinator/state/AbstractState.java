package dvoraka.archbuilder.prototype.statecoordinator.state;

import dvoraka.archbuilder.prototype.statecoordinator.StateContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractState<ID, D, PD, C extends StateContext<ID, D, PD>> implements State<PD> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final C context;


    protected AbstractState(C context) {
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
            log.warn("State failed (" + getContext().getId() + ")", e);
            getContext().stateFailed();
        }
    }

    protected C getContext() {
        return context;
    }

    protected abstract void apply(C context);

    protected abstract void rollback(C context);
}
