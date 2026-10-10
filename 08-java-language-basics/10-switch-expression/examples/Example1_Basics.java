import java.time.DayOfWeek;

/**
 * Example 1: the same logic as a classic switch statement and as a switch expression.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        for (DayOfWeek day : DayOfWeek.values()) {
            System.out.printf("%-9s classic=%-7s expression=%-7s letters=%d%n",
                    day, typeClassic(day), typeExpression(day), letters(day));
        }

        System.out.println("days in Feb (leap): " + daysInMonth(2, true));
        System.out.println("days in Sep:        " + daysInMonth(9, false));

        System.out.println("describe(0)   = " + describe(0));
        System.out.println("describe(7)   = " + describe(7));
        System.out.println("describe(-3)  = " + describe(-3));
        System.out.println("describe(150) = " + describe(150));
    }

    // Old style: a variable, assigned in every branch, with breaks.
    static String typeClassic(DayOfWeek day) {
        String type;
        switch (day) {
            case SATURDAY:
            case SUNDAY:
                type = "Weekend";
                break;
            default:
                type = "Weekday";
                break;
        }
        return type;
    }

    // New style: the switch IS the value. Only two days are listed, so a default is required
    // to make the expression exhaustive.
    static String typeExpression(DayOfWeek day) {
        return switch (day) {
            case SATURDAY, SUNDAY -> "Weekend";
            default -> "Weekday";
        };
    }

    // Every DayOfWeek constant is listed, so no default is needed: the switch is exhaustive.
    static int letters(DayOfWeek day) {
        return switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> 6;
            case TUESDAY -> 7;
            case THURSDAY, SATURDAY -> 8;
            case WEDNESDAY -> 9;
        };
    }

    static int daysInMonth(int month, boolean leap) {
        return switch (month) {
            case 4, 6, 9, 11 -> 30;
            case 2 -> leap ? 29 : 28;
            default -> 31;
        };
    }

    // A block with yield, for a case that needs more than one expression.
    static String describe(int n) {
        return switch (Integer.signum(n)) {
            case 0 -> "zero";
            case 1 -> {
                String size = n > 100 ? "large" : "small";
                yield size + " positive";
            }
            default -> "negative";
        };
    }
}

/* Expected output:
MONDAY    classic=Weekday expression=Weekday letters=6
TUESDAY   classic=Weekday expression=Weekday letters=7
WEDNESDAY classic=Weekday expression=Weekday letters=9
THURSDAY  classic=Weekday expression=Weekday letters=8
FRIDAY    classic=Weekday expression=Weekday letters=6
SATURDAY  classic=Weekend expression=Weekend letters=8
SUNDAY    classic=Weekend expression=Weekend letters=6
days in Feb (leap): 29
days in Sep:        30
describe(0)   = zero
describe(7)   = small positive
describe(-3)  = negative
describe(150) = large positive
*/
