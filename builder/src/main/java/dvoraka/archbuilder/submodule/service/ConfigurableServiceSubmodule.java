package dvoraka.archbuilder.submodule.service;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.TemplateHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dvoraka.archbuilder.util.Utils.noFilenameException;
import static dvoraka.archbuilder.util.Utils.uncapitalize;

public class ConfigurableServiceSubmodule implements ServiceSubmodule, TemplateHelper {

    private final String serviceName;
    private final Class<?> superService;
    private final Collection<Class<?>> typeArguments;

    private final SpringConfigGenerator configGenerator;

    private final List<BeanMapping> configuration;

    private Directory service;


    public ConfigurableServiceSubmodule(
            String serviceName,
            Class<?> superService,
            Collection<Class<?>> typeArguments,
            SpringConfigGenerator configGenerator
    ) {
        this.serviceName = serviceName;
        this.superService = superService;
        this.typeArguments = typeArguments;
        this.configGenerator = configGenerator;

        configuration = new ArrayList<>();
    }


    @Override
    public void addSubmoduleTo(Directory srcBase) {

        String serviceFullName = serviceName + "Service";
        service = new Directory.Builder("service", DirType.SERVICE)
                .parent(srcBase)
                .superType(superService)
                .filename(serviceFullName)
                .parameterType(typeArguments)
                .build();

        String serviceImplFullName = "Default" + serviceFullName;
        Directory serviceImpl = new Directory.Builder("service", DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(service)
                .filename(serviceImplFullName)
                .build();

        String serviceMappingName = uncapitalize(service.getFilename()
                .orElseThrow(() -> noFilenameException(service)));
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

    public Directory getService() {
        if (service == null) {
            throw new GeneratorException("Service is not generated yet.");
        }
        return service;
    }
}
