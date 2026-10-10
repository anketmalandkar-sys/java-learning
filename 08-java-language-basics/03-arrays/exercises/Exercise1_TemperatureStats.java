/**
 * Exercise 1 (Easy): loop over an array.
 *
 * TASK
 *   Given a week of daily temperatures, write:
 *     average(temps)          the mean as a double                  {20, 22, 24} -> 22.0
 *     max(temps)              the highest value                     {20, 22, 24} -> 24
 *     daysAboveAverage(temps) how many days are STRICTLY above the average
 *                                                                   {20, 22, 24} -> 1
 *   You may assume the array has at least one element. Use loops, not streams.
 *
 * EXPECTED OUTPUT
 *   average: PASS
 *   max:     PASS
 *   above:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - Summing ints and dividing by an int length does INTEGER division. Make one side a double.
 *   - Start max at temps[0], not at 0: temperatures can be negative.
 *
 * Run: java exercises/Exercise1_TemperatureStats.java
 */
public class Exercise1_TemperatureStats {

    static double average(int[] temps) {
        return 0; // TODO
    }

    static int max(int[] temps) {
        return 0; // TODO
    }

    static int daysAboveAverage(int[] temps) {
        return 0; // TODO
    }

    public static void main(String[] args) {
        int[] week = {21, 19, 25, 30, 18, 22, 26};
        int[] winter = {-5, -2, -8};

        boolean allPass = true;
        allPass &= check("average:", Math.abs(average(week) - 23.0) < 1e-9
                && Math.abs(average(new int[] {1, 2}) - 1.5) < 1e-9);
        allPass &= check("max:", max(week) == 30 && max(winter) == -2);
        allPass &= check("above:", daysAboveAverage(week) == 3 && daysAboveAverage(new int[] {7}) == 0);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-8s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
