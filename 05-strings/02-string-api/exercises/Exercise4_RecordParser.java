/**
 * Exercise 4 (Medium): parse and validate a fixed-format record.
 *
 * TASK
 *   A warehouse scanner sends lines like:
 *       "ORD-0042|qty=3|price=19.99|file=label.v2.PNG"
 *
 *   1. isValidOrderId(id): true if id is exactly 3 uppercase letters, a dash, and 4 digits.
 *      Use String.matches.                       "ORD-0042" -> true, "ord-0042" -> false
 *
 *   2. intField(line, name): find "name=" in the line and parse the whole number after it, up to
 *      the next '|' or the end. Return -1 if the field is missing or isn't a whole number.
 *        intField(line, "qty") -> 3,   intField(line, "price") -> -1 (19.99 isn't whole)
 *
 *   3. doubleField(line, name): same, but parse a double. Return Double.NaN if missing or invalid.
 *        doubleField(line, "price") -> 19.99
 *
 *   4. isImage(line): true if the file field's EXTENSION (after the LAST dot) is png, jpg or gif,
 *      in any case. Use lastIndexOf, and regionMatches with ignoreCase = true (no toLowerCase,
 *      no equalsIgnoreCase).
 *
 * EXPECTED OUTPUT
 *   isValidOrderId: PASS
 *   intField:       PASS
 *   doubleField:    PASS
 *   isImage:        PASS
 *   ALL PASS
 *
 * HINTS
 *   - indexOf(name + "=") finds the field; indexOf('|', start) finds its end (or -1 = end of line).
 *   - Integer.parseInt throws NumberFormatException: catch it.
 *   - For the extension check, compare the region of the line starting after the last dot,
 *     and make sure the lengths match too ("pngx" isn't "png").
 *
 * Run: java exercises/Exercise4_RecordParser.java
 */
public class Exercise4_RecordParser {

    static boolean isValidOrderId(String id) {
        return false; // TODO
    }

    static int intField(String line, String name) {
        return 0; // TODO
    }

    static double doubleField(String line, String name) {
        return 0; // TODO
    }

    static boolean isImage(String line) {
        return false; // TODO
    }

    public static void main(String[] args) {
        String line = "ORD-0042|qty=3|price=19.99|file=label.v2.PNG";
        String other = "ORD-0043|qty=x|file=notes.txt";
        String noFile = "ORD-0044|qty=12";

        boolean allPass = true;
        allPass &= check("isValidOrderId:", isValidOrderId("ORD-0042") && !isValidOrderId("ord-0042")
                && !isValidOrderId("ORD-42") && !isValidOrderId("ORD-00420") && !isValidOrderId(" ORD-0042"));
        allPass &= check("intField:", intField(line, "qty") == 3 && intField(line, "price") == -1
                && intField(other, "qty") == -1 && intField(noFile, "qty") == 12 && intField(noFile, "price") == -1);
        allPass &= check("doubleField:", doubleField(line, "price") == 19.99 && doubleField(noFile, "qty") == 12.0
                && Double.isNaN(doubleField(other, "price")) && Double.isNaN(doubleField(other, "qty")));
        allPass &= check("isImage:", isImage(line) && !isImage(other) && !isImage(noFile)
                && isImage("A|file=x.jpg") && !isImage("A|file=x.pngx") && !isImage("A|file=png"));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
