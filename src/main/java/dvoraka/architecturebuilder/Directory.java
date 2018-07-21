package dvoraka.architecturebuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Directory {

    private String name;
    private String filename;

    private DirType type;

    @JsonBackReference
    private Directory parent;
    @JsonManagedReference
    private List<Directory> children;


    public Directory() {
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

    boolean isRoot() {
        return getParent() == null;
    }

    boolean isBase() {
        return type.isBaseType();
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
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
        private DirType type;
        private Directory parent;


        public DirectoryBuilder(String name) {
            this.name = name;
        }

        public DirectoryBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public DirectoryBuilder withType(DirType type) {
            this.type = type;
            return this;
        }

        public DirectoryBuilder withParent(Directory parent) {
            this.parent = parent;
            return this;
        }

        public Directory build() {
            Directory directory = new Directory();
            directory.name = this.name;
            directory.type = this.type;
            directory.filename = this.filename;
            directory.parent = this.parent;

            if (parent != null) {
                parent.addChildren(directory);
            }

            return directory;
        }
    }
}
