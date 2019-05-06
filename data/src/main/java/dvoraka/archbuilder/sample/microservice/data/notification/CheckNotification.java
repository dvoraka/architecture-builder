package dvoraka.archbuilder.sample.microservice.data.notification;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class CheckNotification implements Notification {

    private final String id;
    private final Instant timestamp;


    public CheckNotification() {
        id = UUID.randomUUID().toString();
        timestamp = Instant.now();
    }

    @Override
    public NotificationType getType() {
        return NotificationType.CHECK;
    }

    @Override
    public Map<String, Object> getData() {
        return Collections.emptyMap();
    }

    @Override
    public String getId() {
        return getId();
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
