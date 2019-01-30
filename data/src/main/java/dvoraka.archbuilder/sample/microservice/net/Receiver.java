package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface Receiver<M extends Message> extends NetMessageListener<M> {

    void addMessageListener(NetMessageListener<M> listener);

    void removeMessageListener(NetMessageListener<M> listener);
}
