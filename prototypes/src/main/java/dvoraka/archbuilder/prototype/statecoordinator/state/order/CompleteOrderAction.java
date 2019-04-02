package dvoraka.archbuilder.prototype.statecoordinator.state.order;

import dvoraka.archbuilder.prototype.statecoordinator.order.OrderActionContext;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

import java.util.Map;

public final class CompleteOrderAction extends AbstractOrderAction {

    public CompleteOrderAction(OrderActionContext context) {
        super(context);
    }

    @Override
    protected void apply(OrderActionContext context) {
        context.parkState(null);
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
            getContext().stateDone();
        } else if (status == OrderStatus.FAILED || status == OrderStatus.CANCELLED) {
            getContext().stateFailed();
        }
    }
}
