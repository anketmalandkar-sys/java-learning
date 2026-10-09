import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Solution to Exercise 1 (Easy): finish a generic Pair and three generic helper methods.
 *
 * TASK
 *   Part A. Pair<A, B> holds two values of possibly different types.
 *     getFirst(), getSecond()   return the stored values
 *     swap()                    return a NEW Pair with the values the other way round:
 *                               ("Ada", 36) -> (36, "Ada"). Note the return type is Pair<B, A>.
 *
 *   Part B. Generic helpers (they must work for any type, not just String):
 *     lastElement(items)          the last item in the list
 *     countMatches(items, target) how many items are equal to target
 *     repeat(item, times)         a new list containing item, 'times' times
 *
 *   Replace each "return null" / "return 0" marked TODO. Don't change main.
 *
 * EXPECTED OUTPUT
 *   getters:      PASS
 *   swap:         PASS
 *   lastElement:  PASS
 *   countMatches: PASS
 *   repeat:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - swap: the values go in the other order, so the type arguments do too: new Pair<>(second, first).
 *   - countMatches: use equals(), not ==. The list holds objects (e.g. Integer), and == compares references.
 *   - repeat: create a new ArrayList<T>, then loop.
 *
 * Run: java solutions/Solution1_PairAndHelpers.java
 */
public class Solution1_PairAndHelpers {

    static class Pair<A, B> {
        private final A first;
        private final B second;

        Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        A getFirst() {
            return this.first;
        }

        B getSecond() {
            return this.second;
        }

        Pair<B, A> swap() {
            return new Pair<>(this.second, this.first);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

    static <T> T lastElement(List<T> items) {
        return items.get(items.size() - 1);   // items.getLast() also works on Java 21+
    }

    // Collections.frequency(items, target) does the same in one line. It also uses equals().
    static <T> int countMatches(List<T> items, T target) {
        int count = 0;
        for (T item : items) {
            if (item.equals(target)) {   // equals, not ==: 1000 == 1000 is false for two Integer objects
                count++;
            }
        }
        return count;
    }

    // Collections.nCopies(times, item) passes the check too, but it returns an UNMODIFIABLE list:
    // calling add() on it throws. A new ArrayList is safer when the caller may add to it later.
    static <T> List<T> repeat(T item, int times) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            result.add(item);
        }
        return result;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("getters", () -> {
            Pair<String, Integer> person = new Pair<>("Ada", 36);
            String name = person.getFirst();
            Integer age = person.getSecond();
            return "Ada".equals(name) && Integer.valueOf(36).equals(age);
        });

        allPass &= check("swap", () -> {
            Pair<String, Integer> person = new Pair<>("Ada", 36);
            Pair<Integer, String> swapped = person.swap();
            return swapped.toString().equals("(36, Ada)")
                    && person.toString().equals("(Ada, 36)");   // the original must not change
        });

        allPass &= check("lastElement", () -> {
            String lastCity = lastElement(List.of("Pune", "Delhi", "Goa"));
            Double lastPrice = lastElement(List.of(9.99, 4.5));
            return "Goa".equals(lastCity) && Double.valueOf(4.5).equals(lastPrice);
        });

        allPass &= check("countMatches", () -> {
            // 1000 is outside Integer's cache (-128..127), so == would give the wrong answer here.
            List<Integer> orderTotals = List.of(1000, 250, 1000, 99, 1000);
            List<String> votes = List.of("yes", "no", "yes");
            return countMatches(orderTotals, 1000) == 3
                    && countMatches(votes, "yes") == 2
                    && countMatches(votes, "maybe") == 0;
        });

        allPass &= check("repeat", () -> {
            List<String> stars = repeat("*", 3);
            List<Integer> zeros = repeat(0, 2);
            return stars.equals(List.of("*", "*", "*"))
                    && zeros.equals(List.of(0, 0))
                    && repeat("x", 0).isEmpty();
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-13s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-13s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
