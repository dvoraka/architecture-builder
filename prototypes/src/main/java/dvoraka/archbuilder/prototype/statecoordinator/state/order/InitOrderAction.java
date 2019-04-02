package dvoraka.archbuilder.prototype.statecoordinator.state.order;

import dvoraka.archbuilder.prototype.statecoordinator.order.OrderActionContext;

public final class InitOrderAction extends AbstractOrderAction {

    public InitOrderAction(OrderActionContext context) {
        super(context);
    }

    @Override
    protected void apply(OrderActionContext context) {
        context.stateDone();
    }

    @Override
    protected void rollback(OrderActionContext context) {
        context.rollbackDone();
    }
}
