package dvoraka.archbuilder.template.config;

import java.util.Collections;
import java.util.Set;

public class BuildGradleTemplate implements TextFileTemplate {

    private static final String FILENAME = "build.gradle";

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
    public String getText() {

        // POC stuff only

        StringBuilder sb = new StringBuilder();

        // Spring Boot plugin
        sb.append("plugins { id 'org.springframework.boot' version '2.1.0.RELEASE' }\n\n");

        // plugins
        for (String plugin : plugins) {
            sb.append("apply plugin: ");
            sb.append("'");
            sb.append(plugin);
            sb.append("'");
            sb.append("\n");
        }
        sb.append("\n");

        sb.append("repositories {\n");
        sb.append("    jcenter()\n");
        sb.append("    maven { url 'https://jitpack.io' }\n");
        sb.append("    maven { url 'https://repo.gradle.org/gradle/libs-releases' }\n");
        sb.append("}\n\n");

        sb.append("dependencies {\n");
        sb.append("    implementation 'com.github.dvoraka:architecture-builder:master-SNAPSHOT'\n");
        sb.append("}\n\n");

        return sb.toString();
    }

    @Override
    public String getPath() {
        return "";
    }

    public Set<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(Set<String> plugins) {
        this.plugins = plugins;
    }
}
