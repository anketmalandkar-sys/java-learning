package oops.exercises;

/**
 * Exercise 1 (Hard): three inheritance traps.
 * Goes with inheritance/OverridingRules.java.
 *
 * TASK
 *   1. CONSTRUCTOR CALL: Report's constructor builds the header by calling title(), which
 *      SalesReport overrides. The header comes out as "== null ==". Fix it WITHOUT changing
 *      what title() returns and without moving the header out of the Report constructor.
 *      (Hint: pass what the header needs into Report's constructor, or make the header lazy.)
 *
 *   2. FIELD HIDING: PriceTag declares `currency` again, so priceText() (written in Tag, the
 *      parent) prints the parent's value. Remove the hiding so PriceTag("USD") prints "USD 10".
 *      Keep PriceTag's constructor signature.
 *
 *   3. DEFAULT METHODS: predict, without running, what each greeter prints, by filling in the
 *      three PREDICTED_ constants. Then run to check.
 *
 * EXPECTED OUTPUT
 *   header:      PASS
 *   price tag:   PASS
 *   greeter A:   PASS
 *   greeter B:   PASS
 *   greeter C:   PASS
 *   ALL PASS
 *
 * Run: press the green run button next to main in IntelliJ, or from the repo root:
 *   javac -d out $(find 00-oops/05-inheritance -name "*.java") && java -cp out oops.exercises.Exercise1_OverridingTraps
 */
public class Exercise1_OverridingTraps {

    // ---- 1. Overridable call in a constructor ----
    static class Report {
        final String header;

        Report() {
            header = "== " + title() + " ==";
        }

        String title() {
            return "Report";
        }
    }

    static class SalesReport extends Report {
        private String region;

        SalesReport(String region) {
            this.region = region;
        }

        @Override
        String title() {
            return "Sales " + region;
        }
    }

    // ---- 2. Field hiding ----
    static class Tag {
        protected String currency = "INR";
        protected final int amount;

        Tag(int amount) {
            this.amount = amount;
        }

        String priceText() {
            return currency + " " + amount;
        }
    }

    static class PriceTag extends Tag {
        protected String currency;     // TODO: hides Tag.currency

        PriceTag(String currency, int amount) {
            super(amount);
            this.currency = currency;
        }
    }

    // ---- 3. Default-method rules ----
    interface Hello {
        default String hi() {
            return "Hello";
        }
    }

    interface Namaste extends Hello {
        @Override
        default String hi() {
            return "Namaste";
        }
    }

    static class Polite {
        public String hi() {
            return "Good day";
        }
    }

    static class GreeterA implements Hello, Namaste { }

    static class GreeterB extends Polite implements Namaste { }

    static class GreeterC implements Hello {
        @Override
        public String hi() {
            return Hello.super.hi() + "!";
        }
    }

    // TODO: your predictions
    static final String PREDICTED_A = "?";
    static final String PREDICTED_B = "?";
    static final String PREDICTED_C = "?";

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("header:", "== Sales West ==".equals(new SalesReport("West").header)
                && "== Report ==".equals(new Report().header));
        allPass &= check("price tag:", "USD 10".equals(new PriceTag("USD", 10).priceText())
                && "INR 5".equals(new Tag(5).priceText()));
        allPass &= check("greeter A:", PREDICTED_A.equals(new GreeterA().hi()));
        allPass &= check("greeter B:", PREDICTED_B.equals(new GreeterB().hi()));
        allPass &= check("greeter C:", PREDICTED_C.equals(new GreeterC().hi()));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
