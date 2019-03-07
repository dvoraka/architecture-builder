package dvoraka.archbuilder.template.config;

import dvoraka.archbuilder.TextBuilder;

public class SettingsGradleTemplate implements ConfigurationTemplate {

    private static final String FILENAME = "settings.gradle";

    private final String projectName;


    public SettingsGradleTemplate(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String getFilename() {
        return FILENAME;
    }

    @Override
    public String getConfig() {
        return new TextBuilder()
                .addLine("rootProject.name = '${name}'")
                .variable("name", getProjectName())
                .render();
    }

    public String getProjectName() {
        return projectName;
    }
}
