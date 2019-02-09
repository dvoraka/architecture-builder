package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

/**
 * Generic network component.
 *
 * @param <M> the receiving message type
 * @param <R> the sending message type
 */
public interface GenericNetComponent<M extends Message, R extends Message>
        extends NetReceiver<M>, NetSender<R> {
}
