package dvoraka.archbuilder.prototype.actioncoordinator.action.order;

import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionContext;

import java.util.concurrent.TimeUnit;

public final class CheckOrderAction extends AbstractOrderAction {

    public CheckOrderAction(OrderActionContext context) {
        super(context);
    }

    @Override
    protected void apply(OrderActionContext context) {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        context.actionDone();
    }

    @Override
    protected void rollback(OrderActionContext context) {
        context.rollbackDone();
    }
}
