package dvoraka.archbuilder.sample.microservice.net.send;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public abstract class AbstractNetSender<M extends Message> implements NetSender<M> {
}
