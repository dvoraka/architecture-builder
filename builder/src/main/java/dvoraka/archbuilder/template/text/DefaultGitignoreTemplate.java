package dvoraka.archbuilder.template.text;

import dvoraka.archbuilder.TextBuilder;
import dvoraka.archbuilder.template.config.TextFileTemplate;

public class DefaultGitignoreTemplate implements TextFileTemplate {

    @Override
    public String getFilename() {
        return ".gitignore";
    }

    @Override
    public String getText() {
        return TextBuilder.create()
                .addLine("# Gradle")
                .addLine(".gradle/")
                .addLine()
                .addLine("# Idea")
                .addLine(".idea/")
                .getText();
    }

    @Override
    public String getPath() {
        return "";
    }
}
