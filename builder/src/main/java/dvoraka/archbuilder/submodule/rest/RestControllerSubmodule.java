package dvoraka.archbuilder.submodule.rest;

import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.template.TemplateHelper;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

public class RestControllerSubmodule implements RestSubmodule, TemplateHelper {

    public static final String CONTROLLER_DIR = "controller";

    private final String baseName;


    public RestControllerSubmodule(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {

        // controller
        String controllerName = buildServiceControllerName(baseName);
        Directory controller = new Directory.Builder(CONTROLLER_DIR, DirType.NEW_TYPE)
                .parent(srcBase)
                .filename(controllerName)
                .metadata(RestController.class)
                .build();
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        //TODO
        return Collections.emptyList();
    }
}
