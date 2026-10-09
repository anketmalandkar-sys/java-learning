import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Solution to Exercise 3 (Hard): find and fix the generics mistakes in a shipment repository.
 *
 * A teammate wrote a small generic Repository<T> and used it for shipments.
 * It compiles (with a warning), but crashes at runtime with a ClassCastException,
 * far away from the line that caused it.
 *
 * The real problem: Repository<T> LOOKS generic, but three mistakes switch the
 * compiler's type checks off. Because of that, one wrong call in main slipped
 * through.
 *
 * TASK
 *   1. Fix the THREE mistakes inside Repository so it really is type-safe.
 *      Keep the names items, save and findAll (the checker looks for them).
 *   2. Once fixed, the line marked "BAD" in main should no longer compile.
 *      Then delete that line. (If it still compiles, you haven't fixed everything.)
 *   Don't change anything else in main or in Shipment.
 *
 * EXPECTED OUTPUT (after your fix)
 *   Shipments:    [SH-1 -> Pune, SH-2 -> Goa, SH-4 -> Delhi]
 *   Total weight: 24.0 kg
 *   items is typed:        yes
 *   save uses class's T:   yes
 *   findAll is typed:      yes
 *   PASS
 *
 * HINTS (read one at a time if you're stuck)
 *   1. Running it prints "uses unchecked or unsafe operations". That warning
 *      means a raw type is involved somewhere. Where are the <...> missing?
 *   2. Look closely at save's signature. Which T does it use: Repository's,
 *      or a brand-new one?
 *   3. What does findAll's return type tell the caller about what's inside?
 *
 * Run: java solutions/Solution3_ShipmentRepoBug.java
 */
public class Solution3_ShipmentRepoBug {

    static class Shipment {
        private final String id;
        private final String destination;
        private final double weightKg;

        Shipment(String id, String destination, double weightKg) {
            this.id = id;
            this.destination = destination;
            this.weightKg = weightKg;
        }

        double getWeightKg() { return weightKg; }

        @Override
        public String toString() {
            return id + " -> " + destination;
        }
    }

    // ---- The buggy class. Fix it. ----

    // The three fixes:
    //   1. items was a raw List.        Now List<T>, so the compiler knows what it holds.
    //   2. save declared its own <T>,   which hid the class's T and accepted anything.
    //      Now it uses the class's T, so repo.save("text") on a Repository<Shipment> won't compile.
    //   3. findAll returned a raw List. Now List<T>, so callers get Shipments with no casts.
    static class Repository<T> {
        private final List<T> items = new ArrayList<>();

        public void save(T item) {
            items.add(item);
        }

        public List<T> findAll() {
            return List.copyOf(items);   // a copy: callers can't add to or clear the repository's list
        }
    }

    // ---- main is correct, apart from the BAD line. ----

    public static void main(String[] args) throws Exception {
        Repository<Shipment> repo = new Repository<>();
        repo.save(new Shipment("SH-1", "Pune", 12.5));
        repo.save(new Shipment("SH-2", "Goa", 4.0));
        repo.save(new Shipment("SH-4", "Delhi", 7.5));

        boolean ranOk;
        try {
            List<Shipment> all = repo.findAll();
            double totalKg = 0;
            for (Shipment shipment : all) {
                totalKg += shipment.getWeightKg();
            }
            System.out.println("Shipments:    " + all);
            System.out.println("Total weight: " + totalKg + " kg");
            ranOk = all.size() == 3 && totalKg == 24.0;
        } catch (ClassCastException e) {
            System.out.println("Crashed: " + e.getMessage());
            ranOk = false;
        }

        boolean designOk = checkDesign();
        System.out.println(ranOk && designOk ? "PASS" : "FAIL");
    }

    // Checks the fix is real (not just deleting the BAD line) by looking at Repository's declared types.
    static boolean checkDesign() throws Exception {
        Field items = Repository.class.getDeclaredField("items");
        boolean itemsTyped = items.getGenericType() instanceof ParameterizedType;

        Method save = Repository.class.getDeclaredMethod("save", Object.class);
        boolean saveUsesClassT = save.getTypeParameters().length == 0;

        Method findAll = Repository.class.getDeclaredMethod("findAll");
        boolean findAllTyped = findAll.getGenericReturnType() instanceof ParameterizedType;

        System.out.println("items is typed:        " + (itemsTyped ? "yes" : "no"));
        System.out.println("save uses class's T:   " + (saveUsesClassT ? "yes" : "no"));
        System.out.println("findAll is typed:      " + (findAllTyped ? "yes" : "no"));
        return itemsTyped && saveUsesClassT && findAllTyped;
    }
}
