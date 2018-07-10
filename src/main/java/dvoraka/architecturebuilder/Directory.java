package dvoraka.architecturebuilder;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.ArrayList;
import java.util.List;

public class Directory {

    private String name;
    private String filename;

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
        if (getParent() == null) {
            return getName();
        } else {
            return getParent().getPackageName() + "." + getName();
        }
    }

    boolean isRoot() {
        return getParent() == null;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public Directory getParent() {
        return parent;
    }

    public List<Directory> getChildren() {
        return children;
    }
}
