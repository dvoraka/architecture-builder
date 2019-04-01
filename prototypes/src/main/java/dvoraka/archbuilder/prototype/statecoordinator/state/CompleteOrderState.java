package dvoraka.archbuilder.prototype.statecoordinator.state;

import dvoraka.archbuilder.prototype.statecoordinator.OrderStateContext;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

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

        OrderStatus status = null; // get data from notification
        if (status == OrderStatus.COMPLETED) {
            getContext().stateDone();
        } else if (status == OrderStatus.FAILED || status == OrderStatus.CANCELLED) {
            getContext().stateFailed();
        }
    }
}
