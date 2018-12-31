package dvoraka.archbuilder.test;

import java.util.List;

public class Class3c1m {

    private String str;
    private Long index;
    private List<String> names;


    private Class3c1m(String str) {
        this.str = str;
    }

    protected Class3c1m(Long index) {
        this.index = index;
    }

    public Class3c1m(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }
}
