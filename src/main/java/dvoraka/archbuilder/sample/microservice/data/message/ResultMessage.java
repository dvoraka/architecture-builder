package dvoraka.archbuilder.sample.microservice.data.message;


import dvoraka.archbuilder.sample.microservice.data.ResultData;

public interface ResultMessage<D extends ResultData> extends Message {

    String getCorrId();

    D getResultData();
}
