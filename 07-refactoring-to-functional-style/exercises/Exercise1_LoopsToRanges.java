import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 1 (Easy): counting loops to IntStream.
 *
 * TASK
 *   Each method below has an imperative version (done, don't change it). Write the functional
 *   version next to it: no for/while loops, no ArrayList, one stream pipeline each.
 *
 *     squares(n)                 1*1, 2*2, ..., n*n           squares(4)            -> [1, 4, 9, 16]
 *                                use IntStream.rangeClosed + map
 *
 *     steps(start, end, step)    start, start+step, ... up to and including end (step > 0)
 *                                                             steps(2, 20, 5)       -> [2, 7, 12, 17]
 *                                use the 3-argument IntStream.iterate
 *
 *     powersOfTwoBelow(limit)    1, 2, 4, 8, ... while less than limit
 *                                                             powersOfTwoBelow(40)  -> [1, 2, 4, 8, 16, 32]
 *                                use the 2-argument IntStream.iterate + takeWhile
 *
 *   The checks compare your version with the imperative one and with fixed answers.
 *
 * EXPECTED OUTPUT
 *   squares:          PASS
 *   steps:            PASS
 *   powersOfTwoBelow: PASS
 *   ALL PASS
 *
 * HINTS
 *   - An IntStream holds ints. To get a List<Integer>, call .boxed().toList().
 *   - rangeClosed(1, 0) is empty, so squares(0) needs no special case.
 *   - The imperative powersOfTwoBelow has the condition in the for header; for the exercise,
 *     treat it as "loop forever, break when p >= limit".
 *
 * Run: java exercises/Exercise1_LoopsToRanges.java
 */
public class Exercise1_LoopsToRanges {

    static List<Integer> squaresImperative(int n) {
        List<Integer> result = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            result.add(i * i);
        }
        return result;
    }

    static List<Integer> squares(int n) {
        // TODO: IntStream.rangeClosed
        return List.of();
    }

    static List<Integer> stepsImperative(int start, int end, int step) {
        List<Integer> result = new ArrayList<>();
        for (int i = start; i <= end; i += step) {
            result.add(i);
        }
        return result;
    }

    static List<Integer> steps(int start, int end, int step) {
        // TODO: IntStream.iterate(seed, hasNext, next)
        return List.of();
    }

    static List<Integer> powersOfTwoBelowImperative(int limit) {
        List<Integer> result = new ArrayList<>();
        for (int p = 1; ; p *= 2) {
            if (p >= limit) {
                break;
            }
            result.add(p);
        }
        return result;
    }

    static List<Integer> powersOfTwoBelow(int limit) {
        // TODO: IntStream.iterate(seed, next) + takeWhile
        return List.of();
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("squares", () ->
                squares(4).equals(List.of(1, 4, 9, 16))
                        && squares(0).isEmpty()
                        && squares(10).equals(squaresImperative(10)));

        allPass &= check("steps", () ->
                steps(2, 20, 5).equals(List.of(2, 7, 12, 17))
                        && steps(0, 15, 3).equals(List.of(0, 3, 6, 9, 12, 15))
                        && steps(5, 4, 1).isEmpty()
                        && steps(-10, 10, 4).equals(stepsImperative(-10, 10, 4)));

        allPass &= check("powersOfTwoBelow", () ->
                powersOfTwoBelow(40).equals(List.of(1, 2, 4, 8, 16, 32))
                        && powersOfTwoBelow(1).isEmpty()
                        && powersOfTwoBelow(1025).equals(powersOfTwoBelowImperative(1025)));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (RuntimeException e) {
            System.out.printf("%-17s FAIL (%s)%n", name + ":", e);
            return false;
        }
        System.out.printf("%-17s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
