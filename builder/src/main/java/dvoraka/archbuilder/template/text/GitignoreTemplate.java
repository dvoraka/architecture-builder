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
                .addLn("# Gradle")
                .addLn(".gradle/")
                .ln()
                .addLn("# Idea")
                .addLn(".idea/")
                .getText();
    }

    @Override
    public String getPath() {
        return "";
    }
}
