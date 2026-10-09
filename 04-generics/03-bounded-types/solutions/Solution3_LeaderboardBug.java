import java.util.List;

/**
 * Solution to Exercise 3 (Hard): a leaderboard helper that crashes at runtime.
 *
 * THE SITUATION
 *   topPerformer() picks the "greatest" item from a list. Its first version was
 *       static <T extends Comparable<T>> T topPerformer(List<T> items)
 *   but then topPerformer(managers) stopped compiling. To "fix" it, someone changed
 *   the bound to a RAW Comparable. Now everything compiles (with an "unchecked" note),
 *   and the monthly highlight crashes with a ClassCastException.
 *
 * TASK
 *   1. Run it and read the stack trace. Why does the compiler allow
 *      topPerformer(monthlyHighlights()) when the list mixes Employees and Strings?
 *   2. Put the bound back to Comparable<T> (just to see the original problem) and read the
 *      error on topPerformer(managers). Why is a Manager not a Comparable<Manager>?
 *   3. Write the correct bound: one that accepts employees, managers AND scores, but makes
 *      topPerformer(monthlyHighlights()) a COMPILE error. The compiler should also stop
 *      printing the "unchecked" note.
 *   4. Once that call no longer compiles, delete highlightOfTheMonth() and the line in main
 *
 * EXPECTED OUTPUT (after the fix)
 *   employees: PASS
 *   managers:  PASS
 *   scores:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - Manager inherits compareTo(Employee) from Employee. So a Manager is a Comparable<Employee>.
 *   - You need "T can be compared with T or with one of T's parents". See section 5 of the README.
 *   - Compile with javac -Xlint:unchecked exercises/Solution3_LeaderboardBug.java to see which
 *     line the "unchecked" note is about.
 *
 * Run: java solutions/Solution3_LeaderboardBug.java
 */
public class Solution3_LeaderboardBug {

    static class Employee implements Comparable<Employee> {
        private final String name;
        private final int sales;

        Employee(String name, int sales) {
            this.name = name;
            this.sales = sales;
        }

        String name() { return name; }

        @Override
        public int compareTo(Employee other) {
            return Integer.compare(sales, other.sales);
        }

        @Override
        public String toString() { return name + "(" + sales + ")"; }
    }

    static class Manager extends Employee {
        private final int teamSize;

        Manager(String name, int sales, int teamSize) {
            super(name, sales);
            this.teamSize = teamSize;
        }

        int teamSize() { return teamSize; }
    }

    // Why the raw bound crashed: with a raw Comparable, compareTo takes an Object, so the compiler
    // let a List<Comparable<?>> holding Employees AND Strings through. At runtime String.compareTo
    // received an Employee: ClassCastException.
    //
    // Why <T extends Comparable<T>> rejected managers: Manager inherits compareTo(Employee) from
    // Employee, so a Manager is a Comparable<Employee>, not a Comparable<Manager>.
    //
    // The fix: "T can be compared with T or with one of T's parents". Managers fit (T = Manager,
    // compared as an Employee), and the mixed list no longer compiles. Collections.max uses this form.
    static <T extends Comparable<? super T>> T topPerformer(List<T> items) {
        T best = items.get(0);
        for (T item : items) {
            if (item.compareTo(best) > 0) {
                best = item;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        List<Employee> employees = List.of(
                new Employee("Ravi", 120), new Employee("Meera", 340), new Employee("Arjun", 210));
        List<Manager> managers = List.of(
                new Manager("Kavya", 900, 6), new Manager("Sam", 1500, 4), new Manager("Priya", 700, 9));
        List<Integer> scores = List.of(72, 95, 88);

        boolean allPass = true;

        Employee bestEmployee = topPerformer(employees);
        allPass &= report("employees", bestEmployee.name().equals("Meera"));

        Manager bestManager = topPerformer(managers);   // must stay a Manager: teamSize() is Manager-only
        allPass &= report("managers", bestManager.name().equals("Sam") && bestManager.teamSize() == 4);

        Integer bestScore = topPerformer(scores);
        allPass &= report("scores", bestScore == 95);


        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean report(String name, boolean ok) {
        System.out.printf("%-10s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
