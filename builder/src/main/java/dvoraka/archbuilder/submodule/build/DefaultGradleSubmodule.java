package dvoraka.archbuilder.submodule.build;

import dvoraka.archbuilder.data.Directory;
import dvoraka.archbuilder.template.text.BuildGradleTemplate;
import dvoraka.archbuilder.template.text.SettingsGradleTemplate;

public class DefaultGradleSubmodule implements BuildSubmodule {

    private final BuildSubmodule configuredSubmodule;


    public DefaultGradleSubmodule(String projectName) {

        configuredSubmodule = new ConfigurableGradleSubmodule(
                new BuildGradleTemplate(),
                new SettingsGradleTemplate(projectName)
        );
    }

    @Override
    public void addSubmoduleTo(Directory root) {
        configuredSubmodule.addSubmoduleTo(root);
    }
}
