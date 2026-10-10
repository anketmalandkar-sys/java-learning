import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): employee reports, from loops to pipelines.
 *
 * TASK
 *   Each report has an imperative version (done, don't change it). Write the functional version:
 *   no loops, no mutable collections or counters, one stream pipeline each.
 *
 *     activeNamesIn(employees, dept)    names of ACTIVE employees in dept, UPPERCASE, sorted A-Z
 *                                       activeNamesIn(staff, "ENG") -> [ANJALI, RAVI, VIKRAM]
 *                                       (foreach + if + transformation -> filter + map + sorted)
 *
 *     payroll(employees, dept)          total salary of ACTIVE employees in dept
 *                                       payroll(staff, "ENG") -> 375000
 *                                       (accumulator -> mapToInt + sum)
 *
 *     activeHeadcount(employees)        department -> number of ACTIVE employees in it
 *                                       {ENG=3, HR=1, SALES=2}
 *                                       (map.merge in a loop -> Collectors.groupingBy + counting)
 *
 *   Don't change the record, the data, or main.
 *
 * EXPECTED OUTPUT
 *   activeNamesIn:   PASS
 *   payroll:         PASS
 *   activeHeadcount: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Filter first, then map: transform only the employees you keep.
 *   - Method references read well here: Employee::name, String::toUpperCase, Employee::department.
 *   - mapToInt gives an IntStream, which has sum(). An empty IntStream sums to 0.
 *   - groupingBy(classifier, Collectors.counting()) gives a Map<K, Long>.
 *   - A map with the same entries is equal to another regardless of order, so a HashMap is fine.
 *
 * Run: java exercises/Exercise2_EmployeeReports.java
 */
public class Exercise2_EmployeeReports {

    record Employee(String name, String department, int salary, boolean active) {}

    static List<String> activeNamesInImperative(List<Employee> employees, String dept) {
        List<String> names = new ArrayList<>();
        for (Employee e : employees) {
            if (e.active() && e.department().equals(dept)) {
                names.add(e.name().toUpperCase());
            }
        }
        Collections.sort(names);
        return names;
    }

    static List<String> activeNamesIn(List<Employee> employees, String dept) {
        // TODO
        return List.of();
    }

    static int payrollImperative(List<Employee> employees, String dept) {
        int total = 0;
        for (Employee e : employees) {
            if (e.active() && e.department().equals(dept)) {
                total += e.salary();
            }
        }
        return total;
    }

    static int payroll(List<Employee> employees, String dept) {
        // TODO
        return -1;
    }

    static Map<String, Long> activeHeadcountImperative(List<Employee> employees) {
        Map<String, Long> counts = new HashMap<>();
        for (Employee e : employees) {
            if (e.active()) {
                counts.merge(e.department(), 1L, Long::sum);
            }
        }
        return counts;
    }

    static Map<String, Long> activeHeadcount(List<Employee> employees) {
        // TODO
        return Map.of();
    }

    public static void main(String[] args) {
        List<Employee> staff = List.of(
                new Employee("Ravi", "ENG", 120_000, true),
                new Employee("Meera", "HR", 70_000, true),
                new Employee("Vikram", "ENG", 140_000, true),
                new Employee("Sana", "SALES", 60_000, true),
                new Employee("Joel", "ENG", 200_000, false),
                new Employee("Anjali", "ENG", 115_000, true),
                new Employee("Kiran", "SALES", 65_000, true),
                new Employee("Arun", "HR", 72_000, false));

        boolean allPass = true;

        allPass &= check("activeNamesIn", () ->
                activeNamesIn(staff, "ENG").equals(List.of("ANJALI", "RAVI", "VIKRAM"))
                        && activeNamesIn(staff, "HR").equals(activeNamesInImperative(staff, "HR"))
                        && activeNamesIn(staff, "LEGAL").isEmpty());

        allPass &= check("payroll", () ->
                payroll(staff, "ENG") == 375_000
                        && payroll(staff, "SALES") == payrollImperative(staff, "SALES")
                        && payroll(staff, "LEGAL") == 0);

        allPass &= check("activeHeadcount", () ->
                activeHeadcount(staff).equals(Map.of("ENG", 3L, "HR", 1L, "SALES", 2L))
                        && activeHeadcount(staff).equals(activeHeadcountImperative(staff))
                        && activeHeadcount(List.of()).isEmpty());

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (RuntimeException e) {
            System.out.printf("%-16s FAIL (%s)%n", name + ":", e);
            return false;
        }
        System.out.printf("%-16s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
