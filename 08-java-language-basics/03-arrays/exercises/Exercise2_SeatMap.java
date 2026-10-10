/**
 * Exercise 2 (Medium): a jagged 2D array.
 *
 * TASK
 *   A small cinema has rows of different lengths. true = seat taken, false = free.
 *
 *     createHall(rowLengths)    build a boolean[][] where row r has rowLengths[r] seats, all free
 *                               createHall(new int[] {2, 3}) -> [[false, false], [false, false, false]]
 *     book(hall, row, seat)     mark the seat taken and return true. Return false (and change
 *                               nothing) if the seat is already taken or doesn't exist.
 *     freeSeats(hall)           count every free seat in the hall
 *     fullestRow(hall)          index of the row with the most taken seats (first one on a tie)
 *
 * EXPECTED OUTPUT
 *   createHall: PASS
 *   book:       PASS
 *   bad seats:  PASS
 *   freeSeats:  PASS
 *   fullestRow: PASS
 *   ALL PASS
 *
 * HINTS
 *   - new boolean[n][] creates n rows that are all null; create each row separately.
 *   - Use hall[r].length for each row, never hall[0].length.
 *   - "Doesn't exist" includes a negative row or seat.
 *
 * Run: java exercises/Exercise2_SeatMap.java
 */
public class Exercise2_SeatMap {

    static boolean[][] createHall(int[] rowLengths) {
        return new boolean[0][]; // TODO
    }

    static boolean book(boolean[][] hall, int row, int seat) {
        return false; // TODO
    }

    static int freeSeats(boolean[][] hall) {
        return 0; // TODO
    }

    static int fullestRow(boolean[][] hall) {
        return -1; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;

        boolean[][] hall = createHall(new int[] {4, 6, 3});
        allPass &= check("createHall:", hall.length == 3 && hall[0].length == 4
                && hall[1].length == 6 && hall[2].length == 3 && !hall[1][5]);

        boolean first = book(hall, 1, 2);
        boolean again = book(hall, 1, 2);
        allPass &= check("book:", first && !again && hall[1][2]);

        allPass &= check("bad seats:", !book(hall, 2, 3) && !book(hall, 3, 0)
                && !book(hall, -1, 0) && !book(hall, 0, -1));

        book(hall, 2, 0);
        book(hall, 2, 1);
        allPass &= check("freeSeats:", freeSeats(hall) == 13 - 3);

        allPass &= check("fullestRow:", fullestRow(hall) == 2
                && fullestRow(createHall(new int[] {2, 2})) == 0);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-11s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
