/**
 * Exercise 2 (Medium): do-while, break, and while.
 *
 * TASK
 *   1. attemptsUntilSuccess(results, maxAttempts)
 *      A flaky service answers each call with the next value in results (true = success).
 *      Call it with service.call() using a do-while loop. Stop at the first success or after
 *      maxAttempts calls. Return the number of calls made, or -1 if none succeeded.
 *        results {false, false, true}, max 5  -> 3
 *        results {false, false, false}, max 2 -> -1   (only 2 calls made)
 *
 *   2. indexOfFirstOverLimit(amounts, limit)
 *      Index of the first transaction strictly greater than limit, or -1. Use a for loop with
 *      break (not return inside the loop).
 *
 *   3. yearsToDouble(ratePercent)
 *      Starting from 1000, add ratePercent% interest each year (compounded, as a double).
 *      Return how many whole years until the amount is at least 2000. Use a while loop.
 *        yearsToDouble(10) -> 8      yearsToDouble(100) -> 1
 *
 * EXPECTED OUTPUT
 *   attempts:     PASS
 *   overLimit:    PASS
 *   yearsDouble:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - With do-while, the call happens before the condition is checked, which is exactly what
 *     "at least one attempt" means.
 *   - In indexOfFirstOverLimit, keep a result variable declared BEFORE the loop.
 *
 * Run: java exercises/Exercise2_RetryAndSearch.java
 */
public class Exercise2_RetryAndSearch {

    static class FlakyService {
        private final boolean[] results;
        private int calls = 0;

        FlakyService(boolean[] results) {
            this.results = results;
        }

        boolean call() {
            boolean result = calls < results.length && results[calls];
            calls++;
            return result;
        }

        int calls() {
            return calls;
        }
    }

    static int attemptsUntilSuccess(FlakyService service, int maxAttempts) {
        return 0; // TODO: use service.call() in a do-while loop
    }

    static int indexOfFirstOverLimit(int[] amounts, int limit) {
        return -2; // TODO
    }

    static int yearsToDouble(double ratePercent) {
        return 0; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;

        FlakyService s1 = new FlakyService(new boolean[] {false, false, true});
        FlakyService s2 = new FlakyService(new boolean[] {false, false, false});
        FlakyService s3 = new FlakyService(new boolean[] {true});
        allPass &= check("attempts:", attemptsUntilSuccess(s1, 5) == 3 && s1.calls() == 3
                && attemptsUntilSuccess(s2, 2) == -1 && s2.calls() == 2
                && attemptsUntilSuccess(s3, 3) == 1 && s3.calls() == 1);

        allPass &= check("overLimit:", indexOfFirstOverLimit(new int[] {50, 200, 900, 1200}, 500) == 2
                && indexOfFirstOverLimit(new int[] {10, 20}, 500) == -1
                && indexOfFirstOverLimit(new int[] {}, 0) == -1);

        allPass &= check("yearsDouble:", yearsToDouble(10) == 8 && yearsToDouble(100) == 1
                && yearsToDouble(7) == 11);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-13s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
