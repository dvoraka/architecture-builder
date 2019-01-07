package dvoraka.archbuilder.template.config;

import java.util.Collections;
import java.util.Set;

public class BuildGradleTemplate implements ConfigurationTemplate {

    private Set<String> plugins;


    public BuildGradleTemplate() {
        this(Collections.singleton("java"));
    }

    public BuildGradleTemplate(Set<String> plugins) {
        this.plugins = plugins;
    }

    @Override
    public String getConfig() {
        return null;
    }
}
