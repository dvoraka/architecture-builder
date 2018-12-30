package dvoraka.archbuilder.test;

public class Class1p2c1m<T> {

    private String str;
    private T data;


    protected Class1p2c1m(String str) {
        this.str = str;
    }

    public Class1p2c1m(String str, T data) {
        this.str = str;
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
