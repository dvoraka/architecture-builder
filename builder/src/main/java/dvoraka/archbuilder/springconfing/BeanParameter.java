package dvoraka.archbuilder.springconfing;

import dvoraka.archbuilder.Directory;

public class BeanParameter {

    private Class<?> type;
    private Directory typeDir;
    private String name;


    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Directory getTypeDir() {
        return typeDir;
    }

    public void setTypeDir(Directory typeDir) {
        this.typeDir = typeDir;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
