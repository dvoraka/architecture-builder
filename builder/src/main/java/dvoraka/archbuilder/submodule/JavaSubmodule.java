package dvoraka.archbuilder.submodule;

import dvoraka.archbuilder.springconfig.BeanMapping;

import java.util.Collection;

public interface JavaSubmodule extends Submodule {

    Collection<BeanMapping> getConfiguration();
}
