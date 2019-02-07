package dvoraka.archbuilder.sample.microservice.net;

import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;
import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.data.message.ResultMessage;

/**
 * Service network component.
 *
 * @param <M> the request message type
 * @param <R> the response message type
 * @param <D> the response data type
 * @param <E> the response data exception type
 */
public interface ServiceNetComponent<
        M extends Message,
        R extends ResultMessage<D, E>,
        D extends ResultData<E>,
        E extends BaseException>
        extends GenericNetComponent<M, R> {
}
