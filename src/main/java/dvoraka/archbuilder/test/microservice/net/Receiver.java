package dvoraka.archbuilder.test.microservice.net;

import dvoraka.archbuilder.test.microservice.data.message.Message;

public interface Receiver<M extends Message> {

    void addMessageListener(NetworkMessageListener<M> listener);

    void removeMessageListener(NetworkMessageListener<M> listener);
}
