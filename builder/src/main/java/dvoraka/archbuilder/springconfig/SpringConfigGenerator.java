package dvoraka.archbuilder.springconfig;

import java.util.List;

public interface SpringConfigGenerator extends SpringConfigTemplate {

    String genConfiguration(List<BeanMapping> beanMappings) throws ClassNotFoundException;
}
