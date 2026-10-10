/**
 * Example 2: a ride-hailing fare calculator with switch expressions.
 *
 *   - An exhaustive enum switch (no default): adding a new RideType breaks the build
 *     until the fare rule is written, instead of silently charging the wrong fare.
 *   - yield for a case that needs a few lines.
 *   - throw as a case result.
 *   - An arrow-form switch STATEMENT for side effects.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    enum RideType { BIKE, AUTO, CAB, PREMIUM }

    public static void main(String[] args) {
        double km = 12;
        for (RideType type : RideType.values()) {
            System.out.printf("%-7s %5.1f km -> Rs %.2f%n", type, km, fare(type, km, false));
        }
        System.out.printf("CAB at night -> Rs %.2f%n", fare(RideType.CAB, km, true));

        for (String action : new String[] {"book", "CANCEL", "rate", "teleport"}) {
            handle(action);
        }

        try {
            fare(RideType.BIKE, -1, false);
        } catch (IllegalArgumentException e) {
            System.out.println("error: " + e.getMessage());
        }
    }

    static double fare(RideType type, double km, boolean night) {
        if (km < 0) {
            throw new IllegalArgumentException("distance can't be negative");
        }
        double perKm = switch (type) {
            case BIKE -> 6;
            case AUTO -> 10;
            case CAB -> {
                double base = 14;
                yield night ? base * 1.25 : base;
            }
            case PREMIUM -> 22;
        };
        return perKm * km;
    }

    static void handle(String action) {
        // Arrow form as a statement: no fall-through, no breaks.
        switch (action.toLowerCase()) {
            case "book" -> System.out.println("Finding a driver...");
            case "cancel" -> System.out.println("Ride cancelled");
            case "rate" -> {
                int stars = 5;
                System.out.println("Thanks for rating " + stars + " stars");
            }
            default -> System.out.println("Unknown action: " + action);
        }
    }
}

/* Expected output:
BIKE     12.0 km -> Rs 72.00
AUTO     12.0 km -> Rs 120.00
CAB      12.0 km -> Rs 168.00
PREMIUM  12.0 km -> Rs 264.00
CAB at night -> Rs 210.00
Finding a driver...
Ride cancelled
Thanks for rating 5 stars
Unknown action: teleport
error: distance can't be negative
*/
