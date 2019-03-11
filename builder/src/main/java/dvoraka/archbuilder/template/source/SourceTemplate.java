package dvoraka.archbuilder.template.source;

import dvoraka.archbuilder.template.text.TextFileTemplate;

public interface SourceTemplate extends TextFileTemplate {

    String getSource();

    String getTypeName();

    String getPackageName();
}
