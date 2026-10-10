/**
 * Example 1: every control flow statement once.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        // if / else if / else: the first match wins.
        int score = 84;
        char grade;
        if (score >= 90) {
            grade = 'A';
        } else if (score >= 80) {
            grade = 'B';
        } else {
            grade = 'C';
        }
        System.out.println("score " + score + " -> grade " + grade);

        // while: may run zero times.
        int countdown = 3;
        while (countdown > 0) {
            System.out.print(countdown + "... ");
            countdown--;
        }
        System.out.println("go!");

        // do-while: runs at least once, even though the condition is false from the start.
        int attempts = 0;
        do {
            attempts++;
        } while (attempts < 0);
        System.out.println("do-while ran " + attempts + " time(s)");

        // for: init; condition; update.
        System.out.print("even numbers below 10:");
        for (int i = 0; i < 10; i += 2) {
            System.out.print(" " + i);
        }
        System.out.println();

        // enhanced for, with continue and break.
        int[] readings = {4, -1, 7, 0, 9};
        int sum = 0;
        for (int r : readings) {
            if (r < 0) {
                continue;       // skip bad readings
            }
            if (r == 0) {
                break;          // 0 marks the end of valid data
            }
            sum += r;
        }
        System.out.println("sum of readings before the 0, ignoring negatives: " + sum);

        // labeled break: leave both loops as soon as we find the target.
        int[][] grid = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9},
        };
        int target = 5;
        int foundRow = -1;
        int foundCol = -1;
        search:
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (grid[row][col] == target) {
                    foundRow = row;
                    foundCol = col;
                    break search;
                }
            }
        }
        System.out.println(target + " found at row " + foundRow + ", col " + foundCol);

        // return: leaves the method early.
        System.out.println("first vowel in \"rhythm and blues\": " + firstVowel("rhythm and blues"));
    }

    static char firstVowel(String text) {
        for (char c : text.toCharArray()) {
            if ("aeiou".indexOf(c) >= 0) {
                return c;
            }
        }
        return '-';
    }
}

/* Expected output:
score 84 -> grade B
3... 2... 1... go!
do-while ran 1 time(s)
even numbers below 10: 0 2 4 6 8
sum of readings before the 0, ignoring negatives: 11
5 found at row 1, col 1
first vowel in "rhythm and blues": a
*/
