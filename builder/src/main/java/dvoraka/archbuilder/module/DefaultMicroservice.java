package dvoraka.archbuilder.module;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.build.BuildSubmodule;
import dvoraka.archbuilder.submodule.build.DefaultGradleSubmodule;
import dvoraka.archbuilder.submodule.net.ConfigurableNetSubmodule;
import dvoraka.archbuilder.submodule.net.NetConfig;
import dvoraka.archbuilder.submodule.net.NetSubmodule;
import dvoraka.archbuilder.submodule.service.DefaultServiceSubmodule;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;
import dvoraka.archbuilder.submodule.spring.DefaultSpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringConfigSubmodule;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;

public class DefaultMicroservice implements Module, TemplateHelper {

    private final Directory root;


    public DefaultMicroservice(
            String rootDirName,
            String packageName,
            String serviceName,
            NetConfig netConfig,
            SpringConfigGenerator configGenerator
    ) {
        root = root(rootDirName);
        Directory srcRoot = srcRoot(root);
        Directory srcBase = srcBase(srcRoot, pkg2path(packageName));

        // service
        ServiceSubmodule serviceSubmodule = new DefaultServiceSubmodule(serviceName, configGenerator);
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
        BuildSubmodule buildSubmodule = new DefaultGradleSubmodule(serviceName);
        buildSubmodule.addSubmoduleTo(root);

        // gitignore file
        gitignore(root, new GitignoreTemplate());
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
