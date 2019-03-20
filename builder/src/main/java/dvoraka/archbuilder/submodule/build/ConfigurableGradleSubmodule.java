package dvoraka.archbuilder.submodule.build;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.template.TemplateHelper;
import dvoraka.archbuilder.template.text.TextFileTemplate;

public class ConfigurableGradleSubmodule implements BuildSubmodule, TemplateHelper {

    private final TextFileTemplate buildGradle;
    private final TextFileTemplate settingsGradle;


    public ConfigurableGradleSubmodule(TextFileTemplate buildGradle, TextFileTemplate settingsGradle) {
        this.buildGradle = buildGradle;
        this.settingsGradle = settingsGradle;
    }

    @Override
    public void addSubmoduleTo(Directory root) {
        buildGradle(root, buildGradle);
        settingsGradle(root, settingsGradle);
    }
}
