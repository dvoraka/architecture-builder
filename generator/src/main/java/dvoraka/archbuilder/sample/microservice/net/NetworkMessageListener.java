package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface NetworkMessageListener<M extends Message> {

    void onMessage(M message, Acknowledgment acknowledgment);
}
