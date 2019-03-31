package dvoraka.archbuilder.submodule.spring;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.source.SourceTemplate;
import dvoraka.archbuilder.template.source.SpringBootApp2Template;

import java.util.Collection;
import java.util.Collections;

public class DefaultSpringBootAppSubmodule implements SpringBootAppSubmodule, TemplateHelper {

    private final BuilderHelper helper;


    public DefaultSpringBootAppSubmodule(BuilderHelper helper) {
        this.helper = helper;
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {
        SourceTemplate appSourceTemplate =
                new SpringBootApp2Template(helper.serviceAppName(), helper.getPackageName());
        springBootApp(srcBase, appSourceTemplate);
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        return Collections.emptyList();
    }
}
