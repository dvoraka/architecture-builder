package dvoraka.archbuilder.submodule;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;

import java.util.Collection;

public interface Submodule {

    void addSubmoduleTo(Directory root);

    Collection<BeanMapping> getConfiguration();
}
