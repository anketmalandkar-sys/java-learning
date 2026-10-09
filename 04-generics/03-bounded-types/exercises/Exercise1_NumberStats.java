import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/**
 * Exercise 1 (Easy): statistics helpers with bounds.
 *
 * TASK
 *   Each method below has a plain <T>, which only allows Object's methods, so you
 *   can't do maths or compare. For each one:
 *     a) change <T> to the right bound, and
 *     b) replace the TODO with the implementation.
 *
 *     sum(values)               total of all values, as a double.          Bound: Number
 *     average(values)           average as a double; 0.0 for an empty list. Bound: Number
 *     max(items)                the largest item; the caller gets back the same type it passed in.
 *                                                                           Bound: Comparable<T>
 *     clamp(value, low, high)   value, but pulled inside [low, high]:
 *                                 below low -> low, above high -> high, otherwise value.
 *                                                                           Bound: Comparable<T>
 *
 *   Don't change main. Once the bounds are right, the commented-out lines in main
 *   should NOT compile: try uncommenting them to check, then comment them out again.
 *
 * EXPECTED OUTPUT
 *   sum:     PASS
 *   average: PASS
 *   max:     PASS
 *   clamp:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - Number has doubleValue(), which works for Integer, Long, Double and so on.
 *   - Compare with compareTo(), not < or >: those only work on primitives.
 *   - average can reuse sum.
 *
 * Run: java exercises/Exercise1_NumberStats.java
 */
public class Exercise1_NumberStats {

    static <T extends Number> double sum(List<T> values) {
        return values.stream().mapToDouble(Number::doubleValue).sum();
    }

    static <T extends Number> double average(List<T> values) {
        if (values.isEmpty()) {
            return 0.0;   // avoid 0 / 0, which would be NaN
        }
        return sum(values) / values.size();
    }

    static <T extends Comparable<? super T>> T max(List<T> items) {
        // TODO: change the bound, then implement
        return items.stream().max(Comparator.naturalOrder()).orElseThrow(() -> new NoSuchElementException("List is empty"));
    }

    static <T extends Comparable<T>> T clamp(T value, T low, T high) {
        // TODO: change the bound, then implement
        if (value.compareTo(low) < 0) {
            return low;
        }
        if (value.compareTo(high) > 0) {
            return high;
        }
        return value;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("sum", () -> sum(List.of(10, 20, 30)) == 60.0
                && sum(List.of(1.5, 2.5)) == 4.0
                && sum(List.of(5_000_000_000L, 1L)) == 5_000_000_001.0);

        allPass &= check("average", () -> average(List.of(90, 75, 84)) == 83.0
                && average(List.<Double>of()) == 0.0);

        allPass &= check("max", () -> {
            Integer topScore = max(List.of(72, 95, 88));          // must be Integer, not Object
            String lastName = max(List.of("Meera", "Arjun", "Ravi"));
            return topScore == 95 && lastName.equals("Ravi");
        });

        allPass &= check("clamp", () -> {
            Integer volume = clamp(120, 0, 100);
            String grade = clamp("F", "A", "C");
            return volume == 100 && clamp(-5, 0, 100) == 0 && clamp(42, 0, 100) == 42
                    && grade.equals("C");
        });

        // These should NOT compile once your bounds are right:
        // sum(List.of("ten", "twenty"));
        // max(List.of(new Object(), new Object()));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-8s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-8s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
