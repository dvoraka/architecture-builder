package dvoraka.archbuilder.submodule;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;

import java.util.Collection;

public interface Submodule {

    void addSubmodule(Directory root);

    Collection<BeanMapping> getConfiguration();
}
