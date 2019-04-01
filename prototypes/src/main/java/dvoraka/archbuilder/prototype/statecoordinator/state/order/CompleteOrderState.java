package dvoraka.archbuilder.prototype.statecoordinator.state.order;

import dvoraka.archbuilder.prototype.statecoordinator.order.OrderStateContext;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

import java.util.Map;

public final class CompleteOrderState extends AbstractOrderState {

    public CompleteOrderState(OrderStateContext context) {
        super(context);
    }

    @Override
    protected void apply(OrderStateContext context) {
        context.parkState(null);
    }

    @Override
    protected void rollback(OrderStateContext context) {

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
