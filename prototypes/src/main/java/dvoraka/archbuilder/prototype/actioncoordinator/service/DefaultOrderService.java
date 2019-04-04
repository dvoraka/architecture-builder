package dvoraka.archbuilder.prototype.actioncoordinator.service;

import dvoraka.archbuilder.prototype.actioncoordinator.model.OrderActionStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.order.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderActionRepository;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DefaultOrderService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderActionRepository orderActionRepository;
    private final OrderActionCoordinator actionCoordinator;


    public DefaultOrderService(
            OrderRepository orderRepository,
            OrderActionRepository orderActionRepository,
            OrderActionCoordinator actionCoordinator
    ) {
        this.orderRepository = orderRepository;
        this.orderActionRepository = orderActionRepository;
        this.actionCoordinator = actionCoordinator;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public Long process(Order data, String transactionId) {
        // save data
        Order savedOrder = orderRepository.save(data);

        // should be probably anywhere else (coordinator)
        createOrderActionStatus(savedOrder);

        // run async processing
        actionCoordinator.process(savedOrder);

        // return data ID
        return savedOrder.getId();
    }

    private void createOrderActionStatus(Order order) {

        OrderActionStatus status = new OrderActionStatus();
        status.setOrderId(order.getId());
        status.setTransactionId(UUID.randomUUID().toString()); // will not be random
        status.setOrderData(order.toString());

        orderActionRepository.save(status);
        orderActionRepository.flush();
    }
}
