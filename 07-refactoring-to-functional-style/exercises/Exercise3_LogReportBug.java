import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Exercise 3 (Hard): the "refactored" log report.
 *
 * THE SITUATION
 *   A teammate refactored the request-log report from loops to streams. Each line of the log is
 *       time method path durationMs status          e.g.  09:00:02 GET /cart 1500 200
 *   and a restart is written as a single line:  SHUTDOWN
 *
 *   The report has three parts:
 *     errorPercent(file)                  % of requests in the file with status 500 (whole number)
 *     firstSlowPaths(requests, n)         the paths of the first n requests that took over 1000 ms
 *     requestsBeforeShutdown(lines)       every line before the first SHUTDOWN
 *
 *   Since the refactoring, three bug reports came in:
 *     1. The error report crashes with IllegalStateException.
 *     2. The "slow requests" list misses requests that are clearly slow.
 *     3. "Requests before shutdown" includes requests from after the restart.
 *
 * TASK
 *   1. Run it and match each FAIL to a bug report.
 *   2. Fix the three methods. Keep them functional (streams, no loops).
 *   3. errorPercent must also close the file it opens (the teammate's version leaks it).
 *
 * EXPECTED OUTPUT (after the fix)
 *   errorPercent:           PASS
 *   firstSlowPaths:         PASS
 *   requestsBeforeShutdown: PASS
 *   ALL PASS
 *
 * HINTS
 *   - What happens to a stream after its terminal operation has run?
 *   - Read limit(n).filter(...) out loud. Is that the question being asked?
 *   - filter keeps every match anywhere in the stream. Which operation stops at the first non-match?
 *
 * Run: java exercises/Exercise3_LogReportBug.java
 */
public class Exercise3_LogReportBug {

    static int durationOf(String line) {
        return Integer.parseInt(line.split(" ")[3]);
    }

    static String pathOf(String line) {
        return line.split(" ")[2];
    }

    static long errorPercent(Path file) throws IOException {
        Stream<String> lines = Files.lines(file);
        long total = lines.count();
        long errors = lines.filter(line -> line.endsWith(" 500")).count();
        return errors * 100 / total;
    }

    static List<String> firstSlowPaths(List<String> requests, int n) {
        return requests.stream()
                .limit(n)
                .filter(line -> durationOf(line) > 1000)
                .map(Exercise3_LogReportBug::pathOf)
                .toList();
    }

    static List<String> requestsBeforeShutdown(List<String> lines) {
        return lines.stream()
                .filter(line -> !line.equals("SHUTDOWN"))
                .toList();
    }

    // ---- Don't change anything below this line. ----

    public static void main(String[] args) throws IOException {
        List<String> requests = List.of(
                "09:00:01 GET /home 120 200",
                "09:00:02 GET /cart 1500 200",
                "09:00:03 POST /pay 300 500",
                "09:00:04 GET /home 2200 200",
                "09:00:05 GET /search 90 200",
                "09:00:06 POST /pay 1800 500",
                "09:00:07 GET /cart 1300 200");

        List<String> withRestart = Stream.concat(
                requests.stream(),
                Stream.of("SHUTDOWN", "09:05:00 GET /home 100 200", "09:05:01 GET /report 5000 200")).toList();

        Path file = Files.createTempFile("requests", ".log");
        Files.write(file, requests);

        boolean allPass = true;
        try {
            allPass &= check("errorPercent", () -> errorPercent(file) == 28);   // 2 of 7

            allPass &= check("firstSlowPaths", () ->
                    firstSlowPaths(requests, 2).equals(List.of("/cart", "/home"))
                            && firstSlowPaths(requests, 10).equals(List.of("/cart", "/home", "/pay", "/cart"))
                            && firstSlowPaths(requests, 0).isEmpty());

            allPass &= check("requestsBeforeShutdown", () ->
                    requestsBeforeShutdown(withRestart).equals(requests)
                            && requestsBeforeShutdown(requests).equals(requests)
                            && requestsBeforeShutdown(List.of("SHUTDOWN", "09:00:01 GET /home 120 200")).isEmpty());
        } finally {
            Files.deleteIfExists(file);
        }

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    interface Check {
        boolean run() throws IOException;
    }

    static boolean check(String name, Check test) {
        boolean ok;
        try {
            ok = test.run();
        } catch (IOException | RuntimeException e) {
            System.out.printf("%-23s FAIL (%s)%n", name + ":", e);
            return false;
        }
        System.out.printf("%-23s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
