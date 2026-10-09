import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Solution to Exercise 1 (Easy): working around new T(), new T[] and instanceof List<String>.
 *
 * TASK
 *   Implement the three methods. Each one needs something erasure takes away:
 *   the ability to create a T, create a T[], or check a type argument at runtime.
 *   The extra parameter (Supplier<T> or Class<T>) puts that information back.
 *
 *     createMany(factory, count)       a new list of 'count' freshly created Ts.
 *                                      (You can't write new T(), but you can call factory.get().)
 *
 *     filledArray(type, size, value)   a REAL T[] of the given size, every slot holding value.
 *                                      For String.class it must be an actual String[], not an
 *                                      Object[]: main assigns it to a String[] variable.
 *
 *     onlyOfType(items, type)          from a list of anything, a new List<T> holding just the
 *                                      items that are instances of type, in order.
 *                                      (You can't write "item instanceof T", but you can ask type.)
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   createMany:  PASS
 *   filledArray: PASS
 *   onlyOfType:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - java.lang.reflect.Array.newInstance(type, size) returns an Object that really is a T[].
 *     Cast it to T[]. That cast is unchecked, but here it's safe: put @SuppressWarnings("unchecked")
 *     on the method, with a comment saying why.
 *   - java.util.Arrays.fill(array, value) fills every slot.
 *   - Class has isInstance(object) and cast(object).
 *
 * Run: java solutions/Solution1_ErasureWorkarounds.java
 */
public class Solution1_ErasureWorkarounds {

    static class Ticket {
        private static int nextNumber = 1;
        final int number = nextNumber++;
    }

    // new T() is impossible (T is erased), so the caller passes the "how to make one": Ticket::new.
    static <T> List<T> createMany(Supplier<T> factory, int count) {
        List<T> made = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            made.add(factory.get());
        }
        return made;
    }

    // Array.newInstance creates an array whose runtime type really is T[] (e.g. String[]).
    // (T[]) new Object[size] would compile too, but main's String[] variable would then throw.
    @SuppressWarnings("unchecked")   // safe: Array.newInstance(type, ...) always returns a T[]
    static <T> T[] filledArray(Class<T> type, int size, T value) {
        T[] array = (T[]) Array.newInstance(type, size);
        Arrays.fill(array, value);
        return array;
    }

    // "item instanceof T" is impossible (T is erased), but the Class<T> object can check at runtime.
    static <T> List<T> onlyOfType(List<?> items, Class<T> type) {
        List<T> matches = new ArrayList<>();
        for (Object item : items) {
            if (type.isInstance(item)) {
                matches.add(type.cast(item));   // a checked cast: no unchecked warning
            }
        }
        return matches;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("createMany", () -> {
            List<Ticket> tickets = createMany(Ticket::new, 3);
            List<ArrayList<String>> carts = createMany(ArrayList::new, 2);
            return tickets.size() == 3
                    && tickets.get(0).number == 1 && tickets.get(2).number == 3   // three different objects
                    && carts.size() == 2 && carts.get(0) != carts.get(1);
        });

        allPass &= check("filledArray", () -> {
            String[] seats = filledArray(String.class, 3, "free");   // fails here if it's really an Object[]
            Integer[] stock = filledArray(Integer.class, 2, 0);
            return seats.getClass() == String[].class
                    && String.join(",", seats).equals("free,free,free")
                    && stock.length == 2 && stock[1] == 0;
        });

        allPass &= check("onlyOfType", () -> {
            List<Object> parsedJson = List.of("SKU-1", 42, "SKU-2", 3.5, 7);
            List<String> skus = onlyOfType(parsedJson, String.class);
            List<Integer> counts = onlyOfType(parsedJson, Integer.class);
            List<Number> numbers = onlyOfType(parsedJson, Number.class);
            return skus.equals(List.of("SKU-1", "SKU-2"))
                    && counts.equals(List.of(42, 7))
                    && numbers.equals(List.of(42, 3.5, 7));
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. Unfinished TODOs return null, and a fake T[] fails with ClassCastException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-12s FAIL (still returning null?)%n", name + ":");
            return false;
        } catch (ClassCastException e) {
            System.out.printf("%-12s FAIL (ClassCastException: is that array really a T[]?)%n", name + ":");
            return false;
        }
        System.out.printf("%-12s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
