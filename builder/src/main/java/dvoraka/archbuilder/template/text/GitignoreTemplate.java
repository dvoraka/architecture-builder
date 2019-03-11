package dvoraka.archbuilder.template.text;

import dvoraka.archbuilder.TextBuilder;

public class GitignoreTemplate implements TextFileTemplate {

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
