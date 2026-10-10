/**
 * Exercise 3 (Hard): an invoice calculator broken by var.
 *
 * TASK
 *   A developer "cleaned up" invoiceTotal() by switching every declaration to var. It still
 *   compiles, but the totals are wrong. Find the two inference traps and fix them. Keep using
 *   var where it's harmless; the point is to choose the right initializer.
 *
 *   The rules:
 *     - subtotal = sum of price[i] * quantity[i]
 *     - discount = discountPercent% of the subtotal
 *     - tax      = 18% of (subtotal - discount)
 *     - total    = subtotal - discount + tax, rounded to 2 decimals
 *
 *   Then write describe(total): return "SMALL" under 500, "MEDIUM" from 500 up to (but not
 *   including) 5000, "LARGE" otherwise. Use var for at least one local in it, where it makes
 *   sense.
 *
 * EXPECTED OUTPUT
 *   no discount: PASS
 *   10% off:     PASS
 *   describe:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - What type is "var subtotal = 0;"? What happens on "subtotal += 19.99"?
 *   - What is 10 / 100 in int arithmetic?
 *   - Print the intermediate values if you're stuck.
 *
 * Run: java exercises/Exercise3_InvoiceBugs.java
 */
public class Exercise3_InvoiceBugs {

    static double invoiceTotal(double[] prices, int[] quantities, int discountPercent) {
        var subtotal = 0;
        for (var i = 0; i < prices.length; i++) {
            subtotal += prices[i] * quantities[i];
        }
        var discountRate = discountPercent / 100;
        var discount = subtotal * discountRate;
        var taxable = subtotal - discount;
        var tax = taxable * 0.18;
        var total = taxable + tax;
        return Math.round(total * 100) / 100.0;
    }

    static String describe(double total) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        double[] prices = {19.99, 5.50, 120.00};
        int[] quantities = {3, 4, 1};
        // subtotal = 59.97 + 22.00 + 120.00 = 201.97

        boolean allPass = true;
        allPass &= check("no discount:", invoiceTotal(prices, quantities, 0) == 238.32);
        allPass &= check("10% off:", invoiceTotal(prices, quantities, 10) == 214.49);
        allPass &= check("describe:", describe(499.99).equals("SMALL") && describe(500).equals("MEDIUM")
                && describe(4999.99).equals("MEDIUM") && describe(5000).equals("LARGE"));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
