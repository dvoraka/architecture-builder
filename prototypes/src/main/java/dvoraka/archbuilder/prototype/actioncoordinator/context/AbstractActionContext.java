package dvoraka.archbuilder.prototype.actioncoordinator.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.function.Predicate;

public abstract class AbstractActionContext<ID, D, PD> implements ActionContext<ID, D, PD> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ID id;
    private final D data;

    private final Instant created;

    private volatile boolean suspended;
    private Predicate<PD> resumptionCondition;


    protected AbstractActionContext(ID id, D data) {
        this.id = id;
        this.data = data;

        created = Instant.now();
    }

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public Instant getCreated() {
        return created;
    }

    @Override
    public boolean isSuspended() {
        return suspended;
    }

    @Override
    public void suspendAction(Predicate<PD> condition) {
        log.debug("Suspend action ({})", getId());
        resumptionCondition = condition;
        setSuspended(true);
    }

    protected void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    @Override
    public Predicate<PD> getResumptionCondition() {
        return resumptionCondition;
    }

    @Override
    public D getData() {
        return data;
    }
}
