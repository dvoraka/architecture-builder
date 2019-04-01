package dvoraka.archbuilder.prototype.statecoordinator.state;

import dvoraka.archbuilder.prototype.statecoordinator.OrderStateContext;

public final class InitOrderState extends AbstractOrderState {

    public InitOrderState(OrderStateContext context) {
        super(context);
    }

    @Override
    protected void apply(OrderStateContext context) {
        context.stateDone();
    }

    @Override
    protected void rollback(OrderStateContext context) {
        context.rollbackDone();
    }
}
