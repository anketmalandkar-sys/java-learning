import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Employee implements Comparable<Employee> {
    private Integer id;
    private String name;
    private Double salary;

    public Employee(Integer id, String name, Double salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
    }


    @Override
    public int compareTo(Employee o) {
        int res = Double.compare(o.salary, this.salary);
        if (res != 0) {
            return res;
        }
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                '}';
    }
}


public class Excercis4_Asc_Desc {

    public static void main(String args[]) {
        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(new Employee(1, "Sanket", 50000D));
        employeeList.add(new Employee(2, "Yuvraj", 80000D));
        employeeList.add(new Employee(3, "Anket", 40000D));
        employeeList.add(new Employee(4, "Siddhi", 50000D));
        employeeList.add(new Employee(5, "Ganesh", 50000D));
        employeeList.add(new Employee(6, "Maxim", 20000D));

        Collections.sort(employeeList);

        System.out.println(employeeList);

    }

}
