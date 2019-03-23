package dvoraka.archbuilder.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static dvoraka.archbuilder.util.JavaUtils.path2pkg;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Directory {

    private final String id;

    private String name;
    private String filename;
    private String typeName;

    private boolean abstractType;

    private DirType type;

    @JsonBackReference
    private Directory parent;
    @JsonManagedReference
    private final List<Directory> children;

    private List<Directory> superTypes;

    private List<Directory> dependencies;

    private List<String> parameters;
    @JsonIgnore
    private List<String> metadata;

    private String text;
    private String doc;
    //TODO
    @JsonIgnore
    private Supplier<String> textSupplier;

    private Type intType;


    private Directory() {
        id = UUID.randomUUID().toString();
        children = new ArrayList<>();
    }

    private void addChildren(Directory directory) {
        children.add(directory);
    }

    @JsonIgnore
    public String getPackageName() {
        String pkgName;
        if (isBase()) {
            pkgName = path2pkg(getName());
        } else {
            if (getName().isEmpty()) {
                pkgName = getParent().getPackageName();
            } else {
                pkgName = getParent().getPackageName() + "." + path2pkg(getName());
            }
        }

        return pkgName;
    }

    @JsonIgnore
    public String getPath() {
        String path;
        if (isRoot()) {
            path = getName();
        } else {
            if (getName().isEmpty()) {
                path = getParent().getPath();
            } else {
                path = getParent().getPath() + File.separatorChar + getName();
            }
        }

        return path;
    }

    @JsonIgnore
    public boolean isRoot() {
        return getParent() == null;
    }

    @JsonIgnore
    public boolean isBase() {
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

    @JsonIgnore
    public boolean isInterfaceType() {
        return intType == Type.INTERFACE;
    }

    @JsonIgnore
    public boolean isEnumType() {
        return intType == Type.ENUM;
    }

    @JsonIgnore
    public boolean isAnnotationType() {
        return intType == Type.ANNOTATION;
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

    public List<String> getMetadata() {
        return Collections.unmodifiableList(metadata);
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

    public String getDoc() {
        return doc;
    }

    public Type getIntType() {
        return intType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directory directory = (Directory) o;
        return abstractType == directory.abstractType &&
                intType == directory.intType &&
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
        return Objects.hash(id, name, filename, typeName, abstractType, intType, type,
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

    public static final class Builder {

        private String name;
        private String filename;
        private String typeName;

        private boolean abstractType;

        private DirType type;
        private Directory parent;
        private List<Directory> superTypes;

        private List<Directory> dependencies;
        private List<String> parameters;
        private List<String> metadata;
        private String text;
        private Supplier<String> textSupplier;
        private String doc;

        private Type intType = Type.CLASS;


        public Builder(String name, DirType dirType) {
            this.name = name;
            this.type = dirType;

            superTypes = new ArrayList<>();
            dependencies = new ArrayList<>();
            parameters = new ArrayList<>();
            metadata = new ArrayList<>();
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder typeName(String typeName) {
            this.typeName = typeName;
            String[] names = typeName.split("\\.");
            this.filename = names[names.length - 1];
            return this;
        }

        public Builder typeClass(Class<?> clazz) {
            return typeName(clazz.getName());
        }

        public Builder abstractType() {
            this.abstractType = true;
            return this;
        }

        public Builder interfaceType() {
            abstractType();
            this.intType = Type.INTERFACE;
            return this;
        }

        public Builder enumType() {
            this.intType = Type.ENUM;
            return this;
        }

        public Builder annotationType() {
            this.intType = Type.ANNOTATION;
            return this;
        }

        public Builder type(DirType type) {
            this.type = type;
            return this;
        }

        public Builder parent(Directory parent) {
            this.parent = parent;
            return this;
        }

        public Builder superType(Class<?> clazz) {
            if (parent == null) {
                throw new GeneratorException("Parent must be set before superType method.");
            }
            Directory wrapper = Utils.createAbstractDirFor(clazz, parent);
            this.superTypes.add(wrapper);
            return this;
        }

        public Builder superType(Directory superType) {
            dependsOn(superType);
            this.superTypes.add(superType);
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder text(Supplier<String> supplier) {
            this.textSupplier = supplier;
            return this;
        }

        public Builder doc(String doc) {
            this.doc = doc;
            return this;
        }

        public Builder dependsOn(Directory directory) {
            if (type == DirType.SPRING_CONFIG || directory.getType() == DirType.SPRING_CONFIG) {
                throw new GeneratorException("Spring config must not have any dependencies.");
            }
            dependencies.add(directory);
            return this;
        }

        public Builder parameterType(String typeName) {
            parameters.add(typeName);
            return this;
        }

        public Builder parameterType(Class<?> cls) {
            return parameterType(cls.getName());
        }

        public Builder parameterType(Directory directory) {
            dependsOn(directory);
            directory.getFilename()
                    .orElseThrow(() -> Utils.noFilenameException(directory));
            return parameterType(directory.getTypeName());
        }

        public Builder parameterType(Collection<Class<?>> classes) {
            for (Class<?> cls : classes) {
                parameterType(cls.getName());
            }
            return this;
        }

        public Builder metadata(String data) {
            metadata.add(data);
            return this;
        }

        public Builder metadata(Class<?> clazz) {
            return metadata(clazz.getName());
        }

        public Builder metadata(Directory directory) {
            dependsOn(directory);
            return metadata(directory.getTypeName());
        }

        public Directory build() {
            Directory directory = new Directory();
            directory.name = this.name;
            directory.filename = this.filename;
            directory.typeName = this.typeName;

            directory.abstractType = this.abstractType;

            directory.type = this.type;
            directory.parent = this.parent;
            directory.superTypes = this.superTypes;
            directory.dependencies = this.dependencies;
            directory.parameters = this.parameters;
            directory.metadata = this.metadata;

            directory.text = this.text;
            directory.textSupplier = this.textSupplier;
            directory.doc = this.doc;

            directory.intType = this.intType;

            if (parent != null) {
                parent.addChildren(directory);
            }

            // generate typename if necessary
            if (isTypenameNecessary(directory) && directory.typeName == null) {
                if (filename == null || filename.isEmpty()) {
                    throw Utils.noFilenameException(directory);
                }

                if (filename.endsWith(".java")) {
                    String[] parts = filename.split("\\.");
                    String typeName = parts[0];
                    directory.typeName = directory.getPackageName() + "." + typeName;
                } else {
                    //TODO: should be removed
                    directory.typeName = directory.getPackageName() + "." + filename;
                }
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
