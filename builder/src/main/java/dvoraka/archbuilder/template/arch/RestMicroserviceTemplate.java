package dvoraka.archbuilder.template.arch;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.source.SourceTemplate;
import dvoraka.archbuilder.template.source.SpringBootApp2Template;
import dvoraka.archbuilder.template.text.AppPropertiesTemplate;
import dvoraka.archbuilder.template.text.BuildGradleTemplate;
import dvoraka.archbuilder.template.text.GitignoreTemplate;
import dvoraka.archbuilder.template.text.SettingsGradleTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static dvoraka.archbuilder.util.JavaUtils.pkg2path;
import static dvoraka.archbuilder.util.Utils.noFilenameException;
import static dvoraka.archbuilder.util.Utils.uncapitalize;

public class RestMicroserviceTemplate implements ArchitectureTemplate, TemplateHelper {

    private Directory root;


    public RestMicroserviceTemplate(
            String rootDirName,
            String packageName,
            Class<?> superService,
            List<Class<?>> typeArguments,
            String serviceName,
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

        // controller
        String controlerName = serviceName + "Controller";
        Directory controller = new Directory.Builder("controller", DirType.NEW_TYPE)
                .parent(srcBase)
                .filename(controlerName)
                .metadataClass(RestController.class)
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

        beanMappings.add(serviceBeanMapping);

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
