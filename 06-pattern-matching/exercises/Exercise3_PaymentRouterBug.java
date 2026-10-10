/**
 * Exercise 3 (Hard): the payment router.
 *
 * THE SITUATION
 *   PaymentRouter.route(payment) decides where each payment goes. The written rules are:
 *
 *       null payment                 -> "REJECT_MISSING"
 *       card, amount over 50,000     -> "BLOCK"            (fraud team must approve)
 *       card, amount over 1,000      -> "REVIEW"
 *       any other card               -> "CARD_GATEWAY"
 *       UPI                          -> "UPI_GATEWAY"
 *       net banking                  -> "BANK_GATEWAY"
 *       crypto                       -> "REJECT"           (company policy; Crypto was added last month)
 *
 *   Three incidents this week:
 *     1. A 75,000 card payment went to REVIEW instead of BLOCK.
 *     2. Crypto payments are being sent to the card gateway.
 *     3. A request with no payment crashed the router with a NullPointerException.
 *
 * TASK
 *   1. Run it and match each FAIL to an incident.
 *   2. Fix route():
 *      - Order the guards so the strictest rule is checked first.
 *      - DELETE the default. Compile, and read the error: the compiler now tells you which
 *        Payment type isn't handled. Handle it explicitly. From now on, adding a new Payment type
 *        will break the compile instead of silently routing money to the wrong place.
 *      - Handle null inside the switch (no if-statement before it).
 *   3. When you're done, the switch must have no default branch.
 *
 * EXPECTED OUTPUT (after the fix)
 *   Card 300.0         -> CARD_GATEWAY   PASS
 *   Card 5000.0        -> REVIEW         PASS
 *   Card 75000.0       -> BLOCK          PASS
 *   Upi 75000.0        -> UPI_GATEWAY    PASS
 *   NetBanking 900.0   -> BANK_GATEWAY   PASS
 *   Crypto 250.0       -> REJECT         PASS
 *   null               -> REJECT_MISSING PASS
 *   ALL PASS
 *
 * HINTS
 *   - Cases are tried top to bottom; the first one that matches wins. Is 75,000 > 1,000?
 *   - Why didn't the compiler complain when Crypto was added to the sealed interface?
 *   - A pattern switch accepts "case null ->".
 *
 * Run: java exercises/Exercise3_PaymentRouterBug.java
 */
public class Exercise3_PaymentRouterBug {

    sealed interface Payment permits Card, Upi, NetBanking, Crypto {}
    record Card(String last4, double amount) implements Payment {}
    record Upi(String vpa, double amount) implements Payment {}
    record NetBanking(String bank, double amount) implements Payment {}
    record Crypto(String coin, double amount) implements Payment {}

    static class PaymentRouter {
        static String route(Payment payment) {
            return switch (payment) {
                case Card c when c.amount() > 1_000  -> "REVIEW";
                case Card c when c.amount() > 50_000 -> "BLOCK";
                case Card c                          -> "CARD_GATEWAY";
                case Upi u                           -> "UPI_GATEWAY";
                case NetBanking n                    -> "BANK_GATEWAY";
                default                              -> "CARD_GATEWAY";   // "safe fallback"
            };
        }
    }

    // ---- Don't change anything below this line. ----

    public static void main(String[] args) {
        Object[][] cases = {
                {new Card("4242", 300), "CARD_GATEWAY"},
                {new Card("4242", 5_000), "REVIEW"},
                {new Card("4242", 75_000), "BLOCK"},
                {new Upi("ravi@okbank", 75_000), "UPI_GATEWAY"},
                {new NetBanking("HDFC", 900), "BANK_GATEWAY"},
                {new Crypto("BTC", 250), "REJECT"},
                {null, "REJECT_MISSING"},
        };

        boolean allPass = true;
        for (Object[] c : cases) {
            Payment payment = (Payment) c[0];
            String expected = (String) c[1];
            String actual;
            try {
                actual = PaymentRouter.route(payment);
            } catch (NullPointerException e) {
                actual = "crashed: NullPointerException";
            }
            boolean ok = actual.equals(expected);
            allPass &= ok;
            System.out.printf("%-18s -> %-14s %s%n", label(payment), actual,
                    ok ? "PASS" : "FAIL (expected " + expected + ")");
        }
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static String label(Payment payment) {
        if (payment == null) {
            return "null";
        }
        double amount = switch (payment) {
            case Card c -> c.amount();
            case Upi u -> u.amount();
            case NetBanking n -> n.amount();
            case Crypto x -> x.amount();
        };
        return payment.getClass().getSimpleName() + " " + amount;
    }
}
