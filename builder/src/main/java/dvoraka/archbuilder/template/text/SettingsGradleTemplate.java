package dvoraka.archbuilder.template.text;

import dvoraka.archbuilder.TextBuilder;

public class SettingsGradleTemplate implements TextFileTemplate {

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
    public String getText() {
        return new TextBuilder()
                .addLine("rootProject.name = '${name}'")
                .variable("name", getProjectName())
                .render();
    }

    @Override
    public String getPath() {
        return "";
    }

    public String getProjectName() {
        return projectName;
    }
}
