import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Example 2: turning messy log lines into a clean report.
 *   1. Skip blank and comment lines (isBlank, startsWith).
 *   2. Split on a precompiled regex, keeping empty fields (limit -1).
 *   3. Clean each field (strip, toLowerCase(Locale.ROOT)) and pull out parts (substring, indexOf).
 *   4. Print an aligned table with String.format, and an email with a text block.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    // Compiled once, reused for every line: a pipe with any spaces around it.
    private static final Pattern PIPE = Pattern.compile("\\s*\\|\\s*");

    record LogEntry(String time, String level, String user, String message) {}

    static final String RAW_LOG = """
            # exported from the payments service
            2024-03-01T09:15:02 | ERROR | ravi@shop.in  | Card declined
            2024-03-01T09:16:40 |  info |               | Health check ok

            2024-03-01T09:20:11 | Warn  | meera@shop.in | Slow response: 2300 ms
            2024-03-01T09:21:05 | ERROR | arjun@shop.in | Timeout calling bank|
            """;

    public static void main(String[] args) {
        List<LogEntry> entries = new ArrayList<>();
        for (String line : RAW_LOG.lines().toList()) {
            if (line.isBlank() || line.strip().startsWith("#")) {
                continue;                                    // 1. skip noise
            }
            String[] fields = PIPE.split(line.strip(), -1);  // 2. -1 keeps an empty user field
            String time = fields[0].substring(fields[0].indexOf('T') + 1);    // 3. just HH:mm:ss
            String level = fields[1].toUpperCase(Locale.ROOT);
            String user = fields[2].isEmpty() ? "-" : fields[2].substring(0, fields[2].indexOf('@'));
            String message = fields[3];
            entries.add(new LogEntry(time, level, user, message));
        }

        System.out.println("--- Report ---");
        System.out.println(String.format("%-9s %-6s %-6s %s", "TIME", "LEVEL", "USER", "MESSAGE"));
        System.out.println("-".repeat(50));
        for (LogEntry e : entries) {
            System.out.println(String.format("%-9s %-6s %-6s %s", e.time(), e.level(), e.user(), e.message()));
        }

        long errors = entries.stream().filter(e -> e.level().equals("ERROR")).count();
        List<String> errorUsers = entries.stream()
                .filter(e -> e.level().equals("ERROR"))
                .map(LogEntry::user)
                .toList();

        System.out.println();
        System.out.println("--- Alert email ---");
        String email = """
                Subject: %d payment errors

                Hi team,
                Users affected: %s.
                Most recent: "%s".
                """.formatted(errors, String.join(", ", errorUsers), entries.get(entries.size() - 1).message());
        System.out.print(email);
    }
}

/* Expected output:
--- Report ---
TIME      LEVEL  USER   MESSAGE
--------------------------------------------------
09:15:02  ERROR  ravi   Card declined
09:16:40  INFO   -      Health check ok
09:20:11  WARN   meera  Slow response: 2300 ms
09:21:05  ERROR  arjun  Timeout calling bank

--- Alert email ---
Subject: 2 payment errors

Hi team,
Users affected: ravi, arjun.
Most recent: "Timeout calling bank".
*/
