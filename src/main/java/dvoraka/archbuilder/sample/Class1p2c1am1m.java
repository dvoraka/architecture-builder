package dvoraka.archbuilder.sample;

public abstract class Class1p2c1am1m<T> {

    private String str;
    private T data;


    protected Class1p2c1am1m(String str) {
        this.str = str;
    }

    public Class1p2c1am1m(String str, T data) {
        this.str = str;
        this.data = data;
    }

    protected abstract String getStr();

    public T getData() {
        return data;
    }
}
