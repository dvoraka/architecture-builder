package dvoraka.archbuilder.template.config;

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

        StringBuilder sb = new StringBuilder();
        sb.append("rootProject.name = ");
        sb.append("'");
        sb.append(getProjectName());
        sb.append("'");
        sb.append("\n");

        return sb.toString();
    }

    public String getProjectName() {
        return projectName;
    }
}
