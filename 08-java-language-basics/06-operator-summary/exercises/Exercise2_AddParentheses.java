/**
 * Exercise 2 (Medium): fix the formulas with parentheses only.
 *
 * TASK
 *   Each method has the right operators in the right order, but the grouping is wrong.
 *   Fix each one by ADDING PARENTHESES ONLY: don't change, add, or remove operators,
 *   variables or numbers.
 *
 *     average(a, b)               the mean of a and b (integer division is fine)
 *     priceWithTaxShort(price, qty, taxPercent)
 *                                 price * qty, plus taxPercent% of that, in one multiplication:
 *                                 must give the same result as priceWithTax (already correct)
 *     label(name, x, y)           "name: " followed by the SUM of x and y    label("P", 2, 3) -> "P: 5"
 *     shouldAlert(temp, isNight, isWeekend)
 *                                 alert if temp is above 30 AND it's either night or weekend
 *
 * EXPECTED OUTPUT
 *   average:      PASS
 *   priceWithTax: PASS
 *   label:        PASS
 *   shouldAlert:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - Read each expression using the precedence table, then say what it SHOULD compute.
 *   - && binds tighter than ||.
 *
 * Run: java exercises/Exercise2_AddParentheses.java
 */
public class Exercise2_AddParentheses {

    static int average(int a, int b) {
        return a + b / 2; // TODO
    }

    static int priceWithTax(int price, int qty, int taxPercent) {
        return price * qty + price * qty * taxPercent / 100; // already right: leave it as a reference
    }

    static int priceWithTaxShort(int price, int qty, int taxPercent) {
        return price * qty * 100 + taxPercent / 100; // TODO: should equal priceWithTax
    }

    static String label(String name, int x, int y) {
        return name + ": " + x + y; // TODO
    }

    static boolean shouldAlert(int temp, boolean isNight, boolean isWeekend) {
        return temp > 30 && isNight || isWeekend; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("average:", average(10, 20) == 15 && average(3, 4) == 3);
        allPass &= check("priceWithTax:", priceWithTaxShort(200, 3, 18) == priceWithTax(200, 3, 18)
                && priceWithTaxShort(50, 2, 10) == 110);
        allPass &= check("label:", "P: 5".equals(label("P", 2, 3)));
        allPass &= check("shouldAlert:", shouldAlert(35, true, false) && shouldAlert(35, false, true)
                && !shouldAlert(20, false, true) && !shouldAlert(35, false, false));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-13s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
