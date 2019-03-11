package dvoraka.archbuilder.template;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.template.text.TextFileTemplate;

public interface TemplateHelper {

    default Directory textFile(Directory parent, TextFileTemplate template) {
        return new Directory.Builder(template.getPath(), DirType.BUILD_CONFIG)
                .parent(parent)
                .filename(template.getFilename())
                .text(template.getText())
                .build();
    }

    default Directory buildGradle(Directory root, TextFileTemplate buildGradle) {
        return textFile(root, buildGradle);
    }

    default Directory settingsGradle(Directory root, TextFileTemplate settingsGradle) {
        return textFile(root, settingsGradle);
    }

    default Directory gitignore(Directory root, TextFileTemplate template) {
        return textFile(root, template);
    }
}
