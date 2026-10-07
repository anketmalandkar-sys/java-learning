import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Example 2: an online shop's order screen.
 *
 * The same list of orders is shown in different ways depending on who is looking:
 *   - the customer sees newest orders first
 *   - the finance team sees biggest orders first
 *   - the warehouse ships by priority, then oldest first
 * Order has no single "natural" order, so it does NOT implement Comparable.
 * Each screen keeps its own Comparator instead.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    enum Priority { EXPRESS, STANDARD }   // enums compare in declaration order: EXPRESS first

    static class Order {
        private final int id;
        private final String customer;
        private final LocalDate placedOn;
        private final double total;
        private final Priority priority;
        private final String couponCode;   // may be null: most orders have no coupon

        Order(int id, String customer, LocalDate placedOn, double total, Priority priority, String couponCode) {
            this.id = id;
            this.customer = customer;
            this.placedOn = placedOn;
            this.total = total;
            this.priority = priority;
            this.couponCode = couponCode;
        }

        int getId() { return id; }
        String getCustomer() { return customer; }
        LocalDate getPlacedOn() { return placedOn; }
        double getTotal() { return total; }
        Priority getPriority() { return priority; }
        String getCouponCode() { return couponCode; }

        @Override
        public String toString() {
            return "#" + id;
        }
    }

    // Named comparators, defined once and reused. Give them names that read like English.
    static final Comparator<Order> NEWEST_FIRST =
            Comparator.comparing(Order::getPlacedOn).reversed();

    static final Comparator<Order> BIGGEST_FIRST =
            Comparator.comparingDouble(Order::getTotal).reversed();

    // Priority ascending (EXPRESS before STANDARD), then oldest first, then id so no two orders tie.
    static final Comparator<Order> SHIPPING_ORDER =
            Comparator.comparing(Order::getPriority)
                    .thenComparing(Order::getPlacedOn)
                    .thenComparingInt(Order::getId);

    // Coupon codes can be null. Without nullsLast this would throw NullPointerException.
    static final Comparator<Order> BY_COUPON =
            Comparator.comparing(Order::getCouponCode, Comparator.nullsLast(Comparator.naturalOrder()));

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order(101, "Asha",  LocalDate.of(2026, 3, 1), 1200.0, Priority.STANDARD, null),
                new Order(102, "Vikram", LocalDate.of(2026, 3, 4),  450.0, Priority.EXPRESS,  "SAVE10"),
                new Order(103, "Neha",  LocalDate.of(2026, 3, 2), 3100.0, Priority.STANDARD, "FESTIVE"),
                new Order(104, "Rohan", LocalDate.of(2026, 3, 2),  899.0, Priority.EXPRESS,  null),
                new Order(105, "Asha",  LocalDate.of(2026, 3, 5),   99.0, Priority.STANDARD, "SAVE10")
        );

        System.out.println("Customer view (newest first): " + sorted(orders, NEWEST_FIRST));
        System.out.println("Finance view (biggest first): " + sorted(orders, BIGGEST_FIRST));
        System.out.println("Warehouse view:               " + sorted(orders, SHIPPING_ORDER));
        System.out.println("By coupon (no coupon last):   " + sorted(orders, BY_COUPON));

        // The same comparator drives a PriorityQueue: poll() always returns the next order to ship.
        PriorityQueue<Order> shippingQueue = new PriorityQueue<>(SHIPPING_ORDER);
        shippingQueue.addAll(orders);
        System.out.print("Shipping now:                 ");
        while (!shippingQueue.isEmpty()) {
            System.out.print(shippingQueue.poll() + " ");
        }
        System.out.println();

        // Largest order per the finance rule. Note: max with BIGGEST_FIRST would give the SMALLEST,
        // because "max" means "last in this ordering".
        Order biggest = orders.stream().max(Comparator.comparingDouble(Order::getTotal)).orElseThrow();
        System.out.println("Biggest single order:         " + biggest + " by " + biggest.getCustomer());
    }

    // Copy before sorting: List.of(...) is immutable, and we don't want one view to affect another.
    static List<Order> sorted(List<Order> orders, Comparator<Order> order) {
        List<Order> copy = new ArrayList<>(orders);
        copy.sort(order);
        return copy;
    }
}

/* Expected output:
Customer view (newest first): [#105, #102, #103, #104, #101]
Finance view (biggest first): [#103, #101, #104, #102, #105]
Warehouse view:               [#104, #102, #101, #103, #105]
By coupon (no coupon last):   [#103, #102, #105, #101, #104]
Shipping now:                 #104 #102 #101 #103 #105
Biggest single order:         #103 by Neha
*/
