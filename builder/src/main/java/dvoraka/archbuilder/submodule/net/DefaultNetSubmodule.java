package dvoraka.archbuilder.submodule.net;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.TemplateHelper;

import java.util.Collection;

public class DefaultNetSubmodule implements NetSubmodule, TemplateHelper {

    private final NetSubmodule configuredSubmodule;


    public DefaultNetSubmodule(
            String baseName,
            Directory service,
            SpringConfigGenerator configGenerator
    ) {
        configuredSubmodule = new ConfigurableNetSubmodule(
                baseName,
                service,
                new DefaultNetConfig(),
                configGenerator
        );
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {
        configuredSubmodule.addSubmoduleTo(srcBase);
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        return configuredSubmodule.getConfiguration();
    }
}
