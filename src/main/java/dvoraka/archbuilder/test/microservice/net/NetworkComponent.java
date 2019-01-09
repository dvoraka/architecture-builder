package dvoraka.archbuilder.test.microservice.net;

import dvoraka.archbuilder.test.microservice.data.ResultData;
import dvoraka.archbuilder.test.microservice.data.message.Message;
import dvoraka.archbuilder.test.microservice.data.message.ResultMessage;

public interface NetworkComponent<
        M extends Message,
        R extends ResultMessage<D>,
        D extends ResultData> {

    void send(R resultMessage);

    void addMessageListener(NetworkMessageListener<M> listener);

    void removeMessageListener(NetworkMessageListener<M> listener);
}
