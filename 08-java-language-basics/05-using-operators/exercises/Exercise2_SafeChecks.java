/**
 * Exercise 2 (Medium): short-circuiting, the ternary, and instanceof.
 *
 * TASK
 *   Write each method as a single return statement (one expression), without if/else.
 *
 *     hasText(s)               true if s is not null AND has at least one non-space character
 *                              hasText(null) -> false, hasText("  ") -> false, hasText(" a ") -> true
 *     canRent(age, hasLicense, hasCard)
 *                              true if age is at least 21 and they have a license, and also
 *                              either a credit card OR age is at least 25
 *     ticketPrice(age)         0 under 5, 50 for 5..17, 120 for 18..59, 60 for 60+.
 *                              Use the ternary operator (nesting is fine here, one per line).
 *     describe(value)          "text of length N" for a String, "number N" for an Integer,
 *                              "nothing" for null, "unknown" for anything else.
 *                              Use instanceof (pattern binding like "value instanceof String s"
 *                              is allowed, Java 16+).
 *
 * EXPECTED OUTPUT
 *   hasText:     PASS
 *   canRent:     PASS
 *   ticketPrice: PASS
 *   describe:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - Order matters with &&: put the null check first.
 *   - s.isBlank() is true for "" and "   ".
 *   - In describe, null instanceof X is false, so check for null explicitly.
 *
 * Run: java exercises/Exercise2_SafeChecks.java
 */
public class Exercise2_SafeChecks {

    static boolean hasText(String s) {
        return false; // TODO
    }

    static boolean canRent(int age, boolean hasLicense, boolean hasCard) {
        return false; // TODO
    }

    static int ticketPrice(int age) {
        return -1; // TODO
    }

    static String describe(Object value) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("hasText:", !hasText(null) && !hasText("") && !hasText("   ") && hasText(" a "));
        allPass &= check("canRent:", canRent(22, true, true) && !canRent(22, true, false)
                && canRent(30, true, false) && !canRent(30, false, true) && !canRent(20, true, true));
        allPass &= check("ticketPrice:", ticketPrice(4) == 0 && ticketPrice(5) == 50 && ticketPrice(17) == 50
                && ticketPrice(18) == 120 && ticketPrice(59) == 120 && ticketPrice(60) == 60);
        allPass &= check("describe:", "text of length 5".equals(describe("hello"))
                && "number 42".equals(describe(42)) && "nothing".equals(describe(null))
                && "unknown".equals(describe(3.14)));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
