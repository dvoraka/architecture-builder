package dvoraka.archbuilder.prototype.actioncoordinator.action.order;

import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionContext;

public final class InitOrderAction extends AbstractOrderAction {

    public InitOrderAction(OrderActionContext context) {
        super(context);
    }

    @Override
    protected void apply(OrderActionContext context) {
        context.actionDone();
    }

    @Override
    protected void rollback(OrderActionContext context) {
        context.rollbackDone();
    }
}
