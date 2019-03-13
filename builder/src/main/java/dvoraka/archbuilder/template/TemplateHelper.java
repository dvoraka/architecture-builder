package dvoraka.archbuilder.template;

import dvoraka.archbuilder.DirType;
import dvoraka.archbuilder.Directory;
import dvoraka.archbuilder.template.text.TextFileTemplate;

public interface TemplateHelper {

    String JAVA_SRC_DIR = "src/main/java";


    default Directory root(String rootDirName) {
        return new Directory.Builder(rootDirName, DirType.ROOT)
                .parent(null)
                .build();
    }

    default Directory srcRoot(Directory root) {
        return new Directory.Builder(TemplateHelper.JAVA_SRC_DIR, DirType.SRC_ROOT)
                .parent(root)
                .build();
    }

    default Directory srcBase(Directory srcRoot, String pkgPath) {
        return new Directory.Builder(pkgPath, DirType.SRC_BASE)
                .parent(srcRoot)
                .build();
    }

    default Directory textFile(Directory parent, TextFileTemplate template) {
        return new Directory.Builder(template.getPath(), DirType.TEXT)
                .parent(parent)
                .filename(template.getFilename())
                .text(template.getText())
                .build();
    }

    default Directory classFile(Directory parent, TextFileTemplate template) {
        return new Directory.Builder(template.getPath(), DirType.CUSTOM_TYPE)
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

    default Directory properties(Directory root, TextFileTemplate template) {
        return textFile(root, template);
    }

    default Directory springBootApp(Directory root, TextFileTemplate template) {
        return classFile(root, template);
    }
}
