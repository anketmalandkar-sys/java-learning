/**
 * Exercise 3 (Hard): labeled break and continue.
 *
 * TASK
 *   A cinema hall is an int[][]: 0 = free seat, 1 = taken. Rows can have different lengths.
 *
 *   1. findFirstFree(hall)
 *      The {row, seat} of the first free seat, scanning row by row, left to right.
 *      Return null if the hall is full. Use a LABELED BREAK to leave both loops (no return
 *      inside the loops).
 *
 *   2. rowsWithAllFree(hall)
 *      How many rows are completely free. Use a LABELED CONTINUE to skip to the next row as
 *      soon as you see a taken seat.
 *
 *   3. findBlock(hall, size)
 *      The {row, startSeat} of the first run of `size` consecutive free seats in a single row,
 *      or null. Any loop style you like, but no helper methods.
 *        row {1, 0, 0, 1, 0, 0, 0}, size 3 -> {row, 4}
 *
 * EXPECTED OUTPUT
 *   findFirstFree:   PASS
 *   rowsWithAllFree: PASS
 *   findBlock:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - Put the label right before the OUTER for:   rows: for (...) { for (...) { ... } }
 *   - For findBlock, count consecutive free seats; reset the count on a taken seat. When the
 *     count reaches size, the block starts at seat - size + 1.
 *
 * Run: java exercises/Exercise3_NestedLoops.java
 */
public class Exercise3_NestedLoops {

    static int[] findFirstFree(int[][] hall) {
        return null; // TODO
    }

    static int rowsWithAllFree(int[][] hall) {
        return -1; // TODO
    }

    static int[] findBlock(int[][] hall, int size) {
        return null; // TODO
    }

    public static void main(String[] args) {
        int[][] hall = {
            {1, 1, 1},
            {1, 0, 0, 1, 0, 0, 0},
            {0, 0},
            {0, 0, 0, 0},
        };
        int[][] full = {
            {1, 1},
            {1},
        };

        boolean allPass = true;
        allPass &= check("findFirstFree:", same(findFirstFree(hall), 1, 1) && findFirstFree(full) == null);
        allPass &= check("rowsWithAllFree:", rowsWithAllFree(hall) == 2 && rowsWithAllFree(full) == 0);
        allPass &= check("findBlock:", same(findBlock(hall, 3), 1, 4) && same(findBlock(hall, 2), 1, 1)
                && same(findBlock(hall, 4), 3, 0) && findBlock(hall, 5) == null
                && findBlock(full, 1) == null);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean same(int[] actual, int row, int seat) {
        return actual != null && actual.length == 2 && actual[0] == row && actual[1] == seat;
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-16s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
