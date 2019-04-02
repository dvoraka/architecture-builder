package dvoraka.archbuilder.prototype.actioncoordinator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.function.Predicate;

public abstract class AbstractActionContext<ID, D, PD> implements ActionContext<ID, D, PD> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ID id;
    private final D data;

    private final Instant created;

    private volatile boolean parked;
    private Predicate<PD> resumeCondition;


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
    public boolean isParked() {
        return parked;
    }

    @Override
    public void parkAction(Predicate<PD> condition) {
        log.debug("Park action ({})", getId());
        resumeCondition = condition;
        setParked(true);
    }

    protected void setParked(boolean parked) {
        this.parked = parked;
    }

    @Override
    public Predicate<PD> getResumeCondition() {
        return resumeCondition;
    }

    @Override
    public D getData() {
        return data;
    }
}
