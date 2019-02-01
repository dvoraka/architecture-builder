package dvoraka.archbuilder.springconfig;

import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.exception.GeneratorException;

import static java.util.Objects.requireNonNull;

public final class BeanParameter {

    private Class<?> type;
    private Directory typeDir;
    private String name;


    private BeanParameter(Class<?> type, Directory typeDir, String name) {
        this.type = type;
        this.typeDir = typeDir;
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public Directory getTypeDir() {
        return typeDir;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "BeanParameter{" +
                "type=" + type +
                ", typeDir=" + typeDir +
                ", name='" + name + '\'' +
                '}';
    }

    public static final class Builder {

        private Class<?> type;
        private Directory typeDir;
        private String name;


        public Builder(String name) {
            if (name.isEmpty()) {
                throw new GeneratorException("Name is empty");
            }
            this.name = requireNonNull(name);
        }

        public Builder type(Class<?> type) {
            this.type = type;
            return this;
        }

        public Builder typeDir(Directory typeDir) {
            this.typeDir = typeDir;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public BeanParameter build() {
            return new BeanParameter(type, typeDir, name);
        }
    }
}
