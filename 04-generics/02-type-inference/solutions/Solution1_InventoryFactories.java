import java.util.*;
import java.util.function.Supplier;

/**
 * Solution to Exercise 1 (Easy): generic factory methods that rely on inference.
 *
 * TASK
 *   Finish the four methods in Stock. None of them is told its types directly;
 *   main calls them and the compiler infers T, K and V. Replace each TODO.
 *
 *     emptyShelf()             a new, empty, modifiable List<T>
 *                              (T comes from the TARGET: List<String> shelf = Stock.emptyShelf();)
 *     newLedger()              a new, empty, modifiable Map<K, V>
 *                              (K and V also come from the target)
 *     shelfOf(first, second)   a new modifiable List<T> holding the two items
 *                              (T comes from the ARGUMENTS)
 *     totalItems(shelf)        how many items are on the shelf. Its parameter is a List<String>,
 *                              which makes it the target type for a call like
 *                              Stock.totalItems(Stock.emptyShelf()).
 *
 *   Then, in witnessedCount(), replace "return -1" with ONE expression that calls
 *   emptyShelf() and immediately calls .size() on the result. There's no target type
 *   in the middle of that chain, so use an explicit type witness: Stock.<String>emptyShelf().
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   emptyShelf:     PASS
 *   newLedger:      PASS
 *   shelfOf:        PASS
 *   totalItems:     PASS
 *   witnessedCount: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Return new ArrayList<>() / new HashMap<>(). The diamond takes T/K/V from the return type.
 *   - "Modifiable" rules out List.of(...) for shelfOf: main adds to the result.
 *
 * Run: java solutions/Solution1_InventoryFactories.java
 */
public class Solution1_InventoryFactories {

    static class Stock {
        // The diamond takes T from the return type, and the caller's target decides what T is.
        static <T> List<T> emptyShelf() {
            return new ArrayList<>();
        }

        static <K, V> Map<K, V> newLedger() {
            return new HashMap<>();
        }

        // Wrapped in a new ArrayList because List.of / Arrays.asList can't grow, and main adds to it.
        static <T> List<T> shelfOf(T first, T second) {
            return new ArrayList<>(Arrays.asList(first, second));
        }

        static int totalItems(List<String> shelf) {
            return shelf.size();
        }
    }

    // .size() is called on the result, so emptyShelf() has no target type: the witness supplies T.
    static int witnessedCount() {
        return Stock.<String>emptyShelf().size();
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("emptyShelf", () -> {
            List<String> pens = Stock.emptyShelf();          // T = String, from the target
            pens.add("Blue pen");
            List<Integer> sizes = Stock.emptyShelf();        // T = Integer, same method
            sizes.add(42);
            return pens.equals(List.of("Blue pen")) && sizes.equals(List.of(42));
        });

        allPass &= check("newLedger", () -> {
            Map<String, Integer> stockCount = Stock.newLedger();   // K = String, V = Integer
            stockCount.put("Notebook", 12);
            stockCount.merge("Notebook", 3, Integer::sum);
            return stockCount.get("Notebook") == 15;
        });

        allPass &= check("shelfOf", () -> {
            List<String> shelf = Stock.shelfOf("Stapler", "Tape");    // T = String, from the arguments
            shelf.add("Glue");
            List<Double> prices = Stock.shelfOf(2.5, 4.0);            // T = Double
            return shelf.equals(List.of("Stapler", "Tape", "Glue")) && prices.size() == 2;
        });

        allPass &= check("totalItems", () -> {
            // emptyShelf()'s T comes from totalItems' parameter: a method argument is a target type (Java 8+).
            return Stock.totalItems(Stock.emptyShelf()) == 0
                    && Stock.totalItems(Stock.shelfOf("A", "B")) == 2;
        });

        allPass &= check("witnessedCount", () -> witnessedCount() == 0);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-15s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-15s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
