package dvoraka.archbuilder.test.microservice.net;

import dvoraka.archbuilder.test.microservice.data.message.Message;

public interface Sender<M extends Message> {

    void send(M message) throws Exception;
}
