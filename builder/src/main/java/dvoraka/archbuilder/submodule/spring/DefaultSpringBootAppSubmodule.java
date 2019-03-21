package dvoraka.archbuilder.submodule.spring;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.source.SourceTemplate;
import dvoraka.archbuilder.template.source.SpringBootApp2Template;

import java.util.Collection;
import java.util.Collections;

public class DefaultSpringBootAppSubmodule implements SpringBootAppSubmodule, TemplateHelper {

    private final String baseName;
    private final String packageName;


    public DefaultSpringBootAppSubmodule(String baseName, String packageName) {
        this.baseName = baseName;
        this.packageName = packageName;
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {
        String appClassName = buildServiceAppName(baseName);
        SourceTemplate appSourceTemplate = new SpringBootApp2Template(appClassName, packageName);
        springBootApp(srcBase, appSourceTemplate);
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        return Collections.emptyList();
    }
}
