package dvoraka.archbuilder.sample.microservice.data;


public class ResultData<E extends BaseException> {

    protected E exception;


    public E getException() {
        return exception;
    }

    public ResultData<E> setException(E exception) {
        this.exception = exception;
        return this;
    }
}
