import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): one repository class for every kind of entity.
 *
 * TASK
 *   Part A. Implement InMemoryRepository<T extends Entity<ID>, ID>. It must work for
 *   customers (String ids) and orders (Long ids) without any changes.
 *
 *     save(entity)          store it under entity.id(). Saving the same id again replaces the old one.
 *     findById(id)          Optional.empty() if there's nothing with that id
 *     findAll()             every entity, in the order first saved. Return a COPY: a caller
 *                           changing the returned list must not change the repository.
 *     deleteById(id)        remove it; return true if something was removed
 *     findWhere(rule)       every entity the rule matches, in order
 *
 *   Part B. Implement the default method findAllById(ids) in the Repository interface,
 *   using only findById. Skip ids that aren't found. Return the matches in the order of 'ids'.
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   save/find:   PASS
 *   replace:     PASS
 *   findAll:     PASS
 *   delete:      PASS
 *   findWhere:   PASS
 *   findAllById: PASS
 *   ALL PASS
 *
 * HINTS
 *   - A LinkedHashMap<ID, T> keeps the first-saved order, even when a value is replaced.
 *   - List.copyOf(map.values()) gives an independent, unmodifiable copy.
 *   - findWhere takes a Predicate<? super T>, so main can pass a Predicate<Entity<?>>,
 *     which works for any repository.
 *
 * Run: java exercises/Exercise2_InMemoryRepository.java
 */
public class Exercise2_InMemoryRepository {

    interface Entity<ID> {
        ID id();
    }

    interface Repository<T, ID> {
        void save(T entity);
        Optional<T> findById(ID id);
        List<T> findAll();
        boolean deleteById(ID id);
        List<T> findWhere(Predicate<? super T> rule);

        default List<T> findAllById(Collection<? extends ID> ids) {
            // TODO (Part B)
            return null;
        }
    }

    static class InMemoryRepository<T extends Entity<ID>, ID> implements Repository<T, ID> {
        // TODO: a field to store the entities

        @Override
        public void save(T entity) {
            // TODO
        }

        @Override
        public Optional<T> findById(ID id) {
            // TODO
            return null;
        }

        @Override
        public List<T> findAll() {
            // TODO
            return null;
        }

        @Override
        public boolean deleteById(ID id) {
            // TODO
            return false;
        }

        @Override
        public List<T> findWhere(Predicate<? super T> rule) {
            // TODO
            return null;
        }
    }

    record Customer(String id, String name, String city) implements Entity<String> {}
    record Order(Long id, String customerId, double total) implements Entity<Long> {}

    public static void main(String[] args) {
        Repository<Customer, String> customers = new InMemoryRepository<>();
        Repository<Order, Long> orders = new InMemoryRepository<>();

        customers.save(new Customer("C1", "Ravi", "Pune"));
        customers.save(new Customer("C2", "Meera", "Goa"));
        customers.save(new Customer("C3", "Arjun", "Pune"));
        orders.save(new Order(101L, "C1", 250.0));
        orders.save(new Order(102L, "C2", 1200.0));
        orders.save(new Order(103L, "C1", 80.0));

        boolean allPass = true;

        allPass &= check("save/find", () ->
                customers.findById("C2").map(Customer::name).orElse("").equals("Meera")
                        && orders.findById(103L).isPresent()
                        && customers.findById("C9").isEmpty());

        allPass &= check("replace", () -> {
            customers.save(new Customer("C1", "Ravi K", "Mumbai"));   // same id: replaces
            List<Customer> all = customers.findAll();
            return all.size() == 3 && all.get(0).name().equals("Ravi K");   // keeps its original position
        });

        allPass &= check("findAll", () -> {
            List<Order> all = orders.findAll();
            try {
                all.clear();   // either refused, or only clears the copy
            } catch (UnsupportedOperationException ignored) {
            }
            return orders.findAll().size() == 3;
        });

        allPass &= check("delete", () ->
                orders.deleteById(103L) && !orders.deleteById(103L)
                        && orders.findAll().size() == 2);

        allPass &= check("findWhere", () -> {
            List<Customer> inPune = customers.findWhere(c -> c.city().equals("Pune"));
            List<Order> big = orders.findWhere(o -> o.total() > 1000);
            Predicate<Entity<?>> hasLongId = e -> e.id() instanceof Long;   // a general rule
            return inPune.size() == 1 && inPune.get(0).name().equals("Arjun")
                    && big.size() == 1 && big.get(0).id() == 102L
                    && orders.findWhere(hasLongId).size() == 2
                    && customers.findWhere(hasLongId).isEmpty();
        });

        allPass &= check("findAllById", () -> {
            List<Customer> picked = customers.findAllById(List.of("C3", "C9", "C2"));
            List<Order> fromSet = orders.findAllById(Set.of(101L));
            return picked.size() == 2
                    && picked.get(0).name().equals("Arjun") && picked.get(1).name().equals("Meera")
                    && fromSet.size() == 1;
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-12s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-12s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
