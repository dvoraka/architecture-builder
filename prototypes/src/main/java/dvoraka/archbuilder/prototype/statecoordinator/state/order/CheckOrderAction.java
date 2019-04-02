package dvoraka.archbuilder.prototype.statecoordinator.state.order;

import dvoraka.archbuilder.prototype.statecoordinator.order.OrderActionContext;

public final class CheckOrderAction extends AbstractOrderAction {

    public CheckOrderAction(OrderActionContext context) {
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
