package dvoraka.archbuilder.template;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.template.config.ConfigurationTemplate;
import dvoraka.archbuilder.template.text.TextTemplate;

public interface TemplateHelper {

    default Directory buildGradle(Directory root, ConfigurationTemplate buildGradle) {
        return new Directory.Builder("", DirType.BUILD_CONFIG)
                .parent(root)
                .filename(buildGradle.getFilename())
                .text(buildGradle.getConfig())
                .build();
    }

    default Directory settingsGradle(Directory root, ConfigurationTemplate settingsGradle) {
        return new Directory.Builder("", DirType.BUILD_CONFIG)
                .parent(root)
                .filename(settingsGradle.getFilename())
                .text(settingsGradle.getConfig())
                .build();
    }

    default Directory gitignore(Directory root, TextTemplate textTemplate) {
        return new Directory.Builder("", DirType.TEXT)
                .parent(root)
                .filename(".gitignore")
                .text(textTemplate.getText())
                .build();
    }
}
