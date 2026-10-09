import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Example 2: PECS in an order system.
 *   1. copy(source, target): a producer and a consumer in one method.
 *   2. Functional parameters: Predicate<? super T>, Consumer<? super T>, Comparator<? super T>
 *      let one general rule or handler serve many types.
 *   3. Wildcard capture: a List<?> method that needs a helper to set elements.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static class Order {
        final String id;
        final double total;

        Order(String id, double total) {
            this.id = id;
            this.total = total;
        }

        @Override
        public String toString() { return id + "(" + total + ")"; }
    }

    static class ExpressOrder extends Order {
        ExpressOrder(String id, double total) {
            super(id, total);
        }

        @Override
        public String toString() { return "express " + super.toString(); }
    }

    // source produces Ts (extends), target consumes Ts (super).
    static <T> void copy(List<? extends T> source, List<? super T> target) {
        for (T item : source) {
            target.add(item);
        }
    }

    // rule and handler consume Ts, so both are "super".
    static <T> int process(List<? extends T> items, Predicate<? super T> rule, Consumer<? super T> handler) {
        int handled = 0;
        for (T item : items) {
            if (rule.test(item)) {
                handler.accept(item);
                handled++;
            }
        }
        return handled;
    }

    // The public signature stays simple. The helper captures ? as T.
    static void moveLastToFront(List<?> list) {
        moveLastToFrontHelper(list);
    }

    private static <T> void moveLastToFrontHelper(List<T> list) {
        T last = list.remove(list.size() - 1);   // T, not Object, so...
        list.add(0, last);                       // ...it can go back into the same list
    }

    public static void main(String[] args) {
        List<ExpressOrder> express = List.of(new ExpressOrder("E1", 900.0), new ExpressOrder("E2", 150.0));
        List<Order> regular = List.of(new Order("R1", 300.0), new Order("R2", 50.0));

        System.out.println("--- 1. copy: producer extends, consumer super ---");
        List<Order> allOrders = new ArrayList<>();
        copy(express, allOrders);   // T = ExpressOrder: express produces them, allOrders accepts them
        copy(regular, allOrders);   // T = Order
        System.out.println("all orders: " + allOrders);

        List<Object> auditLog = new ArrayList<>();
        copy(express, auditLog);    // an Object list consumes anything
        System.out.println("audit log:  " + auditLog);

        System.out.println();
        System.out.println("--- 2. General rules work for specific types ---");
        Predicate<Order> bigOrder = order -> order.total > 200;     // written once, for Order
        Predicate<Object> notNull = item -> item != null;           // written for Object
        List<String> printed = new ArrayList<>();
        Consumer<Object> printer = item -> printed.add("printed " + item);

        int bigExpress = process(express, bigOrder, printer);       // Predicate<Order> on ExpressOrders
        int anyRegular = process(regular, notNull, printer);        // Predicate<Object> on Orders
        System.out.println("big express: " + bigExpress + ", regular: " + anyRegular);
        printed.forEach(System.out::println);

        Comparator<Order> byTotal = Comparator.comparingDouble(order -> order.total);
        List<ExpressOrder> sortedExpress = new ArrayList<>(express);
        sortedExpress.sort(byTotal);   // sort takes Comparator<? super ExpressOrder>, so an Order comparator is fine
        System.out.println("express by total: " + sortedExpress);

        System.out.println();
        System.out.println("--- 3. Wildcard capture with a helper ---");
        List<String> queue = new ArrayList<>(List.of("pack", "label", "URGENT: refund"));
        List<Integer> ids = new ArrayList<>(List.of(101, 102, 103));
        moveLastToFront(queue);
        moveLastToFront(ids);
        System.out.println("queue: " + queue);
        System.out.println("ids:   " + ids);
    }
}

/* Expected output:
--- 1. copy: producer extends, consumer super ---
all orders: [express E1(900.0), express E2(150.0), R1(300.0), R2(50.0)]
audit log:  [express E1(900.0), express E2(150.0)]

--- 2. General rules work for specific types ---
big express: 1, regular: 2
printed express E1(900.0)
printed R1(300.0)
printed R2(50.0)
express by total: [express E2(150.0), express E1(900.0)]

--- 3. Wildcard capture with a helper ---
queue: [URGENT: refund, pack, label]
ids:   [103, 101, 102]
*/
