package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class AbstractReceiver<M extends Message> implements Receiver<M> {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Set<NetworkMessageListener<M>> listeners;


    public AbstractReceiver() {
        listeners = new CopyOnWriteArraySet<>();
    }

    @Override
    public void onMessage(M message, Acknowledgment acknowledgment) {
        try {
            handleOnMessage(message, acknowledgment);
        } catch (Exception e) {
            log.error("On message failed!", e);
        }
    }

    private void handleOnMessage(M message, Acknowledgment acknowledgment) {
        log.debug("On message: {}", message);
        listeners.forEach(listener -> listener.onMessage(message, acknowledgment));
    }

    @Override
    public void addMessageListener(NetworkMessageListener<M> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeMessageListener(NetworkMessageListener<M> listener) {
        listeners.remove(listener);
    }
}
