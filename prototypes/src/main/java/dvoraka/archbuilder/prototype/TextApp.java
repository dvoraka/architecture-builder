package dvoraka.archbuilder.prototype;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextApp {

    public static void main(String[] args) {

        String var1Name = "var1";
//        String var2Name = "var2";
        String var2Name = ".*var2"; // name must be checked for special characters

        String var1Value = "VALUE";
        String var2Value = "ANOTHER VALUE";

        String text = "Long text with vars and ${unknown vars}, var1: ${"
                + var1Name + "}, var2: ${" + Matcher.quoteReplacement(var2Name) + "}, var1: " + "${" + var1Name + "}";
        System.out.println(text);

        String update1 = text.replaceAll("\\$\\{" + var1Name + "}", var1Value);
        System.out.println(update1);

        String update2 = update1.replaceAll("\\$\\{" + Pattern.quote(var2Name) + "}", var2Value);
        System.out.println(update2);

        System.out.println();

        String coolName = ".$.?.na&me";
        System.out.println(Pattern.quote(coolName));
        System.out.println(Matcher.quoteReplacement(coolName));
    }
}
