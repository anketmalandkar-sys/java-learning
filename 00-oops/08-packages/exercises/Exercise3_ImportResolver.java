import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exercise 3 (Hard): how the compiler resolves a simple type name.
 *
 * TASK
 *   Write resolve(simpleName, currentPackage, imports, packages) that returns the fully
 *   qualified name the compiler would pick for `simpleName`, following these rules
 *   (a simplified version of the Java Language Specification):
 *
 *     packages   maps a package name to the simple names of the types in it, e.g.
 *                "java.util" -> {"List", "Date", "Map"}
 *     imports    the file's import lines, e.g. "java.util.List" or "java.sql.*"
 *
 *   In this order:
 *     1. A SINGLE-TYPE import whose last part equals simpleName wins.
 *        If two different single-type imports match: return "ERROR: conflicting imports".
 *     2. Otherwise, a type with that name in the CURRENT package wins.
 *     3. Otherwise, look in the WILDCARD imports ("pkg.*") AND in java.lang (always imported).
 *        Exactly one match -> that type. More than one -> "ERROR: ambiguous". None ->
 *        "ERROR: cannot find symbol".
 *
 *   Wildcards don't include subpackages: "java.util.*" doesn't find java.util.function types.
 *
 *   Nested types: `packages` may also have a CLASS as a key, listing its public nested types,
 *   e.g. "java.util.Map" -> {"Entry"}. A wildcard on a class ("java.util.Map.*") imports those
 *   nested types, but NOT the class itself. A single-type import of a nested type
 *   ("java.util.Map.Entry") works like any other single-type import.
 *
 * EXPECTED OUTPUT
 *   single-type:    PASS
 *   same package:   PASS
 *   wildcard:       PASS
 *   java.lang:      PASS
 *   ambiguous:      PASS
 *   single wins:    PASS
 *   conflict:       PASS
 *   subpackages:    PASS
 *   not found:      PASS
 *   nested types:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - An import ending in ".*" is a wildcard; its package is everything before ".*".
 *   - For a single-type import, the package is everything before the last '.'.
 *   - Collect wildcard matches in a Set to detect "more than one".
 *
 * Run: java exercises/Exercise3_ImportResolver.java
 */
public class Exercise3_ImportResolver {

    static String resolve(String simpleName, String currentPackage, List<String> imports,
                          Map<String, Set<String>> packages) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        Map<String, Set<String>> packages = Map.of(
                "java.lang", Set.of("String", "Integer", "Math", "Object"),
                "java.util", Set.of("List", "Date", "Map", "ArrayList"),
                "java.util.function", Set.of("Function", "Supplier"),
                "java.sql", Set.of("Date", "Connection"),
                "java.awt", Set.of("List", "Color"),
                "com.shop", Set.of("Order", "Date"),
                "java.util.Map", Set.of("Entry"));

        boolean allPass = true;
        allPass &= check("single-type:", "java.util.List".equals(
                resolve("List", "com.app", List.of("java.util.List"), packages)));
        allPass &= check("same package:", "com.shop.Order".equals(
                resolve("Order", "com.shop", List.of("java.util.*"), packages)));
        allPass &= check("wildcard:", "java.sql.Connection".equals(
                resolve("Connection", "com.app", List.of("java.util.*", "java.sql.*"), packages)));
        allPass &= check("java.lang:", "java.lang.String".equals(
                resolve("String", "com.app", List.of(), packages)));
        allPass &= check("ambiguous:", "ERROR: ambiguous".equals(
                resolve("Date", "com.app", List.of("java.util.*", "java.sql.*"), packages)));
        allPass &= check("single wins:", "java.sql.Date".equals(
                resolve("Date", "com.shop", List.of("java.util.*", "java.sql.Date"), packages)));
        allPass &= check("conflict:", "ERROR: conflicting imports".equals(
                resolve("List", "com.app", List.of("java.util.List", "java.awt.List"), packages)));
        allPass &= check("subpackages:", "ERROR: cannot find symbol".equals(
                resolve("Function", "com.app", List.of("java.util.*"), packages)));
        allPass &= check("not found:", "ERROR: cannot find symbol".equals(
                resolve("Widget", "com.app", List.of("java.util.*", "java.awt.*"), packages)));
        allPass &= check("nested types:", "java.util.Map.Entry".equals(
                resolve("Entry", "com.app", List.of("java.util.Map.*"), packages))
                && "java.util.Map.Entry".equals(resolve("Entry", "com.app", List.of("java.util.Map.Entry"), packages))
                && "ERROR: cannot find symbol".equals(resolve("Map", "com.app", List.of("java.util.Map.*"), packages)));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
