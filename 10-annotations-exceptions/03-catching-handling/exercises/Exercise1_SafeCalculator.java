import java.util.List;

/**
 * Exercise 1 (Easy): catch, multi-catch and finally.
 *
 * TASK
 *   1. priceOf(menu, choice): `choice` is a 1-based menu number typed by a user.
 *        - a valid number -> the price at that position
 *        - not a number           -> -1  (NumberFormatException)
 *        - a number off the menu  -> -2  (IndexOutOfBoundsException)
 *      Use two separate catch blocks.
 *
 *   2. perPerson(total, people): both are text. Return total / people (integer division), or
 *      -1 if either isn't a number OR people is 0. Use ONE multi-catch block.
 *
 *   3. Every call to perPerson must increment CALLS exactly once, whatever happens, including
 *      when it returns early. Use finally.
 *
 * EXPECTED OUTPUT
 *   priceOf:    PASS
 *   perPerson:  PASS
 *   calls:      PASS
 *   ALL PASS
 *
 * HINTS
 *   - Integer division by zero throws ArithmeticException.
 *   - In a multi-catch, the types are separated by | and must not be subclasses of each other.
 *
 * Run: java exercises/Exercise1_SafeCalculator.java
 */
public class Exercise1_SafeCalculator {

    static int CALLS = 0;

    static int priceOf(List<Integer> menu, String choice) {
        return menu.get(Integer.parseInt(choice) - 1); // TODO: handle the two failures
    }

    static int perPerson(String total, String people) {
        return Integer.parseInt(total) / Integer.parseInt(people); // TODO: multi-catch + finally
    }

    public static void main(String[] args) {
        List<Integer> menu = List.of(120, 80, 150);
        boolean allPass = true;

        allPass &= check("priceOf:", safe(() -> priceOf(menu, "2") == 80 && priceOf(menu, "two") == -1
                && priceOf(menu, "4") == -2 && priceOf(menu, "0") == -2));

        CALLS = 0;
        allPass &= check("perPerson:", safe(() -> perPerson("300", "3") == 100 && perPerson("300", "0") == -1
                && perPerson("300", "x") == -1 && perPerson("abc", "2") == -1));
        allPass &= check("calls:", CALLS == 4);

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

    static boolean check(String name, boolean ok) {
        System.out.printf("%-11s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
