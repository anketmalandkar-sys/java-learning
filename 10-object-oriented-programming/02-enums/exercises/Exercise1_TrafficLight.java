/**
 * Exercise 1 (Easy): a basic enum.
 *
 * TASK
 *   1. Declare enum Light with the constants RED, GREEN, YELLOW (in that order).
 *   2. Give it a method next(): RED -> GREEN -> YELLOW -> RED. Write it with values() and
 *      ordinal(), so it keeps working if a constant is added at the end.
 *   3. Write secondsFor(light) as a switch EXPRESSION with no default: RED 30, GREEN 25, YELLOW 5.
 *   4. Write cycleSeconds(): the sum of secondsFor over every constant, using values().
 *
 *   Then delete the placeholder Light enum below and uncomment the checks in main.
 *
 * EXPECTED OUTPUT
 *   next:   PASS
 *   wrap:   PASS
 *   timing: PASS
 *   cycle:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - (ordinal() + 1) % values().length gives the next position, wrapping to 0.
 *
 * Run: java exercises/Exercise1_TrafficLight.java
 */
public class Exercise1_TrafficLight {

    // TODO: replace this placeholder with the real enum, including next()
    enum Light { PLACEHOLDER }

    static int secondsFor(Light light) {
        return 0; // TODO: switch expression
    }

    static int cycleSeconds() {
        return 0; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        // uncomment:
        // allPass &= check("next:", Light.RED.next() == Light.GREEN && Light.GREEN.next() == Light.YELLOW);
        // allPass &= check("wrap:", Light.YELLOW.next() == Light.RED);
        // allPass &= check("timing:", secondsFor(Light.RED) == 30 && secondsFor(Light.GREEN) == 25
        //         && secondsFor(Light.YELLOW) == 5);
        allPass &= check("cycle:", cycleSeconds() == 60);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-7s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
