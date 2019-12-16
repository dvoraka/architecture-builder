package dvoraka.archbuilder.submodule.service.rest;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.sample.microservice.service.AbstractBaseService;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.service.ServiceSubmodule;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.util.JavaUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dvoraka.archbuilder.util.Utils.noFilenameException;
import static dvoraka.archbuilder.util.Utils.uncapitalize;

public class ConfigurableRestClientServiceSubmodule implements ServiceSubmodule, TemplateHelper {

    private final Class<?> superService;
    private final Collection<Class<?>> typeArguments;

    private final BuilderHelper helper;
    private final SpringConfigGenerator configGenerator;

    private final List<BeanMapping> configuration;

    private Directory service;


    public ConfigurableRestClientServiceSubmodule(
            Class<?> superService,
            Collection<Class<?>> typeArguments,
            BuilderHelper helper,
            SpringConfigGenerator configGenerator
    ) {
        this.superService = superService;
        this.typeArguments = typeArguments;
        this.helper = helper;
        this.configGenerator = configGenerator;

        configuration = new ArrayList<>();
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {

        String serviceName = helper.restClientServiceName();
        String serviceImplName = helper.restClientServiceImplName();

        // client service
        service = new Directory.Builder(helper.servicePkgName(), DirType.SERVICE)
                .parent(srcBase)
                .superType(superService)
                .filename(serviceName)
                .parameterType(typeArguments)
                .build();
        Directory serviceImpl = new Directory.Builder(helper.servicePkgName(), DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(service)
                .superType(AbstractBaseService.class)
                .filename(serviceImplName)
                .build();

        // Spring config
        String serviceFilename = service.getFilename()
                .orElseThrow(() -> noFilenameException(service));

        String serviceMappingName = uncapitalize(JavaUtils.removeJavaSuffix(serviceFilename));

        BeanMapping serviceBeanMapping = new BeanMapping.Builder(serviceMappingName)
                .typeDir(service)
                .toTypeDir(serviceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();

        configuration.add(serviceBeanMapping);
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        return configuration;
    }

    @Override
    public Directory getService() {
        if (service == null) {
            throw new GeneratorException("Service is not generated yet.");
        }
        return service;
    }
}
