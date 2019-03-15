package dvoraka.archbuilder.template.text;

import dvoraka.archbuilder.util.TextBuilder;

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

        TextBuilder tb = new TextBuilder();

        // Spring Boot plugin
        tb.addLn("plugins { id 'org.springframework.boot' version '2.1.0.RELEASE' }");
        tb.ln();

        // plugins
        for (String plugin : plugins) {
            tb.add("apply plugin: ").add("'").add(plugin).addLn("'");
        }
        tb.ln();

        // repositories
        tb.addLn("repositories {");
        tb.tab().addLn("jcenter()");
        tb.tab().addLn("maven { url 'https://jitpack.io' }");
        tb.tab().addLn("maven { url 'https://repo.gradle.org/gradle/libs-releases' }");
        tb.addLn("}");
        tb.ln();

        // dependencies
        tb.addLn("dependencies {");
        tb.tab().addLn("implementation 'com.github.dvoraka:architecture-builder:master-SNAPSHOT'");
        tb.addLn("}");
        tb.ln();

        return tb.getText();
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
