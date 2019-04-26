package dvoraka.archbuilder.prototype.actioncoordinator.service;

import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.order.CreateOrderAction;

public interface OrderService extends StateService<Order, Long, CreateOrderAction> {
}
