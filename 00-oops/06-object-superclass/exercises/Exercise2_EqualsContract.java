import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Exercise 2 (Medium): broken equals implementations.
 *
 * TASK
 *   Each class below has ONE bug in equals or hashCode that breaks the contract. The checks in
 *   main show which property fails. Fix each class so all checks pass. Keep each class's idea
 *   of equality (written in its comment).
 *
 *     Coupon      equal when the code matches, ignoring case
 *     Temperature equal when celsius matches
 *     Seat        equal when row and number match
 *     Tag         equal when the name matches
 *
 * EXPECTED OUTPUT
 *   Coupon (hashCode consistent):  PASS
 *   Temperature (null-safe):       PASS
 *   Seat (symmetric):              PASS
 *   Tag (really overrides):        PASS
 *   ALL PASS
 *
 * HINTS
 *   - If equals ignores case, hashCode must too.
 *   - What does o.getClass() do when o is null?
 *   - Seat.equals accepts a String like "B12", but String.equals doesn't accept a Seat.
 *   - Which equals does HashSet call: equals(Object) or equals(Tag)?
 *
 * Run: java exercises/Exercise2_EqualsContract.java
 */
public class Exercise2_EqualsContract {

    // Equal when the code matches, ignoring case.
    static class Coupon {
        final String code;

        Coupon(String code) {
            this.code = code;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Coupon c && code.equalsIgnoreCase(c.code);
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }
    }

    // Equal when celsius matches.
    static class Temperature {
        final double celsius;

        Temperature(double celsius) {
            this.celsius = celsius;
        }

        @Override
        public boolean equals(Object o) {
            if (o.getClass() != Temperature.class) {
                return false;
            }
            return Double.compare(celsius, ((Temperature) o).celsius) == 0;
        }

        @Override
        public int hashCode() {
            return Double.hashCode(celsius);
        }
    }

    // Equal when row and number match.
    static class Seat {
        final char row;
        final int number;

        Seat(char row, int number) {
            this.row = row;
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof String s) {
                return s.equals("" + row + number);   // "convenient", but one-sided
            }
            return o instanceof Seat other && row == other.row && number == other.number;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, number);
        }
    }

    // Equal when the name matches.
    static class Tag {
        final String name;

        Tag(String name) {
            this.name = name;
        }

        public boolean equals(Tag other) {
            return other != null && name.equals(other.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        Set<Coupon> used = new HashSet<>();
        used.add(new Coupon("SAVE10"));
        allPass &= check("Coupon (hashCode consistent):", used.contains(new Coupon("save10")));

        boolean nullSafe;
        try {
            nullSafe = !new Temperature(21.5).equals(null) && new Temperature(21.5).equals(new Temperature(21.5));
        } catch (NullPointerException e) {
            nullSafe = false;
        }
        allPass &= check("Temperature (null-safe):", nullSafe);

        Seat b12 = new Seat('B', 12);
        Object asText = "B12";
        allPass &= check("Seat (symmetric):", b12.equals(asText) == asText.equals(b12)
                && b12.equals(new Seat('B', 12)));

        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("java"));
        tags.add(new Tag("java"));
        allPass &= check("Tag (really overrides):", tags.size() == 1);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-30s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
