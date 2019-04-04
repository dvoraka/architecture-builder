package dvoraka.archbuilder.prototype.actioncoordinator.action.order;


import dvoraka.archbuilder.prototype.actioncoordinator.action.AbstractNetworkAction;
import dvoraka.archbuilder.prototype.actioncoordinator.order.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionContext;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public abstract class AbstractOrderAction
        extends AbstractNetworkAction<Long, Order, Notification, OrderActionContext> {

    protected AbstractOrderAction(OrderActionContext context) {
        super(context);
    }
}
