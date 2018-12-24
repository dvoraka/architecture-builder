package dvoraka.archbuilder.test.microservice.data;

import java.time.Instant;

public interface Message {

    String getId();

    Instant getTimestamp();
}
