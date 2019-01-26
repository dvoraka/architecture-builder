package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public interface ServiceNetworkComponent<M extends Message, R extends Message>
        extends Sender<M>, Receiver<R> {
}
