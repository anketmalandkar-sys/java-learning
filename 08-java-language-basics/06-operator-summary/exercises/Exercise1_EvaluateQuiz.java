/**
 * Exercise 1 (Easy): predict the value.
 *
 * TASK
 *   For each expression, work out the value ON PAPER using the precedence table in the
 *   README, then type your prediction as a String. The program compares it with what Java
 *   computes.
 *
 *     e1   2 + 3 * 4
 *     e2   20 - 6 - 4
 *     e3   20 / 4 * 5
 *     e4   17 % 5 * 2
 *     e5   1 << 2 + 1
 *     e6   5 > 3 && 2 > 4 || 1 < 2
 *     e7   "A" + 1 + 2
 *     e8   1 + 2 + "A" + 1 + 2
 *
 * EXPECTED OUTPUT
 *   e1: PASS
 *   ...
 *   e8: PASS
 *   ALL PASS
 *
 * Run: java exercises/Exercise1_EvaluateQuiz.java
 */
public class Exercise1_EvaluateQuiz {

    public static void main(String[] args) {
        boolean allPass = true;
        // TODO: replace each "?" with your prediction
        allPass &= check("e1", 2 + 3 * 4, "?");
        allPass &= check("e2", 20 - 6 - 4, "?");
        allPass &= check("e3", 20 / 4 * 5, "?");
        allPass &= check("e4", 17 % 5 * 2, "?");
        allPass &= check("e5", 1 << 2 + 1, "?");
        allPass &= check("e6", 5 > 3 && 2 > 4 || 1 < 2, "?");
        allPass &= check("e7", "A" + 1 + 2, "?");
        allPass &= check("e8", 1 + 2 + "A" + 1 + 2, "?");
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Object actual, String predicted) {
        boolean ok = String.valueOf(actual).equals(predicted);
        System.out.println(name + ": " + (ok ? "PASS" : "FAIL (you said " + predicted + ", Java says " + actual + ")"));
        return ok;
    }
}
