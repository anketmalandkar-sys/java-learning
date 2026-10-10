import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 3 (Hard): a leaky record and a local record.
 *
 * TASK
 *   1. Team is a record, so it LOOKS immutable, but the checks show its members can be changed
 *      from outside in two ways: through the list passed in, and through members().
 *      Fix it in the compact constructor only (no custom accessor). Also reject a null list
 *      or a null member with NullPointerException, and strip every member's name.
 *
 *   2. Write busiestMonth(List<Shift> shifts): the month (1-12) with the most total hours, and
 *      the hours, as a MonthHours record. On a tie, the earlier month wins.
 *      Inside the method, use a LOCAL record (e.g. record Total(int month, int hours) {}) for
 *      the per-month totals.
 *
 * EXPECTED OUTPUT
 *   not leaky (input):    PASS
 *   not leaky (accessor): PASS
 *   nulls rejected:       PASS
 *   names stripped:       PASS
 *   busiest month:        PASS
 *   ALL PASS
 *
 * HINTS
 *   - List.copyOf makes an unmodifiable copy and throws NullPointerException for null elements.
 *   - Strip names before copying: build a new ArrayList, then List.copyOf it.
 *
 * Run: java exercises/Exercise3_TeamRoster.java
 */
public class Exercise3_TeamRoster {

    record Team(String name, List<String> members) {
        Team {
            // TODO
        }
    }

    record Shift(String person, int month, int hours) { }

    record MonthHours(int month, int hours) { }

    static MonthHours busiestMonth(List<Shift> shifts) {
        return null; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;

        List<String> input = new ArrayList<>(List.of("Asha", "Ravi"));
        Team team = new Team("Blue", input);
        input.add("Mallory");
        allPass &= check("not leaky (input):", team.members().size() == 2);

        boolean accessorSafe;
        try {
            team.members().add("Eve");
            accessorSafe = false;
        } catch (UnsupportedOperationException e) {
            accessorSafe = true;
        }
        allPass &= check("not leaky (accessor):", accessorSafe && team.members().size() == 2);

        List<String> withNull = new ArrayList<>();
        withNull.add(null);
        allPass &= check("nulls rejected:", throwsNpe(() -> new Team("X", null)) && throwsNpe(() -> new Team("X", withNull)));

        allPass &= check("names stripped:", new Team("Y", List.of(" Zoya ", "Kiran ")).members().equals(List.of("Zoya", "Kiran")));

        List<Shift> shifts = List.of(
                new Shift("asha", 1, 8), new Shift("ravi", 2, 6), new Shift("asha", 2, 6),
                new Shift("zoya", 3, 12), new Shift("ravi", 1, 4));
        MonthHours busiest = busiestMonth(shifts);
        allPass &= check("busiest month:", new MonthHours(1, 12).equals(busiest));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean throwsNpe(Runnable action) {
        try {
            action.run();
            return false;
        } catch (NullPointerException e) {
            return true;
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-21s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
