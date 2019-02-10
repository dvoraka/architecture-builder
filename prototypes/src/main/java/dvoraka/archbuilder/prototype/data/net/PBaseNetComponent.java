package dvoraka.archbuilder.prototype.data.net;

import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;
import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.data.message.ResponseMessage;
import dvoraka.archbuilder.sample.microservice.net.BaseNetReceiver;
import dvoraka.archbuilder.sample.microservice.net.ServiceNetComponent;

public abstract class PBaseNetComponent<
        M extends Message,
        R extends ResponseMessage<D, E>,
        D extends ResultData<E>,
        E extends BaseException>
        extends BaseNetReceiver<M>
        implements ServiceNetComponent<M, R, D, E> {
}
