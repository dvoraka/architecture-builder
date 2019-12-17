package dvoraka.archbuilder.submodule.net;

import dvoraka.archbuilder.BuilderHelper;
import dvoraka.archbuilder.data.DirType;
import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.exception.GeneratorException;
import dvoraka.archbuilder.springconfig.BeanMapping;
import dvoraka.archbuilder.template.TemplateHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RestNetSubmodule implements NetSubmodule, TemplateHelper {

    public static final String MESSAGE_DIR = "data/message";

    private final BuilderHelper helper;

    private final List<BeanMapping> configuration;


    public RestNetSubmodule(BuilderHelper helper) {
        this.helper = helper;

        configuration = new ArrayList<>();
    }

    @Override
    public void addSubmoduleTo(Directory srcBase) {

        if (!srcBase.isBase()) {
            throw new GeneratorException("Src base must be base.");
        }

        // messages
        Directory responseMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superType(Object.class)
                .filename(helper.responseMessageName())
                .build();
        Directory requestMessage = new Directory.Builder(MESSAGE_DIR, DirType.IMPL)
                .parent(srcBase)
                .superType(Object.class)
                .filename(helper.requestMessageName())
                .build();
    }

    @Override
    public Collection<BeanMapping> getConfiguration() {
        return configuration;
    }
}
