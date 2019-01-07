package dvoraka.archbuilder.template.config;

import java.util.Collections;
import java.util.Set;

public class BuildGradleTemplate implements ConfigurationTemplate {

    private final String FILENAME = "build.gradle";

    private Set<String> plugins;


    public BuildGradleTemplate() {
        this(Collections.singleton("java"));
    }

    public BuildGradleTemplate(Set<String> plugins) {
        this.plugins = plugins;
    }

    @Override
    public String getFilename() {
        return FILENAME;
    }

    @Override
    public String getConfig() {

        StringBuilder sb = new StringBuilder();

        for (String plugin : plugins) {
            sb.append("apply plugin: ");
            sb.append("'");
            sb.append(plugin);
            sb.append("'");
            sb.append("\n");
        }

        sb.append("\n");

        return sb.toString();
    }

    public Set<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(Set<String> plugins) {
        this.plugins = plugins;
    }
}
