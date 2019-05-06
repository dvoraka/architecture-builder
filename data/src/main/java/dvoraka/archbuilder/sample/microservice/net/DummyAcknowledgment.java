package dvoraka.archbuilder.sample.microservice.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyAcknowledgment implements Acknowledgment {

    private static final Logger log = LoggerFactory.getLogger(DummyAcknowledgment.class);


    @Override
    public void ack() {
        log.debug("ACK");
    }
}
