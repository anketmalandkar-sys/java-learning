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
        if (id == null) {
            return false;
        }
        // matches() must match the whole string, so no ^ or $ needed
        return id.matches("[A-Z]{3}-\\d{4}");
    }

    static int intField(String line, String name) {
        String rawValue = extractFieldRaw(line, name);
        if (rawValue == null) {
            return -1;
        }

        try {
            return Integer.parseInt(rawValue);
        } catch (NumberFormatException e) {
            return -1; // Not a valid whole number (e.g., decimals like 19.99)
        }
    }

    static double doubleField(String line, String name) {
        String rawValue = extractFieldRaw(line, name);
        if (rawValue == null) {
            return Double.NaN;
        }

        try {
            return Double.parseDouble(rawValue);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    static boolean isImage(String line) {
        String fileName = extractFieldRaw(line, "file");
        if (fileName == null) {
            return false;
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return false; // No extension found
        }

        int extStart = lastDotIndex + 1;
        return hasExtension(fileName, extStart, "png")
                || hasExtension(fileName, extStart, "jpg")
                || hasExtension(fileName, extStart, "gif");
    }

    // Compares the region of fileName from start to the end against ext, ignoring case, without copying it
    private static boolean hasExtension(String fileName, int start, String ext) {
        return fileName.length() - start == ext.length()   // "pngx" isn't "png", "" isn't "png"
                && fileName.regionMatches(true, start, ext, 0, ext.length());
    }

    // Helper method to extract raw field value based on "|name=" up to next '|' or end
    private static String extractFieldRaw(String line, String name) {
        if (line == null || name == null) {
            return null;
        }

        // Every field follows a '|', so searching for "|name=" stops "maxqty=" matching "qty"
        String searchKey = "|" + name + "=";
        int startIndex = line.indexOf(searchKey);
        if (startIndex == -1) {
            return null; // Field is missing
        }

        startIndex += searchKey.length();
        int endIndex = line.indexOf('|', startIndex);

        if (endIndex == -1) {
            return line.substring(startIndex); // Field goes until the end of the line
        } else {
            return line.substring(startIndex, endIndex);
        }
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
