package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

/**
 * Generic network component.
 *
 * @param <M> the type of the request message
 * @param <R> the type of the response message
 */
public interface GenericNetComponent<M extends Message, R extends Message>
        extends NetReceiver<M>, NetSender<R> {
}
