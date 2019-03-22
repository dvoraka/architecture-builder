package dvoraka.archbuilder.submodule.rest;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.template.TemplateHelper;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

public class DefaultRestSubmodule implements RestSubmodule, TemplateHelper {

    private final String baseName;


    public DefaultRestSubmodule(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {

        // controller
        String controllerName = buildServiceControllerName(baseName);
        Directory controller = new Directory.Builder("controller", DirType.NEW_TYPE)
                .parent(srcBase)
                .filename(controllerName)
                .metadata(RestController.class)
                .build();
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        //TODO
        return null;
    }
}
