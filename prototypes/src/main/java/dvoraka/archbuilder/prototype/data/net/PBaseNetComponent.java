package dvoraka.archbuilder.prototype.data.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.GenericNetComponent;

public abstract class PBaseNetComponent<M extends Message, R extends ResponseMessage>
        extends PBaseNetReceiver<M>
        implements GenericNetComponent<M, R> {
}
