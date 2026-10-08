import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Exercise 3 (Hard): find and fix the bugs in a bank's fraud-alert rules.
 *
 * A teammate built the rules from small lambdas. The code compiles and runs,
 * but it flags the wrong transactions and charges the wrong amount.
 *
 * REQUIRED BEHAVIOUR
 *   A transaction is RISKY if it is:
 *       international,
 *       OR (large, meaning over $5,000, AND made at night, between 00:00 and 05:59)
 *   A transaction between the SAME account (moving money to yourself) is never flagged.
 *
 *   The amount charged in rupees: convert dollars to rupees at 83 per dollar,
 *   THEN add a flat Rs 100 processing fee.
 *
 * EXPECTED OUTPUT (after your fix)
 *   Flagged: [T1, T2]
 *   T1 charged: Rs 16700.0
 *   PASS
 *
 * There are THREE separate bugs, all in the rule definitions. Fix only those;
 * the Transaction class and main are correct.
 *
 * HINTS (read one at a time if you're stuck)
 *   1. a.or(b).and(c) runs left to right. Which way does it group?
 *   2. f.compose(g) and f.andThen(g): which function runs first?
 *   3. The account numbers are Integer objects, not ints. What does == compare for objects?
 *
 * Run: java exercises/Exercise3_FraudAlertBug.java
 */
public class Exercise3_FraudAlertBug {

    static class Transaction {
        private final String id;
        private final Integer fromAccount;
        private final Integer toAccount;
        private final double amountUsd;
        private final boolean international;
        private final int hourOfDay;   // 0-23

        Transaction(String id, Integer fromAccount, Integer toAccount, double amountUsd,
                    boolean international, int hourOfDay) {
            this.id = id;
            this.fromAccount = fromAccount;
            this.toAccount = toAccount;
            this.amountUsd = amountUsd;
            this.international = international;
            this.hourOfDay = hourOfDay;
        }

        String getId() { return id; }
        Integer getFromAccount() { return fromAccount; }
        Integer getToAccount() { return toAccount; }
        double getAmountUsd() { return amountUsd; }
        boolean isInternational() { return international; }
        int getHourOfDay() { return hourOfDay; }

        @Override
        public String toString() {
            return id;
        }
    }

    // ---- The buggy rules are below. Fix them. ----

    static final Predicate<Transaction> IS_INTERNATIONAL = Transaction::isInternational;
    static final Predicate<Transaction> IS_LARGE = t -> t.getAmountUsd() > 5000;
    static final Predicate<Transaction> IS_NIGHT = t -> t.getHourOfDay() < 6;

    static final Predicate<Transaction> IS_RISKY = IS_INTERNATIONAL.or(IS_LARGE.and(IS_NIGHT));

    static final BiPredicate<Integer, Integer> SAME_ACCOUNT = Integer::equals;

    static final Function<Double, Double> TO_RUPEES = usd -> usd * 83;
    static final Function<Double, Double> ADD_FEE = rupees -> rupees + 100;

    static final Function<Double, Double> AMOUNT_CHARGED = TO_RUPEES.andThen(ADD_FEE);

    // ---- The code below is correct. ----

    static final Predicate<Transaction> SHOULD_ALERT =
            t -> !SAME_ACCOUNT.test(t.getFromAccount(), t.getToAccount()) && IS_RISKY.test(t);

    public static void main(String[] args) {
        List<Transaction> transactions = List.of(
                //               id    from     to       usd     intl   hour
                new Transaction("T1", 100234, 558812,   200.0,  true,  14),   // international
                new Transaction("T2", 100234, 774410,  9000.0,  false,  2),   // large at night
                new Transaction("T3", 100234, 774410,  9000.0,  false, 15),   // large, daytime
                new Transaction("T4", 330981, 558812,    50.0,  false,  3),   // small, night
                new Transaction("T5", 100234, 100234, 12000.0,  false,  1)    // moving own money
        );

        List<Transaction> flagged = new ArrayList<>();
        for (Transaction t : transactions) {
            if (SHOULD_ALERT.test(t)) {
                flagged.add(t);
            }
        }
        System.out.println("Flagged: " + flagged);

        double charged = AMOUNT_CHARGED.apply(transactions.get(0).getAmountUsd());
        System.out.println("T1 charged: Rs " + charged);

        boolean pass = flagged.toString().equals("[T1, T2]") && charged == 16700.0;
        System.out.println(pass ? "PASS" : "FAIL: expected Flagged: [T1, T2] and Rs 16700.0");
    }
}
