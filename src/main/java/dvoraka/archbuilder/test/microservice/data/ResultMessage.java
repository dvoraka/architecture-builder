package dvoraka.archbuilder.test.microservice.data;


public interface ResultMessage<D extends ResultData> extends Message {

    String getCorrId();

    D getResultData();
}
