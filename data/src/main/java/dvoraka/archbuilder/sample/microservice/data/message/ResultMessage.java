package dvoraka.archbuilder.sample.microservice.data.message;


import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;

public interface ResultMessage<
        D extends ResultData<E>,
        E extends BaseException>
        extends Message {

    String getCorrId();

    D getResultData();

    void checkStatus() throws E;
}
