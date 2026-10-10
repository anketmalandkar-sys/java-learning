import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Exercise 3 (Hard): three boxing bugs from a support-ticket system.
 *
 * TASK
 *   Each method has ONE bug caused by autoboxing. They pass small tests and fail in production.
 *   Find and fix each one; don't change the method signatures.
 *
 *     closeTicket(openIds, ticketId)    remove the ticket with that ID from the list
 *                                       (IDs are large numbers like 1001)
 *     sameAssignee(a, b)                true if both tickets have the same assignee user ID;
 *                                       either may be null (unassigned): two nulls are NOT the same
 *     priorityOf(ticketId, overrides)   the override priority if one exists in the map,
 *                                       otherwise the default 3
 *
 *   Then write:
 *     averageResponseMinutes(minutes)   average of a List<Integer>, ignoring null entries,
 *                                       as a double. 0.0 for an empty or all-null list.
 *
 * EXPECTED OUTPUT
 *   closeTicket:   PASS
 *   sameAssignee:  PASS
 *   priorityOf:    PASS
 *   average:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - List<Integer> has remove(int index) AND remove(Object o). Which one does
 *     remove(ticketId) call when ticketId is an int?
 *   - == between two Integers compares references. Which values are cached?
 *   - In "cond ? boxed : 3", what type is the whole ternary?
 *
 * Run: java exercises/Exercise3_BoxingBugs.java
 */
public class Exercise3_BoxingBugs {

    static void closeTicket(List<Integer> openIds, int ticketId) {
        openIds.remove(ticketId);
    }

    static boolean sameAssignee(Integer a, Integer b) {
        return a != null && a == b;
    }

    static Integer priorityOf(int ticketId, Map<Integer, Integer> overrides) {
        return overrides.containsKey(ticketId) ? overrides.get(ticketId) : 3;
    }

    static double averageResponseMinutes(List<Integer> minutes) {
        return -1; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("closeTicket:", safe(() -> {
            List<Integer> open = new ArrayList<>(List.of(1001, 1002, 1003));
            closeTicket(open, 1002);
            List<Integer> small = new ArrayList<>(List.of(5, 0, 1));
            closeTicket(small, 1);
            return open.equals(List.of(1001, 1003)) && small.equals(List.of(5, 0));
        }));

        allPass &= check("sameAssignee:", safe(() -> sameAssignee(42, 42)
                && sameAssignee(5000, 5000) && !sameAssignee(5000, 5001)
                && !sameAssignee(null, null) && !sameAssignee(null, 7)));

        allPass &= check("priorityOf:", safe(() -> {
            Map<Integer, Integer> overrides = new java.util.HashMap<>();
            overrides.put(1001, 1);
            overrides.put(1002, null);   // override removed by an admin: means "no override"
            Integer p1 = priorityOf(1001, overrides);
            Integer p2 = priorityOf(1003, overrides);
            Integer p3 = priorityOf(1002, overrides);
            return p1 == 1 && p2 == 3 && p3 == 3;
        }));

        allPass &= check("average:", safe(() -> {
            List<Integer> mins = new ArrayList<>(List.of(10, 20));
            mins.add(null);
            mins.add(30);
            List<Integer> nulls = new ArrayList<>();
            nulls.add(null);
            return averageResponseMinutes(mins) == 20.0 && averageResponseMinutes(List.of()) == 0.0
                    && averageResponseMinutes(nulls) == 0.0;
        }));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    interface Check {
        boolean run();
    }

    static boolean safe(Check check) {
        try {
            return check.run();
        } catch (RuntimeException e) {
            return false;
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-13s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
