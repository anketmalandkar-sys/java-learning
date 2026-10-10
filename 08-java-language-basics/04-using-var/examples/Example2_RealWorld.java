import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example 2: grouping orders by customer, before and after var.
 *
 * The nested generic type Map<String, List<Integer>> is long. With var, the declaration
 * says it once, on the right, and the variable names carry the meaning.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    public static void main(String[] args) {
        String[] customers = {"Asha", "Ravi", "Asha", "Meera", "Ravi", "Asha"};
        int[] amounts = {250, 120, 90, 400, 60, 30};

        // Without var: the type is written twice.
        Map<String, List<Integer>> explicitOrders = new HashMap<String, List<Integer>>();
        explicitOrders.put("demo", new ArrayList<Integer>());

        // With var: same type, written once.
        var ordersByCustomer = new HashMap<String, List<Integer>>();
        for (var i = 0; i < customers.length; i++) {
            var customer = customers[i];
            ordersByCustomer.computeIfAbsent(customer, key -> new ArrayList<>()).add(amounts[i]);
        }

        for (var name : List.of("Asha", "Meera", "Ravi")) {
            var orders = ordersByCustomer.get(name);
            // 0.0, not 0: we want a double average, so the literal must be a double.
            var total = 0.0;
            for (var amount : orders) {
                total += amount;
            }
            var average = total / orders.size();
            System.out.printf("%-5s orders=%s total=%.0f average=%.2f%n", name, orders, total, average);
        }

        // Where var would HURT readability, write the type. The reader shouldn't need
        // to look up what largestOrder() returns.
        int largest = largestOrder(amounts);
        System.out.println("Largest single order: " + largest);
    }

    static int largestOrder(int[] amounts) {
        var max = amounts[0];
        for (var amount : amounts) {
            max = Math.max(max, amount);
        }
        return max;
    }
}

/* Expected output:
Asha  orders=[250, 90, 30] total=370 average=123.33
Meera orders=[400] total=400 average=400.00
Ravi  orders=[120, 60] total=180 average=90.00
Largest single order: 400
*/
