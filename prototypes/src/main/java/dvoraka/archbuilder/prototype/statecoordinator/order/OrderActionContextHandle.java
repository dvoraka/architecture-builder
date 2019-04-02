package dvoraka.archbuilder.prototype.statecoordinator.order;


import dvoraka.archbuilder.prototype.statecoordinator.ActionContextHandle;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public interface OrderActionContextHandle extends ActionContextHandle<Long, OrderData, Notification> {
}
