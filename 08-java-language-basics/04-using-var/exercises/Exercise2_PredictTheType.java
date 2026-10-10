import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 2 (Medium): predict what var infers.
 *
 * TASK
 *   Each line in main declares a var. For each one, fill in your PREDICTION of the runtime class
 *   (as getClass().getSimpleName() would print it, e.g. "Integer" for an int) and its value
 *   (as String.valueOf would print it). Then run: the program compares your predictions with
 *   what Java actually did.
 *
 *     a    var a = 7 / 2;
 *     b    var b = 7 / 2.0;
 *     c    var c = 'A' + 2;
 *     d    var d = (char) ('A' + 2);
 *     e    var e = 1 + 2 + "3";
 *     f    var f = "1" + 2 + 3;
 *     g    var g = 3_000_000_000L / 1000;
 *     h    var h = new ArrayList<>(List.of(1, 2));     (just the class; value is printed as a list)
 *
 * EXPECTED OUTPUT
 *   a: PASS
 *   b: PASS
 *   ...
 *   h: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Primitives show up boxed: int -> Integer, double -> Double, char -> Character, long -> Long.
 *   - + is evaluated left to right. Once a String is involved, + means "concatenate".
 *   - A char plus an int is an int.
 *
 * Run: java exercises/Exercise2_PredictTheType.java
 */
public class Exercise2_PredictTheType {

    public static void main(String[] args) {
        var a = 7 / 2;
        var b = 7 / 2.0;
        var c = 'A' + 2;
        var d = (char) ('A' + 2);
        var e = 1 + 2 + "3";
        var f = "1" + 2 + 3;
        var g = 3_000_000_000L / 1000;
        var h = new ArrayList<>(List.of(1, 2));

        boolean allPass = true;
        // TODO: replace each "?" with your prediction: (variable, predicted class, predicted value)
        allPass &= check("a", a, "?", "?");
        allPass &= check("b", b, "?", "?");
        allPass &= check("c", c, "?", "?");
        allPass &= check("d", d, "?", "?");
        allPass &= check("e", e, "?", "?");
        allPass &= check("f", f, "?", "?");
        allPass &= check("g", g, "?", "?");
        allPass &= check("h", h, "?", "[1, 2]");
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Object actual, String predictedClass, String predictedValue) {
        String actualClass = actual.getClass().getSimpleName();
        String actualValue = String.valueOf(actual);
        boolean ok = actualClass.equals(predictedClass) && actualValue.equals(predictedValue);
        if (ok) {
            System.out.println(name + ": PASS");
        } else {
            System.out.println(name + ": FAIL (you said " + predictedClass + " " + predictedValue
                    + ", Java says " + actualClass + " " + actualValue + ")");
        }
        return ok;
    }
}
