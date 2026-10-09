import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Example 1: why generics exist, and the basic forms.
 *   1. The problem: an Object box needs casts and fails at runtime.
 *   2. A generic class: Box<T>.
 *   3. Two type parameters: Pair<K, V>.
 *   4. A generic method: <T> T lastOf(List<T>).
 *   5. Primitives: use the wrapper type.
 *   6. A generic constructor in a non-generic class.
 *   7. Subtyping: ArrayList<String> is a List<String>, but List<Integer> is not a List<Number>.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // Before generics: holds anything, so the compiler can't help you.
    static class ObjectBox {
        private Object value;

        void set(Object value) { this.value = value; }
        Object get() { return value; }
    }

    // T is a placeholder. In a Box<String>, every T below means String.
    static class Box<T> {
        private T value;

        void set(T value) { this.value = value; }
        T get() { return value; }
    }

    static class Pair<K, V> {
        private final K key;
        private final V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        K getKey() { return key; }
        V getValue() { return value; }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    // Label isn't generic, but its constructor declares its own <T>.
    static class Label {
        private final String text;

        <T> Label(T value) {
            this.text = "[" + value + "]";
        }

        @Override
        public String toString() {
            return text;
        }
    }

    // The <T> before the return type declares the method's own type parameter.
    static <T> T lastOf(List<T> items) {
        return items.get(items.size() - 1);
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Object box: casts, and errors only at runtime ---");
        ObjectBox objectBox = new ObjectBox();
        objectBox.set("hello");
        String text = (String) objectBox.get();   // cast needed every time
        System.out.println("Got: " + text);

        objectBox.set(42);                         // compiler is happy...
        try {
            String broken = (String) objectBox.get();
            System.out.println(broken);
        } catch (ClassCastException e) {
            System.out.println("...but at runtime: ClassCastException, an Integer is not a String");
        }

        System.out.println();
        System.out.println("--- 2. Box<T>: no casts, errors at compile time ---");
        Box<String> nameBox = new Box<>();         // diamond: T is String, taken from the left
        nameBox.set("Ada");
        String name = nameBox.get();               // no cast
        System.out.println("Got: " + name);
        // nameBox.set(42);                        // won't compile: int is not a String

        Box<Integer> countBox = new Box<>();       // same class, different type
        countBox.set(7);
        int doubled = countBox.get() * 2;          // get() returns Integer, unboxed for the maths
        System.out.println("Doubled: " + doubled);

        System.out.println();
        System.out.println("--- 3. Two type parameters: Pair<K, V> ---");
        Pair<String, Integer> stock = new Pair<>("Pens", 120);
        String item = stock.getKey();
        int quantity = stock.getValue();
        System.out.println(stock + "  (item: " + item + ", quantity: " + quantity + ")");

        System.out.println();
        System.out.println("--- 4. Generic method: T is inferred from the argument ---");
        String lastCity = lastOf(List.of("Pune", "Delhi", "Goa"));   // T = String
        Integer lastScore = lastOf(List.of(90, 75, 88));             // T = Integer
        System.out.println("Last city:  " + lastCity);
        System.out.println("Last score: " + lastScore);

        System.out.println();
        System.out.println("--- 5. Primitives aren't allowed: use the wrapper ---");
        // List<int> numbers;                      // won't compile
        List<Integer> numbers = new ArrayList<>();
        numbers.add(5);                            // int 5 is autoboxed to Integer
        numbers.add(10);
        System.out.println("numbers = " + numbers);

        System.out.println();
        System.out.println("--- 6. Generic constructor: T inferred per call ---");
        Label countLabel = new Label(42);          // T = Integer
        Label nameLabel = new Label("Ada");        // T = String
        System.out.println(countLabel + " " + nameLabel);

        System.out.println();
        System.out.println("--- 7. Subtyping: the class can vary, the <...> can't ---");
        ArrayList<String> cities = new ArrayList<>(List.of("Pune", "Goa"));
        List<String> asList = cities;              // ArrayList<String> is a List<String>
        Collection<String> asCollection = asList;  // List<String> is a Collection<String>
        System.out.println("Same object, seen as a Collection: " + asCollection);
        // List<Number> nums = new ArrayList<Integer>();   // won't compile: different type argument
    }
}

/* Expected output:
--- 1. Object box: casts, and errors only at runtime ---
Got: hello
...but at runtime: ClassCastException, an Integer is not a String

--- 2. Box<T>: no casts, errors at compile time ---
Got: Ada
Doubled: 14

--- 3. Two type parameters: Pair<K, V> ---
Pens=120  (item: Pens, quantity: 120)

--- 4. Generic method: T is inferred from the argument ---
Last city:  Goa
Last score: 88

--- 5. Primitives aren't allowed: use the wrapper ---
numbers = [5, 10]

--- 6. Generic constructor: T inferred per call ---
[42] [Ada]

--- 7. Subtyping: the class can vary, the <...> can't ---
Same object, seen as a Collection: [Pune, Goa]
*/
