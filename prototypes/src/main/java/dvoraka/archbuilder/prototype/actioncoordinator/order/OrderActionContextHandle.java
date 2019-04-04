package dvoraka.archbuilder.prototype.actioncoordinator.order;


import dvoraka.archbuilder.prototype.actioncoordinator.ActionContextHandle;
import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public interface OrderActionContextHandle extends ActionContextHandle<Long, Order, Notification> {
}
