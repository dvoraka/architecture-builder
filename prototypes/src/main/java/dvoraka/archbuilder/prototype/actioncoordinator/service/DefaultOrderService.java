package dvoraka.archbuilder.prototype.actioncoordinator.service;

import dvoraka.archbuilder.prototype.actioncoordinator.order.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderActionCoordinator actionCoordinator;


    public DefaultOrderService(OrderRepository orderRepository, OrderActionCoordinator actionCoordinator) {
        this.orderRepository = orderRepository;
        this.actionCoordinator = actionCoordinator;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Long process(Order data) {
        return null;
    }
}
