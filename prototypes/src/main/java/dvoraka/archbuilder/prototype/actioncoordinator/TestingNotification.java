package dvoraka.archbuilder.prototype.actioncoordinator;

import dvoraka.archbuilder.prototype.actioncoordinator.action.order.OrderStatus;
import dvoraka.archbuilder.sample.microservice.data.notification.Notification;
import dvoraka.archbuilder.sample.microservice.data.notification.NotificationType;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestingNotification implements Notification {

    private final String id;
    private final Instant timestamp;
    private final Map<String, Object> data;


    public TestingNotification(long orderId) {
        id = UUID.randomUUID().toString();
        timestamp = Instant.now();

        data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("orderStatus", OrderStatus.COMPLETED);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.CHECK;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "CheckNotification{" +
                "id='" + id + '\'' +
                ", timestamp=" + timestamp +
                ", data=" + data +
                '}';
    }
}
