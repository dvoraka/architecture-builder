package dvoraka.archbuilder.prototype.actioncoordinator;

import dvoraka.archbuilder.prototype.actioncoordinator.action.order.OrderStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.model.OrderActionStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderData;
import dvoraka.archbuilder.prototype.actioncoordinator.repository.OrderActionRepository;
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
    private OrderActionRepository repository;


    public static void main(String[] args) {
        SpringApplication.run(CoordinatorApp.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("Coordinator app");

            // create order data
            OrderData order = new OrderData();
            order.setUserId(2);
            order.setItemId(3);
            order.setStatus(OrderStatus.NEW);
            // create action status data
            OrderActionStatus status = new OrderActionStatus();
            status.setTransactionId(UUID.randomUUID().toString());
            status.setOrderData(order.toString());
            // save and get ID
            status = repository.save(status);
            repository.flush();
            // set ID
            order.setId(status.getId());

            // second order
            OrderData order2 = new OrderData();
            order2.setUserId(2);
            order2.setItemId(5);
            order2.setStatus(OrderStatus.NEW);
            OrderActionStatus status2 = new OrderActionStatus();
            status2.setTransactionId(UUID.randomUUID().toString());
            status2.setOrderData(order.toString());
            status2 = repository.save(status2);
            repository.flush();
            order2.setId(status2.getId());

            OrderActionCoordinator coordinator = new OrderActionCoordinator(repository);
            coordinator.start();

            coordinator.process(order);
            coordinator.process(order2);

            // wait for async stuff
            TimeUnit.SECONDS.sleep(30);
        };
    }
}
