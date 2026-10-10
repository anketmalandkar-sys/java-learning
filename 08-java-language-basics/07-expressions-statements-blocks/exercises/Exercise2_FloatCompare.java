/**
 * Exercise 2 (Medium): comparing floating-point results.
 *
 * TASK
 *   1. nearlyEqual(a, b, tolerance): true when a and b differ by at most tolerance.
 *        nearlyEqual(0.1 + 0.2, 0.3, 1e-9) -> true
 *
 *   2. fitsBudgetBuggy() compares a double total with == and wrongly says three items at
 *      0.10, 0.20 and 0.30 don't add up to a 0.60 budget. Write fitsBudget(prices, budget)
 *      that returns true when the total is less than OR equal to the budget, using
 *      nearlyEqual with a tolerance of 1e-9 for the "equal" part.
 *
 *   3. totalCents(prices): add up prices given in rupees (e.g. 19.99) and return the total
 *      as an exact long number of paise. Convert each price with Math.round(price * 100)
 *      BEFORE adding, so rounding errors can't build up.
 *        totalCents({0.10, 0.20, 0.30}) -> 60
 *
 * EXPECTED OUTPUT
 *   buggy says 0.1+0.2+0.3 fits 0.6? false
 *   nearlyEqual: PASS
 *   fitsBudget:  PASS
 *   totalCents:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - Math.abs(a - b) is the distance between two numbers.
 *   - "less than or nearly equal" is two conditions joined with ||.
 *
 * Run: java exercises/Exercise2_FloatCompare.java
 */
public class Exercise2_FloatCompare {

    static boolean fitsBudgetBuggy(double[] prices, double budget) {
        double total = 0;
        for (double p : prices) {
            total += p;
        }
        return total < budget || total == budget;
    }

    static boolean nearlyEqual(double a, double b, double tolerance) {
        return false; // TODO
    }

    static boolean fitsBudget(double[] prices, double budget) {
        return false; // TODO
    }

    static long totalCents(double[] prices) {
        return 0; // TODO
    }

    public static void main(String[] args) {
        double[] cart = {0.10, 0.20, 0.30};
        System.out.println("buggy says 0.1+0.2+0.3 fits 0.6? " + fitsBudgetBuggy(cart, 0.6));

        boolean allPass = true;
        allPass &= check("nearlyEqual:", nearlyEqual(0.1 + 0.2, 0.3, 1e-9)
                && !nearlyEqual(0.3, 0.31, 1e-9) && nearlyEqual(5, 5, 0));
        allPass &= check("fitsBudget:", fitsBudget(cart, 0.6) && fitsBudget(cart, 1.0)
                && !fitsBudget(cart, 0.59));
        allPass &= check("totalCents:", totalCents(cart) == 60
                && totalCents(new double[] {19.99, 0.01, 4.10}) == 2410
                && totalCents(new double[] {}) == 0);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
