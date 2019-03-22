package dvoraka.archbuilder.module.microservice;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.build.BuildSubmodule;
import dvoraka.archbuilder.submodule.build.DefaultGradleSubmodule;
import dvoraka.archbuilder.submodule.service.DefaultServiceSubmodule;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;
import dvoraka.archbuilder.submodule.spring.DefaultSpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringConfigSubmodule;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;
import org.springframework.web.bind.annotation.RestController;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;

public class DefaultRestMicroservice implements Module, TemplateHelper {

    private final Directory root;


    public DefaultRestMicroservice(
            String rootDirName,
            String packageName,
            String serviceName,
            SpringConfigGenerator configGenerator
    ) {
        root = root(rootDirName);
        Directory srcBase = srcRootAndBase(root, pkg2path(packageName));

        // service
        ServiceSubmodule serviceSubmodule = new DefaultServiceSubmodule(serviceName, configGenerator);
        serviceSubmodule.addSubmoduleTo(srcBase);

        // controller
        String controllerName = buildServiceControllerName(serviceName);
        Directory controller = new Directory.Builder("controller", DirType.NEW_TYPE)
                .parent(srcBase)
                .filename(controllerName)
                .metadata(RestController.class)
                .build();

        // Spring Boot application
        SpringBootAppSubmodule springBootAppSubmodule =
                new DefaultSpringBootAppSubmodule(serviceName, packageName);
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
