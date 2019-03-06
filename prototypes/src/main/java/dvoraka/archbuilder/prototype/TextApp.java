package dvoraka.archbuilder.prototype;

public class TextApp {

    public static void main(String[] args) {

        String var1Name = "var1";
        String var2Name = "var2";

        String var1Value = "VALUE";
        String var2Value = "ANOTHER VALUE";

        String text = "Long text with vars and ${unknown vars}, var1: ${"
                + var1Name + "}, var2: ${" + var2Name + "}, var1: " + "${" + var1Name + "}";
        System.out.println(text);

        String update1 = text.replaceAll("\\$\\{" + var1Name + "}", var1Value);
        System.out.println(update1);

        String update2 = update1.replaceAll("\\$\\{" + var2Name + "}", var2Value);
        System.out.println(update2);
    }
}
