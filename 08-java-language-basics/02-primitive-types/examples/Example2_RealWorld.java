/**
 * Example 2: picking types in a small video-streaming service.
 *
 *   - Bytes streamed per month pass 2 billion quickly, so they need long.
 *   - Prices are kept in paise (long), not double, so totals are exact.
 *   - A plan code is a single char; "is premium" is a boolean.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static final long BYTES_PER_GB = 1_073_741_824L;   // 1024 * 1024 * 1024

    public static void main(String[] args) {
        // An HD stream is about 3 GB per hour. 30 hours of watching in a month:
        int hoursWatched = 30;
        int wrongBytes = hoursWatched * 3 * 1_073_741_824;          // int math overflows
        long rightBytes = hoursWatched * 3 * BYTES_PER_GB;          // long operand -> long math
        System.out.println("Bytes streamed (int, overflowed): " + wrongBytes);
        System.out.println("Bytes streamed (long, correct):   " + rightBytes);

        // Money: three add-ons at Rs 0.10 each.
        double addOnsDouble = 0.10 + 0.10 + 0.10;
        long addOnsPaise = 10 + 10 + 10;
        System.out.println("Add-ons as double: " + addOnsDouble);
        System.out.println("Add-ons in paise:  " + addOnsPaise + " (Rs " + addOnsPaise / 100 + "."
                + String.format("%02d", addOnsPaise % 100) + ")");

        char planCode = 'P';
        boolean isPremium = planCode == 'P';
        System.out.println("Plan " + planCode + " premium? " + isPremium);

        // char is a number underneath, so you can step through letters.
        char nextPlan = (char) (planCode + 1);
        System.out.println("Next plan code: " + nextPlan + " (code " + (int) nextPlan + ")");
    }
}

/* Expected output:
Bytes streamed (int, overflowed): -2147483648
Bytes streamed (long, correct):   96636764160
Add-ons as double: 0.30000000000000004
Add-ons in paise:  30 (Rs 0.30)
Plan P premium? true
Next plan code: Q (code 81)
*/
