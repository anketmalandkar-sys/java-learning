/**
 * Exercise 1 (Easy): if/else-if, for with continue, while.
 *
 * TASK
 *   grade(score)           "A" for 90+, "B" for 75..89, "C" for 50..74, "F" below 50.
 *                          Return "INVALID" for scores below 0 or above 100. Use an if/else-if chain.
 *   sumSkippingMultiplesOf3(n)
 *                          sum of 1..n, skipping numbers divisible by 3. Use a for loop and continue.
 *                          sumSkippingMultiplesOf3(7) -> 1+2+4+5+7 = 19
 *   halvingSteps(n)        how many times you can halve n (integer division) before it reaches 0.
 *                          Use a while loop.   halvingSteps(10) -> 4   (10, 5, 2, 1, 0)
 *
 * EXPECTED OUTPUT
 *   grade:        PASS
 *   sumSkipping:  PASS
 *   halvingSteps: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Check the invalid range first.
 *   - halvingSteps(0) is 0: the loop body never runs.
 *
 * Run: java exercises/Exercise1_GradesAndLoops.java
 */
public class Exercise1_GradesAndLoops {

    static String grade(int score) {
        return ""; // TODO
    }

    static int sumSkippingMultiplesOf3(int n) {
        return 0; // TODO
    }

    static int halvingSteps(int n) {
        return -1; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("grade:", "A".equals(grade(90)) && "B".equals(grade(89)) && "B".equals(grade(75))
                && "C".equals(grade(50)) && "F".equals(grade(49)) && "F".equals(grade(0))
                && "INVALID".equals(grade(-1)) && "INVALID".equals(grade(101)));
        allPass &= check("sumSkipping:", sumSkippingMultiplesOf3(7) == 19 && sumSkippingMultiplesOf3(2) == 3
                && sumSkippingMultiplesOf3(0) == 0);
        allPass &= check("halvingSteps:", halvingSteps(10) == 4 && halvingSteps(1) == 1
                && halvingSteps(0) == 0 && halvingSteps(1024) == 11);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-13s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
