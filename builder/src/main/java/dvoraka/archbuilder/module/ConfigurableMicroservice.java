package dvoraka.archbuilder.module;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.net.ConfigurableNetSubmodule;
import dvoraka.archbuilder.submodule.net.NetConfig;
import dvoraka.archbuilder.submodule.service.ConfigurableServiceSubmodule;
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

public class ConfigurableMicroservice implements Module, TemplateHelper {

    public static final String MESSAGE_DIR = "data/message";

    private final Directory root;


    public ConfigurableMicroservice(
            String rootDirName,
            String packageName,
            Class<?> superService,
            List<Class<?>> typeArguments,
            String serviceName,
            NetConfig netConfig,
            SpringConfigGenerator configGenerator
    ) {
        root = root(rootDirName);
        Directory srcRoot = srcRoot(root);
        Directory srcBase = srcBase(srcRoot, pkg2path(packageName));

        // service
        ConfigurableServiceSubmodule serviceSubmodule = new ConfigurableServiceSubmodule(
                serviceName, superService, typeArguments, configGenerator);
        serviceSubmodule.addSubmoduleTo(srcBase);

        // network
        ConfigurableNetSubmodule netSubmodule = new ConfigurableNetSubmodule(
                serviceName, serviceSubmodule.getService(), netConfig, configGenerator);
        netSubmodule.addSubmoduleTo(srcBase);

        // Spring Boot application
        String appClassName = serviceName + "App";
        SourceTemplate appSourceTemplate = new SpringBootApp2Template(appClassName, packageName);
        springBootApp(srcBase, appSourceTemplate);

        // Spring configuration
        List<BeanMapping> beanMappings = new ArrayList<>();
        beanMappings.addAll(serviceSubmodule.getConfiguration());
        beanMappings.addAll(netSubmodule.getConfiguration());

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
