package dvoraka.archbuilder.prototype.statecoordinator.state;


import dvoraka.archbuilder.prototype.statecoordinator.Notification;
import dvoraka.archbuilder.prototype.statecoordinator.OrderData;
import dvoraka.archbuilder.prototype.statecoordinator.OrderStateContext;

public abstract class AbstractOrderState
        extends AbstractNetworkState<Long, OrderData, Notification, OrderStateContext> {

    protected AbstractOrderState(OrderStateContext context) {
        super(context);
    }
}
