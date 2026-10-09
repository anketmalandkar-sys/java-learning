import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 1 (Easy): pick the right wildcard.
 *
 * TASK
 *   Every method below takes a List<?>. That compiles for every call in main, but
 *   with a plain ? you can only read Objects and can't add anything. For each method:
 *     a) decide which wildcard it needs: ? extends ..., ? super ..., or keep ?
 *     b) change the parameter type, then
 *     c) replace the TODO with the implementation.
 *
 *     totalWeight(weights)      the sum of all weights, as a double
 *     averageOf(values)         the average, as a double; 0.0 for an empty list
 *     addZeros(target, count)   add 'count' Integer zeros to the end of target
 *     countNonNull(items)       how many items are not null
 *
 *   Ask PECS for each: does the method READ numbers out (producer), or PUT Integers in (consumer)?
 *   Or does it only look at the items as Objects?
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   totalWeight:  PASS
 *   averageOf:    PASS
 *   addZeros:     PASS
 *   countNonNull: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Number has doubleValue().
 *   - A target that accepts Integers might be a List<Integer>, a List<Number> or a List<Object>.
 *   - If a method only needs Objects, the simplest signature is the right one.
 *
 * Run: java exercises/Exercise1_WildcardSignatures.java
 */
public class Exercise1_WildcardSignatures {

    static double totalWeight(List<?> weights) {
        // TODO: choose the wildcard, then implement
        return -1;
    }

    static double averageOf(List<?> values) {
        // TODO: choose the wildcard, then implement
        return -1;
    }

    static void addZeros(List<?> target, int count) {
        // TODO: choose the wildcard, then implement
    }

    static int countNonNull(List<?> items) {
        // TODO: choose the wildcard, then implement
        return -1;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("totalWeight", () -> {
            List<Integer> parcelGrams = List.of(250, 1200, 75);
            List<Double> bagKilos = List.of(1.5, 2.25);
            return totalWeight(parcelGrams) == 1525.0 && totalWeight(bagKilos) == 3.75;
        });

        allPass &= check("averageOf", () -> {
            List<Long> visits = List.of(10L, 20L, 60L);
            List<Number> mixed = List.of(1, 2.0, 3L);
            return averageOf(visits) == 30.0 && averageOf(mixed) == 2.0
                    && averageOf(List.<Double>of()) == 0.0;
        });

        allPass &= check("addZeros", () -> {
            List<Integer> scores = new ArrayList<>(List.of(7));
            List<Number> readings = new ArrayList<>(List.of(1.5));
            List<Object> row = new ArrayList<>(List.of("total"));
            addZeros(scores, 2);
            addZeros(readings, 1);
            addZeros(row, 3);
            return scores.equals(List.of(7, 0, 0))
                    && readings.equals(List.of(1.5, 0))
                    && row.equals(List.of("total", 0, 0, 0));
        });

        allPass &= check("countNonNull", () -> {
            List<String> names = Arrays.asList("Ravi", null, "Meera");
            List<Double> prices = Arrays.asList(null, null, 9.5);
            return countNonNull(names) == 2 && countNonNull(prices) == 1
                    && countNonNull(List.of()) == 0;
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok = test.get();
        System.out.printf("%-13s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
