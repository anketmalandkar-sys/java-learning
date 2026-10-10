import java.util.Random;

/**
 * Exercise 3 (Hard): a small Math toolkit.
 *
 * TASK
 *   Use Math methods (no loops except in rollUntil, no if-chains for rounding).
 *
 *     pageCount(items, perPage)   pages needed to show every item       pageCount(41, 10) -> 5
 *                                 0 items -> 0 pages
 *     roundToStep(value, step)    round to the nearest multiple of step (halves round up)
 *                                 roundToStep(17, 5) -> 15, roundToStep(18, 5) -> 20,
 *                                 roundToStep(2.26, 0.05) -> 2.25 (compare with a tolerance)
 *     distance(x1, y1, x2, y2)    straight-line distance between two points  (0,0)-(3,4) -> 5.0
 *     bearingDegrees(dx, dy)      the angle of the vector (dx, dy) in degrees, 0..360,
 *                                 measured counter-clockwise from the positive x axis
 *                                 (1,0) -> 0, (0,1) -> 90, (-1,0) -> 180, (0,-1) -> 270
 *     rollUntil(rng, target)      roll a 6-sided die (rng.nextInt(6) + 1) until it shows target;
 *                                 return how many rolls it took. Use the Random you're given,
 *                                 so the result is repeatable.
 *
 * EXPECTED OUTPUT
 *   pageCount:      PASS
 *   roundToStep:    PASS
 *   distance:       PASS
 *   bearingDegrees: PASS
 *   rollUntil:      PASS
 *   ALL PASS
 *
 * HINTS
 *   - pageCount: Math.ceil on a DOUBLE division, then cast. Or the integer trick (a + b - 1) / b.
 *   - roundToStep: divide by the step, Math.round, multiply back.
 *   - Math.hypot(dx, dy) is sqrt(dx*dx + dy*dy) without overflow.
 *   - Math.atan2(y, x) returns radians in -PI..PI. Convert with Math.toDegrees, then fix negatives.
 *
 * Run: java exercises/Exercise3_MathToolkit.java
 */
public class Exercise3_MathToolkit {

    static int pageCount(int items, int perPage) {
        return 0; // TODO
    }

    static double roundToStep(double value, double step) {
        return 0; // TODO
    }

    static double distance(double x1, double y1, double x2, double y2) {
        return 0; // TODO
    }

    static double bearingDegrees(double dx, double dy) {
        return -1; // TODO
    }

    static int rollUntil(Random rng, int target) {
        return 0; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("pageCount:", pageCount(41, 10) == 5 && pageCount(40, 10) == 4
                && pageCount(0, 10) == 0 && pageCount(1, 10) == 1);
        allPass &= check("roundToStep:", close(roundToStep(17, 5), 15) && close(roundToStep(18, 5), 20)
                && close(roundToStep(17.5, 5), 20) && close(roundToStep(2.26, 0.05), 2.25));
        allPass &= check("distance:", close(distance(0, 0, 3, 4), 5) && close(distance(1, 1, 1, 1), 0)
                && close(distance(-1, -1, 2, 3), 5));
        allPass &= check("bearingDegrees:", close(bearingDegrees(1, 0), 0) && close(bearingDegrees(0, 1), 90)
                && close(bearingDegrees(-1, 0), 180) && close(bearingDegrees(0, -1), 270)
                && close(bearingDegrees(1, 1), 45));
        int expected = referenceRolls(new Random(7), 6);
        allPass &= check("rollUntil:", rollUntil(new Random(7), 6) == expected && expected > 0);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // The reference answer, computed the same way the task describes.
    static int referenceRolls(Random rng, int target) {
        int rolls = 0;
        int face;
        do {
            face = rng.nextInt(6) + 1;
            rolls++;
        } while (face != target);
        return rolls;
    }

    static boolean close(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
