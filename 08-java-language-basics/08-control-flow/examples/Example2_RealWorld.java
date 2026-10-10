/**
 * Example 2: a warehouse order picker.
 *
 *   - for-each over orders, continue to skip cancelled ones
 *   - while to pick items until the order is filled or stock runs out
 *   - do-while for a delivery retry that must be attempted at least once
 *   - break to stop when the truck is full
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    record Order(String id, int quantity, boolean cancelled) { }

    public static void main(String[] args) {
        Order[] orders = {
            new Order("A1", 3, false),
            new Order("A2", 5, true),
            new Order("A3", 4, false),
            new Order("A4", 6, false),
        };
        int stock = 8;
        int truckCapacity = 7;
        int loaded = 0;

        for (Order order : orders) {
            if (order.cancelled()) {
                System.out.println(order.id() + ": cancelled, skipped");
                continue;
            }
            if (loaded >= truckCapacity) {
                System.out.println(order.id() + ": truck full, stopping");
                break;
            }

            int picked = 0;
            while (picked < order.quantity() && stock > 0 && loaded < truckCapacity) {
                picked++;
                stock--;
                loaded++;
            }
            System.out.println(order.id() + ": picked " + picked + "/" + order.quantity()
                    + " (stock " + stock + ", truck " + loaded + "/" + truckCapacity + ")");
        }

        // Delivery: try at least once, retry up to 3 attempts. Attempts fail on odd numbers here.
        int attempt = 0;
        boolean delivered;
        do {
            attempt++;
            delivered = attempt % 2 == 0;
            System.out.println("delivery attempt " + attempt + ": " + (delivered ? "ok" : "failed"));
        } while (!delivered && attempt < 3);
    }
}

/* Expected output:
A1: picked 3/3 (stock 5, truck 3/7)
A2: cancelled, skipped
A3: picked 4/4 (stock 1, truck 7/7)
A4: truck full, stopping
delivery attempt 1: failed
delivery attempt 2: ok
*/
