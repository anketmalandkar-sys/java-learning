import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Example 2: the standard workarounds, in a small inventory app.
 *   1. No new T(): pass a Supplier<T>.
 *   2. No new T[n]: pass a Class<T> and use Array.newInstance.
 *   3. No instanceof List<String>: check List<?>, then check the elements.
 *   4. A type-safe store keyed by Class<T>, instead of an unchecked cast.
 *   5. @SafeVarargs on a varargs method that only reads its array.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static class Crate {
        private static int created = 0;
        final int number = ++created;

        @Override
        public String toString() { return "Crate#" + number; }
    }

    // 1. The caller says how to make a T: makeAll(Crate::new, 3).
    static <T> List<T> makeAll(Supplier<T> factory, int count) {
        List<T> made = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            made.add(factory.get());
        }
        return made;
    }

    // 2. Array.newInstance builds a real String[] (or Crate[]), not an Object[].
    @SuppressWarnings("unchecked")   // safe: Array.newInstance(type, ...) really returns a T[]
    static <T> T[] newArray(Class<T> type, int size) {
        return (T[]) Array.newInstance(type, size);
    }

    // 3. We can only check "is it a List" at runtime, then look at each element.
    static boolean isListOf(Object candidate, Class<?> elementType) {
        if (!(candidate instanceof List<?> list)) {
            return false;
        }
        for (Object element : list) {
            if (!elementType.isInstance(element)) {
                return false;
            }
        }
        return true;
    }

    // 4. The Class<T> key carries the type at runtime, so type.cast() really checks.
    static class Settings {
        private final Map<Class<?>, Object> values = new HashMap<>();

        <T> void put(Class<T> type, T value) {
            values.put(type, value);
        }

        <T> T get(Class<T> type) {
            return type.cast(values.get(type));
        }
    }

    // 5. Only reads the varargs array, never stores into it or hands it out: safe.
    @SafeVarargs
    static <T> List<T> listOf(T... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Supplier<T> instead of new T() ---");
        List<Crate> crates = makeAll(Crate::new, 3);
        List<StringBuilder> buffers = makeAll(StringBuilder::new, 2);
        System.out.println("crates: " + crates + ", buffers: " + buffers.size());

        System.out.println();
        System.out.println("--- 2. Class<T> instead of new T[n] ---");
        String[] labels = newArray(String.class, 3);
        labels[0] = "fragile";
        System.out.println("labels is a " + labels.getClass().getSimpleName() + ": " + Arrays.toString(labels));
        Crate[] slots = newArray(Crate.class, 2);
        System.out.println("slots is a " + slots.getClass().getSimpleName());

        System.out.println();
        System.out.println("--- 3. Checking a List's contents at runtime ---");
        Object fromJson = List.of("SKU-1", "SKU-2");
        Object mixed = List.of("SKU-1", 42);
        System.out.println("fromJson is a list of String? " + isListOf(fromJson, String.class));
        System.out.println("mixed is a list of String?    " + isListOf(mixed, String.class));

        System.out.println();
        System.out.println("--- 4. A type-safe settings store ---");
        Settings settings = new Settings();
        settings.put(Integer.class, 30);
        settings.put(String.class, "warehouse-7");
        int timeout = settings.get(Integer.class);   // no cast at the call site, and none can fail
        String site = settings.get(String.class);
        System.out.println("timeout=" + timeout + ", site=" + site);
        // settings.put(Integer.class, "30");        // won't compile: "30" is not an Integer

        System.out.println();
        System.out.println("--- 5. @SafeVarargs ---");
        List<String> zones = listOf("A", "B", "C");  // no warning at the call site
        zones.add("D");
        System.out.println("zones = " + zones);
    }
}

/* Expected output:
--- 1. Supplier<T> instead of new T() ---
crates: [Crate#1, Crate#2, Crate#3], buffers: 2

--- 2. Class<T> instead of new T[n] ---
labels is a String[]: [fragile, null, null]
slots is a Crate[]

--- 3. Checking a List's contents at runtime ---
fromJson is a list of String? true
mixed is a list of String?    false

--- 4. A type-safe settings store ---
timeout=30, site=warehouse-7

--- 5. @SafeVarargs ---
zones = [A, B, C, D]
*/
