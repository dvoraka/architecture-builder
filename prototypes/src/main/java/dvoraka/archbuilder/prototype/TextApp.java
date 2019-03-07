package dvoraka.archbuilder.prototype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextApp {

    public static void main(String[] args) {

        String var1Name = "var1";
        String var2Name = "var.*";
        String var3Name = "var.+";

        String var1Value = "VALUE";
        String var2Value = "**..**";
        String var3Value = "$$..$$";

        System.out.println(Pattern.quote(var3Value));
        System.out.println(Matcher.quoteReplacement(var3Value));

        String text = "Long text with vars and ${unknown vars}, var1: ${"
                + var1Name
                + "}, var2: ${"
                + var2Name
                + "}, var1: ${"
                + var1Name
                + "}, var3: ${"
                + var3Name
                + "}";
        System.out.println(text);

        String update1 = text.replaceAll("\\$\\{" + var1Name + "}",
                Matcher.quoteReplacement(var1Value));
        System.out.println(update1);

        String update2 = update1.replaceAll("\\$\\{" + Pattern.quote(var2Name) + "}",
                Matcher.quoteReplacement(var2Value));
        System.out.println(update2);

        String update3 = update2.replaceAll("\\$\\{" + Pattern.quote(var3Name) + "}",
                Matcher.quoteReplacement(var3Value));
        System.out.println(update3);
    }
}
