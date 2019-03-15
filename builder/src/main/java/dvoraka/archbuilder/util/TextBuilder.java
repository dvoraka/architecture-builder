package dvoraka.archbuilder.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextBuilder {

    private static final String TAB = "    ";

    private final StringBuilder builder;
    private final Map<String, String> variables;


    public TextBuilder() {
        builder = new StringBuilder();
        variables = new HashMap<>();
    }

    public static TextBuilder create() {
        return new TextBuilder();
    }

    public TextBuilder ln() {
        builder.append("\n");
        return this;
    }

    public TextBuilder tab() {
        builder.append(TAB);
        return this;
    }

    public TextBuilder add(String text) {
        builder.append(text);
        return this;
    }

    public TextBuilder addLn(String text) {
        builder.append(text);
        ln();
        return this;
    }

    public TextBuilder addTab(String text) {
        tab();
        builder.append(text);
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
