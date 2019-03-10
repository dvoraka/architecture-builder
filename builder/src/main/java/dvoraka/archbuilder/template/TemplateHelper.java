package dvoraka.archbuilder.template;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.template.config.ConfigurationTemplate;
import dvoraka.archbuilder.template.text.TextTemplate;

public interface TemplateHelper {

    default Directory configuration(Directory parent, ConfigurationTemplate template) {
        return new Directory.Builder("", DirType.BUILD_CONFIG)
                .parent(parent)
                .filename(template.getFilename())
                .text(template.getConfig())
                .build();
    }

    default Directory buildGradle(Directory root, ConfigurationTemplate buildGradle) {
        return configuration(root, buildGradle);
    }

    default Directory settingsGradle(Directory root, ConfigurationTemplate settingsGradle) {
        return configuration(root, settingsGradle);
    }

    default Directory gitignore(Directory root, TextTemplate textTemplate) {
        return new Directory.Builder("", DirType.TEXT)
                .parent(root)
                .filename(".gitignore")
                .text(textTemplate.getText())
                .build();
    }
}
