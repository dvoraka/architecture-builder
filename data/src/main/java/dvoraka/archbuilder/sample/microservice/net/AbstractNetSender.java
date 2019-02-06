package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.message.Message;

public abstract class AbstractNetSender<M extends Message> implements NetSender<M> {
}
