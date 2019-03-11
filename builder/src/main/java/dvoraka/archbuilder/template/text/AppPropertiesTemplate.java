package dvoraka.archbuilder.template.text;

import dvoraka.archbuilder.TextBuilder;

public class AppPropertiesTemplate implements TextFileTemplate {

    @Override
    public String getFilename() {
        return "application.properties";
    }

    @Override
    public String getText() {
        return TextBuilder.create()
                .addLine("# Logging")
                .addLine("logging.level.root=info")
                .getText();
    }

    @Override
    public String getPath() {
        return "src/main/resources";
    }
}
