import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exercise 2 (Medium): handle exceptions at the right level.
 *
 * TASK
 *   An order pipeline has three layers:
 *       importOrders (top)  ->  importOrder  ->  reserveStock (bottom)
 *
 *   reserveStock throws a checked OutOfStockException. Right now reserveStock catches it ITSELF,
 *   prints a message and carries on, so the order is marked as imported anyway. The business
 *   rule is:
 *     - An order with an out-of-stock item must NOT be imported, and its id must be reported in
 *       ImportResult.rejected, with the exception's message.
 *     - The other orders in the batch must still be imported.
 *     - A quantity that isn't a number is a broken file: that's not recoverable, and the whole
 *       import should stop with the NumberFormatException (don't catch it).
 *
 *   Fix it:
 *     1. Remove the try/catch from reserveStock and declare `throws OutOfStockException`.
 *     2. importOrder can't do anything useful about it either: declare it too.
 *     3. importOrders handles it per order, so one bad order doesn't stop the batch.
 *
 * EXPECTED OUTPUT
 *   imported:    PASS
 *   rejected:    PASS
 *   stock kept:  PASS
 *   bad file:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - The compiler tells you where a `throws` is missing once you remove the catch.
 *   - Make sure a rejected order doesn't reserve some of its items before failing on a later one:
 *     check all lines first, then reserve.
 *
 * Run: java exercises/Exercise2_HandleAtTheRightLevel.java
 */
public class Exercise2_HandleAtTheRightLevel {

    static class OutOfStockException extends Exception {
        OutOfStockException(String sku, int wanted, int available) {
            super(sku + ": wanted " + wanted + ", only " + available + " left");
        }
    }

    record Order(String id, List<String> lines) { }          // lines like "PEN:3"

    record ImportResult(List<String> imported, List<String> rejected) { }

    static Map<String, Integer> stock;

    // Bottom layer.
    static void reserveStock(String sku, int qty) {
        try {
            int available = stock.getOrDefault(sku, 0);
            if (qty > available) {
                throw new OutOfStockException(sku, qty, available);
            }
            stock.put(sku, available - qty);
        } catch (OutOfStockException e) {
            System.out.println("  (reserveStock swallowed: " + e.getMessage() + ")");   // TODO: wrong level
        }
    }

    // Middle layer.
    static void importOrder(Order order) {
        for (String line : order.lines()) {
            String[] parts = line.split(":");
            reserveStock(parts[0], Integer.parseInt(parts[1]));
        }
    }

    // Top layer.
    static ImportResult importOrders(List<Order> orders) {
        List<String> imported = new ArrayList<>();
        List<String> rejected = new ArrayList<>();
        for (Order order : orders) {
            importOrder(order);
            imported.add(order.id());
        }
        return new ImportResult(imported, rejected);
    }

    public static void main(String[] args) {
        boolean allPass = true;

        stock = new java.util.HashMap<>(Map.of("PEN", 10, "INK", 2));
        ImportResult result = importOrders(List.of(
                new Order("A1", List.of("PEN:3")),
                new Order("A2", List.of("PEN:2", "INK:5")),
                new Order("A3", List.of("INK:2"))));

        allPass &= check("imported:", result.imported().equals(List.of("A1", "A3")));
        allPass &= check("rejected:", result.rejected().equals(List.of("A2: INK: wanted 5, only 2 left")));
        allPass &= check("stock kept:", stock.equals(Map.of("PEN", 7, "INK", 0)));

        stock = new java.util.HashMap<>(Map.of("PEN", 10));
        boolean stopped;
        try {
            importOrders(List.of(new Order("B1", List.of("PEN:x"))));
            stopped = false;
        } catch (NumberFormatException e) {
            stopped = true;
        }
        allPass &= check("bad file:", stopped);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
