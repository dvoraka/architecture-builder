package dvoraka.archbuilder.submodule.service;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.util.JavaUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static dvoraka.archbuilder.util.JavaUtils.javaSuffix;
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
        String serviceImplFullName = "Default" + serviceFullName;
        String dataServiceFullName = serviceName + "DataService";
        String dataServiceImplFullName = "Default" + serviceName + "DataService";

        // service
        service = new Directory.Builder("service", DirType.SERVICE)
                .parent(srcBase)
                .superType(superService)
                .filename(serviceFullName)
                .parameterType(typeArguments)
                .build();
        Directory serviceImpl = new Directory.Builder("service", DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(service)
                .filename(serviceImplFullName)
                .build();

        // data service
        Directory dataService = new Directory.Builder("service", DirType.NEW_TYPE)
                .parent(srcBase)
                .filename(javaSuffix(dataServiceFullName))
                .interfaceType()
                .build();
        Directory dataServiceImpl = new Directory.Builder("service", DirType.IMPL)
                .parent(srcBase)
                .superType(dataService)
                .filename(javaSuffix(dataServiceImplFullName))
                .metadata(Service.class)
                .build();

        // Spring config
        String serviceFilename = service.getFilename()
                .orElseThrow(() -> noFilenameException(service));
        String dataServiceFilename = dataService.getFilename()
                .orElseThrow(() -> noFilenameException(service));

        String serviceMappingName = uncapitalize(JavaUtils.removeJavaSuffix(serviceFilename));
        String dataServiceMappingName = uncapitalize(JavaUtils.removeJavaSuffix(dataServiceFilename));

        BeanMapping serviceBeanMapping = new BeanMapping.Builder(serviceMappingName)
                .typeDir(service)
                .toTypeDir(serviceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();
        BeanMapping dataServiceBeanMapping = new BeanMapping.Builder(dataServiceMappingName)
                .typeDir(dataService)
                .toTypeDir(dataServiceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();

        configuration.add(serviceBeanMapping);
        configuration.add(dataServiceBeanMapping);
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
