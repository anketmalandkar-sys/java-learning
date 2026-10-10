import java.util.Arrays;

/**
 * Example 2: a week of shop sales, using the java.util.Arrays helpers.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    public static void main(String[] args) {
        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int[] sales = {120, 95, 140, 80, 160, 210, 175};

        System.out.println("Sales: " + Arrays.toString(sales));
        System.out.println("Total: " + Arrays.stream(sales).sum());

        // Find the best day with a plain loop: we need the index, not just the value.
        int bestDay = 0;
        for (int i = 1; i < sales.length; i++) {
            if (sales[i] > sales[bestDay]) {
                bestDay = i;
            }
        }
        System.out.println("Best day: " + dayNames[bestDay] + " (" + sales[bestDay] + ")");

        // Sort a COPY, so the original day order is kept.
        int[] sorted = Arrays.copyOf(sales, sales.length);
        Arrays.sort(sorted);
        System.out.println("Sorted copy: " + Arrays.toString(sorted));
        System.out.println("Original:    " + Arrays.toString(sales));

        // binarySearch only works on a sorted array.
        int position = Arrays.binarySearch(sorted, 160);
        System.out.println("160 is at index " + position + " of the sorted copy");

        // Next week's targets start at 100 each; fill sets every slot.
        int[] targets = new int[7];
        Arrays.fill(targets, 100);
        System.out.println("Targets: " + Arrays.toString(targets));

        // Two branches' weekly sales: same numbers, different arrays.
        int[] branchA = {5, 6, 7};
        int[] branchB = {5, 6, 7};
        System.out.println("branchA == branchB?             " + (branchA == branchB));
        System.out.println("Arrays.equals(branchA, branchB)? " + Arrays.equals(branchA, branchB));
    }
}

/* Expected output:
Sales: [120, 95, 140, 80, 160, 210, 175]
Total: 980
Best day: Sat (210)
Sorted copy: [80, 95, 120, 140, 160, 175, 210]
Original:    [120, 95, 140, 80, 160, 210, 175]
160 is at index 4 of the sorted copy
Targets: [100, 100, 100, 100, 100, 100, 100]
branchA == branchB?             false
Arrays.equals(branchA, branchB)? true
*/
