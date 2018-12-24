package dvoraka.archbuilder.test.microservice.data;

import dvoraka.archbuilder.test.microservice.service.BaseService;

import java.util.function.Function;


public abstract class RequestMessage<S extends BaseService> extends BaseMessage
        implements Message, Function<S, Message> {

    @Override
    public abstract Message apply(S s);
}
