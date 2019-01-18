package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface NetworkComponent<M extends Message, R extends Message>
        extends Sender<M>, Receiver<R> {
}
