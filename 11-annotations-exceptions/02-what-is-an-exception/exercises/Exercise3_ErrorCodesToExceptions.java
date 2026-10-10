import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Exercise 3 (Hard): from error codes to the right kind of exception.
 *
 * TASK
 *   A ticket-booking API reports problems with return codes. Replace them with exceptions:
 *
 *   1. Create a CHECKED exception class SoldOutException (the caller can recover: offer
 *      another show). Its message: "<show> is sold out".
 *
 *   2. Change book(show, seats) so it returns nothing (void) and:
 *        - seats < 1 or seats > 10  -> throw IllegalArgumentException("seats must be 1..10")
 *          (that's a bug in the caller: unchecked)
 *        - show not in the schedule -> throw IllegalArgumentException("unknown show: <show>")
 *        - not enough seats left    -> throw SoldOutException (declare it with throws)
 *        - otherwise reduce the remaining seats.
 *
 *   3. Rewrite bookFirstAvailable(shows, seats): try each show in order and return the first one
 *      that could be booked; if all are sold out, return "none". Invalid arguments must NOT be
 *      swallowed: let IllegalArgumentException out.
 *
 *   Delete the old code constants when you're done.
 *
 * EXPECTED OUTPUT
 *   exception is checked:  PASS
 *   book declares it:      PASS
 *   bad seats:             PASS
 *   unknown show:          PASS
 *   first available:       PASS
 *   all sold out:          PASS
 *   ALL PASS
 *
 * HINTS
 *   - A checked exception extends Exception (not RuntimeException).
 *   - The reflection checks look for a method book(String, int) whose getExceptionTypes()
 *     contains SoldOutException.
 *
 * Run: java exercises/Exercise3_ErrorCodesToExceptions.java
 */
public class Exercise3_ErrorCodesToExceptions {

    // TODO: delete these once book() throws exceptions
    static final int OK = 0;
    static final int BAD_SEATS = -1;
    static final int UNKNOWN_SHOW = -2;
    static final int SOLD_OUT = -3;

    // TODO: class SoldOutException

    static java.util.Map<String, Integer> remaining;

    static int book(String show, int seats) {
        if (seats < 1 || seats > 10) {
            return BAD_SEATS;
        }
        Integer left = remaining.get(show);
        if (left == null) {
            return UNKNOWN_SHOW;
        }
        if (left < seats) {
            return SOLD_OUT;
        }
        remaining.put(show, left - seats);
        return OK;
    }

    static String bookFirstAvailable(List<String> shows, int seats) {
        for (String show : shows) {
            if (book(show, seats) == OK) {      // TODO: ignores BAD_SEATS and UNKNOWN_SHOW silently
                return show;
            }
        }
        return "none";
    }

    public static void main(String[] args) throws Exception {
        boolean allPass = true;

        Class<?> soldOut = findClass("SoldOutException");
        allPass &= check("exception is checked:", soldOut != null && Exception.class.isAssignableFrom(soldOut)
                && !RuntimeException.class.isAssignableFrom(soldOut));

        Method book = Exercise3_ErrorCodesToExceptions.class.getDeclaredMethod("book", String.class, int.class);
        allPass &= check("book declares it:", soldOut != null && book.getReturnType() == void.class
                && Arrays.asList(book.getExceptionTypes()).contains(soldOut));

        remaining = new java.util.HashMap<>(java.util.Map.of("10am", 4, "1pm", 50, "6pm", 0));
        allPass &= check("bad seats:", throwsIae(() -> bookFirstAvailable(List.of("1pm"), 0), "seats must be 1..10")
                && throwsIae(() -> bookFirstAvailable(List.of("1pm"), 11), "seats must be 1..10"));
        allPass &= check("unknown show:", throwsIae(() -> bookFirstAvailable(List.of("9pm"), 2), "unknown show: 9pm"));

        allPass &= check("first available:", "1pm".equals(bookFirstAvailable(List.of("6pm", "10am", "1pm"), 5))
                && remaining.get("1pm") == 45 && remaining.get("10am") == 4);
        allPass &= check("all sold out:", "none".equals(bookFirstAvailable(List.of("6pm", "10am"), 6)));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static Class<?> findClass(String simpleName) {
        for (Class<?> c : Exercise3_ErrorCodesToExceptions.class.getDeclaredClasses()) {
            if (c.getSimpleName().equals(simpleName)) {
                return c;
            }
        }
        return null;
    }

    static boolean throwsIae(Runnable action, String message) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException e) {
            return message.equals(e.getMessage());
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-22s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
