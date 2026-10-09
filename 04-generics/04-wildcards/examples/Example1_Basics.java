import java.util.ArrayList;
import java.util.List;

/**
 * Example 1: the three wildcards, and what each lets you do.
 *   1. Invariance: why List<Integer> can't be passed as a List<Number>.
 *   2. ? extends Number: read as Number, can't add.
 *   3. ? super Integer: add Integers, reads are Object.
 *   4. ?: List<?> vs List<Object>.
 *   5. "Read-only" isn't quite read-only.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static double sumStrict(List<Number> values) {
        double total = 0;
        for (Number n : values) {
            total += n.doubleValue();
        }
        return total;
    }

    // Producer: we only read from values.
    static double sum(List<? extends Number> values) {
        double total = 0;
        for (Number n : values) {
            total += n.doubleValue();
        }
        // values.add(1);   // won't compile: values might be a List<Double>
        return total;
    }

    // Consumer: we only add Integers to target.
    static void addFirstPrimes(List<? super Integer> target) {
        target.add(2);
        target.add(3);
        target.add(5);
        Object first = target.get(0);   // reads only promise Object
        // Integer x = target.get(0);   // won't compile: target might be a List<Object>
    }

    static String describe(List<?> items) {
        return items.size() + " items: " + items;
    }

    static void addAnything(List<Object> items) {
        items.add("text");
        items.add(42);
    }

    public static void main(String[] args) {
        List<Integer> scores = new ArrayList<>(List.of(90, 75, 84));
        List<Double> prices = List.of(19.99, 5.01);
        List<Number> numbers = new ArrayList<>(List.of(1, 2.5));

        System.out.println("--- 1. Invariance ---");
        System.out.println("sumStrict(numbers) = " + sumStrict(numbers));
        // sumStrict(scores);   // won't compile: a List<Integer> is not a List<Number>

        System.out.println();
        System.out.println("--- 2. ? extends Number: a producer ---");
        System.out.println("sum(scores)  = " + sum(scores));
        System.out.println("sum(prices)  = " + sum(prices));
        System.out.println("sum(numbers) = " + sum(numbers));

        System.out.println();
        System.out.println("--- 3. ? super Integer: a consumer ---");
        List<Integer> intTarget = new ArrayList<>();
        List<Number> numberTarget = new ArrayList<>();
        List<Object> objectTarget = new ArrayList<>(List.of("header"));
        addFirstPrimes(intTarget);
        addFirstPrimes(numberTarget);
        addFirstPrimes(objectTarget);
        System.out.println("into List<Integer>: " + intTarget);
        System.out.println("into List<Number>:  " + numberTarget);
        System.out.println("into List<Object>:  " + objectTarget);
        // addFirstPrimes(prices);   // won't compile: Double is not a supertype of Integer

        System.out.println();
        System.out.println("--- 4. List<?> vs List<Object> ---");
        System.out.println(describe(scores));         // List<?> accepts any list
        System.out.println(describe(List.of("a", "b")));
        List<Object> mixed = new ArrayList<>();
        addAnything(mixed);                           // List<Object> accepts only a List<Object>...
        System.out.println("mixed = " + mixed);       // ...but lets you add anything
        // addAnything(scores);                       // won't compile

        System.out.println();
        System.out.println("--- 5. ? extends is not truly read-only ---");
        List<? extends Number> view = scores;
        view.add(null);                               // null fits every type
        System.out.println("after add(null): " + scores);
        view.removeIf(n -> n == null || n.intValue() < 80);
        System.out.println("after removeIf:  " + scores);
        view.clear();
        System.out.println("after clear():   " + scores);
    }
}

/* Expected output:
--- 1. Invariance ---
sumStrict(numbers) = 3.5

--- 2. ? extends Number: a producer ---
sum(scores)  = 249.0
sum(prices)  = 25.0
sum(numbers) = 3.5

--- 3. ? super Integer: a consumer ---
into List<Integer>: [2, 3, 5]
into List<Number>:  [2, 3, 5]
into List<Object>:  [header, 2, 3, 5]

--- 4. List<?> vs List<Object> ---
3 items: [90, 75, 84]
2 items: [a, b]
mixed = [text, 42]

--- 5. ? extends is not truly read-only ---
after add(null): [90, 75, 84, null]
after removeIf:  [90, 84]
after clear():   []
*/
