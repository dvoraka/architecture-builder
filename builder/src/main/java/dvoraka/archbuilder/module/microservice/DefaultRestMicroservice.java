package dvoraka.archbuilder.module.microservice;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.build.BuildSubmodule;
import dvoraka.archbuilder.submodule.build.DefaultGradleSubmodule;
import dvoraka.archbuilder.submodule.rest.DefaultRestSubmodule;
import dvoraka.archbuilder.submodule.rest.RestSubmodule;
import dvoraka.archbuilder.submodule.service.DefaultServiceSubmodule;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;
import dvoraka.archbuilder.submodule.spring.DefaultSpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringConfigSubmodule;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;

public class DefaultRestMicroservice implements Module, TemplateHelper {

    private final Directory root;


    public DefaultRestMicroservice(BuilderHelper helper, SpringConfigGenerator configGenerator) {

        root = root(helper.getRootDirName());
        Directory srcBase = srcRootAndBase(root, pkg2path(helper.getPackageName()));

        String serviceName = helper.getBaseName();

        // service
        ServiceSubmodule serviceSubmodule = new DefaultServiceSubmodule(helper, configGenerator);
        serviceSubmodule.addSubmoduleTo(srcBase);

        // controller
        RestSubmodule restSubmodule = new DefaultRestSubmodule(serviceName);
        restSubmodule.addSubmoduleTo(srcBase);

        // Spring Boot application
        SpringBootAppSubmodule springBootAppSubmodule =
                new DefaultSpringBootAppSubmodule(serviceName, helper.getPackageName());
        springBootAppSubmodule.addSubmoduleTo(srcBase);

        // Spring configuration
        SpringConfigSubmodule springConfigSubmodule = new SpringConfigSubmodule(serviceName, configGenerator);
        springConfigSubmodule.addMappings(serviceSubmodule.getConfiguration());
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
