package dvoraka.archbuilder;

import java.util.HashMap;
import java.util.Map;

public class TextBuilder {

    private final StringBuilder builder;
    private final Map<String, String> variables;


    public TextBuilder() {
        builder = new StringBuilder();
        variables = new HashMap<>();
    }

    public static TextBuilder create() {
        return new TextBuilder();
    }

    public TextBuilder addLine() {
        builder.append("\n");
        return this;
    }

    public TextBuilder addLine(String line) {
        builder.append(line);
        builder.append("\n");
        return this;
    }

    public TextBuilder variable(String varName, String varValue) {
        checkSpecialChars(varName);
        checkSpecialChars(varValue);
        variables.put(varName, varValue);
        return this;
    }

    public String getText() {
        return builder.toString();
    }

    public String render() {
        String newText = getText();
        for (Map.Entry<String, String> varEntry : variables.entrySet()) {
            String varRegex = "\\$\\{" + varEntry.getKey() + "}";
            newText = newText.replaceAll(varRegex, varEntry.getValue());
        }

        return newText;
    }

    private void checkSpecialChars(String string) {
        // \.[]{}()<>*+-=!?^$|
    }
}
