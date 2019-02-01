package dvoraka.archbuilder.springconfig;

import java.util.List;

public interface SpringConfigGenerator {

    String genConfiguration(List<BeanMapping> beanMappings);
}
