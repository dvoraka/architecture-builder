package dvoraka.archbuilder.template.text;

import dvoraka.archbuilder.TextBuilder;

public class DefaultGitignoreTemplate implements TextTemplate {

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
}
