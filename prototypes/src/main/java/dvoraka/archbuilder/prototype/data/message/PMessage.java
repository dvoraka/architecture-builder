package dvoraka.archbuilder.prototype.data.message;

public class PMessage<T> {

    private String id;
    private final T data;


    public PMessage(T data) {
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }
}
