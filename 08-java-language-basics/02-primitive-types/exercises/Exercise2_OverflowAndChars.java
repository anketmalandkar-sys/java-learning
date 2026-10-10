/**
 * Exercise 2 (Medium): an overflow bug, then char arithmetic.
 *
 * TASK
 *   1. millisInDays(days) returns the number of milliseconds in that many days.
 *      It works for 1 day but is wrong for 30 days. Fix it (keep the return type long).
 *
 *   2. shift(letter, steps) moves a lowercase letter forward in the alphabet, wrapping
 *      from z back to a. Only use char/int arithmetic: no Strings, no arrays.
 *        shift('a', 1)  -> 'b'
 *        shift('x', 3)  -> 'a'
 *        shift('m', 26) -> 'm'
 *
 * EXPECTED OUTPUT
 *   millis 1 day:   PASS
 *   millis 30 days: PASS
 *   shift a+1:      PASS
 *   shift x+3:      PASS
 *   shift m+26:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - In days * 24 * 60 * 60 * 1000, every operand is an int, so the math is done in int.
 *   - letter - 'a' gives a position 0..25. Use % 26 to wrap. Add 'a' back and cast to char.
 *
 * Run: java exercises/Exercise2_OverflowAndChars.java
 */
public class Exercise2_OverflowAndChars {

    static long millisInDays(int days) {
        return days * 24 * 60 * 60 * 1000;   // TODO: bug
    }

    static char shift(char letter, int steps) {
        return letter; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("millis 1 day:", millisInDays(1) == 86_400_000L);
        allPass &= check("millis 30 days:", millisInDays(30) == 2_592_000_000L);
        allPass &= check("shift a+1:", shift('a', 1) == 'b');
        allPass &= check("shift x+3:", shift('x', 3) == 'a');
        allPass &= check("shift m+26:", shift('m', 26) == 'm');
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
