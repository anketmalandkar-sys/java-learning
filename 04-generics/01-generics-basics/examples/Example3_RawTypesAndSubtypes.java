import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example 3: naming conventions, the raw-type assignment rules, and subtypes with extra
 * type parameters.
 *
 * Run: java examples/Example3_RawTypesAndSubtypes.java
 * To see the unchecked warnings the raw-type section causes:
 *   javac -Xlint:unchecked -d out examples/Example3_RawTypesAndSubtypes.java
 * (the @SuppressWarnings below hides them from the plain `java` run, so the output stays clean)
 */
public class Example3_RawTypesAndSubtypes {

    static class Box<T> {
        private T value;

        void set(T value) {
            this.value = value;
        }

        T get() {
            return value;
        }
    }

    // N for a number type: the bound makes the convention visible.
    static <N extends Number> N larger(N a, N b) {
        return a.doubleValue() >= b.doubleValue() ? a : b;
    }

    // T, U: a second independent type gets the next letter.
    static <T, U> Map<T, U> zip(List<T> keys, List<U> values) {
        Map<T, U> result = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            result.put(keys.get(i), values.get(i));
        }
        return result;
    }

    // A subtype with an extra type parameter P. It's still a List<E>.
    static class PayloadList<E, P> extends ArrayList<E> {
        private final Map<Integer, P> payloads = new HashMap<>();

        void setPayload(int index, P payload) {
            payloads.put(index, payload);
        }

        P payload(int index) {
            return payloads.get(index);
        }
    }

    static int countAll(List<String> items) {
        return items.size();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void rawTypeRules() {
        Box<String> typed = new Box<>();
        typed.set("label");

        Box raw = typed;              // parameterized -> raw: allowed silently (only a rawtypes lint)
        raw.set(8);                   // unchecked call: an Integer goes into a Box<String>
        Box<String> again = raw;      // raw -> parameterized: unchecked conversion

        Object inside = again.get();  // fine as Object...
        System.out.println("the Box<String> now holds an object of type " + inside.getClass().getSimpleName());
        try {
            String text = again.get();   // ...but the hidden cast to String fails here
            System.out.println("never printed " + text);
        } catch (ClassCastException e) {
            System.out.println("reading it as a String -> ClassCastException");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Naming conventions ---");
        System.out.println("larger(3, 7)     = " + larger(3, 7));
        System.out.println("larger(2.5, 1.5) = " + larger(2.5, 1.5));
        System.out.println("zip              = " + zip(List.of("pen"), List.of(10)));

        System.out.println("\n--- Raw-type rules ---");
        rawTypeRules();

        System.out.println("\n--- A subtype with an extra type parameter ---");
        PayloadList<String, Integer> orders = new PayloadList<>();
        orders.add("A101");
        orders.add("A102");
        orders.setPayload(1, 499);
        List<String> asList = orders;                 // PayloadList<String, Integer> is a List<String>
        System.out.println("as List<String>: " + asList + ", countAll = " + countAll(orders));
        System.out.println("payload of A102 = " + orders.payload(1));
        // List<Integer> ints = orders;               // compile error: it's a List<String>
    }
}

/* Expected output:
--- Naming conventions ---
larger(3, 7)     = 7
larger(2.5, 1.5) = 2.5
zip              = {pen=10}

--- Raw-type rules ---
the Box<String> now holds an object of type Integer
reading it as a String -> ClassCastException

--- A subtype with an extra type parameter ---
as List<String>: [A101, A102], countAll = 2
payload of A102 = 499
*/
