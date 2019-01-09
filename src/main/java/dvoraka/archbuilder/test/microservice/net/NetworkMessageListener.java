package dvoraka.archbuilder.test.microservice.net;

import dvoraka.archbuilder.test.microservice.data.message.Message;

public interface NetworkMessageListener<M extends Message> {

    void onMessage(M message, Acknowledgment acknowledgment);
}
