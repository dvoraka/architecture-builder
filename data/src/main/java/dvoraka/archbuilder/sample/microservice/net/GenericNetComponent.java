package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

/**
 * Generic network component.
 *
 * @param <M> the request message type
 * @param <R> the response message type
 */
public interface GenericNetComponent<M extends Message, R extends Message>
        extends NetReceiver<M>, NetSender<R> {
}
