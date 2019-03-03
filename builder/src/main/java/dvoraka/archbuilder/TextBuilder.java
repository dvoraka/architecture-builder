package dvoraka.archbuilder;

public class TextBuilder {

    private final StringBuilder builder;


    public TextBuilder() {
        builder = new StringBuilder();
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

    public String getText() {
        return builder.toString();
    }
}
