import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Example 2: one generic Page<T> and two generic helpers, reused for orders and employees.
 *
 * Without generics you'd write OrderPage and EmployeePage (copy-paste), or one Page
 * of Objects with casts everywhere. With Page<T>, the paging logic is written once
 * and the compiler still knows a Page<Order> holds Orders.
 *
 * Uses records (Java 16+) to keep the data classes short.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    record Order(String id, double amount) {
        @Override
        public String toString() {
            return id + "(Rs " + amount + ")";
        }
    }

    record Employee(String name, String department) {
        @Override
        public String toString() {
            return name;
        }
    }

    // One page of results, for any type of item.
    static class Page<T> {
        private final List<T> items;
        private final int pageNumber;   // starts at 1
        private final int totalPages;

        Page(List<T> items, int pageNumber, int totalPages) {
            this.items = items;
            this.pageNumber = pageNumber;
            this.totalPages = totalPages;
        }

        List<T> getItems() { return items; }
        boolean hasNext() { return pageNumber < totalPages; }

        @Override
        public String toString() {
            return "Page " + pageNumber + "/" + totalPages + " " + items;
        }
    }

    // Generic method: works for a list of anything, and returns a Page of the same type.
    static <T> Page<T> paginate(List<T> all, int pageNumber, int pageSize) {
        int totalPages = (all.size() + pageSize - 1) / pageSize;   // round up
        int from = (pageNumber - 1) * pageSize;
        int to = Math.min(from + pageSize, all.size());
        return new Page<>(all.subList(from, to), pageNumber, totalPages);
    }

    // Optional<T> is itself generic: "maybe a T". Better than returning null.
    static <T> Optional<T> findFirst(List<T> items, Predicate<T> rule) {
        for (T item : items) {
            if (rule.test(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("O-1", 1200),
                new Order("O-2", 450),
                new Order("O-3", 8900),
                new Order("O-4", 300),
                new Order("O-5", 6100)
        );

        List<Employee> employees = List.of(
                new Employee("Asha", "Engineering"),
                new Employee("Ravi", "Sales"),
                new Employee("Meera", "Engineering"),
                new Employee("John", "Finance")
        );

        System.out.println("--- Same paging code, two types ---");
        Page<Order> page = paginate(orders, 1, 2);
        System.out.println("Orders:    " + page);
        while (page.hasNext()) {
            page = paginate(orders, page.pageNumber + 1, 2);
            System.out.println("Orders:    " + page);
        }

        Page<Employee> staff = paginate(employees, 2, 3);
        System.out.println("Employees: " + staff);

        // The compiler knows the items are Orders, so no cast is needed to read amount().
        double firstPageTotal = 0;
        for (Order order : paginate(orders, 1, 2).getItems()) {
            firstPageTotal += order.amount();
        }
        System.out.println("Total on order page 1: Rs " + firstPageTotal);

        System.out.println();
        System.out.println("--- Same search code, two types ---");
        Optional<Order> bigOrder = findFirst(orders, order -> order.amount() > 5000);
        System.out.println("First order over Rs 5000: " + bigOrder.orElse(null));

        Optional<Employee> engineer = findFirst(employees, e -> e.department().equals("Engineering"));
        System.out.println("First engineer:           " + engineer.orElse(null));

        Optional<Employee> hr = findFirst(employees, e -> e.department().equals("HR"));
        System.out.println("Anyone in HR?             " + (hr.isPresent() ? hr.get() : "no one"));
    }
}

/* Expected output:
--- Same paging code, two types ---
Orders:    Page 1/3 [O-1(Rs 1200.0), O-2(Rs 450.0)]
Orders:    Page 2/3 [O-3(Rs 8900.0), O-4(Rs 300.0)]
Orders:    Page 3/3 [O-5(Rs 6100.0)]
Employees: Page 2/2 [John]
Total on order page 1: Rs 1650.0

--- Same search code, two types ---
First order over Rs 5000: O-3(Rs 8900.0)
First engineer:           Asha
Anyone in HR?             no one
*/
