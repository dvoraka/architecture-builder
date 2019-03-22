package dvoraka.archbuilder.module.microservice;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.build.BuildSubmodule;
import dvoraka.archbuilder.submodule.build.ConfigurableGradleSubmodule;
import dvoraka.archbuilder.submodule.net.ConfigurableNetSubmodule;
import dvoraka.archbuilder.submodule.net.NetConfig;
import dvoraka.archbuilder.submodule.net.NetSubmodule;
import dvoraka.archbuilder.submodule.service.ConfigurableServiceSubmodule;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;
import dvoraka.archbuilder.submodule.spring.DefaultSpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringConfigSubmodule;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.BuildGradleTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;
import dvoraka.archbuilder.template.text.SettingsGradleTemplate;

import java.util.List;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;

public class ConfigurableMicroservice implements Module, TemplateHelper {

    public static final String MESSAGE_DIR = "data/message";

    private final Directory root;


    public ConfigurableMicroservice(
            String rootDirName,
            String packageName,
            Class<?> superService,
            List<Class<?>> typeArguments,
            String serviceName,
            NetConfig netConfig,
            SpringConfigGenerator configGenerator
    ) {
        root = root(rootDirName);
        Directory srcBase = srcRootAndBase(root, pkg2path(packageName));

        // service
        ServiceSubmodule serviceSubmodule = new ConfigurableServiceSubmodule(
                serviceName, superService, typeArguments, configGenerator);
        serviceSubmodule.addSubmoduleTo(srcBase);

        // network
        NetSubmodule netSubmodule = new ConfigurableNetSubmodule(
                serviceName, serviceSubmodule.getService(), netConfig, configGenerator);
        netSubmodule.addSubmoduleTo(srcBase);

        // Spring Boot application
        SpringBootAppSubmodule springBootAppSubmodule =
                new DefaultSpringBootAppSubmodule(serviceName, packageName);
        springBootAppSubmodule.addSubmoduleTo(srcBase);

        // Spring configuration
        SpringConfigSubmodule springConfigSubmodule = new SpringConfigSubmodule(serviceName, configGenerator);
        springConfigSubmodule.addMappings(serviceSubmodule.getConfiguration());
        springConfigSubmodule.addMappings(netSubmodule.getConfiguration());
        springConfigSubmodule.addSubmoduleTo(srcBase);

        // application properties
        properties(root, new AppPropertiesTemplate());

        // build
        BuildSubmodule buildSubmodule = new ConfigurableGradleSubmodule(
                new BuildGradleTemplate(), new SettingsGradleTemplate(serviceName));
        buildSubmodule.addSubmoduleTo(root);

        // gitignore file
        gitignore(root, new GitignoreTemplate());
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
