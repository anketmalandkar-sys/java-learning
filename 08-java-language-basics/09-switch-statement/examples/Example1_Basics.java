/**
 * Example 1: classic switch with break, grouped labels, default, and fall-through.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        System.out.println("Day names:");
        for (int day = 1; day <= 8; day++) {
            System.out.println("  " + day + " -> " + dayName(day));
        }

        System.out.println("Days in month (2024):");
        int[] months = {1, 2, 4, 9, 12};
        for (int month : months) {
            System.out.println("  month " + month + ": " + daysInMonth(month, true));
        }

        System.out.println("Fall-through, on purpose:");
        System.out.println("  months left after October: " + monthsLeftAfter(10));

        System.out.println("Fall-through, by accident:");
        System.out.println("  size code 'M' -> " + sizeBuggy('M') + "   (should be Medium)");
    }

    static String dayName(int day) {
        String name;
        switch (day) {
            case 1:
                name = "Monday";
                break;
            case 2:
                name = "Tuesday";
                break;
            case 3:
                name = "Wednesday";
                break;
            case 4:
                name = "Thursday";
                break;
            case 5:
                name = "Friday";
                break;
            case 6:
            case 7:
                name = "Weekend";       // two labels share one action
                break;
            default:
                name = "Invalid";
                break;
        }
        return name;
    }

    static int daysInMonth(int month, boolean leapYear) {
        int days;
        switch (month) {
            case 4: case 6: case 9: case 11:
                days = 30;
                break;
            case 2:
                days = leapYear ? 29 : 28;
                break;
            default:
                days = 31;
                break;
        }
        return days;
    }

    // Each case adds its month, then deliberately falls through to add the following ones.
    static String monthsLeftAfter(int month) {
        StringBuilder left = new StringBuilder();
        switch (month) {
            case 9:
                left.append("Oct ");
                // fall through
            case 10:
                left.append("Nov ");
                // fall through
            case 11:
                left.append("Dec");
                break;
            default:
                left.append("(only handles Sep..Nov)");
        }
        return left.toString().trim();
    }

    // The missing breaks make 'M' run every case below it.
    static String sizeBuggy(char code) {
        String size = "";
        switch (code) {
            case 'S':
                size = "Small";
            case 'M':
                size = "Medium";
            case 'L':
                size = "Large";
            default:
                size = "Unknown";
        }
        return size;
    }
}

/* Expected output:
Day names:
  1 -> Monday
  2 -> Tuesday
  3 -> Wednesday
  4 -> Thursday
  5 -> Friday
  6 -> Weekend
  7 -> Weekend
  8 -> Invalid
Days in month (2024):
  month 1: 31
  month 2: 29
  month 4: 30
  month 9: 30
  month 12: 31
Fall-through, on purpose:
  months left after October: Nov Dec
Fall-through, by accident:
  size code 'M' -> Unknown   (should be Medium)
*/
