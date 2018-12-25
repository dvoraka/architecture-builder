package dvoraka.archbuilder.test.microservice.data.message;


import dvoraka.archbuilder.test.microservice.data.ResultData;

public interface ResultMessage<D extends ResultData> extends Message {

    String getCorrId();

    D getResultData();
}
