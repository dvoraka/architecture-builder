package dvoraka.archbuilder.sample.microservice.net.receive;

import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.net.NetMessageListener;

public interface NetReceiver<M extends Message> extends NetMessageListener<M> {

    void addMessageListener(NetMessageListener<M> listener);

    void removeMessageListener(NetMessageListener<M> listener);
}
