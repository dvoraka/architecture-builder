package dvoraka.archbuilder.springconfig;

import java.util.List;

public interface SpringConfigGenerator extends SpringConfigTemplate {

    //TODO: exceptions
    String genConfiguration(List<BeanMapping> beanMappings) throws ClassNotFoundException;
}
