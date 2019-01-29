package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface Receiver<M extends Message> extends NetworkMessageListener<M> {

    void addMessageListener(NetworkMessageListener<M> listener);

    void removeMessageListener(NetworkMessageListener<M> listener);
}
