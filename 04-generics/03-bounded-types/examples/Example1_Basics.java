import java.util.List;

/**
 * Example 1: the three kinds of bound.
 *   1. A class bound: <T extends Number>, so doubleValue() is available.
 *   2. An interface bound: <T extends Comparable<T>>, so compareTo() is available.
 *   3. The caller gets its own type back (Integer, not Number).
 *   4. Multiple bounds: <T extends Number & Comparable<T>>.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // Every T is a Number, so doubleValue() is allowed.
    static <T extends Number> double sum(List<T> values) {
        double total = 0;
        for (T value : values) {
            total += value.doubleValue();
        }
        return total;
    }

    // Every T can be compared with another T, so compareTo() is allowed.
    static <T extends Comparable<T>> T max(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    // Two bounds: doubleValue() comes from Number, compareTo() from Comparable.
    static <T extends Number & Comparable<T>> T maxAbove(List<T> values, double floor) {
        T best = null;
        for (T value : values) {
            if (value.doubleValue() > floor && (best == null || value.compareTo(best) > 0)) {
                best = value;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Class bound: <T extends Number> ---");
        System.out.println("sum of Integers: " + sum(List.of(1, 2, 3)));
        System.out.println("sum of Doubles:  " + sum(List.of(2.5, 1.5)));
        // sum(List.of("a", "b"));       // won't compile: String is not a Number

        System.out.println();
        System.out.println("--- 2. Interface bound: <T extends Comparable<T>> ---");
        System.out.println("max city:  " + max(List.of("Pune", "Delhi", "Goa")));
        System.out.println("max score: " + max(List.of(72, 95, 88)));
        // max(List.of(new Object()));   // won't compile: Object is not Comparable

        System.out.println();
        System.out.println("--- 3. The caller keeps its own type ---");
        Integer topScore = max(List.of(72, 95, 88));   // an Integer, no cast
        int bonus = topScore + 5;                      // so Integer maths works directly
        System.out.println("top score + bonus = " + bonus);

        System.out.println();
        System.out.println("--- 4. Multiple bounds: <T extends Number & Comparable<T>> ---");
        List<Double> temperatures = List.of(18.5, 31.0, 27.5, 35.5);
        System.out.println("hottest above 30: " + maxAbove(temperatures, 30));
        System.out.println("hottest above 40: " + maxAbove(temperatures, 40));
    }
}

/* Expected output:
--- 1. Class bound: <T extends Number> ---
sum of Integers: 6.0
sum of Doubles:  4.0

--- 2. Interface bound: <T extends Comparable<T>> ---
max city:  Pune
max score: 95

--- 3. The caller keeps its own type ---
top score + bonus = 100

--- 4. Multiple bounds: <T extends Number & Comparable<T>> ---
hottest above 30: 35.5
hottest above 40: null
*/
