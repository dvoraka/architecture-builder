package dvoraka.archbuilder.module.microservice;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.module.Module;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.build.BuildSubmodule;
import dvoraka.archbuilder.submodule.build.DefaultGradleSubmodule;
import dvoraka.archbuilder.submodule.rest.DefaultRestSubmodule;
import dvoraka.archbuilder.submodule.rest.RestSubmodule;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;
import dvoraka.archbuilder.submodule.service.rest.RestClientServiceSubmodule;
import dvoraka.archbuilder.submodule.service.rest.RestServerServiceSubmodule;
import dvoraka.archbuilder.submodule.spring.ServiceSpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringBootAppSubmodule;
import dvoraka.archbuilder.submodule.spring.SpringConfigSubmodule;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;

public class V2RestMicroservice implements Module, TemplateHelper {

    private final Directory root;


    public V2RestMicroservice(BuilderHelper helper, SpringConfigGenerator configGenerator) {

        root = root(helper.getRootDirName());
        Directory srcBase = srcRootAndBase(root, pkg2path(helper.getPackageName()));

        // client service
        ServiceSubmodule clientServiceSubmodule = new RestClientServiceSubmodule(helper, configGenerator);
        clientServiceSubmodule.addSubmoduleTo(srcBase);
        // server service
        ServiceSubmodule serverServiceSubmodule = new RestServerServiceSubmodule(helper, configGenerator);
        serverServiceSubmodule.addSubmoduleTo(srcBase);

        // controller
        RestSubmodule restSubmodule = new DefaultRestSubmodule(helper.getBaseName());
        restSubmodule.addSubmoduleTo(srcBase);

        // Spring Boot application
        SpringBootAppSubmodule springBootAppSubmodule = new ServiceSpringBootAppSubmodule(helper);
        springBootAppSubmodule.addSubmoduleTo(srcBase);

        // Spring configuration
        SpringConfigSubmodule springConfigSubmodule = new SpringConfigSubmodule(helper.getBaseName(), configGenerator);
        springConfigSubmodule.addMappings(clientServiceSubmodule.getConfiguration());
        springConfigSubmodule.addMappings(serverServiceSubmodule.getConfiguration());
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
