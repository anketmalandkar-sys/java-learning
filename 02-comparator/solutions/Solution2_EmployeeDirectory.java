import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Solution 2 (Medium): an employee directory with a multi-field sort.
 *
 * TASK
 *   Write directoryOrder() so the directory is sorted by:
 *     1. department, A to Z
 *     2. within a department, salary HIGHEST first
 *     3. if salary is also equal, name A to Z
 *
 *   Then write byManager(): sort by manager name A to Z, where employees with NO manager
 *   (manager == null, e.g. the CEO) come FIRST. Ties broken by name A to Z.
 *
 * EXPECTED OUTPUT
 *   Directory:   [Farah(Eng,150000), Arjun(Eng,120000), Divya(Eng,120000), Ira(HR,70000), Ben(Sales,90000), Chen(Sales,60000)]
 *   By manager:  [Farah(Eng,150000), Chen(Sales,60000), Arjun(Eng,120000), Ben(Sales,90000), Divya(Eng,120000), Ira(HR,70000)]
 *   PASS
 *
 * HINTS
 *   - Chain with thenComparing / thenComparingInt.
 *   - To reverse ONE key, not the whole chain: thenComparing(keyFn, Comparator.reverseOrder()).
 *     Putting .reversed() at the very end would also flip the department order.
 *   - For nulls: Comparator.comparing(Employee::getManager, Comparator.nullsFirst(Comparator.naturalOrder()))
 *
 * Run: java exercises/Solution2_EmployeeDirectory.java
 */
public class Solution2_EmployeeDirectory {

    static class Employee {
        private final String name;
        private final String department;
        private final int salary;
        private final String manager;   // null for the CEO

        Employee(String name, String department, int salary, String manager) {
            this.name = name;
            this.department = department;
            this.salary = salary;
            this.manager = manager;
        }

        String getName() { return name; }
        String getDepartment() { return department; }
        int getSalary() { return salary; }
        String getManager() { return manager; }

        @Override
        public String toString() {
            return name + "(" + department + "," + salary + ")";
        }
    }

    static Comparator<Employee> directoryOrder() {
        // reverseOrder() on the salary key only, so departments stay A-Z.
        return Comparator.comparing(Employee::getDepartment)
                .thenComparing(Comparator.comparingInt(Employee::getSalary).reversed())
                .thenComparing(Employee::getName);
    }

    static Comparator<Employee> byManager() {
        // nullsFirst wraps the key comparator, so the CEO (manager == null) sorts first instead of throwing.
        return Comparator.comparing(Employee::getManager, Comparator.nullsFirst(Comparator.naturalOrder()))
                .thenComparing(Employee::getName);
    }

    public static void main(String[] args) {
        List<Employee> staff = new ArrayList<>(List.of(
                new Employee("Chen", "Sales", 60000, "Ben"),
                new Employee("Divya", "Eng", 120000, "Farah"),
                new Employee("Ben", "Sales", 90000, "Farah"),
                new Employee("Farah", "Eng", 150000, null),
                new Employee("Ira", "HR", 70000, "Farah"),
                new Employee("Arjun", "Eng", 120000, "Farah")
        ));

        if (directoryOrder() == null || byManager() == null) {
            System.out.println("FAIL: implement directoryOrder() and byManager() first");
            return;
        }

        List<Employee> directory = new ArrayList<>(staff);
        directory.sort(directoryOrder());
        System.out.println("Directory:   " + directory);

        List<Employee> managerView = new ArrayList<>(staff);
        managerView.sort(byManager());
        System.out.println("By manager:  " + managerView);

        System.out.println(check(directory, managerView) ? "PASS" : "FAIL");
    }

    // Compares names only, in order.
    static boolean check(List<Employee> directory, List<Employee> managerView) {
        boolean directoryOk = names(directory).equals(List.of("Farah", "Arjun", "Divya", "Ira", "Ben", "Chen"));
        // Farah (no manager) first; then manager "Ben": Chen; then manager "Farah": Arjun, Ben, Divya, Ira.
        boolean managerOk = names(managerView).equals(List.of("Farah", "Chen", "Arjun", "Ben", "Divya", "Ira"));

        if (!directoryOk) System.out.println("  directoryOrder() is wrong. Expected names: [Farah, Arjun, Divya, Ira, Ben, Chen]");
        if (!managerOk) System.out.println("  byManager() is wrong. Expected names: [Farah, Chen, Arjun, Ben, Divya, Ira]");
        return directoryOk && managerOk;
    }

    static List<String> names(List<Employee> list) {
        List<String> result = new ArrayList<>();
        for (Employee e : list) {
            result.add(e.getName());
        }
        return result;
    }
}
