package dvoraka.archbuilder.sample.microservice.data.message;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseMessage implements Message {

    protected String id;
    protected Instant timestamp;


    protected BaseMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }
}
