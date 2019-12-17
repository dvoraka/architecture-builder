package dvoraka.archbuilder.submodule.service.rest;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.sample.microservice.service.BaseService;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;

import java.util.Collection;
import java.util.Collections;

public class RestClientServiceSubmodule implements ServiceSubmodule {

    private final ServiceSubmodule configuredSubmodule;


    public RestClientServiceSubmodule(BuilderHelper helper, SpringConfigGenerator configGenerator) {

        configuredSubmodule = new ConfigurableRestServicesSubmodule(
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
