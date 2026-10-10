/**
 * Exercise 3 (Hard): debug a command switch.
 *
 * TASK
 *   A vending machine takes text commands. handle(command) updates the machine's state and
 *   returns a reply. The intended behaviour:
 *
 *     "coin"    credit += 10, reply "Credit: <credit>"
 *     "refund"  reply "Refunded <credit>", then credit = 0
 *     "buy"     if credit >= 30: credit -= 30, stock -= 1, reply "Enjoy!"
 *               if stock is 0: reply "Sold out" (credit unchanged)
 *               otherwise: reply "Need <30 - credit> more"
 *     anything else (including null): reply "Unknown command", nothing changes
 *
 *   Commands are case-insensitive. The current code has these problems. Find and fix them all,
 *   keeping a classic switch statement (case X: ... break;):
 *     - some commands do more than they should
 *     - unknown commands return an empty reply
 *     - a null command crashes the program
 *     - one check is in the wrong order
 *
 * EXPECTED OUTPUT
 *   coin:       PASS
 *   buy:        PASS
 *   not enough: PASS
 *   refund:     PASS
 *   sold out:   PASS
 *   unknown:    PASS
 *   null:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - Trace "coin" by hand: which lines run after credit += 10?
 *   - What should happen when the machine is empty AND the customer has enough credit?
 *
 * Run: java exercises/Exercise3_VendingCommands.java
 */
public class Exercise3_VendingCommands {

    static final int PRICE = 30;
    static int credit = 0;
    static int stock = 2;

    static String handle(String command) {
        String reply = "";
        switch (command.toLowerCase()) {
            case "coin":
                credit += 10;
                reply = "Credit: " + credit;
            case "refund":
                reply = "Refunded " + credit;
                credit = 0;
                break;
            case "buy":
                if (credit >= PRICE) {
                    credit -= PRICE;
                    stock -= 1;
                    reply = "Enjoy!";
                } else if (stock == 0) {
                    reply = "Sold out";
                } else {
                    reply = "Need " + (PRICE - credit) + " more";
                }
                break;
        }
        return reply;
    }

    static void reset(int newCredit, int newStock) {
        credit = newCredit;
        stock = newStock;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        reset(0, 2);
        String r1 = handle("coin");
        String r2 = handle("COIN");
        allPass &= check("coin:", "Credit: 10".equals(r1) && "Credit: 20".equals(r2) && credit == 20);

        reset(30, 2);
        allPass &= check("buy:", "Enjoy!".equals(handle("buy")) && credit == 0 && stock == 1);

        reset(10, 2);
        allPass &= check("not enough:", "Need 20 more".equals(handle("Buy")) && credit == 10 && stock == 2);

        reset(40, 2);
        allPass &= check("refund:", "Refunded 40".equals(handle("refund")) && credit == 0);

        reset(50, 0);
        allPass &= check("sold out:", "Sold out".equals(handle("buy")) && credit == 50 && stock == 0);

        reset(10, 2);
        allPass &= check("unknown:", "Unknown command".equals(handle("dance")) && credit == 10);

        reset(10, 2);
        String nullReply;
        try {
            nullReply = handle(null);
        } catch (NullPointerException e) {
            nullReply = "crashed";
        }
        allPass &= check("null:", "Unknown command".equals(nullReply) && credit == 10);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-11s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
