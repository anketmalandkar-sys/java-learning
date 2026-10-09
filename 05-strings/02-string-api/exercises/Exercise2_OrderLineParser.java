import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): parse messy order lines and print an aligned table.
 *
 * TASK
 *   An old system exports orders as pipe-separated text. Each line has 4 fields:
 *       id | customer | amount | note
 *   The export is messy: random spaces around fields, blank lines, a header line, and an
 *   EMPTY note at the end of some lines (the line simply ends with "|").
 *
 *   Part A. parse(line) returns an Order, or null for a line that should be skipped:
 *             - blank lines (including lines with only spaces)
 *             - the header line, which starts with "id" (any case)
 *           For real lines: strip every field, keep an empty note as "", and parse the
 *           amount with Double.parseDouble.
 *
 *   Part B. format(order) returns ONE table row, exactly like this:
 *             id left-aligned in 6 characters, a space,
 *             customer left-aligned in 12 characters, a space,
 *             amount right-aligned in 10 characters with 2 decimals and thousands separators,
 *             then " " and the note, and no trailing spaces when the note is empty.
 *
 *           "A101   Ravi Kumar    12,499.50 gift wrap"
 *           "A102   Meera            499.00"
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   parse:  PASS
 *   skip:   PASS
 *   format: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Splitting on "|" needs escaping: split("\\|", -1). Why -1? Try without it on "A102|Meera|499|".
 *   - String.format("%-6s %-12s %,10.2f", ...) does the alignment.
 *   - strip() removes the spaces; stripTrailing() helps with the empty-note case.
 *
 * Run: java exercises/Exercise2_OrderLineParser.java
 */
public class Exercise2_OrderLineParser {

    record Order(String id, String customer, double amount, String note) {}

    static Order parse(String line) {
        // TODO
        return null;
    }

    static String format(Order order) {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        List<String> export = List.of(
                "ID | Customer | Amount | Note",
                "  A101 |  Ravi Kumar | 12499.5 | gift wrap ",
                "",
                "A102|Meera|499|",
                "    ",
                " A103 | Arjun Mehta |  1250000 |  call first");

        boolean allPass = true;

        allPass &= check("parse", () -> {
            Order ravi = parse(export.get(1));
            Order meera = parse(export.get(3));
            Order arjun = parse(export.get(5));
            return ravi.equals(new Order("A101", "Ravi Kumar", 12499.5, "gift wrap"))
                    && meera.equals(new Order("A102", "Meera", 499.0, ""))
                    && arjun.equals(new Order("A103", "Arjun Mehta", 1250000.0, "call first"));
        });

        allPass &= check("skip", () ->
                parse(export.get(0)) == null && parse(export.get(2)) == null && parse(export.get(4)) == null
                        && parse(export.get(1)) != null);   // real lines must not be skipped

        allPass &= check("format", () -> {
            String row1 = format(new Order("A101", "Ravi Kumar", 12499.5, "gift wrap"));
            String row2 = format(new Order("A102", "Meera", 499.0, ""));
            String row3 = format(new Order("A103", "Arjun Mehta", 1250000.0, "call first"));
            System.out.println("  [" + row1 + "]");
            System.out.println("  [" + row2 + "]");
            System.out.println("  [" + row3 + "]");
            return row1.equals("A101   Ravi Kumar    12,499.50 gift wrap")
                    && row2.equals("A102   Meera            499.00")
                    && row3.equals("A103   Arjun Mehta  1,250,000.00 call first");
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-7s FAIL (still returning null?)%n", name + ":");
            return false;
        } catch (RuntimeException e) {
            System.out.printf("%-7s FAIL (%s)%n", name + ":", e);
            return false;
        }
        System.out.printf("%-7s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
