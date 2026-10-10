/**
 * Exercise 1 (Easy): package naming conventions.
 *
 * TASK
 *   Write packageFor(domain, project): the package prefix a company owning `domain` should use
 *   for `project`, following the Java conventions:
 *
 *     1. Reverse the domain's parts:            shop.example.com  -> com.example.shop
 *     2. Append the project name:               + "billing"       -> com.example.shop.billing
 *     3. Lowercase everything.
 *     4. Fix each part that isn't a valid identifier:
 *          - replace '-' with '_'               my-shop           -> my_shop
 *          - a part starting with a digit gets a leading '_'      123go -> _123go
 *          - a part that is a Java keyword gets a trailing '_'    int   -> int_
 *            (use the KEYWORDS array below)
 *
 *   Examples:
 *     packageFor("example.com", "shop")                    -> "com.example.shop"
 *     packageFor("hyphenated-name.example.org", "app")     -> "org.example.hyphenated_name.app"
 *     packageFor("example.int", "core")                    -> "int_.example.core"
 *     packageFor("123name.Example.COM", "Data")            -> "com.example._123name.data"
 *
 *   Also write isReserved(packageName): true if it starts with "java." or "javax." (or is
 *   exactly "java" or "javax"), which only the platform may use.
 *
 * EXPECTED OUTPUT
 *   simple:    PASS
 *   hyphen:    PASS
 *   keyword:   PASS
 *   digit:     PASS
 *   reserved:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - domain.split("\\.") splits on dots (a plain "." is a regex wildcard).
 *   - Character.isDigit(part.charAt(0)) detects a leading digit.
 *   - String.join(".", parts) joins them back.
 *
 * Run: java exercises/Exercise1_PackageNames.java
 */
public class Exercise1_PackageNames {

    static final String[] KEYWORDS = {
        "abstract", "boolean", "byte", "case", "catch", "char", "class", "const", "default", "do",
        "double", "else", "enum", "final", "float", "for", "goto", "if", "import", "int",
        "interface", "long", "native", "new", "package", "private", "protected", "public",
        "return", "short", "static", "super", "switch", "this", "throw", "try", "void", "while",
    };

    static String packageFor(String domain, String project) {
        return ""; // TODO
    }

    static boolean isReserved(String packageName) {
        return false; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("simple:", "com.example.shop".equals(packageFor("example.com", "shop"))
                && "com.example.shop.billing".equals(packageFor("shop.example.com", "billing")));
        allPass &= check("hyphen:", "org.example.hyphenated_name.app".equals(packageFor("hyphenated-name.example.org", "app")));
        allPass &= check("keyword:", "int_.example.core".equals(packageFor("example.int", "core"))
                && "com.acme.package_".equals(packageFor("acme.com", "package")));
        allPass &= check("digit:", "com.example._123name.data".equals(packageFor("123name.Example.COM", "Data")));
        allPass &= check("reserved:", isReserved("java.util") && isReserved("javax.swing") && isReserved("java")
                && !isReserved("javafx.scene") && !isReserved("com.java.tools"));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-10s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
