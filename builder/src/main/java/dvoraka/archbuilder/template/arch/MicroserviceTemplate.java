package dvoraka.archbuilder.template.arch;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.generate.JavaUtils;
import dvoraka.archbuilder.generate.Utils;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.config.BuildGradleTemplate;
import dvoraka.archbuilder.template.config.ConfigurationTemplate;
import dvoraka.archbuilder.template.config.SettingsGradleTemplate;
import dvoraka.archbuilder.template.source.SourceTemplate;
import dvoraka.archbuilder.template.source.SpringBootApplicationTemplate;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MicroserviceTemplate implements ArchitectureTemplate {

    public static final String JAVA_SRC_DIR = "src/main/java";
    public static final String MESSAGE_DIR = "data/message";

    private Directory root;
    private Directory srcRoot;
    private Directory srcBase;
    private Directory srcBaseAbs;


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
        root = new Directory.DirectoryBuilder(rootDirName)
                .type(DirType.ROOT)
                .parent(null)
                .build();

        srcRoot = new Directory.DirectoryBuilder(JAVA_SRC_DIR)
                .type(DirType.SRC_ROOT)
                .parent(root)
                .build();

        String pkgPath = JavaUtils.pkg2path(packageName);
        srcBase = new Directory.DirectoryBuilder(pkgPath)
                .type(DirType.SRC_BASE)
                .parent(srcRoot)
                .build();

        String absPkgPath = "";
        srcBaseAbs = new Directory.DirectoryBuilder(absPkgPath)
                .type(DirType.SRC_BASE_ABSTRACT)
                .parent(root)
                .build();

        // service
        Directory abstractService = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE_ABSTRACT)
                .parent(srcBaseAbs)
                .typeClass(superService)
                .build();

        String serviceFullName = serviceName + "Service";
        Directory.DirectoryBuilder serviceBuilder = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE)
                .parent(srcBase)
                .superType(abstractService)
                .filename(serviceFullName);
        for (Class<?> typeArgument : typeArguments) {
            serviceBuilder.parameterTypeClass(typeArgument);
        }
        Directory service = serviceBuilder
                .build();

        String serviceImplFullName = "Default" + serviceFullName;
        Directory serviceImpl = new Directory.DirectoryBuilder("service")
                .type(DirType.SERVICE_IMPL)
                .parent(srcBase)
                .superType(service)
                .filename(serviceImplFullName)
                .build();

        // exception
        Directory exceptionAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(baseException)
                .build();

        String exceptionName = serviceName + "Exception";
        Directory exception = new Directory.DirectoryBuilder("exception")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(exceptionAbs)
                .filename(exceptionName)
                .build();

        // data
        Directory dataAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getBaseResultData())
                .build();

        String dataName = serviceName + "Data";
        Directory data = new Directory.DirectoryBuilder("data")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(dataAbs)
                .filename(dataName)
                .parameterTypeDir(exception)
                .build();

        // messages
        Directory responseMessageAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getResponseBaseMessage())
                .build();

        String responseMessageName = serviceName + "ResponseMessage";
        Directory responseMessage = new Directory.DirectoryBuilder(MESSAGE_DIR)
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(responseMessageAbs)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(responseMessageName)
                .build();

        Directory requestMessageAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getRequestBaseMessage())
                .build();

        String requestMessageName = serviceName + "Message";
        Directory requestMessage = new Directory.DirectoryBuilder(MESSAGE_DIR)
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(requestMessageAbs)
                .parameterTypeDir(service)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(requestMessageName)
                .build();

        // server
        Directory serverAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(superServer)
                .build();

        String serverName = serviceName + "Server";
        Directory server = new Directory.DirectoryBuilder("server")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(serverAbs)
                .filename(serverName)
                .build();

        // network components
        Directory networkComponentAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getSuperNetComponent())
                .build();

        Directory networkReceiverAbs = new Directory.DirectoryBuilder("")
                .type(DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getSuperNetReceiver())
                .build();

        String networkComponentName = serviceName + "NetComponent";
        Directory serviceNetworkComponent = new Directory.DirectoryBuilder("net")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(networkComponentAbs)
                .interfaceType()
                .parameterTypeDir(requestMessage)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(networkComponentName)
                .build();

        Directory baseNetComponentAbs = Utils.createAbstractDirFor(
                netConfig.getBaseNetComponent(), srcBase);

        String netAdapterName = serviceName + "NetAdapter";
        Directory serviceNetAdapter = new Directory.DirectoryBuilder("net")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(serviceNetworkComponent)
                .superType(baseNetComponentAbs)
                .parameterTypeDir(requestMessage)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(netAdapterName)
                .build();

        String networkReceiverName = serviceName + "NetReceiver";
        Directory networkReceiver = new Directory.DirectoryBuilder("net")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(networkReceiverAbs)
                .interfaceType()
                .parameterTypeDir(requestMessage)
                .filename(networkReceiverName)
                .build();

        String networkResponseReceiverName = serviceName + "NetResponseReceiver";
        Directory networkResponseReceiver = new Directory.DirectoryBuilder("net")
                .type(DirType.IMPL)
                .parent(srcBase)
                .superType(networkReceiverAbs)
                .interfaceType()
                .parameterTypeDir(responseMessage)
                .filename(networkResponseReceiverName)
                .build();

        // application properties
        Directory srcProps = new Directory.DirectoryBuilder("src/main/resources")
                .type(DirType.SRC_PROPERTIES)
                .parent(root)
                .filename("application.properties")
                .text("prop1=value\nprop2=value2\n")
                .build();

        // application
        String appClassName = serviceName + "App";
        SourceTemplate sourceTemplate = new SpringBootApplicationTemplate(appClassName, packageName);
        Directory application = new Directory.DirectoryBuilder("")
                .type(DirType.CUSTOM_TYPE)
                .parent(srcBase)
                .filename(appClassName)
                .text(sourceTemplate.getSource())
                .build();

        // Spring configuration
        List<BeanMapping> beanMappings = new ArrayList<>();
        // service mapping
        String serviceMappingName = StringUtils.uncapitalize(service.getFilename()
                .orElseThrow(() -> new GeneratorException("No filename.")));
        BeanMapping serviceBeanMapping = new BeanMapping.Builder(serviceMappingName)
                .typeDir(service)
                .toTypeDir(serviceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();

        beanMappings.add(serviceBeanMapping);

        String springConfigName = serviceName + "Config";
        Directory springConfig = new Directory.DirectoryBuilder("configuration")
                .type(DirType.SPRING_CONFIG)
                .parent(srcBase)
                .filename(springConfigName)
                .build();
        Supplier<String> callback = () ->
                configGenerator.genConfiguration(beanMappings, springConfig);
        springConfig.setTextSupplier(callback);

        // build configuration
        ConfigurationTemplate buildGradleTemplate = new BuildGradleTemplate();
        Directory buildGradle = new Directory.DirectoryBuilder("")
                .type(DirType.BUILD_CONFIG)
                .parent(root)
                .filename(buildGradleTemplate.getFilename())
                .text(buildGradleTemplate.getConfig())
                .build();

        ConfigurationTemplate settingsGradleTemplate = new SettingsGradleTemplate("Project1");
        Directory settingsGradle = new Directory.DirectoryBuilder("")
                .type(DirType.BUILD_CONFIG)
                .parent(root)
                .filename(settingsGradleTemplate.getFilename())
                .text(settingsGradleTemplate.getConfig())
                .build();
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
