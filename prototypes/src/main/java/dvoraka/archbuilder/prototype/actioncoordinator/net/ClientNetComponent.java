package dvoraka.archbuilder.prototype.actioncoordinator.net;

import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.message.Message;
import dvoraka.archbuilder.sample.microservice.data.message.ResultMessage;

import java.util.function.Consumer;

public interface ClientNetComponent<M extends Message, R extends ResultMessage, E, T, RD> {

    //TODO: exception
    void send(M message, Consumer<R> callback) throws BaseException;

    void sendFast(M message, Consumer<R> callback);
}
