package dvoraka.archbuilder.sample.microservice.data;


import java.util.Optional;

public class ResultData<E extends BaseException> {

    protected E exception;


    public Optional<E> getException() {
        return Optional.ofNullable(exception);
    }

    public ResultData<E> setException(E exception) {
        this.exception = exception;
        return this;
    }
}
