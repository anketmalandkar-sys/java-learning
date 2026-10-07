import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Example 2: a realistic use of Comparable.
 *
 * Part A: software versions like "1.10.0". Sorting them as Strings gives the wrong
 *         order, so Version defines a numeric natural order (major, then minor, then patch).
 * Part B: support tickets in a PriorityQueue. The queue always hands out the most
 *         urgent ticket first, using the ticket's natural order.
 *
 * Uses records (Java 16+).
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    // ---------- Part A: versions ----------

    record Version(int major, int minor, int patch) implements Comparable<Version> {

        static Version parse(String text) {
            String[] parts = text.split("\\.");
            return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }

        @Override
        public int compareTo(Version other) {
            // Compare the most important field first and only fall through on a tie
            int byMajor = Integer.compare(this.major, other.major);
            if (byMajor != 0) {
                return byMajor;
            }
            int byMinor = Integer.compare(this.minor, other.minor);
            if (byMinor != 0) {
                return byMinor;
            }
            return Integer.compare(this.patch, other.patch);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + patch;
        }
    }

    // ---------- Part B: support tickets ----------

    enum Severity { CRITICAL, HIGH, LOW }   // enums are Comparable by declaration order

    record Ticket(int id, Severity severity, String title) implements Comparable<Ticket> {
        @Override
        public int compareTo(Ticket other) {
            int bySeverity = this.severity.compareTo(other.severity);
            if (bySeverity != 0) {
                return bySeverity;
            }
            // Same severity: older ticket (lower id) first. This also keeps compareTo
            // consistent with equals, because ids are unique.
            return Integer.compare(this.id, other.id);
        }
    }

    public static void main(String[] args) {
        List<String> releases = List.of("1.10.0", "1.2.3", "2.0.0", "1.9.12", "1.2.10");

        // String order compares character by character, so "1.10" lands before "1.2"
        List<String> asStrings = new ArrayList<>(releases);
        Collections.sort(asStrings);
        System.out.println("Sorted as Strings:  " + asStrings + "   <- wrong");

        List<Version> versions = new ArrayList<>();
        for (String release : releases) {
            versions.add(Version.parse(release));
        }
        Collections.sort(versions);
        System.out.println("Sorted as Versions: " + versions);
        System.out.println("Latest release:     " + Collections.max(versions));

        Version installed = Version.parse("1.9.12");
        Version available = Version.parse("1.10.0");
        if (available.compareTo(installed) > 0) {
            System.out.println("Update available: " + installed + " -> " + available);
        }

        // PriorityQueue.poll() always returns the "smallest" element by natural order
        PriorityQueue<Ticket> queue = new PriorityQueue<>();
        queue.add(new Ticket(101, Severity.LOW, "Typo on About page"));
        queue.add(new Ticket(102, Severity.CRITICAL, "Payments failing"));
        queue.add(new Ticket(103, Severity.HIGH, "Slow search"));
        queue.add(new Ticket(104, Severity.CRITICAL, "Login down"));

        System.out.println("\nHandling tickets in priority order:");
        while (!queue.isEmpty()) {
            Ticket next = queue.poll();
            System.out.println("  #" + next.id() + " [" + next.severity() + "] " + next.title());
        }
    }
}

/* Expected output:
Sorted as Strings:  [1.10.0, 1.2.10, 1.2.3, 1.9.12, 2.0.0]   <- wrong
Sorted as Versions: [1.2.3, 1.2.10, 1.9.12, 1.10.0, 2.0.0]
Latest release:     2.0.0
Update available: 1.9.12 -> 1.10.0

Handling tickets in priority order:
  #102 [CRITICAL] Payments failing
  #104 [CRITICAL] Login down
  #103 [HIGH] Slow search
  #101 [LOW] Typo on About page
*/
