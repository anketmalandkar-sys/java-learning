/**
 * Exercise 3 (Hard): wrong change from a vending machine.
 *
 * TASK
 *   coinsForChangeDouble() pays change in 5-cent coins, using double dollars. A customer pays
 *   $1.00 for a $0.90 snack and gets ONE coin instead of TWO. Run the file to see it.
 *
 *   Write coinsForChange(paidCents, priceCents) that does the same job with long cents only:
 *     - Returns how many 5-cent coins to give back.
 *     - Returns -1 if the customer paid too little.
 *     - The machine only has 5-cent coins. If the change isn't a multiple of 5, round DOWN
 *       (the machine keeps the extra cents).
 *
 *   Then write formatCents(cents) that turns a number of cents into a dollar string with two
 *   decimals, without using double or float:
 *     formatCents(1005) -> "$10.05"      formatCents(7) -> "$0.07"
 *
 * EXPECTED OUTPUT (the double line shows the bug and stays wrong)
 *   double version gives 1 coin(s) for $1.00 - $0.90
 *   change 100-90:     PASS
 *   change 200-35:     PASS
 *   change 100-100:    PASS
 *   change 100-92:     PASS
 *   paid too little:   PASS
 *   format 1005:       PASS
 *   format 7:          PASS
 *   ALL PASS
 *
 * HINTS
 *   - 1.00 - 0.90 in double is 0.09999999999999998; dividing by 0.05 gives 1.999..., and the
 *     cast to int cuts it to 1.
 *   - Integer division already rounds down: 8 / 5 == 1.
 *   - For two decimals, cents % 100 might be 5: you need "05", not "5".
 *
 * Run: java exercises/Exercise3_ChangeMachine.java
 */
public class Exercise3_ChangeMachine {

    static int coinsForChangeDouble(double paid, double price) {
        double change = paid - price;
        return (int) (change / 0.05);
    }

    static long coinsForChange(long paidCents, long priceCents) {
        return 0; // TODO
    }

    static String formatCents(long cents) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        System.out.println("double version gives " + coinsForChangeDouble(1.00, 0.90)
                + " coin(s) for $1.00 - $0.90");

        boolean allPass = true;
        allPass &= check("change 100-90:", coinsForChange(100, 90) == 2);
        allPass &= check("change 200-35:", coinsForChange(200, 35) == 33);
        allPass &= check("change 100-100:", coinsForChange(100, 100) == 0);
        allPass &= check("change 100-92:", coinsForChange(100, 92) == 1);
        allPass &= check("paid too little:", coinsForChange(50, 90) == -1);
        allPass &= check("format 1005:", "$10.05".equals(formatCents(1005)));
        allPass &= check("format 7:", "$0.07".equals(formatCents(7)));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-18s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
