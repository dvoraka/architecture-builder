package dvoraka.archbuilder.prototype.actioncoordinator.action.order;


import dvoraka.archbuilder.prototype.actioncoordinator.action.AbstractNetworkAction;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionContext;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderData;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public abstract class AbstractOrderAction
        extends AbstractNetworkAction<Long, OrderData, Notification, OrderActionContext> {

    protected AbstractOrderAction(OrderActionContext context) {
        super(context);
    }
}
