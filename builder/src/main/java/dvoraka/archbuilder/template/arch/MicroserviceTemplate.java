package dvoraka.archbuilder.template.arch;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.TextBuilder;
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
import dvoraka.archbuilder.template.text.DefaultGitignoreTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MicroserviceTemplate implements ArchitectureTemplate {

    public static final String JAVA_SRC_DIR = "src/main/java";
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
        root = new Directory.Builder(rootDirName, DirType.ROOT)
                .parent(null)
                .build();

        Directory srcRoot = new Directory.Builder(JAVA_SRC_DIR, DirType.SRC_ROOT)
                .parent(root)
                .build();

        String pkgPath = JavaUtils.pkg2path(packageName);
        Directory srcBase = new Directory.Builder(pkgPath, DirType.SRC_BASE)
                .parent(srcRoot)
                .build();

        String absPkgPath = "";
        Directory srcBaseAbs = new Directory.Builder(absPkgPath, DirType.SRC_BASE_ABSTRACT)
                .parent(root)
                .build();

        // service
        Directory abstractService = new Directory.Builder("service", DirType.SERVICE_ABSTRACT)
                .parent(srcBaseAbs)
                .typeClass(superService)
                .build();
        String serviceFullName = serviceName + "Service";
        Directory.Builder serviceBuilder = new Directory.Builder("service", DirType.SERVICE)
                .parent(srcBase)
                .superType(abstractService)
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
        Directory exceptionAbs = new Directory.Builder("", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(baseException)
                .build();
        String exceptionName = serviceName + "Exception";
        Directory exception = new Directory.Builder("exception", DirType.IMPL)
                .parent(srcBase)
                .superType(exceptionAbs)
                .filename(exceptionName)
                .build();

        // data
        Directory dataAbs = new Directory.Builder("", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getBaseResultData())
                .build();
        String dataName = serviceName + "Data";
        Directory data = new Directory.Builder("data", DirType.IMPL)
                .parent(srcBase)
                .superType(dataAbs)
                .filename(dataName)
                .parameterTypeDir(exception)
                .build();

        // messages
        Directory responseMessageAbs = new Directory.Builder("", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getResponseBaseMessage())
                .build();
        String responseMessageName = serviceName + "ResponseMessage";
        Directory responseMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superType(responseMessageAbs)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(responseMessageName)
                .build();
        Directory requestMessageAbs = new Directory.Builder("", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getRequestBaseMessage())
                .build();
        String requestMessageName = serviceName + "Message";
        Directory requestMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superType(requestMessageAbs)
                .parameterTypeDir(service)
                .parameterTypeDir(responseMessage)
                .parameterTypeDir(data)
                .parameterTypeDir(exception)
                .filename(requestMessageName)
                .build();

        // server
        Directory serverAbs = new Directory.Builder("", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(superServer)
                .build();
        String serverName = serviceName + "Server";
        Directory server = new Directory.Builder("server", DirType.IMPL)
                .parent(srcBase)
                .superType(serverAbs)
                .filename(serverName)
                .metadataClass(Service.class)
                .build();

        // network components
        Directory networkComponentAbs = new Directory.Builder("", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getSuperNetComponent())
                .build();
        Directory networkReceiverAbs = new Directory.Builder("", DirType.ABSTRACT)
                .parent(srcBase)
                .typeClass(netConfig.getSuperNetReceiver())
                .build();
        String networkComponentName = serviceName + "NetComponent";
        Directory serviceNetworkComponent = new Directory.Builder("net", DirType.IMPL)
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
        Directory serviceNetAdapter = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(serviceNetworkComponent)
                .superType(baseNetComponentAbs)
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
                .superType(networkReceiverAbs)
                .interfaceType()
                .parameterTypeDir(requestMessage)
                .filename(networkReceiverName)
                .build();
        String networkResponseReceiverName = serviceName + "NetResponseReceiver";
        Directory networkResponseReceiver = new Directory.Builder("net", DirType.IMPL)
                .parent(srcBase)
                .superType(networkReceiverAbs)
                .interfaceType()
                .parameterTypeDir(responseMessage)
                .filename(networkResponseReceiverName)
                .build();

        // application properties
        String propertiesText = TextBuilder.create()
                .addLine("prop1=value")
                .addLine("prop2=value2")
                .getText();
        Directory srcProps = new Directory.Builder("src/main/resources", DirType.SRC_PROPERTIES)
                .parent(root)
                .filename("application.properties")
                .text(propertiesText)
                .build();

        // application
        String appClassName = serviceName + "App";
        SourceTemplate sourceTemplate = new SpringBootApplicationTemplate(appClassName, packageName);
        Directory application = new Directory.Builder("", DirType.CUSTOM_TYPE)
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
        Directory springConfig = new Directory.Builder("configuration", DirType.SPRING_CONFIG)
                .parent(srcBase)
                .filename(springConfigName)
                .build();
        Supplier<String> callback = () ->
                configGenerator.genConfiguration(beanMappings, springConfig);
        springConfig.setTextSupplier(callback);

        // build configuration
        ConfigurationTemplate buildGradleTemplate = new BuildGradleTemplate();
        Directory buildGradle = new Directory.Builder("", DirType.BUILD_CONFIG)
                .parent(root)
                .filename(buildGradleTemplate.getFilename())
                .text(buildGradleTemplate.getConfig())
                .build();
        ConfigurationTemplate settingsGradleTemplate = new SettingsGradleTemplate("Project1");
        Directory settingsGradle = new Directory.Builder("", DirType.BUILD_CONFIG)
                .parent(root)
                .filename(settingsGradleTemplate.getFilename())
                .text(settingsGradleTemplate.getConfig())
                .build();

        // gitignore file
        String gitignoreText = new DefaultGitignoreTemplate().getText();
        Directory gitignore = new Directory.Builder("", DirType.TEXT)
                .parent(root)
                .filename(".gitignore")
                .text(gitignoreText)
                .build();
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
