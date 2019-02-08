package dvoraka.archbuilder.sample.microservice.data.message;

import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;
import dvoraka.archbuilder.sample.microservice.service.BaseService;

import java.util.function.Function;


public abstract class RequestMessage<
        S extends BaseService,
        R extends ResponseMessage<D, E>,
        D extends ResultData<E>,
        E extends BaseException>
        extends BaseMessage implements Message, Function<S, R> {

    @Override
    public abstract R apply(S service);
}
