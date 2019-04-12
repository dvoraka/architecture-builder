package dvoraka.archbuilder.prototype.actioncoordinator.service;

import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderActionRepository;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderRepository;
import dvoraka.archbuilder.sample.microservice.service.AbstractBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static java.util.Objects.requireNonNull;

@Service
public class DefaultOrderService extends AbstractBaseService implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderActionRepository orderActionRepository;
    private final OrderActionCoordinator actionCoordinator;

    private static final Logger log = LoggerFactory.getLogger(DefaultOrderService.class);


    @Autowired
    public DefaultOrderService(
            OrderRepository orderRepository,
            OrderActionRepository orderActionRepository,
            OrderActionCoordinator actionCoordinator
    ) {
        this.orderRepository = requireNonNull(orderRepository);
        this.orderActionRepository = requireNonNull(orderActionRepository);
        this.actionCoordinator = requireNonNull(actionCoordinator);
    }

    @PostConstruct
    public void start() {
        // load not completed orders and check orderActionRepository
    }

    @Override
    public Long process(Order order, String transactionId) {
        log.debug("Saving order: {}", order);
        Order savedOrder = orderRepository.save(order);

        // run async processing
        actionCoordinator.process(savedOrder);

        // return data ID
        return savedOrder.getId();
    }

    @Override
    public void cancel(Long id) {
        actionCoordinator.cancel(id);
    }
}
