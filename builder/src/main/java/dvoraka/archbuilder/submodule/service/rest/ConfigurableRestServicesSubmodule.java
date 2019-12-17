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

public class ConfigurableRestServicesSubmodule implements ServiceSubmodule, TemplateHelper {

    private final Class<?> superService;
    private final Collection<Class<?>> typeArguments;

    private final BuilderHelper helper;
    private final SpringConfigGenerator configGenerator;

    private final List<BeanMapping> configuration;

    private Directory clientService;
    private Directory serverService;


    public ConfigurableRestServicesSubmodule(
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

        String clientServiceName = helper.restClientServiceName();
        String clientServiceImplName = helper.restClientServiceImplName();
        String serverServiceName = helper.restServerServiceName();
        String serverServiceImplName = helper.restServerServiceImplName();

        // client service
        clientService = new Directory.Builder(helper.servicePkgName(), DirType.SERVICE)
                .parent(srcBase)
                .superType(superService)
                .filename(clientServiceName)
                .parameterType(typeArguments)
                .build();
        Directory clientServiceImpl = new Directory.Builder(helper.servicePkgName(), DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(clientService)
                .superType(AbstractBaseService.class)
                .filename(clientServiceImplName)
                .build();

        // server service
        serverService = new Directory.Builder(helper.servicePkgName(), DirType.SERVICE)
                .parent(srcBase)
                .superType(superService)
                .filename(serverServiceName)
                .parameterType(typeArguments)
                .build();
        Directory serverServiceImpl = new Directory.Builder(helper.servicePkgName(), DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(serverService)
                .superType(AbstractBaseService.class)
                .filename(serverServiceImplName)
                .build();

        // Spring config
        String clientServiceFilename = clientService.getFilename()
                .orElseThrow(() -> noFilenameException(clientService));
        String serverServiceFilename = serverService.getFilename()
                .orElseThrow(() -> noFilenameException(serverService));

        String clientServiceMappingName = uncapitalize(JavaUtils.removeJavaSuffix(clientServiceFilename));
        String serverServiceMappingName = uncapitalize(JavaUtils.removeJavaSuffix(serverServiceFilename));

        BeanMapping clientServiceBeanMapping = new BeanMapping.Builder(clientServiceMappingName)
                .typeDir(clientService)
                .toTypeDir(clientServiceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();
        BeanMapping serverServiceBeanMapping = new BeanMapping.Builder(serverServiceMappingName)
                .typeDir(serverService)
                .toTypeDir(serverServiceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();

        configuration.add(clientServiceBeanMapping);
        configuration.add(serverServiceBeanMapping);
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        return configuration;
    }

    @Override
    public Directory getService() {
        if (clientService == null) {
            throw new GeneratorException("Service is not generated yet.");
        }
        return clientService;
    }
}
