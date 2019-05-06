package dvoraka.archbuilder.prototype.actioncoordinator;

import dvoraka.archbuilder.prototype.actioncoordinator.action.order.OrderStatus;
import dvoraka.archbuilder.prototype.actioncoordinator.model.Order;
import dvoraka.archbuilder.prototype.actioncoordinator.net.NotificationComponent;
import dvoraka.archbuilder.prototype.actioncoordinator.order.OrderActionCoordinator;
import dvoraka.archbuilder.prototype.actioncoordinator.service.OrderService;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.data.notification.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class CoordinatorApp {

    @Autowired
    private OrderActionCoordinator actionCoordinator;
    @Autowired
    private OrderService orderService;
    @Autowired
    private NotificationComponent notificationComponent;


    public static void main(String[] args) {
        SpringApplication.run(CoordinatorApp.class, args);
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            System.out.println("Order service app");

            final int orderCount = 3;
            long start = System.currentTimeMillis();
            for (int i = 0; i < orderCount; i++) {
                Order order = new Order();
                order.setUserId(2);
                order.setItemId(3);
                order.setStatus(OrderStatus.NEW);

                orderService.process(order, UUID.randomUUID().toString());
            }
            long responseTime = System.currentTimeMillis() - start;
            TimeUnit.SECONDS.sleep(1);

            while (actionCoordinator.getSize() != 0) {
                System.out.println("waiting...");
                TimeUnit.SECONDS.sleep(2);
            }
            System.out.println("done in " + (System.currentTimeMillis() - start) + " ms");
            System.out.println("response time: " + responseTime + " ms");

            Notification notification = new Notification() {
                @Override
                public NotificationType getType() {
                    return null;
                }

                @Override
                public Map<String, Object> getData() {
                    return null;
                }

                @Override
                public String getId() {
                    return null;
                }

                @Override
                public Instant getTimestamp() {
                    return null;
                }
            };
            notificationComponent.onMessage(notification, null);
        };
    }
}
