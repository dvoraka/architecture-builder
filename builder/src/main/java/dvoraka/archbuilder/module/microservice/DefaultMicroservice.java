package dvoraka.archbuilder.module.microservice;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.build.BuildSubmodule;
import dvoraka.archbuilder.submodule.build.DefaultGradleSubmodule;
import dvoraka.archbuilder.submodule.net.DefaultNetSubmodule;
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


    public DefaultMicroservice(BuilderHelper helper, SpringConfigGenerator configGenerator) {

        root = root(helper.getRootDirName());
        Directory srcBase = srcRootAndBase(root, pkg2path(helper.getPackageName()));

        // service
        ServiceSubmodule serviceSubmodule = new DefaultServiceSubmodule(helper, configGenerator);
        serviceSubmodule.addSubmoduleTo(srcBase);

        // network
        NetSubmodule netSubmodule = new DefaultNetSubmodule(
                helper.getBaseName(), serviceSubmodule.getService(), configGenerator);
        netSubmodule.addSubmoduleTo(srcBase);

        // Spring Boot application
        SpringBootAppSubmodule springBootAppSubmodule =
                new DefaultSpringBootAppSubmodule(helper.getBaseName(), helper.getPackageName());
        springBootAppSubmodule.addSubmoduleTo(srcBase);

        // Spring configuration
        SpringConfigSubmodule springConfigSubmodule =
                new SpringConfigSubmodule(helper.getBaseName(), configGenerator);
        springConfigSubmodule.addMappings(serviceSubmodule.getConfiguration());
        springConfigSubmodule.addMappings(netSubmodule.getConfiguration());
        springConfigSubmodule.addSubmoduleTo(srcBase);

        // application properties
        properties(root, new AppPropertiesTemplate());

        // build
        BuildSubmodule buildSubmodule = new DefaultGradleSubmodule(helper.getBaseName());
        buildSubmodule.addSubmoduleTo(root);

        // gitignore file
        gitignore(root, new GitignoreTemplate());
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
