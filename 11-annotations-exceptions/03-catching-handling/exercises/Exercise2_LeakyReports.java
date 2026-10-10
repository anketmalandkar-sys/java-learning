import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 2 (Medium): fix resource leaks with try-with-resources.
 *
 * TASK
 *   Report jobs read from a DataSource and write to an Output. Both must ALWAYS be closed.
 *   The checks track how many are still open, and the order they were closed in.
 *
 *   1. countRows(): closes the source only when nothing goes wrong. Fix it with
 *      try-with-resources.
 *   2. exportReport(): opens a source and an output by hand and closes them in the wrong order,
 *      also only on success. Rewrite it so both are opened in ONE try-with-resources: the output
 *      must be closed BEFORE the source (it was opened after it).
 *   3. exportAll(): reuses an existing `audit` Output created before the try. It must be closed
 *      at the end too. Use it directly as a resource: try (audit) { ... } (Java 9+).
 *
 *   Each method must still throw its exceptions to the caller (the checks expect that); you
 *   only need to guarantee the closing.
 *
 * EXPECTED OUTPUT
 *   countRows ok:      PASS
 *   countRows failing: PASS
 *   export order:      PASS
 *   export failing:    PASS
 *   exportAll:         PASS
 *   ALL PASS
 *
 * Run: java exercises/Exercise2_LeakyReports.java
 */
public class Exercise2_LeakyReports {

    static int open = 0;
    static final List<String> closed = new ArrayList<>();

    static class DataSource implements AutoCloseable {
        private final List<String> rows;

        DataSource(List<String> rows) {
            this.rows = rows;
            open++;
        }

        List<String> read() {
            if (rows.contains("CORRUPT")) {
                throw new IllegalStateException("corrupt row");
            }
            return rows;
        }

        @Override
        public void close() {
            open--;
            closed.add("source");
        }
    }

    static class Output implements AutoCloseable {
        private final String name;
        final List<String> written = new ArrayList<>();

        Output(String name) {
            this.name = name;
            open++;
        }

        void write(String line) {
            written.add(line);
        }

        @Override
        public void close() {
            open--;
            closed.add(name);
        }
    }

    // TODO 1
    static int countRows(List<String> rows) {
        DataSource source = new DataSource(rows);
        int count = source.read().size();
        source.close();
        return count;
    }

    // TODO 2
    static List<String> exportReport(List<String> rows) {
        DataSource source = new DataSource(rows);
        Output out = new Output("output");
        for (String row : source.read()) {
            out.write(row.toUpperCase());
        }
        source.close();
        out.close();
        return out.written;
    }

    // TODO 3
    static int exportAll(List<List<String>> batches) {
        Output audit = new Output("audit");
        int total = 0;
        for (List<String> batch : batches) {
            total += exportReport(batch).size();
            audit.write("batch of " + batch.size());
        }
        return total;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        reset();
        allPass &= check("countRows ok:", countRows(List.of("a", "b")) == 2 && open == 0);

        reset();
        boolean threw = throwsIse(() -> countRows(List.of("a", "CORRUPT")));
        allPass &= check("countRows failing:", threw && open == 0);

        reset();
        List<String> result = exportReport(List.of("x", "y"));
        allPass &= check("export order:", result.equals(List.of("X", "Y")) && open == 0
                && closed.equals(List.of("output", "source")));

        reset();
        threw = throwsIse(() -> exportReport(List.of("CORRUPT")));
        allPass &= check("export failing:", threw && open == 0 && closed.equals(List.of("output", "source")));

        reset();
        int total = exportAll(List.of(List.of("a"), List.of("b", "c")));
        allPass &= check("exportAll:", total == 3 && open == 0 && closed.get(closed.size() - 1).equals("audit"));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static void reset() {
        open = 0;
        closed.clear();
    }

    static boolean throwsIse(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-18s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
