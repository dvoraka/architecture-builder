package dvoraka.archbuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import dvoraka.archbuilder.generate.JavaUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Directory {

    private String id;

    private String name;
    private String filename;
    private String typeName;

    private boolean abstractType;
    private boolean interfaceType;

    private DirType type;

    @JsonBackReference
    private Directory parent;
    @JsonManagedReference
    private List<Directory> children;

    private Directory superType;

    private List<Directory> dependencies;
    private List<String> parameters;

    private String text;


    private Directory() {
        id = UUID.randomUUID().toString();
        children = new ArrayList<>();
        dependencies = new ArrayList<>();
        parameters = new ArrayList<>();
    }

    public void addChildren(Directory directory) {
        children.add(directory);
    }

    @JsonIgnore
    public String getPackageName() {
        if (isBase()) {
            return getName();
        } else {
            String packagePath = getParent().getPackageName();
            if (!getName().isEmpty()) {
                packagePath += File.separatorChar + getName();
            }
            return JavaUtils.path2pkg(packagePath);
        }
    }

    @JsonIgnore
    public String getPath() {
        if (isRoot()) {
            return getName();
        } else {
            return getParent().getPath() + File.separatorChar + getName();
        }
    }

    @JsonIgnore
    public boolean isRoot() {
        return getParent() == null;
    }

    @JsonIgnore
    boolean isBase() {
        return type.isBase();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getFilename() {
        return Optional.ofNullable(filename);
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isAbstractType() {
        return abstractType;
    }

    public boolean isInterfaceType() {
        return interfaceType;
    }

    public DirType getType() {
        return type;
    }

    public Directory getParent() {
        return parent;
    }

    public List<Directory> getChildren() {
        return children;
    }

    public Optional<Directory> getSuperType() {
        return Optional.ofNullable(superType);
    }

    public List<Directory> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public List<String> getParameters() {
        return Collections.unmodifiableList(parameters);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directory directory = (Directory) o;
        return abstractType == directory.abstractType &&
                interfaceType == directory.interfaceType &&
                id.equals(directory.id) &&
                name.equals(directory.name) &&
                Objects.equals(filename, directory.filename) &&
                Objects.equals(typeName, directory.typeName) &&
                type == directory.type &&
                //TODO: check parent
//                Objects.equals(parent, directory.parent) &&
                Objects.equals(children, directory.children) &&
                Objects.equals(superType, directory.superType) &&
                Objects.equals(dependencies, directory.dependencies) &&
                Objects.equals(parameters, directory.parameters) &&
                Objects.equals(text, directory.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, filename, typeName, abstractType, interfaceType, type,
                children, superType, dependencies, parameters, text);
    }

    @Override
    public String toString() {
        return "Directory{" +
                "name='" + name + '\'' +
                ", filename='" + filename + '\'' +
                ", type=" + type +
                ", parent=" + (parent != null ? parent.getName() : null) +
                ", children=" + children +
                '}';
    }

    public static final class DirectoryBuilder {

        private String name;
        private String filename;
        private String typeName;

        private boolean abstractType;
        private boolean interfaceType;

        private DirType type;
        private Directory parent;
        private Directory superType;

        private List<Directory> dependencies;
        private List<String> parameters;
        private String text;


        public DirectoryBuilder(String name) {
            this.name = name;
            dependencies = new ArrayList<>();
            parameters = new ArrayList<>();
        }

        public DirectoryBuilder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public DirectoryBuilder typeName(String typeName) {
            this.typeName = typeName;
            String[] names = typeName.split("\\.");
            this.filename = names[names.length - 1];
            return this;
        }

        public DirectoryBuilder typeClass(Class<?> clazz) {
            return typeName(clazz.getName());
        }

        public DirectoryBuilder abstractType() {
            this.abstractType = true;
            return this;
        }

        public DirectoryBuilder interfaceType() {
            abstractType();
            this.interfaceType = true;
            return this;
        }

        public DirectoryBuilder type(DirType type) {
            this.type = type;
            return this;
        }

        public DirectoryBuilder parent(Directory parent) {
            this.parent = parent;
            return this;
        }

        public DirectoryBuilder superType(Directory superType) {
            this.superType = superType;
            dependsOn(superType);
            return this;
        }

        public DirectoryBuilder text(String text) {
            this.text = text;
            return this;
        }

        public DirectoryBuilder dependsOn(Directory directory) {
            dependencies.add(directory);
            return this;
        }

        public DirectoryBuilder parameterTypeName(String className) {
            parameters.add(className);
            return this;
        }

        public DirectoryBuilder parameterTypeClass(Class<?> clazz) {
            return parameterTypeName(clazz.getName());
        }

        public DirectoryBuilder parameterTypeDir(Directory directory) {
            dependsOn(directory);
            return parameterTypeName(JavaUtils.getClassName(directory));
        }

        public Directory build() {
            Directory directory = new Directory();
            directory.name = this.name;
            directory.filename = this.filename;
            directory.typeName = this.typeName;

            directory.abstractType = this.abstractType;
            directory.interfaceType = this.interfaceType;

            directory.type = this.type;
            directory.parent = this.parent;
            directory.superType = this.superType;
            directory.dependencies = this.dependencies;
            directory.parameters = this.parameters;
            directory.text = this.text;

            if (parent != null) {
                parent.addChildren(directory);
            }

            // generate typename if necessary
            if (directory.type == DirType.IMPL && directory.typeName == null) {
                directory.typeName = directory.getPackageName() + "." + filename;
            }

            return directory;
        }
    }
}
