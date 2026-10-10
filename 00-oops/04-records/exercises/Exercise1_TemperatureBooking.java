/**
 * Exercise 1 (Easy): basic records.
 *
 * TASK
 *   1. Declare record Temperature(double celsius) with:
 *        - a compact constructor that rejects values below -273.15 with
 *          IllegalArgumentException("below absolute zero: " + celsius)
 *        - a method fahrenheit() = celsius * 9 / 5 + 32
 *        - a static factory fromFahrenheit(double f)
 *
 *   2. Declare record Booking(String guest, int nights) with:
 *        - a compact constructor that strips the guest name and rejects nights < 1
 *          with IllegalArgumentException("nights must be at least 1")
 *        - a second constructor Booking(String guest) for a 1-night booking, delegating with this(...)
 *
 *   Then delete the placeholders below and uncomment the checks in main.
 *
 * EXPECTED OUTPUT
 *   temperature: PASS
 *   absolute 0:  PASS
 *   booking:     PASS
 *   equality:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - In a compact constructor, reassign the parameter: guest = guest.strip();
 *   - Records get equals/hashCode for free: two Bookings with the same values are equal.
 *
 * Run: java exercises/Exercise1_TemperatureBooking.java
 */
public class Exercise1_TemperatureBooking {

    // TODO: replace these placeholders with the real records
    record Temperature() { }
    record Booking() { }

    public static void main(String[] args) {
        boolean allPass = true;
        // uncomment:
        // allPass &= check("temperature:", new Temperature(100).fahrenheit() == 212
        //         && Math.abs(Temperature.fromFahrenheit(32).celsius()) < 1e-9);
        // allPass &= check("absolute 0:", rejects(() -> new Temperature(-300), "below absolute zero: -300.0"));
        // allPass &= check("booking:", new Booking("  Asha ", 2).guest().equals("Asha")
        //         && new Booking("Ravi").nights() == 1 && rejects(() -> new Booking("Zoya", 0), "nights must be at least 1"));
        // allPass &= check("equality:", new Booking("Asha", 2).equals(new Booking(" Asha", 2))
        //         && new Booking("Asha", 2).toString().equals("Booking[guest=Asha, nights=2]"));
        allPass &= check("uncommented:", false);   // TODO: delete this line after uncommenting
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean rejects(Runnable action, String message) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException e) {
            return message.equals(e.getMessage());
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
