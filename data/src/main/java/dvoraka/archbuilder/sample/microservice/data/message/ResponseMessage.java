package dvoraka.archbuilder.sample.microservice.data.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dvoraka.archbuilder.sample.microservice.data.BaseException;
import dvoraka.archbuilder.sample.microservice.data.ResultData;

import static java.util.Objects.requireNonNull;


public abstract class ResponseMessage<
        D extends ResultData<E>,
        E extends BaseException>
        extends BaseMessage
        implements ResultMessage<D, E> {

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

    @Override
    public void checkStatus() throws E {
        if (getResultData() != null && getResultData().getException().isPresent()) {
            throw getResultData().getException().get();
        }
    }
}
