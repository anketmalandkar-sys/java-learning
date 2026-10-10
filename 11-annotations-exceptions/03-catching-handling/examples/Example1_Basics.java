import java.util.List;

/**
 * Example 1: catch order, multi-catch, and when finally runs.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static final List<String> MENU = List.of("tea", "coffee", "juice");

    static String pick(String choice) {
        try {
            int index = Integer.parseInt(choice);
            return "you picked " + MENU.get(index);
        } catch (NumberFormatException e) {                 // specific handlers first
            return "not a number: " + choice;
        } catch (IndexOutOfBoundsException e) {
            return "no item " + choice;
        }
    }

    static String share(String total, String people) {
        try {
            return "each pays " + Integer.parseInt(total) / Integer.parseInt(people);
        } catch (NumberFormatException | ArithmeticException e) {   // same handling for both
            return "can't split: " + e.getClass().getSimpleName();
        }
    }

    static int attempts = 0;

    static String withFinally(boolean fail) {
        try {
            if (fail) {
                throw new IllegalStateException("boom");
            }
            return "returned normally";                     // finally runs before this return completes
        } catch (IllegalStateException e) {
            return "caught " + e.getMessage();
        } finally {
            attempts++;                                     // runs on every path
        }
    }

    static int countUntil(int limit) {
        int steps = 0;
        for (int i = 0; ; i++) {
            try {
                if (i == limit) {
                    break;                                  // finally runs on break too
                }
            } finally {
                steps++;
            }
        }
        return steps;
    }

    public static void main(String[] args) {
        System.out.println("--- Separate catch blocks ---");
        for (String choice : List.of("1", "x", "9")) {
            System.out.println("  " + choice + " -> " + pick(choice));
        }

        System.out.println("--- Multi-catch ---");
        System.out.println("  " + share("300", "3"));
        System.out.println("  " + share("300", "0"));
        System.out.println("  " + share("300", "three"));

        System.out.println("--- finally runs on every exit ---");
        System.out.println("  " + withFinally(false) + ", attempts=" + attempts);
        System.out.println("  " + withFinally(true) + ", attempts=" + attempts);
        System.out.println("  countUntil(3) ran finally " + countUntil(3) + " times (including the break)");
    }
}

/* Expected output:
--- Separate catch blocks ---
  1 -> you picked coffee
  x -> not a number: x
  9 -> no item 9
--- Multi-catch ---
  each pays 100
  can't split: ArithmeticException
  can't split: NumberFormatException
--- finally runs on every exit ---
  returned normally, attempts=1
  caught boom, attempts=2
  countUntil(3) ran finally 4 times (including the break)
*/
