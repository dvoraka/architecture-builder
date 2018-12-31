package dvoraka.archbuilder.test;

import java.util.List;

public class Class4c1m {

    private String str;
    private Long index;
    private List<String> names;


    private Class4c1m(String str) {
        this.str = str;
    }

    protected Class4c1m(Long index) {
        this.index = index;
    }

    Class4c1m(String str, Long index) {
        this(str);
        this.index = index;
    }

    public Class4c1m(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }
}
