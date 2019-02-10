package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.net.receive.NetReceiver;
import dvoraka.archbuilder.sample.microservice.net.send.NetSender;

/**
 * Generic network component.
 *
 * @param <M> the receiving message type
 * @param <R> the sending message type
 */
public interface GenericNetComponent<M extends Message, R extends Message>
        extends NetReceiver<M>, NetSender<R> {
}
