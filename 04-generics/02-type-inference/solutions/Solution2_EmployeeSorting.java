import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Solution to Exercise 2 (Medium): comparator chains and the "lambda parameter is Object" trap.
 *
 * TASK
 *   Return the comparators described below. Each one needs a chain:
 *   comparing(...) followed by .reversed() or .thenComparing(...).
 *
 *   The trap: this does NOT compile,
 *       Comparator.comparing(e -> e.salary()).reversed()
 *   because comparing(...) is the receiver of .reversed(), so it has no target type
 *   and e is inferred as Object.
 *
 *   Use a DIFFERENT fix in each method, so you've tried all three:
 *     bySalaryHighestFirst()   salary, highest first.            Fix: a method reference (Employee::salary).
 *     byDeptThenName()         department A-Z, then name A-Z.    Fix: an explicitly typed lambda,
 *                                                                (Employee e) -> e.department()
 *     byNameLengthLongestFirst()  name length, longest first;    Fix: an explicit type witness,
 *                              ties broken by name A-Z.               Comparator.<Employee, Integer>comparing(...)
 *                              (there's no method that returns the name length, so a lambda is needed here)
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   bySalaryHighestFirst:     PASS
 *   byDeptThenName:           PASS
 *   byNameLengthLongestFirst: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Comparator.comparing(...).reversed() reverses the WHOLE comparator built so far.
 *   - thenComparing(Employee::name) is fine after a typed first step: the type is already known.
 *   - If you see "cannot find symbol: method salary() in Object", that's the trap. Pick one of the fixes.
 *
 * Run: java solutions/Solution2_EmployeeSorting.java
 */
public class Solution2_EmployeeSorting {

    record Employee(String name, String department, int salary) {}

    // Fix 1, method reference: Employee::salary names the type, so nothing needs inferring.
    static Comparator<Employee> bySalaryHighestFirst() {
        return Comparator.comparing(Employee::salary).reversed();
    }

    // Fix 2, explicitly typed lambda: (Employee e) tells the compiler what e is.
    // Employee::department works just as well, but this exercise asked for the lambda form.
    static Comparator<Employee> byDeptThenName() {
        return Comparator.comparing((Employee e) -> e.department())
                .thenComparing(Employee::name);   // the type is known by now, so a method ref is fine
    }

    // Fix 3, type witness: <Employee, Integer> fills in comparing's T and U.
    // The lambda is needed here because no method returns the name length.
    static Comparator<Employee> byNameLengthLongestFirst() {
        return Comparator.<Employee, Integer>comparing(e -> e.name().length())
                .reversed()                        // reverses only the length part built so far
                .thenComparing(Employee::name);    // ties: A-Z, not reversed
    }

    public static void main(String[] args) {
        List<Employee> staff = List.of(
                new Employee("Priya", "Sales", 72000),
                new Employee("Arjun", "IT", 95000),
                new Employee("Kavya", "IT", 88000),
                new Employee("Sam", "Sales", 61000),
                new Employee("Rohan", "HR", 58000),
                new Employee("Nandini", "HR", 67000));

        boolean allPass = true;

        allPass &= check("bySalaryHighestFirst", () -> names(staff, bySalaryHighestFirst())
                .equals(List.of("Arjun", "Kavya", "Priya", "Nandini", "Sam", "Rohan")));

        allPass &= check("byDeptThenName", () -> names(staff, byDeptThenName())
                .equals(List.of("Nandini", "Rohan", "Arjun", "Kavya", "Priya", "Sam")));

        allPass &= check("byNameLengthLongestFirst", () -> names(staff, byNameLengthLongestFirst())
                .equals(List.of("Nandini", "Arjun", "Kavya", "Priya", "Rohan", "Sam")));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static List<String> names(List<Employee> staff, Comparator<Employee> order) {
        return staff.stream().sorted(order).map(Employee::name).toList();
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-25s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-25s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
