package dvoraka.archbuilder.springconfig;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public final class BeanMapping {

    private final Class<?> type;
    private final Directory typeDir;
    private final Class<?> toType;
    private final Directory toTypeDir;
    private final String name;
    private final List<BeanParameter> parameters;
    private final String code;
    private final Function<BeanMapping, Object> codeTemplate;


    private BeanMapping(
            Class<?> type,
            Directory typeDir,
            Class<?> toType,
            Directory toTypeDir,
            String name,
            List<BeanParameter> parameters,
            String code,
            Function<BeanMapping, Object> codeTemplate
    ) {
        this.type = type;
        this.typeDir = typeDir;
        this.toType = toType;
        this.toTypeDir = toTypeDir;
        this.name = name;
        this.parameters = parameters;
        this.code = code;
        this.codeTemplate = codeTemplate;
    }

    public Class<?> getType() {
        return type;
    }

    public Directory getTypeDir() {
        return typeDir;
    }

    public Class<?> getToType() {
        return toType;
    }

    public Directory getToTypeDir() {
        return toTypeDir;
    }

    public String getName() {
        return name;
    }

    public List<BeanParameter> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public String getCode() {
        return code;
    }

    public Function<BeanMapping, Object> getCodeTemplate() {
        return codeTemplate;
    }

    @Override
    public String toString() {
        return "BeanMapping{" +
                "type=" + type +
                ", typeDir=" + typeDir +
                ", toType=" + toType +
                ", toTypeDir=" + toTypeDir +
                ", name='" + name + '\'' +
                ", parameters=" + parameters +
                ", code='" + code + '\'' +
                ", codeTemplate=" + codeTemplate +
                '}';
    }

    public static class Builder {

        private Class<?> type;
        private Directory typeDir;
        private Class<?> toType;
        private Directory toTypeDir;
        private String name;
        private List<BeanParameter> parameters;
        private String code;
        private Function<BeanMapping, Object> codeTemplate;


        public Builder(String name) {
            if (requireNonNull(name).isEmpty()) {
                throw new GeneratorException("Name is empty.");
            }
            this.name = name;

            parameters = new ArrayList<>();
        }

        public Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        public Builder typeDir(Directory typeDir) {
            this.typeDir = typeDir;
            return this;
        }

        public Builder toType(Class<?> toType) {
            this.toType = toType;
            return this;
        }

        public Builder toTypeDir(Directory toTypeDir) {
            this.toTypeDir = toTypeDir;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder addParameter(BeanParameter parameter) {
            parameters.add(parameter);
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder codeTemplate(Function<BeanMapping, Object> template) {
            this.codeTemplate = template;
            return this;
        }

        public BeanMapping build() {
            return new BeanMapping(type, typeDir, toType, toTypeDir, name, parameters, code, codeTemplate);
        }
    }
}
