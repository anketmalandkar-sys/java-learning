/**
 * Exercise 1 (Easy): / and %.
 *
 * TASK
 *   Use only arithmetic operators (no Strings for the math, no Math methods):
 *     isEven(n)              true for even numbers, including negatives    isEven(-4) -> true
 *     isOdd(n)               true for odd numbers, including negatives     isOdd(-3)  -> true
 *     digitSum(n)            sum of the digits of a non-negative int       digitSum(4096) -> 19
 *     formatDuration(secs)   "H:MM:SS" for a non-negative number of seconds
 *                            formatDuration(3725) -> "1:02:05"
 *
 * EXPECTED OUTPUT
 *   isEven:         PASS
 *   isOdd:          PASS
 *   digitSum:       PASS
 *   formatDuration: PASS
 *   ALL PASS
 *
 * HINTS
 *   - n % 10 is the last digit; n / 10 drops it.
 *   - -3 % 2 is -1, not 1.
 *   - For two-digit minutes/seconds, String.format("%02d", value) pads with a zero.
 *
 * Run: java exercises/Exercise1_DigitsAndTime.java
 */
public class Exercise1_DigitsAndTime {

    static boolean isEven(int n) {
        return false; // TODO
    }

    static boolean isOdd(int n) {
        return false; // TODO
    }

    static int digitSum(int n) {
        return 0; // TODO
    }

    static String formatDuration(int totalSeconds) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("isEven:", isEven(4) && isEven(0) && isEven(-4) && !isEven(7));
        allPass &= check("isOdd:", isOdd(7) && isOdd(-3) && !isOdd(0) && !isOdd(-8));
        allPass &= check("digitSum:", digitSum(4096) == 19 && digitSum(0) == 0 && digitSum(7) == 7);
        allPass &= check("formatDuration:", "1:02:05".equals(formatDuration(3725))
                && "0:00:59".equals(formatDuration(59)) && "10:00:00".equals(formatDuration(36000)));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
