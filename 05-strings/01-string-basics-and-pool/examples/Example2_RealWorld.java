import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Example 2: strings from "outside" in an order-import job.
 *   1. Text read from a CSV line is built at runtime: == fails, equals works.
 *   2. HashMap lookups use equals/hashCode, so they work with runtime strings.
 *   3. Null-safe comparisons.
 *   4. Many rows repeat the same few values: intern() shares one copy of each.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    // Pretend this came from a file: every field below is created at runtime by split().
    static final List<String> CSV_ROWS = List.of(
            "A101,IN,PAID",
            "A102,US,PENDING",
            "A103,IN,PAID",
            "A104,IN,CANCELLED",
            "A105,US,PAID");

    static final Map<String, String> COUNTRY_NAMES = new HashMap<>(Map.of("IN", "India", "US", "United States"));

    public static void main(String[] args) {
        System.out.println("--- 1. == vs equals on imported data ---");
        int paidByReference = 0;
        int paidByContent = 0;
        for (String row : CSV_ROWS) {
            String status = row.split(",")[2];
            if (status == "PAID") {          // compares objects: never true for split() output
                paidByReference++;
            }
            if (status.equals("PAID")) {     // compares characters
                paidByContent++;
            }
        }
        System.out.println("paid orders counted with == : " + paidByReference);
        System.out.println("paid orders counted with equals: " + paidByContent);

        System.out.println();
        System.out.println("--- 2. Map lookups use equals ---");
        String countryFromFile = CSV_ROWS.get(1).split(",")[1];   // "US", built at runtime
        System.out.println("same object as the key literal? " + (countryFromFile == "US"));
        System.out.println("lookup still works: " + COUNTRY_NAMES.get(countryFromFile));

        System.out.println();
        System.out.println("--- 3. Null-safe comparisons ---");
        String couponCode = null;                                  // optional column, left empty
        System.out.println("\"SAVE10\".equals(null)     : " + "SAVE10".equals(couponCode));
        System.out.println("Objects.equals(null, null): " + Objects.equals(couponCode, null));
        try {
            System.out.println(couponCode.equals("SAVE10"));
        } catch (NullPointerException e) {
            System.out.println("couponCode.equals(...)    : NullPointerException");
        }

        System.out.println();
        System.out.println("--- 4. intern() to share repeated values ---");
        System.out.println("distinct country objects, plain : " + distinctObjects(countryColumn(false)));
        System.out.println("distinct country objects, intern: " + distinctObjects(countryColumn(true)));
    }

    static List<String> countryColumn(boolean intern) {
        return CSV_ROWS.stream()
                .map(row -> row.split(",")[1])
                .map(country -> intern ? country.intern() : country)
                .toList();
    }

    // Counts different OBJECTS, not different texts: IdentityHashMap compares with ==.
    static int distinctObjects(List<String> values) {
        Set<String> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        seen.addAll(values);
        return seen.size();
    }
}

/* Expected output:
--- 1. == vs equals on imported data ---
paid orders counted with == : 0
paid orders counted with equals: 3

--- 2. Map lookups use equals ---
same object as the key literal? false
lookup still works: United States

--- 3. Null-safe comparisons ---
"SAVE10".equals(null)     : false
Objects.equals(null, null): true
couponCode.equals(...)    : NullPointerException

--- 4. intern() to share repeated values ---
distinct country objects, plain : 5
distinct country objects, intern: 2
*/
