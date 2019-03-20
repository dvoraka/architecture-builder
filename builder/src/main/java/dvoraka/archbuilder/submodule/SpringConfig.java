package dvoraka.archbuilder.submodule;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.springconfig.SpringConfigGenerator;

import java.util.List;
import java.util.function.Supplier;

public class SpringConfig implements Submodule {

    private final String baseName;
    private final List<BeanMapping> beanMappings;
    private final SpringConfigGenerator configGenerator;


    public SpringConfig(String baseName, List<BeanMapping> beanMappings, SpringConfigGenerator configGenerator) {
        this.baseName = baseName;
        this.beanMappings = beanMappings;
        this.configGenerator = configGenerator;
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {

        String springConfigName = baseName + "Config";

        Directory springConfig = new Directory.Builder("configuration", DirType.SPRING_CONFIG)
                .parent(srcBase)
                .filename(springConfigName)
                .build();

        Supplier<String> callback = () -> configGenerator.genConfiguration(beanMappings, springConfig);

        springConfig.setTextSupplier(callback);
    }
}
