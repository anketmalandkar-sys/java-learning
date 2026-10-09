import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): a shop receipt.
 *
 * TASK
 *   Part A. receipt(shop, items) returns the full receipt as ONE string, built with a
 *   StringBuilder, exactly in this layout (every line ends with "\n", including the last):
 *
 *                 Chai Point             <- the shop name, centred in 30 characters (see below)
 *       ------------------------------  <- 30 dashes
 *       Masala chai      2 x    40.00
 *       Samosa           3 x    25.00
 *       ------------------------------
 *       Subtotal                155.00
 *       GST 5%                    7.75
 *       TOTAL                   162.75
 *
 *     Item lines:  name left-aligned in 15 chars, a space, quantity right-aligned in 2,
 *                  " x ", price right-aligned in 8 with 2 decimals.     -> format "%-15s %2d x %8.2f"
 *     Total lines: label left-aligned in 20 chars, amount right-aligned in 10, 2 decimals.
 *                                                                        -> format "%-20s%10.2f"
 *     Centring: pad with (30 - name length) / 2 spaces on the left, nothing on the right.
 *     GST is 5% of the subtotal.
 *
 *   Part B. summary(items) returns one line made with a stream and Collectors.joining:
 *       "2 items: Masala chai x2, Samosa x3"
 *     For an empty list: "0 items: none". (Hint: joining doesn't do that for you.)
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   (the receipt, printed)
 *   receipt: PASS
 *   summary: PASS
 *   ALL PASS
 *
 * HINTS
 *   - sb.append(String.format(...)).append('\n') per line. Or use "%n"? Careful: %n is "\r\n"
 *     on Windows, and main compares against "\n".
 *   - " ".repeat(n) for the centring padding.
 *
 * Run: java exercises/Exercise2_ReceiptPrinter.java
 */
public class Exercise2_ReceiptPrinter {

    record Item(String name, int quantity, double price) {}

    static String receipt(String shop, List<Item> items) {
        // TODO
        return null;
    }

    static String summary(List<Item> items) {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        List<Item> order = List.of(new Item("Masala chai", 2, 40.0), new Item("Samosa", 3, 25.0));

        String expectedReceipt = """
                          Chai Point
                ------------------------------
                Masala chai      2 x    40.00
                Samosa           3 x    25.00
                ------------------------------
                Subtotal                155.00
                GST 5%                    7.75
                TOTAL                   162.75
                """;

        boolean allPass = true;

        allPass &= check("receipt", () -> {
            String printed = receipt("Chai Point", order);
            if (printed != null) {
                System.out.print(printed);
            }
            return printed.equals(expectedReceipt);
        });

        allPass &= check("summary", () ->
                summary(order).equals("2 items: Masala chai x2, Samosa x3")
                        && summary(List.of()).equals("0 items: none"));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-8s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-8s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
