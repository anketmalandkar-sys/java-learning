package oops.exercises;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Exercise 4 (Medium): evolve a published interface without breaking anyone.
 * Goes with abstraction/InterfaceAndAbstractRules.java.
 *
 * TASK
 *   Exporter is used by other teams; LegacyCsvExporter was written by one of them and you
 *   may NOT edit it.
 *
 *   1. Add fileName(String base) to Exporter WITHOUT breaking LegacyCsvExporter:
 *      it returns base + "." + extension(), and extension() returns "txt" unless an exporter
 *      overrides it. Both must be default methods.
 *      Move the "lowercase and replace spaces with '-'" logic into a PRIVATE interface method
 *      slug(String) that fileName uses:  fileName("Sales Report") -> "sales-report.txt"
 *
 *   2. Add a constant MAX_ROWS = 10_000 to Exporter (no modifiers needed: why?).
 *
 *   3. Create interface Compressing extends Exporter, with one abstract method
 *      int compressionLevel(). Every compressing exporter must choose its own file extension,
 *      so also REDECLARE extension() as abstract in Compressing (no body): the "txt" default
 *      no longer applies to compressing exporters.
 *
 *   4. Make AbstractJsonExporter (already abstract) implement Exporter PARTIALLY: give it
 *      export() (joins rows as a JSON array of strings: ["a","b"]) and extension() "json".
 *      Then make GzipJsonExporter extend AbstractJsonExporter and implement Compressing,
 *      with compressionLevel() returning 9 and extension() returning "json.gz".
 *
 *   Uncomment the checks in main once everything compiles.
 *
 * EXPECTED OUTPUT
 *   legacy works:  PASS
 *   defaults:      PASS
 *   private slug:  PASS
 *   constant:      PASS
 *   json:          PASS
 *   gzip:          PASS
 *   ALL PASS
 *
 * Run: press the green run button next to main in IntelliJ, or from the repo root:
 *   javac -d out $(find 00-oops -name "*.java") && java -cp out oops.exercises.Exercise4_EvolveTheInterface
 */
public class Exercise4_EvolveTheInterface {

    interface Exporter {
        String export(List<String> rows);

        // TODO: MAX_ROWS, default fileName, default extension, private slug
    }

    // Written by another team. DO NOT EDIT.
    static class LegacyCsvExporter implements Exporter {
        @Override
        public String export(List<String> rows) {
            return String.join(",", rows);
        }
    }

    // TODO: interface Compressing extends Exporter

    abstract static class AbstractJsonExporter {   // TODO: implements Exporter, export(), extension()
    }

    // TODO: static class GzipJsonExporter extends AbstractJsonExporter implements Compressing

    public static void main(String[] args) throws Exception {
        boolean allPass = true;
        Exporter legacy = new LegacyCsvExporter();
        allPass &= check("legacy works:", "a,b".equals(legacy.export(List.of("a", "b"))));

        // uncomment:
        // allPass &= check("defaults:", "sales-report.txt".equals(legacy.fileName("Sales Report"))
        //         && Exporter.class.getMethod("fileName", String.class).isDefault()
        //         && Exporter.class.getMethod("extension").isDefault());
        // Method slug = Exporter.class.getDeclaredMethod("slug", String.class);
        // allPass &= check("private slug:", Modifier.isPrivate(slug.getModifiers()));
        // allPass &= check("constant:", Exporter.MAX_ROWS == 10_000);
        // Exporter json = new AbstractJsonExporter() { };
        // allPass &= check("json:", "[\"a\",\"b\"]".equals(json.export(List.of("a", "b")))
        //         && "q1.json".equals(json.fileName("Q1")));
        // GzipJsonExporter gz = new GzipJsonExporter();
        // allPass &= check("gzip:", gz.compressionLevel() == 9 && "q1.json.gz".equals(gz.fileName("Q1"))
        //         && gz instanceof Compressing && gz instanceof AbstractJsonExporter
        //         && Modifier.isAbstract(Compressing.class.getDeclaredMethod("extension").getModifiers()));

        allPass &= check("uncommented:", false);   // TODO: delete after uncommenting
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-14s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
