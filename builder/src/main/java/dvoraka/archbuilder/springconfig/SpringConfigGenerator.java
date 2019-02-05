package dvoraka.archbuilder.springconfig;

import dvoraka.archbuilder.Directory;

import java.util.List;

public interface SpringConfigGenerator extends SpringConfigTemplate {

    //TODO: exceptions
    String genConfiguration(List<BeanMapping> beanMappings, Directory dir) throws ClassNotFoundException;
}
