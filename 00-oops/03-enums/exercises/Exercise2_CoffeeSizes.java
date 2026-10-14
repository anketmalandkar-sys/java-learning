import java.util.Optional;

/**
 * Exercise 2 (Medium): an enum with fields, and safe parsing.
 *
 * TASK
 *   A coffee shop sells three sizes. Complete the CupSize enum:
 *
 *     SMALL  label "S", 250 ml, Rs 120
 *     MEDIUM label "M", 350 ml, Rs 150
 *     LARGE  label "L", 500 ml, Rs 190
 *
 *   1. Add final fields label, ml and priceRupees, a constructor, and accessor methods
 *      label(), ml(), priceRupees().
 *   2. pricePerMl(): price divided by ml, as a double.
 *   3. static Optional<CupSize> parse(String input): accept the constant name OR the label,
 *      case-insensitive, ignoring surrounding spaces. Return Optional.empty() for null, blank or
 *      unknown input. It must never throw.
 *        parse(" m ") -> MEDIUM,   parse("large") -> LARGE,   parse("XL") -> empty
 *   4. static CupSize bestValue(): the size with the lowest price per ml.
 *
 * EXPECTED OUTPUT
 *   fields:    PASS
 *   perMl:     PASS
 *   parse:     PASS
 *   bestValue: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Loop over values() and compare both name() and label() with equalsIgnoreCase.
 *   - valueOf throws for unknown names, so a loop is simpler (and safe) here.
 *
 * Run: java exercises/Exercise2_CoffeeSizes.java
 */
public class Exercise2_CoffeeSizes {

    enum CupSize {
        SMALL("S", 250, 120), MEDIUM("M", 350, 150), LARGE("L", 500, 190);   // TODO: add constructor arguments, fields and a constructor

        private final String label;
        private final int ml;
        private final int priceRupees;

        private CupSize(String label, int ml, int priceRupees) {
            this.label = label;
            this.ml = ml;
            this.priceRupees = priceRupees;
        }

        String label() {
            return this.label; // TODO
        }

        int ml() {
            return this.ml; // TODO
        }

        int priceRupees() {
            return this.priceRupees; // TODO
        }

        double pricePerMl() {
            return (double) this.priceRupees/ this.ml; // TODO
        }

        static Optional<CupSize> parse(String input) {
            if (input == null || input.isEmpty()) {
                return Optional.empty();
            }
            String trimmedInput = input.trim();
            for (CupSize cupSize : CupSize.values()) {
                if (cupSize.name().equalsIgnoreCase(trimmedInput) || cupSize.label().equalsIgnoreCase(trimmedInput)) {
                    return Optional.of(cupSize);
                }
            }
            return Optional.empty(); // TODO
        }

        static CupSize bestValue() {
            CupSize best = CupSize.SMALL;
            for (CupSize cupSize : CupSize.values()) {
                if (cupSize.pricePerMl() < best.pricePerMl()) {
                    best = cupSize;
                }
            }
            return best; // TODO
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("fields:", "M".equals(CupSize.MEDIUM.label()) && CupSize.LARGE.ml() == 500
                && CupSize.SMALL.priceRupees() == 120);
        allPass &= check("perMl:", Math.abs(CupSize.SMALL.pricePerMl() - 0.48) < 1e-9);
        allPass &= check("parse:", CupSize.parse(" m ").orElse(null) == CupSize.MEDIUM
                && CupSize.parse("large").orElse(null) == CupSize.LARGE
                && CupSize.parse("S").orElse(null) == CupSize.SMALL
                && CupSize.parse("XL").isEmpty() && CupSize.parse("  ").isEmpty() && CupSize.parse(null).isEmpty());
        allPass &= check("bestValue:", CupSize.bestValue() == CupSize.LARGE);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-10s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
