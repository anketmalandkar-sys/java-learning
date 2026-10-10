/**
 * Exercise 2 (Medium): yield, throw, and an exhaustive enum switch.
 *
 * TASK
 *   Write shippingCost(zone, weightKg) as ONE switch expression over Zone, with NO default
 *   (list every constant, so the compiler checks you covered them all):
 *
 *     LOCAL          flat 40
 *     REGIONAL       60 for up to 2 kg, plus 15 for every started kg above 2
 *                    (2.5 kg -> 60 + 15 = 75, 4 kg -> 60 + 30 = 90)
 *     NATIONAL       100 + 20 per started kg
 *                    (1 kg -> 120, 1.2 kg -> 140)
 *     INTERNATIONAL  throw IllegalArgumentException("International shipping not available")
 *
 *   Use a block with yield for REGIONAL and NATIONAL. "Started kg" means round up:
 *   (int) Math.ceil(weightKg).
 *
 *   Then: uncomment the EXPRESS constant in the enum, and run. The compile error tells you
 *   which switch needs updating. Add EXPRESS -> 250 and run again.
 *
 * EXPECTED OUTPUT
 *   local:         PASS
 *   regional:      PASS
 *   national:      PASS
 *   international: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Don't use return inside a case block; that's what yield is for.
 *   - throw can be the whole right-hand side of an arrow: case X -> throw new ...;
 *
 * Run: java exercises/Exercise2_ShippingYield.java
 */
public class Exercise2_ShippingYield {

    enum Zone { LOCAL, REGIONAL, NATIONAL, INTERNATIONAL /*, EXPRESS */ }

    static int shippingCost(Zone zone, double weightKg) {
        return 0; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("local:", shippingCost(Zone.LOCAL, 0.5) == 40 && shippingCost(Zone.LOCAL, 30) == 40);
        allPass &= check("regional:", shippingCost(Zone.REGIONAL, 1) == 60 && shippingCost(Zone.REGIONAL, 2) == 60
                && shippingCost(Zone.REGIONAL, 2.5) == 75 && shippingCost(Zone.REGIONAL, 4) == 90);
        allPass &= check("national:", shippingCost(Zone.NATIONAL, 1) == 120
                && shippingCost(Zone.NATIONAL, 1.2) == 140 && shippingCost(Zone.NATIONAL, 10) == 300);
        boolean threw;
        try {
            shippingCost(Zone.INTERNATIONAL, 1);
            threw = false;
        } catch (IllegalArgumentException e) {
            threw = "International shipping not available".equals(e.getMessage());
        }
        allPass &= check("international:", threw);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-14s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
