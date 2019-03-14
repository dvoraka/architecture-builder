package dvoraka.archbuilder.template.arch;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.source.SourceTemplate;
import dvoraka.archbuilder.template.source.SpringBootApp2Template;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.BuildGradleTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;
import dvoraka.archbuilder.template.text.SettingsGradleTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dvoraka.archbuilder.generate.JavaUtils.pkg2path;
import static dvoraka.archbuilder.generate.Utils.noFilenameException;
import static dvoraka.archbuilder.generate.Utils.uncapitalize;

public class MicroserviceTemplate implements ArchitectureTemplate, TemplateHelper {

    public static final String MESSAGE_DIR = "data/message";

    private Directory root;


    public MicroserviceTemplate(
            String rootDirName,
            String packageName,
            Class<?> superService,
            List<Class<?>> typeArguments,
            String serviceName,
            Class<?> baseException,
            Class<?> superServer,
            NetTemplateConfig netConfig,
            SpringConfigGenerator configGenerator
    ) {
        root = root(rootDirName);
        Directory srcRoot = srcRoot(root);

        Directory srcBase = srcBase(srcRoot, pkg2path(packageName));

        // service
        String serviceFullName = serviceName + "Service";
        Directory.Builder serviceBuilder = new Directory.Builder("service", DirType.SERVICE)
                .parent(srcBase)
                .superTypeClass(superService)
                .filename(serviceFullName);
        for (Class<?> typeArgument : typeArguments) {
            serviceBuilder.parameterTypeClass(typeArgument);
        }
        Directory service = serviceBuilder
                .build();
        String serviceImplFullName = "Default" + serviceFullName;
        Directory serviceImpl = new Directory.Builder("service", DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(service)
                .filename(serviceImplFullName)
                .build();

        // exception
        String exceptionName = serviceName + "Exception";
        Directory exception = new Directory.Builder("exception", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(baseException)
                .filename(exceptionName)
                .build();

        // data
        String dataName = serviceName + "Data";
        Directory data = new Directory.Builder("data", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(netConfig.getBaseResultData())
                .filename(dataName)
                .parameterTypeDir(exception)
                .build();

        // messages
        String responseMessageName = serviceName + "ResponseMessage";
        Directory responseMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(netConfig.getResponseBaseMessage())
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(responseMessageName)
                .build();
        String requestMessageName = serviceName + "Message";
        Directory requestMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(netConfig.getRequestBaseMessage())
                .parameterTypeDir(service)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(requestMessageName)
                .build();

        // server
        String serverName = serviceName + "Server";
        Directory server = new Directory.Builder("server", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(superServer)
                .filename(serverName)
                .metadataClass(Service.class)
                .build();

        // network components
        String networkComponentName = serviceName + "NetComponent";
        Directory serviceNetworkComponent = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(netConfig.getSuperNetComponent())
                .interfaceType()
                .parameterTypeDir(requestMessage)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(networkComponentName)
                .build();
        String netAdapterName = serviceName + "NetAdapter";
        Directory serviceNetAdapter = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(serviceNetworkComponent)
                .superTypeClass(netConfig.getBaseNetComponent())
                .parameterTypeDir(requestMessage)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .metadataClass(Service.class)
                .filename(netAdapterName)
                .build();
        String networkReceiverName = serviceName + "NetReceiver";
        Directory networkReceiver = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(netConfig.getSuperNetReceiver())
                .interfaceType()
                .parameterTypeDir(requestMessage)
                .filename(networkReceiverName)
                .build();
        String networkResponseReceiverName = serviceName + "NetResponseReceiver";
        Directory networkResponseReceiver = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superTypeClass(netConfig.getSuperNetReceiver())
                .interfaceType()
                .parameterTypeDir(responseMessage)
                .filename(networkResponseReceiverName)
                .build();

        // Spring Boot application
        String appClassName = serviceName + "App";
        SourceTemplate appSourceTemplate = new SpringBootApp2Template(appClassName, packageName);
        springBootApp(srcBase, appSourceTemplate);

        // Spring configuration
        List<BeanMapping> beanMappings = new ArrayList<>();
        // mappings
        String serviceMappingName = uncapitalize(service.getFilename()
                .orElseThrow(() -> noFilenameException(service)));
        BeanMapping serviceBeanMapping = new BeanMapping.Builder(serviceMappingName)
                .typeDir(service)
                .toTypeDir(serviceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();
        BeanMapping serverBeanMapping = new BeanMapping.Builder(uncapitalize(serverName))
                .typeDir(server)
                .toTypeDir(server)
                .codeTemplate(configGenerator::simpleReturn)
                .build();
        BeanMapping adapterBeanMapping = new BeanMapping.Builder(uncapitalize(networkComponentName))
                .typeDir(serviceNetworkComponent)
                .toTypeDir(serviceNetAdapter)
                .codeTemplate(configGenerator::simpleReturn)
                .build();

        beanMappings.add(serviceBeanMapping);
        beanMappings.add(serverBeanMapping);
        beanMappings.add(adapterBeanMapping);

        String springConfigName = serviceName + "Config";
        Directory springConfig = new Directory.Builder("configuration", DirType.SPRING_CONFIG)
                .parent(srcBase)
                .filename(springConfigName)
                .build();
        Supplier<String> callback = () ->
                configGenerator.genConfiguration(beanMappings, springConfig);
        springConfig.setTextSupplier(callback);

        // application properties
        properties(root, new AppPropertiesTemplate());

        // build configuration
        buildGradle(root, new BuildGradleTemplate());
        settingsGradle(root, new SettingsGradleTemplate("Budget"));

        // gitignore file
        gitignore(root, new GitignoreTemplate());
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
