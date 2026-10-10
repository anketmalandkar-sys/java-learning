import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exercise 2 (Medium): a Money value record.
 *
 * TASK
 *   Complete record Money(long paise, String currency) implements Comparable<Money>:
 *
 *   1. Compact constructor: currency must not be null or blank (IllegalArgumentException
 *      "currency required"); normalise it to upper case without spaces (" inr " -> "INR").
 *   2. static Money of(String rupees, String currency): parse a decimal string like "12.50"
 *      into paise (1250). Use java.math.BigDecimal: new BigDecimal(rupees).movePointRight(2)
 *      .longValueExact(). More than 2 decimals ("1.234") must throw ArithmeticException.
 *   3. Money plus(Money other): the sum. Different currencies -> IllegalArgumentException
 *      ("currency mismatch: INR vs USD").
 *   4. Money times(int qty).
 *   5. compareTo: by paise (only call it on the same currency; no check needed).
 *   6. Override toString() to return "INR 12.50" (always two decimals).
 *
 * EXPECTED OUTPUT
 *   normalise: PASS
 *   of:        PASS
 *   math:      PASS
 *   mismatch:  PASS
 *   toString:  PASS
 *   sort:      PASS
 *   ALL PASS
 *
 * HINTS
 *   - toString: paise / 100 and paise % 100, padded with String.format("%d.%02d", ...).
 *   - Overriding toString in a record is allowed; equals and hashCode stay generated.
 *
 * Run: java exercises/Exercise2_MoneyValue.java
 */
public class Exercise2_MoneyValue {

    record Money(long paise, String currency) implements Comparable<Money> {

        // TODO: compact constructor

        static Money of(String rupees, String currency) {
            return null; // TODO
        }

        Money plus(Money other) {
            return null; // TODO
        }

        Money times(int qty) {
            return null; // TODO
        }

        @Override
        public int compareTo(Money other) {
            return 0; // TODO
        }

        // TODO: override toString
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("normalise:", safe(() -> new Money(100, " inr ").currency().equals("INR")
                && rejects(() -> new Money(1, "  "), IllegalArgumentException.class)
                && rejects(() -> new Money(1, null), IllegalArgumentException.class)));
        allPass &= check("of:", safe(() -> Money.of("12.50", "inr").paise() == 1250 && Money.of("7", "INR").paise() == 700
                && rejects(() -> Money.of("1.234", "INR"), ArithmeticException.class)));
        allPass &= check("math:", safe(() -> Money.of("10.25", "INR").plus(Money.of("0.75", "INR")).equals(new Money(1100, "INR"))
                && Money.of("2.50", "INR").times(3).paise() == 750));
        allPass &= check("mismatch:", safe(() -> rejects(() -> new Money(1, "INR").plus(new Money(1, "USD")), IllegalArgumentException.class)));
        allPass &= check("toString:", safe(() -> "INR 12.50".equals(Money.of("12.5", "INR").toString())
                && "INR 0.05".equals(new Money(5, "INR").toString())));
        allPass &= check("sort:", safe(() -> {
            List<Money> prices = new ArrayList<>(List.of(new Money(500, "INR"), new Money(50, "INR"), new Money(5000, "INR")));
            Collections.sort(prices);
            return prices.get(0).paise() == 50 && prices.get(2).paise() == 5000;
        }));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    interface Check {
        boolean run();
    }

    static boolean safe(Check c) {
        try {
            return c.run();
        } catch (RuntimeException e) {
            return false;
        }
    }

    static boolean rejects(Runnable action, Class<? extends RuntimeException> type) {
        try {
            action.run();
            return false;
        } catch (RuntimeException e) {
            return type.isInstance(e);
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-10s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
