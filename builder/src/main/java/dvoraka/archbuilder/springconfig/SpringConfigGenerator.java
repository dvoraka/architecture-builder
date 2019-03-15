package dvoraka.archbuilder.springconfig;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;

import java.util.List;

public interface SpringConfigGenerator extends SpringConfigTemplate {

    String genConfiguration(List<BeanMapping> beanMappings, Directory dir) throws GeneratorException;
}
