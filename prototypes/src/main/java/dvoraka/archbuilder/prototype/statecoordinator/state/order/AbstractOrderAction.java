package dvoraka.archbuilder.prototype.statecoordinator.state.order;


import dvoraka.archbuilder.prototype.statecoordinator.order.OrderActionContext;
import dvoraka.archbuilder.prototype.statecoordinator.order.OrderData;
import dvoraka.archbuilder.prototype.statecoordinator.state.AbstractNetworkAction;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public abstract class AbstractOrderAction
        extends AbstractNetworkAction<Long, OrderData, Notification, OrderActionContext> {

    protected AbstractOrderAction(OrderActionContext context) {
        super(context);
    }
}
