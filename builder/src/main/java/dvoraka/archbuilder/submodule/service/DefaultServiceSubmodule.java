package dvoraka.archbuilder.submodule.service;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.sample.microservice.service.BaseService;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;

import java.util.Collection;
import java.util.Collections;

public class DefaultServiceSubmodule implements ServiceSubmodule {

    private final ServiceSubmodule configuredSubmodule;


    public DefaultServiceSubmodule(BuilderHelper helper, SpringConfigGenerator configGenerator) {

        configuredSubmodule = new ConfigurableServiceSubmodule(
                BaseService.class,
                Collections.emptyList(),
                helper,
                configGenerator
        );
    }

    @Override
    public Directory getService() {
        return configuredSubmodule.getService();
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        return configuredSubmodule.getConfiguration();
    }

    @Override
    public void addSubmoduleTo(Directory root) {
        configuredSubmodule.addSubmoduleTo(root);
    }
}
