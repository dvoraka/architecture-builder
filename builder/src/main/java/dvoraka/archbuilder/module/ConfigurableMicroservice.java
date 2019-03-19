package dvoraka.archbuilder.module;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.NetSubmodule;
import dvoraka.archbuilder.submodule.Submodule;
import dvoraka.archbuilder.template.NetTemplateConfig;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.source.SourceTemplate;
import dvoraka.archbuilder.template.source.SpringBootApp2Template;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.BuildGradleTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;
import dvoraka.archbuilder.template.text.SettingsGradleTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;
import static dvoraka.archbuilder.util.Utils.noFilenameException;
import static dvoraka.archbuilder.util.Utils.uncapitalize;

public class ConfigurableMicroservice implements Module, TemplateHelper {

    public static final String MESSAGE_DIR = "data/message";

    private final Directory root;


    public ConfigurableMicroservice(
            String rootDirName,
            String packageName,
            Class<?> superService,
            List<Class<?>> typeArguments,
            String serviceName,
            NetTemplateConfig netConfig,
            SpringConfigGenerator configGenerator
    ) {
        root = root(rootDirName);
        Directory srcRoot = srcRoot(root);
        Directory srcBase = srcBase(srcRoot, pkg2path(packageName));

        // service
        String serviceFullName = serviceName + "Service";
        Directory service = new Directory.Builder("service", DirType.SERVICE)
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

        // network
        Submodule networkModule = new NetSubmodule(serviceName, service, netConfig, configGenerator);
        networkModule.addSubmodule(srcBase);

        // Spring Boot application
        String appClassName = serviceName + "App";
        SourceTemplate appSourceTemplate = new SpringBootApp2Template(appClassName, packageName);
        springBootApp(srcBase, appSourceTemplate);

        // Spring configuration
        List<BeanMapping> beanMappings = new ArrayList<>();
        // mappings
        //TODO: getFilename should return .java suffix
        String serviceMappingName = uncapitalize(service.getFilename()
                .orElseThrow(() -> noFilenameException(service)));
        BeanMapping serviceBeanMapping = new BeanMapping.Builder(serviceMappingName)
                .typeDir(service)
                .toTypeDir(serviceImpl)
                .codeTemplate(configGenerator::simpleReturn)
                .build();

        beanMappings.add(serviceBeanMapping);
        beanMappings.addAll(networkModule.getConfiguration());

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
        settingsGradle(root, new SettingsGradleTemplate(serviceName));

        // gitignore file
        gitignore(root, new GitignoreTemplate());
    }

    @Override
    public Directory getRootDirectory() {
        return root;
    }
}
