import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Example 2: a Repository<T, ID> and a Result<T> in a small shop backend.
 *   1. One InMemoryRepository class storing two kinds of entity with different id types.
 *   2. A sealed Result<T> (Java 17): a value or an error message, with map().
 *   3. Putting them together: a lookup that returns a Result instead of null or an exception.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    // ---------- Repository ----------

    interface Entity<ID> {
        ID id();
    }

    interface Repository<T, ID> {
        void save(T entity);
        Optional<T> findById(ID id);
        List<T> findAll();

        default boolean existsById(ID id) {   // free for every implementation
            return findById(id).isPresent();
        }
    }

    // T must expose its id, so the bound is Entity<ID>.
    static class InMemoryRepository<T extends Entity<ID>, ID> implements Repository<T, ID> {
        private final Map<ID, T> store = new LinkedHashMap<>();

        @Override
        public void save(T entity) {
            store.put(entity.id(), entity);
        }

        @Override
        public Optional<T> findById(ID id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<T> findAll() {
            return List.copyOf(store.values());   // a copy: callers can't change the store
        }
    }

    record Customer(String id, String name) implements Entity<String> {}
    record Order(Long id, String customerId, double total) implements Entity<Long> {}

    // ---------- Result ----------

    sealed interface Result<T> permits Success, Failure {
        static <T> Result<T> success(T value) { return new Success<>(value); }
        static <T> Result<T> failure(String error) { return new Failure<>(error); }

        // The function consumes a T and produces an R: PECS.
        <R> Result<R> map(Function<? super T, ? extends R> mapper);

        T getOrElse(T fallback);
    }

    record Success<T>(T value) implements Result<T> {
        public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
            return Result.success(mapper.apply(value));
        }
        public T getOrElse(T fallback) { return value; }
    }

    record Failure<T>(String error) implements Result<T> {
        public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
            return Result.failure(error);   // nothing to map: pass the error along
        }
        public T getOrElse(T fallback) { return fallback; }
    }

    // ---------- Using both ----------

    static Result<Order> findOrder(Repository<Order, Long> orders, long id) {
        return orders.findById(id)
                .map(Result::success)
                .orElseGet(() -> Result.failure("no order #" + id));
    }

    static String describe(Result<?> result) {
        // Pattern matching in switch (Java 21+). Sealed, so the compiler knows these are the only two cases.
        return switch (result) {
            case Success<?> s -> "OK: " + s.value();
            case Failure<?> f -> "ERROR: " + f.error();
        };
    }

    public static void main(String[] args) {
        System.out.println("--- 1. One repository class, two entity types ---");
        Repository<Customer, String> customers = new InMemoryRepository<>();
        customers.save(new Customer("C1", "Ravi"));
        customers.save(new Customer("C2", "Meera"));

        Repository<Order, Long> orders = new InMemoryRepository<>();
        orders.save(new Order(101L, "C1", 250.0));
        orders.save(new Order(102L, "C2", 1200.0));

        System.out.println("customers: " + customers.findAll());
        System.out.println("order 102 exists? " + orders.existsById(102L));
        System.out.println("order 999 exists? " + orders.existsById(999L));
        // orders.findById("102");   // won't compile: Order ids are Long

        System.out.println();
        System.out.println("--- 2. Result<T> and map ---");
        Result<Integer> parsed = Result.success(42);
        Result<String> label = parsed.map(n -> "qty " + n);   // Integer -> String
        Result<Integer> broken = Result.failure("not a number: 'abc'");
        System.out.println(describe(label));
        System.out.println(describe(broken.map(n -> n * 2)));  // map skipped: still the same failure
        System.out.println("broken or 0: " + broken.getOrElse(0));

        System.out.println();
        System.out.println("--- 3. Lookups that return a Result ---");
        for (long id : new long[] {101L, 999L}) {
            Result<String> customerName = findOrder(orders, id)
                    .map(Order::customerId)
                    .map(cid -> customers.findById(cid).map(Customer::name).orElse("?"));
            System.out.println("order " + id + " -> " + describe(customerName));
        }
    }
}

/* Expected output:
--- 1. One repository class, two entity types ---
customers: [Customer[id=C1, name=Ravi], Customer[id=C2, name=Meera]]
order 102 exists? true
order 999 exists? false

--- 2. Result<T> and map ---
OK: qty 42
ERROR: not a number: 'abc'
broken or 0: 0

--- 3. Lookups that return a Result ---
order 101 -> OK: Ravi
order 999 -> ERROR: no order #999
*/
