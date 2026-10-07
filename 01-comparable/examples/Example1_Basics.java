import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * Example 1: the basics of Comparable.
 * A Student has a natural order: by roll number, ascending.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static class Student implements Comparable<Student> {
        private final String name;
        private final int rollNumber;

        Student(String name, int rollNumber) {
            this.name = name;
            this.rollNumber = rollNumber;
        }

        @Override
            public int compareTo(Student other) {
            // Integer.compare is overflow-safe, unlike "this.rollNumber - other.rollNumber"
            return Integer.compare(this.rollNumber, other.rollNumber);
        }

        @Override
        public String toString() {
            return name + "(" + rollNumber + ")";
        }
    }

    public static void main(String[] args) {
        // 1. Built-in types already implement Comparable
        List<String> names = new ArrayList<>(List.of("Ravi", "Asha", "Meera"));
        Collections.sort(names);
        System.out.println("Sorted strings:  " + names);

        // 2. Only the SIGN of compareTo matters
        System.out.println("\"apple\".compareTo(\"banana\") = " + "apple".compareTo("banana") + "  (negative: apple comes first)");
        System.out.println("Integer.compare(10, 3)       = " + Integer.compare(10, 3) + "   (positive: 10 comes after 3)");

        // 3. Our own class: Collections.sort uses compareTo automatically
        List<Student> students = new ArrayList<>(List.of(
                new Student("Ravi", 42),
                new Student("Asha", 7),
                new Student("Meera", 19)
        ));
        System.out.println("\nBefore sort:     " + students);
        Collections.sort(students);
        System.out.println("After sort:      " + students);

        // 4. min/max also use the natural order, with no extra code
        System.out.println("Lowest roll no:  " + Collections.min(students));
        System.out.println("Highest roll no: " + Collections.max(students));

        // 5. TreeSet keeps elements sorted as you add them
        TreeSet<Student> register = new TreeSet<>();
        register.add(new Student("Kiran", 30));
        register.add(new Student("Dev", 3));
        register.add(new Student("Neha", 15));
        System.out.println("TreeSet:         " + register);

        // 6. Reverse order without touching the class
        students.sort(Collections.reverseOrder());
        System.out.println("Reversed:        " + students);
    }
}

/* Expected output:
Sorted strings:  [Asha, Meera, Ravi]
"apple".compareTo("banana") = -1  (negative: apple comes first)
Integer.compare(10, 3)       = 1   (positive: 10 comes after 3)

Before sort:     [Ravi(42), Asha(7), Meera(19)]
After sort:      [Asha(7), Meera(19), Ravi(42)]
Lowest roll no:  Asha(7)
Highest roll no: Ravi(42)
TreeSet:         [Dev(3), Neha(15), Kiran(30)]
Reversed:        [Ravi(42), Meera(19), Asha(7)]
*/
