package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface ServiceNetComponent<M extends Message, R extends Message>
        extends NetSender<M>, NetReceiver<R> {
}
