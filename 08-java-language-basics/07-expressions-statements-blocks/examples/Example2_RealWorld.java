/**
 * Example 2: a daily sales report, where block placement decides correctness.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    public static void main(String[] args) {
        String[] branches = {"North", "South"};
        int[][] hourlySales = {
            {120, 80, 0, 150},
            {60, 0, 0, 90},
        };

        // grandTotal lives in the method's block: it accumulates across all branches.
        int grandTotal = 0;

        for (int b = 0; b < branches.length; b++) {
            // branchTotal and quietHours live in the loop's block: a fresh 0 for every branch.
            int branchTotal = 0;
            int quietHours = 0;

            for (int sales : hourlySales[b]) {
                branchTotal += sales;
                if (sales == 0) {
                    quietHours++;
                }
            }

            grandTotal += branchTotal;
            System.out.println(branches[b] + ": total=" + branchTotal + ", quiet hours=" + quietHours);

            if (quietHours > 1) {
                String warning = branches[b] + " had " + quietHours + " quiet hours";
                System.out.println("  WARNING: " + warning);
            }
        }

        System.out.println("Grand total: " + grandTotal);

        // The read-loop idiom: an assignment used as an expression inside a condition.
        String[] queue = {"order-17", "order-18", "order-19"};
        int position = 0;
        String next;
        while ((next = position < queue.length ? queue[position++] : null) != null) {
            System.out.println("processing " + next);
        }
    }
}

/* Expected output:
North: total=350, quiet hours=1
South: total=150, quiet hours=2
  WARNING: South had 2 quiet hours
Grand total: 500
processing order-17
processing order-18
processing order-19
*/
