import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Example 2: building an invoice email from order data.
 *   1. A StringBuilder with a known capacity, filled in a loop with formatted lines.
 *   2. String.join / StringJoiner / Collectors.joining: separators only BETWEEN items.
 *   3. setEmptyValue for "nothing to list".
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    record Item(String name, int quantity, double price) {
        double lineTotal() { return quantity * price; }
    }

    static String invoice(String customer, List<Item> items) {
        // About 40 characters per line: sizing the buffer up front avoids regrowing it.
        StringBuilder sb = new StringBuilder(40 * (items.size() + 6));
        sb.append("Invoice for ").append(customer).append('\n');
        sb.append("=".repeat(36)).append('\n');
        double total = 0;
        for (Item item : items) {
            // format builds one line; append adds it without copying what came before
            sb.append(String.format("%-18s %3d x %7.2f%n", item.name(), item.quantity(), item.price()));
            total += item.lineTotal();
        }
        sb.append("-".repeat(36)).append('\n');
        sb.append(String.format("%-24s %11.2f%n", "TOTAL", total));
        return sb.toString();
    }

    public static void main(String[] args) {
        List<Item> items = List.of(
                new Item("Keyboard", 1, 1499.00),
                new Item("USB cable", 3, 199.50),
                new Item("Mouse pad", 2, 249.00));

        System.out.println("--- 1. StringBuilder in a loop ---");
        System.out.print(invoice("Ravi Kumar", items));

        System.out.println();
        System.out.println("--- 2. Joining with separators ---");
        List<String> names = items.stream().map(Item::name).toList();
        System.out.println("String.join:        " + String.join(", ", names));

        StringJoiner tags = new StringJoiner(" | ", "{ ", " }");
        tags.add("paid").add("gift").add("express");
        System.out.println("StringJoiner:       " + tags);

        String summary = items.stream()
                .filter(item -> item.lineTotal() > 500)
                .map(item -> item.name() + " (" + item.quantity() + ")")
                .collect(Collectors.joining(", ", "Big lines: ", "."));
        System.out.println("Collectors.joining: " + summary);

        System.out.println();
        System.out.println("--- 3. When there's nothing to join ---");
        StringJoiner backorders = new StringJoiner(", ", "Backordered: ", "");
        backorders.setEmptyValue("Nothing on backorder");
        System.out.println(backorders);
        backorders.add("Mouse pad");
        System.out.println(backorders);
    }
}

/* Expected output:
--- 1. StringBuilder in a loop ---
Invoice for Ravi Kumar
====================================
Keyboard             1 x 1499.00
USB cable            3 x  199.50
Mouse pad            2 x  249.00
------------------------------------
TOTAL                        2595.50

--- 2. Joining with separators ---
String.join:        Keyboard, USB cable, Mouse pad
StringJoiner:       { paid | gift | express }
Collectors.joining: Big lines: Keyboard (1), USB cable (3).

--- 3. When there's nothing to join ---
Nothing on backorder
Backordered: Mouse pad
*/
