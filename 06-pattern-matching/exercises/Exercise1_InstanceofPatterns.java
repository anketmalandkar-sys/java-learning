import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 1 (Easy): instanceof patterns.
 *
 * TASK
 *   Use instanceof with type patterns: no casts, and no switch in this exercise.
 *
 *     summarize(obj)    one line describing obj:
 *                         null                  -> "nothing"
 *                         a blank String        -> "empty text"
 *                         any other String      -> "text: <the string, stripped>"
 *                         an even Integer       -> "even number 4"
 *                         an odd Integer        -> "odd number 7"
 *                         an empty List         -> "empty list"
 *                         a non-empty List      -> "list starting with <first element>"
 *                         anything else         -> "unsupported: <simple class name>"
 *
 *     lengthOf(obj)     the length of obj if it's any CharSequence (String, StringBuilder, ...),
 *                       otherwise -1. Write it in the "early return" style:
 *                         if (!(obj instanceof CharSequence text)) { return -1; }
 *                         ...use text here...
 *
 *     Money.equals(o)   two Money objects are equal if amount and currency are equal.
 *                       Write it as ONE return statement using an instanceof pattern.
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   summarize: PASS
 *   lengthOf:  PASS
 *   equals:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - if (obj instanceof String s && s.isBlank()) ... checks the type and uses s in one condition.
 *   - A List of anything: obj instanceof List<?> list.
 *   - getClass().getSimpleName() gives "Double" for 3.5.
 *
 * Run: java exercises/Exercise1_InstanceofPatterns.java
 */
public class Exercise1_InstanceofPatterns {

    static String summarize(Object obj) {
        // TODO
        return "";
    }

    static int lengthOf(Object obj) {
        // TODO
        return 0;
    }

    static final class Money {
        private final long amount;
        private final String currency;

        Money(long amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }

        @Override
        public boolean equals(Object o) {
            // TODO: one return statement with an instanceof pattern
            return false;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(amount) * 31 + currency.hashCode();
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("summarize", () ->
                summarize(null).equals("nothing")
                        && summarize("   ").equals("empty text")
                        && summarize("  Pune ").equals("text: Pune")
                        && summarize(4).equals("even number 4")
                        && summarize(7).equals("odd number 7")
                        && summarize(List.of()).equals("empty list")
                        && summarize(List.of("Goa", "Delhi")).equals("list starting with Goa")
                        && summarize(3.5).equals("unsupported: Double"));

        allPass &= check("lengthOf", () ->
                lengthOf("Ravi") == 4
                        && lengthOf(new StringBuilder("abc")) == 3
                        && lengthOf(42) == -1
                        && lengthOf(null) == -1);

        allPass &= check("equals", () -> {
            Money price = new Money(499, "INR");
            return price.equals(new Money(499, "INR"))
                    && !price.equals(new Money(499, "USD"))
                    && !price.equals(new Money(500, "INR"))
                    && !price.equals("499 INR")
                    && !price.equals(null)
                    && price.equals(price);
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (RuntimeException e) {
            System.out.printf("%-10s FAIL (%s)%n", name + ":", e);
            return false;
        }
        System.out.printf("%-10s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
