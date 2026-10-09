import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Example 2: a bound on your own interface, in a small store app.
 *   1. Repository<T extends Identifiable>: the class can call getId() on every T.
 *   2. The same repository class, reused for products and customers.
 *   3. A generic "top N" helper with <T extends Comparable<? super T>>,
 *      which also accepts subclasses of a Comparable class (PremiumCustomer).
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    interface Identifiable {
        String getId();
    }

    // Without the bound, Repository couldn't call getId(): a plain T only has Object's methods.
    static class Repository<T extends Identifiable> {
        private final Map<String, T> byId = new HashMap<>();

        void save(T item) {
            byId.put(item.getId(), item);
        }

        Optional<T> findById(String id) {
            return Optional.ofNullable(byId.get(id));
        }

        int count() {
            return byId.size();
        }
    }

    record Product(String id, String name, double price) implements Identifiable {
        @Override public String getId() { return id; }
    }

    static class Customer implements Identifiable, Comparable<Customer> {
        private final String id;
        private final String name;
        private final int points;

        Customer(String id, String name, int points) {
            this.id = id;
            this.name = name;
            this.points = points;
        }

        @Override public String getId() { return id; }
        String name() { return name; }

        // More points = "greater", so the top customers sort to the end.
        @Override
        public int compareTo(Customer other) {
            return Integer.compare(points, other.points);
        }

        @Override
        public String toString() { return name + "(" + points + ")"; }
    }

    // A PremiumCustomer is a Comparable<Customer>, NOT a Comparable<PremiumCustomer>.
    static class PremiumCustomer extends Customer {
        PremiumCustomer(String id, String name, int points) {
            super(id, name, points);
        }
    }

    // <T extends Comparable<? super T>>: T can be compared with T or with one of its parents.
    // With <T extends Comparable<T>>, topN(premiumList, 2) would not compile.
    static <T extends Comparable<? super T>> List<T> topN(List<T> items, int n) {
        List<T> sorted = new ArrayList<>(items);
        sorted.sort((a, b) -> b.compareTo(a));   // highest first
        return sorted.subList(0, Math.min(n, sorted.size()));
    }

    public static void main(String[] args) {
        System.out.println("--- 1 & 2. One bounded Repository class, two uses ---");
        Repository<Product> products = new Repository<>();
        products.save(new Product("P1", "Keyboard", 1499.0));
        products.save(new Product("P2", "Mouse", 599.0));

        Repository<Customer> customers = new Repository<>();
        customers.save(new Customer("C1", "Ravi", 120));
        customers.save(new Customer("C2", "Meera", 340));

        System.out.println("products: " + products.count() + ", customers: " + customers.count());
        System.out.println("P2 -> " + products.findById("P2").map(Product::name).orElse("missing"));
        System.out.println("C9 -> " + customers.findById("C9").map(Customer::name).orElse("missing"));
        // Repository<String> names;   // won't compile: String is not Identifiable

        System.out.println();
        System.out.println("--- 3. topN with Comparable<? super T> ---");
        List<Customer> everyone = List.of(
                new Customer("C1", "Ravi", 120),
                new Customer("C2", "Meera", 340),
                new Customer("C3", "Arjun", 210));
        System.out.println("top 2 customers: " + topN(everyone, 2));

        List<PremiumCustomer> premium = List.of(
                new PremiumCustomer("C4", "Kavya", 900),
                new PremiumCustomer("C5", "Sam", 1500),
                new PremiumCustomer("C6", "Priya", 700));
        List<PremiumCustomer> topPremium = topN(premium, 2);   // still a List<PremiumCustomer>
        System.out.println("top 2 premium:   " + topPremium);
        System.out.println("top 2 numbers:   " + topN(List.of(4, 11, 7, 2), 2));
    }
}

/* Expected output:
--- 1 & 2. One bounded Repository class, two uses ---
products: 2, customers: 2
P2 -> Mouse
C9 -> missing

--- 3. topN with Comparable<? super T> ---
top 2 customers: [Meera(340), Arjun(210)]
top 2 premium:   [Sam(1500), Kavya(900)]
top 2 numbers:   [11, 7]
*/
