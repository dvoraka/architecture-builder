package dvoraka.archbuilder.test.microservice.net;

import dvoraka.archbuilder.test.microservice.data.ResultData;
import dvoraka.archbuilder.test.microservice.data.message.ResultMessage;

public interface NetworkComponent<R extends ResultMessage<D>, D extends ResultData> {

    void send(R resultMessage);
}
