package dvoraka.archbuilder.prototype.data.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.net.Acknowledgment;
import dvoraka.archbuilder.sample.microservice.net.NetMessageListener;
import dvoraka.archbuilder.sample.microservice.net.NetReceiver;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class PBaseNetReceiver<M extends Message> implements NetReceiver<M> {

    private final Set<NetMessageListener<M>> listeners;


    public PBaseNetReceiver() {
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
