package dvoraka.archbuilder.sample.microservice.data.message;

import dvoraka.archbuilder.sample.microservice.service.BaseService;

import java.util.function.Function;


public abstract class RequestMessage<S extends BaseService> extends BaseMessage
        implements Message, Function<S, Message> {

    @Override
    public abstract Message apply(S s);
}
