package dvoraka.archbuilder.prototype.statecoordinator.order;


import dvoraka.archbuilder.prototype.statecoordinator.StateContextHandle;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;

public interface OrderStateContextHandle extends StateContextHandle<Long, OrderData, Notification> {
}
