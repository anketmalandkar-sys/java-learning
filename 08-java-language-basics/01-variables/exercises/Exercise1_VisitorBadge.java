/**
 * Exercise 1 (Easy): static counter + instance field.
 *
 * TASK
 *   A museum gives each visitor a badge. Badge numbers start at 1 and go up by one per visitor.
 *   Complete the Visitor class:
 *     1. Add a STATIC int field that counts how many visitors have been created so far.
 *     2. Add an INSTANCE int field that holds this visitor's badge number.
 *     3. In the constructor, increase the counter and give this visitor the new count as their badge.
 *     4. Make badgeNumber() and totalVisitors() return the right fields.
 *
 * EXPECTED OUTPUT
 *   first badge:   PASS
 *   second badge:  PASS
 *   third badge:   PASS
 *   total:         PASS
 *   ALL PASS
 *
 * HINTS
 *   - If every visitor ends up with the same badge number, which field did you make static?
 *   - The static counter is shared, so after 3 visitors it's 3 for everyone.
 *
 * Run: java exercises/Exercise1_VisitorBadge.java
 */
public class Exercise1_VisitorBadge {

    static class Visitor {
        // TODO: static field counting visitors

        // TODO: instance field for this visitor's badge number

        final String name;

        Visitor(String name) {
            this.name = name;
            // TODO: increase the counter and assign this visitor's badge number
        }

        int badgeNumber() {
            return 0; // TODO
        }

        static int totalVisitors() {
            return 0; // TODO
        }
    }

    public static void main(String[] args) {
        Visitor meera = new Visitor("Meera");
        Visitor arjun = new Visitor("Arjun");
        Visitor lena = new Visitor("Lena");

        boolean allPass = true;
        allPass &= check("first badge:", meera.badgeNumber() == 1);
        allPass &= check("second badge:", arjun.badgeNumber() == 2);
        allPass &= check("third badge:", lena.badgeNumber() == 3);
        allPass &= check("total:", Visitor.totalVisitors() == 3);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-14s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
