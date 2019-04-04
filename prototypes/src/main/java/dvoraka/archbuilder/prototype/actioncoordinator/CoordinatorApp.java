package dvoraka.archbuilder.prototype.actioncoordinator;

import dvoraka.archbuilder.prototype.actioncoordinator.action.order.OrderStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
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

            // second order
            Order order2 = new Order();
            order2.setUserId(2);
            order2.setItemId(5);
            order2.setStatus(OrderStatus.NEW);

            OrderService orderService = new DefaultOrderService(
                    orderRepository, orderActionRepository, actionCoordinator);

            // process orders
//            actionCoordinator.process(order);
//            actionCoordinator.process(order2);
            orderService.process(order, "abc");
            orderService.process(order2, "ddd");

            // wait for async stuff
            TimeUnit.SECONDS.sleep(30);
        };
    }
}
