package dvoraka.archbuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        variables.put(varName, varValue);
        return this;
    }

    public String getText() {
        return builder.toString();
    }

    public String render() {
        String newText = getText();
        for (Map.Entry<String, String> varEntry : variables.entrySet()) {
            String varRegex = "\\$\\{" + Pattern.quote(varEntry.getKey()) + "}";
            String varValue = Matcher.quoteReplacement(varEntry.getValue());
            newText = newText.replaceAll(varRegex, varValue);
        }

        return newText;
    }
}
