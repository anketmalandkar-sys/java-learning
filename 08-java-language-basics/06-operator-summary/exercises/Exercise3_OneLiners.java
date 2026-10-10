/**
 * Exercise 3 (Hard): single-expression checks.
 *
 * TASK
 *   Write each method as ONE return statement: no if, no loops, no helper methods, no Math.
 *   Use parentheses wherever they make the grouping obvious to a reader.
 *
 *     isLeapYear(year)          divisible by 4, except centuries, except every 400 years
 *                               2024 -> true, 1900 -> false, 2000 -> true, 2023 -> false
 *     inRange(x, low, high)     low <= x <= high, OR when exclusive is true, low < x < high
 *                               inRange(5, 1, 5, false) -> true, inRange(5, 1, 5, true) -> false
 *     isPowerOfTwo(n)           1, 2, 4, 8, ... -> true; 0, negatives, 6 -> false
 *                               Use bitwise operators: a power of two has exactly one bit set,
 *                               and n & (n - 1) clears the lowest set bit.
 *     sign(n)                   -1, 0 or 1. Use the ternary operator.
 *
 * EXPECTED OUTPUT
 *   isLeapYear:   PASS
 *   inRange:      PASS
 *   isPowerOfTwo: PASS
 *   sign:         PASS
 *   ALL PASS
 *
 * HINTS
 *   - isPowerOfTwo: without parentheses, "n & n - 1 == 0" means n & ((n - 1) == 0). It won't compile.
 *   - inRange: the ternary can choose between two boolean expressions.
 *
 * Run: java exercises/Exercise3_OneLiners.java
 */
public class Exercise3_OneLiners {

    static boolean isLeapYear(int year) {
        return false; // TODO
    }

    static boolean inRange(int x, int low, int high, boolean exclusive) {
        return false; // TODO
    }

    static boolean isPowerOfTwo(int n) {
        return false; // TODO
    }

    static int sign(int n) {
        return 99; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("isLeapYear:", isLeapYear(2024) && !isLeapYear(1900) && isLeapYear(2000)
                && !isLeapYear(2023) && isLeapYear(1600) && !isLeapYear(2100));
        allPass &= check("inRange:", inRange(5, 1, 5, false) && !inRange(5, 1, 5, true)
                && inRange(3, 1, 5, true) && !inRange(0, 1, 5, false) && inRange(1, 1, 1, false));
        allPass &= check("isPowerOfTwo:", isPowerOfTwo(1) && isPowerOfTwo(2) && isPowerOfTwo(1024)
                && !isPowerOfTwo(0) && !isPowerOfTwo(6) && !isPowerOfTwo(-8)
                && isPowerOfTwo(1 << 30) && !isPowerOfTwo(Integer.MIN_VALUE));
        allPass &= check("sign:", sign(-42) == -1 && sign(0) == 0 && sign(7) == 1);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-13s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
