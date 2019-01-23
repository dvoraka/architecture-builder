package dvoraka.archbuilder.sample.microservice.data.message;

import java.time.Instant;

public interface Message {

    String getId();

    Instant getTimestamp();
}
