import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

/**
 * Example 2: an order import that parses CSV-style text, calculates, and prints an invoice.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    public static void main(String[] args) {
        String[] lines = {
            "Notebook,3,45.50",
            "Pen,10,12.00",
            "Stapler,abc,150.00",     // bad quantity: skipped with a message
            "Backpack,1,1299.99",
        };

        // Fixed symbols so ',' groups and '.' is the decimal point on every machine.
        DecimalFormat money = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        double total = 0;

        System.out.printf("%-10s %4s %10s %10s%n", "Item", "Qty", "Price", "Amount");
        for (String line : lines) {
            String[] parts = line.split(",");
            int qty;
            double price;
            try {
                qty = Integer.parseInt(parts[1]);
                price = Double.parseDouble(parts[2]);
            } catch (NumberFormatException e) {
                System.out.println("  skipped \"" + line + "\": " + e.getMessage());
                continue;
            }
            double amount = qty * price;
            total += amount;
            System.out.printf("%-10s %4d %10s %10s%n", parts[0], qty, money.format(price), money.format(amount));
        }

        System.out.printf("%-10s %4s %10s %10s%n", "TOTAL", "", "", money.format(total));

        // Shipping boxes hold 4 items: ceil tells us how many boxes we need.
        int items = 3 + 10 + 1;
        int boxes = (int) Math.ceil(items / 4.0);
        System.out.println(items + " items need " + boxes + " boxes");

        // A seeded Random gives the same "random" order number on every run: useful in tests.
        Random rng = new Random(2024);
        int orderNumber = 10_000 + rng.nextInt(90_000);
        System.out.println("Order number: " + orderNumber);
    }
}

/* Expected output:
Item        Qty      Price     Amount
Notebook      3      45.50     136.50
Pen          10      12.00     120.00
  skipped "Stapler,abc,150.00": For input string: "abc"
Backpack      1   1,299.99   1,299.99
TOTAL                        1,556.49
14 items need 4 boxes
Order number: 89050
*/
