import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Example 1: where the compiler gets the type arguments from.
 *   1. From the arguments (and the most specific common type).
 *   2. From the target type: the variable the result is assigned to.
 *   3. From a method parameter (Java 8+).
 *   4. An explicit type witness, when there is no target.
 *   5. Diamond + a generic constructor: two type parameters inferred at once.
 *   6. var + diamond: no target, so Object.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static <T> T pick(T a, T b) {
        return b;
    }

    static int countNames(List<String> names) {
        return names.size();
    }

    // T comes from the class (diamond / target), U from the constructor argument.
    static class Box<T> {
        private T value;
        private final String note;

        <U> Box(U seed) {
            this.note = "seeded with a " + seed.getClass().getSimpleName();
        }

        void set(T value) { this.value = value; }
        T get() { return value; }
        String note() { return note; }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. From the arguments ---");
        String city = pick("Pune", "Goa");                            // T = String
        Integer score = pick(90, 75);                                 // T = Integer
        Serializable mixed = pick("text", new ArrayList<String>());  // T = the closest type both share
        System.out.println(city + ", " + score + ", " + mixed.getClass().getSimpleName());

        System.out.println();
        System.out.println("--- 2. From the target type ---");
        List<String> noNames = Collections.emptyList();   // no arguments: T = String from the variable
        System.out.println("Empty list of names, size " + noNames.size());

        System.out.println();
        System.out.println("--- 3. From a method parameter (Java 8+) ---");
        // countNames wants a List<String>, so emptyList's T = String. In Java 7 this didn't compile.
        System.out.println("countNames(emptyList()) = " + countNames(Collections.emptyList()));

        System.out.println();
        System.out.println("--- 4. Explicit type witness ---");
        // .size() is called on the result, so there's no target type here. The witness says it anyway.
        int size = Collections.<String>emptyList().size();
        System.out.println("Witnessed list size: " + size);

        System.out.println();
        System.out.println("--- 5. Diamond + generic constructor ---");
        Box<Integer> box = new Box<>("abc");   // T = Integer (target), U = String (argument)
        box.set(42);
        int value = box.get();                 // no cast: T really is Integer
        System.out.println("Box holds " + value + ", " + box.note());

        System.out.println();
        System.out.println("--- 6. var + diamond ---");
        var anything = new ArrayList<>();      // no target: ArrayList<Object>
        anything.add("text");
        anything.add(3.14);                    // compiles, because it's a list of Object
        var strings = new ArrayList<String>(); // type written on the right instead
        strings.add("text");
        // strings.add(3.14);                  // won't compile
        System.out.println("anything = " + anything + ", strings = " + strings);
    }
}

/* Expected output:
--- 1. From the arguments ---
Goa, 75, ArrayList

--- 2. From the target type ---
Empty list of names, size 0

--- 3. From a method parameter (Java 8+) ---
countNames(emptyList()) = 0

--- 4. Explicit type witness ---
Witnessed list size: 0

--- 5. Diamond + generic constructor ---
Box holds 42, seeded with a String

--- 6. var + diamond ---
anything = [text, 3.14], strings = [text]
*/
