package dvoraka.architecturebuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Directory {

    private String id;

    private String name;
    private String filename;
    private String typeName;

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
            String path = getParent().getPackageName() + File.separatorChar + getName();
            return path2pkg(path);
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

    public boolean isRoot() {
        return getParent() == null;
    }

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

    private String pkg2path(String packageName) {
        return packageName.replace('.', File.separatorChar);
    }

    private String path2pkg(String packageName) {
        return packageName.replace(File.separatorChar, '.');
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

        public DirectoryBuilder parameterType(String className) {
            parameters.add(className);
            return this;
        }

        public Directory build() {
            Directory directory = new Directory();
            directory.name = this.name;
            directory.filename = this.filename;
            directory.typeName = this.typeName;

            directory.type = this.type;
            directory.parent = this.parent;
            directory.superType = this.superType;
            directory.dependencies = this.dependencies;
            directory.parameters = this.parameters;
            directory.text = this.text;

            if (parent != null) {
                parent.addChildren(directory);
            }

            return directory;
        }
    }
}
