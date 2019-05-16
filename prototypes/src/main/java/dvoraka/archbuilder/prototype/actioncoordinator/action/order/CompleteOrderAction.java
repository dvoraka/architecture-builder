package dvoraka.archbuilder.prototype.actioncoordinator.action.order;

import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionContext;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

import java.util.Map;

public final class CompleteOrderAction extends AbstractOrderAction {

    public CompleteOrderAction(OrderActionContext context) {
        super(context);
    }

    @Override
    protected void apply(OrderActionContext context) {
        context.suspendAction(null);
    }

    @Override
    protected void rollback(OrderActionContext context) {
    }

    @Override
    public void resume(Notification notification) {
        log.debug("Resume ({})", getContext().getId());

        Map<String, Object> data = notification.getData();
        OrderStatus status = (OrderStatus) data.get("orderStatus");

        if (status == OrderStatus.COMPLETED) {
            getContext().actionDone();
        } else if (status == OrderStatus.FAILED || status == OrderStatus.CANCELLED) {
            getContext().actionFailed();
        } else {
            log.warn("Unknown status: {}", status);
        }
    }
}
