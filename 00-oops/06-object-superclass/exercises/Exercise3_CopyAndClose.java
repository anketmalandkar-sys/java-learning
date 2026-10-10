import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 3 (Hard): copies and cleanup.
 *
 * TASK
 *   1. Itinerary.clone() is shallow: changing the clone's stops changes the original's.
 *      Fix clone() so the copy is independent (deep-copy the list).
 *
 *   2. Add a copy constructor Itinerary(Itinerary other) that also makes an independent copy,
 *      and make the static copyOf(Itinerary other) below use it.
 *
 *   3. Connection relies on a finalize-style "cleanup()" that nobody calls, so connections leak.
 *      Make Connection implement AutoCloseable: close() must set open = false and decrement
 *      OPEN_COUNT. Calling close() twice must not decrement twice.
 *      Then rewrite runReport() to use try-with-resources, so the connection is closed even
 *      when the query throws.
 *
 * EXPECTED OUTPUT
 *   clone independent:  PASS
 *   copy constructor:   PASS
 *   closed on success:  PASS
 *   closed on failure:  PASS
 *   close twice:        PASS
 *   ALL PASS
 *
 * HINTS
 *   - Inside clone(): call super.clone(), then replace the list field with new ArrayList<>(stops).
 *     The field must not be final for that.
 *   - try (Connection c = new Connection()) { ... } calls c.close() automatically.
 *
 * Run: java exercises/Exercise3_CopyAndClose.java
 */
public class Exercise3_CopyAndClose {

    static class Itinerary implements Cloneable {
        String traveller;
        List<String> stops = new ArrayList<>();

        Itinerary(String traveller) {
            this.traveller = traveller;
        }

        // TODO: copy constructor Itinerary(Itinerary other)

        @Override
        public Itinerary clone() {
            try {
                return (Itinerary) super.clone();   // TODO: shallow
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }

    static Itinerary copyOf(Itinerary other) {
        return other; // TODO: return a copy made with the copy constructor
    }

    static class Connection {   // TODO: implements AutoCloseable
        static int OPEN_COUNT = 0;
        boolean open = true;

        Connection() {
            OPEN_COUNT++;
        }

        String query(String sql) {
            if (sql.contains("DROP")) {
                throw new IllegalStateException("not allowed: " + sql);
            }
            return "rows for " + sql;
        }

        // Called by nobody: the old "finalize" approach.
        void cleanup() {
            open = false;
            OPEN_COUNT--;
        }
    }

    static String runReport(String sql) {
        Connection c = new Connection();     // TODO: try-with-resources
        return c.query(sql);
    }

    public static void main(String[] args) {
        boolean allPass = true;

        Itinerary trip = new Itinerary("Asha");
        trip.stops.add("Pune");
        Itinerary cloned = trip.clone();
        cloned.stops.add("Goa");
        allPass &= check("clone independent:", trip.stops.size() == 1 && cloned.stops.size() == 2);

        Itinerary copied = copyOf(trip);
        copied.stops.add("Delhi");
        allPass &= check("copy constructor:", copied != trip && trip.stops.size() == 1 && copied.stops.size() == 2);

        Connection.OPEN_COUNT = 0;
        runReport("SELECT 1");
        allPass &= check("closed on success:", Connection.OPEN_COUNT == 0);

        try {
            runReport("DROP TABLE x");
        } catch (IllegalStateException e) {
            // expected
        }
        allPass &= check("closed on failure:", Connection.OPEN_COUNT == 0);

        boolean twiceOk;
        try {
            Connection c = new Connection();
            AutoCloseable closeable = (AutoCloseable) (Object) c;
            closeable.close();
            closeable.close();
            twiceOk = Connection.OPEN_COUNT == 0 && !c.open;
        } catch (Exception e) {
            twiceOk = false;
        }
        allPass &= check("close twice:", twiceOk);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-19s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
