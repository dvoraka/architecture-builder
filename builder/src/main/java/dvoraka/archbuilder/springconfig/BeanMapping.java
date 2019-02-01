package dvoraka.archbuilder.springconfig;

import dvoraka.archbuilder.Directory;

import java.util.ArrayList;
import java.util.List;

public class BeanMapping {

    private Class<?> type;
    private Directory typeDir;
    private String name;
    private List<BeanParameter> parameters;
    private String code;


    public BeanMapping() {
        parameters = new ArrayList<>();
    }

    public void addParameter(BeanParameter parameter) {
        parameters.add(parameter);
    }

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

    public List<BeanParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<BeanParameter> parameters) {
        this.parameters = parameters;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
