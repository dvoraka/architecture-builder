package dvoraka.archbuilder.sample;

import java.util.List;

public abstract class AbstractClassE3c1am1m implements Interface1am {

    private String str;
    private Long index;
    private List<String> names;


    private AbstractClassE3c1am1m(String str) {
        this.str = str;
    }

    protected AbstractClassE3c1am1m(Long index) {
        this.index = index;
    }

    public AbstractClassE3c1am1m(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }
}
