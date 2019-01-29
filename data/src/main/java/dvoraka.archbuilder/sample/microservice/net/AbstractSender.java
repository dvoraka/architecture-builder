package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public abstract class AbstractSender<M extends Message> implements Sender<M> {
}
