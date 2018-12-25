package dvoraka.archbuilder.test.microservice.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static java.util.Objects.requireNonNull;


public abstract class ResponseMessage<D extends ResultData>
        extends BaseMessage implements ResultMessage<D> {

    protected String corrId;
    protected D resultData;


    protected ResponseMessage(String corrId, D resultData) {
        this.corrId = requireNonNull(corrId);
        this.resultData = resultData;
    }

    @Override
    public String getCorrId() {
        return corrId;
    }

    @JsonIgnore
    @Override
    public D getResultData() {
        return resultData;
    }
}
