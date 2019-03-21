package dvoraka.archbuilder.submodule.spring;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;
import dvoraka.archbuilder.submodule.Submodule;
import dvoraka.archbuilder.template.TemplateHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class SpringConfigSubmodule implements Submodule, TemplateHelper {

    private final String baseName;
    private final List<BeanMapping> beanMappings;
    private final SpringConfigGenerator configGenerator;


    public SpringConfigSubmodule(String baseName, SpringConfigGenerator configGenerator) {
        this.baseName = baseName;
        this.configGenerator = configGenerator;

        beanMappings = new ArrayList<>();
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {

        String springConfigName = buildConfigurationName(baseName);

        Directory springConfig = new Directory.Builder("configuration", DirType.SPRING_CONFIG)
                .parent(srcBase)
                .filename(springConfigName)
                .build();

        Supplier<String> callback = () -> configGenerator.genConfiguration(beanMappings, springConfig);

        springConfig.setTextSupplier(callback);
    }

    public void addMappings(Collection<BeanMapping> beanMappings) {
        this.beanMappings.addAll(beanMappings);
    }
}
