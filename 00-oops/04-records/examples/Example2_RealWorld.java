import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Example 2: records as data carriers in an order-processing service.
 *
 *   - OrderLine and Order are immutable values, validated on creation.
 *   - Order copies its list defensively, so callers can't change it afterwards.
 *   - A LOCAL record holds an intermediate result inside a method.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    record OrderLine(String sku, int qty, long unitPaise) {
        OrderLine {
            if (qty <= 0) {
                throw new IllegalArgumentException("qty must be positive: " + qty);
            }
            sku = sku.toUpperCase();
        }

        long totalPaise() {
            return qty * unitPaise;
        }
    }

    record Order(String id, String city, List<OrderLine> lines) {
        Order {
            lines = List.copyOf(lines);   // unmodifiable copy: later changes to the caller's list don't leak in
        }

        long totalPaise() {
            long sum = 0;
            for (OrderLine line : lines) {
                sum += line.totalPaise();
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        List<OrderLine> draft = new ArrayList<>();
        draft.add(new OrderLine("pen", 10, 1200));
        draft.add(new OrderLine("book", 2, 45000));
        Order first = new Order("A1", "Pune", draft);

        draft.add(new OrderLine("bag", 1, 99900));   // changes the caller's list only
        System.out.println(first.id() + " has " + first.lines().size() + " lines, total Rs " + first.totalPaise() / 100);

        try {
            first.lines().add(new OrderLine("ink", 1, 100));
        } catch (UnsupportedOperationException e) {
            System.out.println("lines() is unmodifiable");
        }

        List<Order> orders = List.of(
                first,
                new Order("A2", "Mumbai", List.of(new OrderLine("ink", 3, 5000))),
                new Order("A3", "Pune", List.of(new OrderLine("bag", 1, 99900))));

        System.out.println("Revenue by city:");
        for (CitySummary s : summarise(orders)) {
            System.out.println("  " + s.city() + ": " + s.orders() + " order(s), Rs " + s.revenuePaise() / 100);
        }
    }

    record CitySummary(String city, long orders, long revenuePaise) { }

    static List<CitySummary> summarise(List<Order> orders) {
        // A local record: only this method needs this shape.
        record CityTotal(String city, long paise) { }

        List<CityTotal> totals = new ArrayList<>();
        for (Order o : orders) {
            totals.add(new CityTotal(o.city(), o.totalPaise()));
        }

        Map<String, List<CityTotal>> byCity = totals.stream().collect(Collectors.groupingBy(CityTotal::city));
        List<CitySummary> result = new ArrayList<>();
        for (Map.Entry<String, List<CityTotal>> e : byCity.entrySet()) {
            long revenue = e.getValue().stream().mapToLong(CityTotal::paise).sum();
            result.add(new CitySummary(e.getKey(), e.getValue().size(), revenue));
        }
        result.sort(Comparator.comparingLong(CitySummary::revenuePaise).reversed());
        return result;
    }
}

/* Expected output:
A1 has 2 lines, total Rs 1020
lines() is unmodifiable
Revenue by city:
  Pune: 2 order(s), Rs 2019
  Mumbai: 1 order(s), Rs 150
*/
