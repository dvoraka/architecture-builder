package dvoraka.archbuilder.prototype.statecoordinator.state.order;


import dvoraka.archbuilder.prototype.statecoordinator.order.OrderData;
import dvoraka.archbuilder.prototype.statecoordinator.order.OrderStateContext;
import dvoraka.archbuilder.prototype.statecoordinator.state.AbstractNetworkState;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public abstract class AbstractOrderState
        extends AbstractNetworkState<Long, OrderData, Notification, OrderStateContext> {

    protected AbstractOrderState(OrderStateContext context) {
        super(context);
    }
}
