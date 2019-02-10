package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

//TODO: merge with abstract net receiver

/**
 * Base class for network receiver.
 *
 * @param <M> the receiving message type
 */
public class BaseNetReceiver<M extends Message> implements NetReceiver<M> {

    private final Set<NetMessageListener<M>> listeners;


    public BaseNetReceiver() {
        listeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public void addMessageListener(NetMessageListener<M> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeMessageListener(NetMessageListener<M> listener) {
        listeners.remove(listener);
    }

    @Override
    public void onMessage(M message, Acknowledgment acknowledgment) {
        listeners.forEach(listener -> listener.onMessage(message, acknowledgment));
    }
}
