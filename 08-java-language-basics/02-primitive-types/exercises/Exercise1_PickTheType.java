/**
 * Exercise 1 (Easy): write the right literal for each value.
 *
 * TASK
 *   Replace each placeholder return value with the correct literal. Change the return TYPE too
 *   where the comment asks for one. Don't call any methods or do any math: just a literal.
 *
 *     worldPopulation()  8.1 billion, as a long, written with underscores
 *     brandOrange()      the color FF8800 written as a HEX int literal
 *     permissions()      the bits 1010 written as a BINARY int literal
 *     taxRate()          0.18 as a float (change the return type)
 *     topGrade()         the letter A as a char (change the return type)
 *     hasDiscount()      yes, as a boolean (change the return type)
 *
 * EXPECTED OUTPUT
 *   worldPopulation: PASS
 *   brandOrange:     PASS
 *   permissions:     PASS
 *   taxRate:         PASS
 *   topGrade:        PASS
 *   hasDiscount:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - 8_100_000_000 is too big for an int. What suffix makes it a long?
 *   - Hex starts with 0x, binary with 0b.
 *   - The checks only pass once each return type is right (0.18 as a double is not a float).
 *
 * Run: java exercises/Exercise1_PickTheType.java
 */
public class Exercise1_PickTheType {

    static long worldPopulation() {
        return 0; // TODO
    }

    static int brandOrange() {
        return 0; // TODO
    }

    static int permissions() {
        return 0; // TODO
    }

    static double taxRate() {   // TODO: float
        return 0; // TODO
    }

    static int topGrade() {     // TODO: char
        return 0; // TODO
    }

    static Object hasDiscount() {   // TODO: boolean
        return null; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("worldPopulation:", worldPopulation() == 8_100_000_000L);
        allPass &= check("brandOrange:", brandOrange() == 16746496);
        allPass &= check("permissions:", permissions() == 10);
        Object rate = taxRate();
        allPass &= check("taxRate:", rate instanceof Float f && f == 0.18f);
        Object grade = topGrade();
        allPass &= check("topGrade:", grade instanceof Character c && c == 'A');
        Object discount = hasDiscount();
        allPass &= check("hasDiscount:", discount instanceof Boolean b && b);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-16s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
