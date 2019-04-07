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
            System.out.println("Order service app");

            OrderService orderService = new DefaultOrderService(
                    orderRepository, orderActionRepository, actionCoordinator);

            long start = System.currentTimeMillis();
            for (int i = 0; i < 100; i++) {
                Order order = new Order();
                order.setUserId(2);
                order.setItemId(3);
                order.setStatus(OrderStatus.NEW);

                orderService.process(order, UUID.randomUUID().toString());
            }

            while (actionCoordinator.getSize() != 0) {
                System.out.println("waiting...");
                TimeUnit.SECONDS.sleep(2);
            }
            System.out.println("done in " + (System.currentTimeMillis() - start) + " ms");
        };
    }
}
