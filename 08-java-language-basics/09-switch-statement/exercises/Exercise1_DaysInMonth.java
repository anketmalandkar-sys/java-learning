/**
 * Exercise 1 (Easy): grouped labels and default.
 *
 * TASK
 *   Use classic switch statements (case X: ... break;), not if/else chains.
 *
 *     daysInMonth(month, year)   30 for Apr, Jun, Sep, Nov; 28 or 29 for Feb; 31 for the others.
 *                                Return -1 for a month outside 1..12.
 *                                Leap year: divisible by 4, except centuries, except every 400.
 *     quarter(month)             "Q1" for 1-3, "Q2" for 4-6, "Q3" for 7-9, "Q4" for 10-12,
 *                                "INVALID" otherwise. Group the labels.
 *
 * EXPECTED OUTPUT
 *   daysInMonth: PASS
 *   leap years:  PASS
 *   quarter:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - Several "case N:" lines in a row share the code under the last one.
 *   - The month check (valid or not) fits in the default branch.
 *
 * Run: java exercises/Exercise1_DaysInMonth.java
 */
public class Exercise1_DaysInMonth {

    static int daysInMonth(int month, int year) {
        return 0; // TODO
    }

    static String quarter(int month) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("daysInMonth:", daysInMonth(1, 2023) == 31 && daysInMonth(4, 2023) == 30
                && daysInMonth(11, 2023) == 30 && daysInMonth(12, 2023) == 31
                && daysInMonth(0, 2023) == -1 && daysInMonth(13, 2023) == -1);
        allPass &= check("leap years:", daysInMonth(2, 2024) == 29 && daysInMonth(2, 2023) == 28
                && daysInMonth(2, 1900) == 28 && daysInMonth(2, 2000) == 29);
        allPass &= check("quarter:", "Q1".equals(quarter(1)) && "Q1".equals(quarter(3)) && "Q2".equals(quarter(4))
                && "Q3".equals(quarter(9)) && "Q4".equals(quarter(12)) && "INVALID".equals(quarter(13)));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
