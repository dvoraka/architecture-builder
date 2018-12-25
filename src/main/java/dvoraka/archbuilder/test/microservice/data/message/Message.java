package dvoraka.archbuilder.test.microservice.data.message;

import java.time.Instant;

public interface Message {

    String getId();

    Instant getTimestamp();
}
