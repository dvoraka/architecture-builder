package dvoraka.archbuilder.prototype.actioncoordinator;

import dvoraka.archbuilder.prototype.actioncoordinator.action.order.OrderStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.model.OrderActionStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.order.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderActionRepository;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderRepository;
import dvoraka.archbuilder.prototype.actioncoordinator.service.DefaultOrderService;
import dvoraka.archbuilder.prototype.actioncoordinator.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CoordinatorApp {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderActionRepository orderActionRepository;
    @Autowired
    private OrderActionCoordinator actionCoordinator;


    public static void main(String[] args) {
        SpringApplication.run(CoordinatorApp.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("Coordinator app");

            // create order data
            Order order = new Order();
            order.setUserId(2);
            order.setItemId(3);
            order.setStatus(OrderStatus.NEW);
            createActionStatus(order);

            // second order
            Order order2 = new Order();
            order2.setUserId(2);
            order2.setItemId(5);
            order2.setStatus(OrderStatus.NEW);
            createActionStatus(order2);

            OrderService orderService = new DefaultOrderService(orderRepository, actionCoordinator);

            // process orders
            actionCoordinator.process(order);
            actionCoordinator.process(order2);

            // wait for async stuff
            TimeUnit.SECONDS.sleep(30);
        };
    }

    private void createActionStatus(Order order) {
        // create order action status data
        OrderActionStatus status = new OrderActionStatus();
        status.setTransactionId(UUID.randomUUID().toString()); // will not be random
        status.setOrderData(order.toString());
        // save and get ID
        status = orderActionRepository.save(status);
        orderActionRepository.flush();
        // set ID
        order.setId(status.getId()); // order must have some ID before
    }
}
