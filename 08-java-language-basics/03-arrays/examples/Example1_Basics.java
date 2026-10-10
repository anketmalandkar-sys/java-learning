import java.util.Arrays;

/**
 * Example 1: creating, filling, looping over, and copying arrays.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        // new int[5] creates 5 slots, all starting at the default 0.
        int[] scores = new int[5];
        scores[0] = 90;
        scores[1] = 72;
        scores[4] = 85;
        System.out.println("scores = " + Arrays.toString(scores) + ", length " + scores.length);

        // The shortcut syntax: the length comes from the number of values.
        String[] days = {"Mon", "Tue", "Wed"};
        for (int i = 0; i < days.length; i++) {
            System.out.println("  day " + i + " = " + days[i]);
        }

        int total = 0;
        for (int score : scores) {      // for-each: no index needed
            total += score;
        }
        System.out.println("total = " + total);

        // A jagged 2D array: each row is its own array.
        int[][] triangle = {
            {1},
            {1, 1},
            {1, 2, 1},
        };
        for (int[] row : triangle) {
            System.out.println("  row of " + row.length + ": " + Arrays.toString(row));
        }

        // Three ways to copy the middle three numbers.
        int[] source = {10, 20, 30, 40, 50};

        int[] byLoop = new int[3];
        for (int i = 0; i < 3; i++) {
            byLoop[i] = source[i + 1];
        }

        int[] byArraycopy = new int[3];     // arraycopy needs the destination to exist
        System.arraycopy(source, 1, byArraycopy, 0, 3);

        int[] byCopyOfRange = Arrays.copyOfRange(source, 1, 4);   // end index 4 is excluded

        System.out.println("loop        " + Arrays.toString(byLoop));
        System.out.println("arraycopy   " + Arrays.toString(byArraycopy));
        System.out.println("copyOfRange " + Arrays.toString(byCopyOfRange));
        System.out.println("all equal?  " + (Arrays.equals(byLoop, byArraycopy)
                && Arrays.equals(byArraycopy, byCopyOfRange)));
    }
}

/* Expected output:
scores = [90, 72, 0, 0, 85], length 5
  day 0 = Mon
  day 1 = Tue
  day 2 = Wed
total = 247
  row of 1: [1]
  row of 2: [1, 1]
  row of 3: [1, 2, 1]
loop        [20, 30, 40]
arraycopy   [20, 30, 40]
copyOfRange [20, 30, 40]
all equal?  true
*/
