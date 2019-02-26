package dvoraka.archbuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import dvoraka.archbuilder.generate.JavaUtils;
import dvoraka.archbuilder.generate.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
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

    private List<Directory> superTypes;

    private List<Directory> dependencies;
    private List<String> parameters;

    private String text;
    //TODO
    @JsonIgnore
    private Supplier<String> textSupplier;


    private Directory() {
        id = UUID.randomUUID().toString();
        children = new ArrayList<>();
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
        return Collections.unmodifiableList(children);
    }

    public List<Directory> getSuperTypes() {
        return Collections.unmodifiableList(superTypes);
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

    public Supplier<String> getTextSupplier() {
        return textSupplier;
    }

    public void setTextSupplier(Supplier<String> textSupplier) {
        this.textSupplier = textSupplier;
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
                Objects.equals(superTypes, directory.superTypes) &&
                Objects.equals(dependencies, directory.dependencies) &&
                Objects.equals(parameters, directory.parameters) &&
                Objects.equals(text, directory.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, filename, typeName, abstractType, interfaceType, type,
                children, superTypes, dependencies, parameters, text);
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
        private List<Directory> superTypes;

        private List<Directory> dependencies;
        private List<String> parameters;
        private String text;
        private Supplier<String> textSupplier;


        /**
         * @deprecated use {@link #DirectoryBuilder(String, DirType)}
         */
        public DirectoryBuilder(String name) {
            this.name = name;
            superTypes = new ArrayList<>();
            dependencies = new ArrayList<>();
            parameters = new ArrayList<>();
        }

        public DirectoryBuilder(String name, DirType dirType) {
            this(name);
            this.type = dirType;
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
            dependsOn(superType);
            this.superTypes.add(superType);
            return this;
        }

        public DirectoryBuilder text(String text) {
            this.text = text;
            return this;
        }

        public DirectoryBuilder textSupplier(Supplier<String> supplier) {
            this.textSupplier = supplier;
            return this;
        }

        public DirectoryBuilder dependsOn(Directory directory) {
            if (type == DirType.SPRING_CONFIG || directory.getType() == DirType.SPRING_CONFIG) {
                throw new RuntimeException("Spring config must not have any dependencies.");
            }
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

            directory.getFilename().orElseThrow(() -> Utils.noFilenameException(directory));

            return parameterTypeName(directory.getTypeName());
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
            directory.superTypes = this.superTypes;
            directory.dependencies = this.dependencies;
            directory.parameters = this.parameters;

            directory.text = this.text;
            directory.textSupplier = this.textSupplier;

            if (parent != null) {
                parent.addChildren(directory);
            }

            // generate typename if necessary
            if (isTypenameNecessary(directory) && directory.typeName == null) {
                if (filename == null || filename.isEmpty()) {
                    throw Utils.noFilenameException(directory);
                }
                directory.typeName = directory.getPackageName() + "." + filename;
            }

            return directory;
        }

        private boolean isTypenameNecessary(Directory directory) {
            return directory.type == DirType.CUSTOM_TYPE
                    || directory.type == DirType.IMPL
                    || directory.type == DirType.NEW_TYPE
                    || directory.type == DirType.SERVICE
                    || directory.type == DirType.SERVICE_IMPL
                    || directory.type == DirType.SPRING_CONFIG;
        }
    }
}
