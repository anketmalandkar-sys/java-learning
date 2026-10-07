import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Example 1: the basics of Comparator.
 *
 * One Student class, no Comparable, and several different sort orders
 * chosen at the call site.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static class Student {
        private final String name;
        private final int marks;

        Student(String name, int marks) {
            this.name = name;
            this.marks = marks;
        }

        String getName() {
            return name;
        }

        int getMarks() {
            return marks;
        }

        @Override
        public String toString() {
            return name + "(" + marks + ")";
        }
    }

    public static void main(String[] args) {
        List<Student> students = new ArrayList<>(List.of(
                new Student("Ravi", 82),
                new Student("Anita", 95),
                new Student("Karan", 82),
                new Student("Meera", 70)
        ));
        System.out.println("Original:           " + students);

        // 1. Lambda: spell out the rule yourself.
        //    Integer.compare avoids the overflow bug of a.getMarks() - b.getMarks().
        Comparator<Student> byMarksLambda = (a, b) -> Integer.compare(a.getMarks(), b.getMarks());
        students.sort(byMarksLambda);
        System.out.println("By marks (lambda):  " + students);

        // 2. Factory method: same rule, shorter, nothing to get wrong.
        students.sort(Comparator.comparingInt(Student::getMarks));
        System.out.println("By marks (factory): " + students);

        // 3. Reverse an existing comparator instead of writing a new one.
        students.sort(Comparator.comparingInt(Student::getMarks).reversed());
        System.out.println("Marks high-to-low:  " + students);

        // 4. A completely different order for the same class.
        students.sort(Comparator.comparing(Student::getName));
        System.out.println("By name:            " + students);

        // 5. Tie-breaker: Ravi and Karan both have 82, so name decides between them.
        students.sort(Comparator.comparingInt(Student::getMarks).reversed()
                .thenComparing(Student::getName));
        System.out.println("Marks desc, name:   " + students);

        // 6. Comparators work on types you don't own, like String.
        //    Natural String order puts ALL uppercase letters before lowercase.
        List<String> words = new ArrayList<>(List.of("banana", "Cherry", "apple", "fig"));
        words.sort(Comparator.naturalOrder());
        System.out.println("Natural (case-sensitive): " + words);
        words.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("Case-insensitive:         " + words);
        words.sort(Comparator.comparingInt(String::length));
        System.out.println("By length:                " + words);
    }
}

/* Expected output:
Original:           [Ravi(82), Anita(95), Karan(82), Meera(70)]
By marks (lambda):  [Meera(70), Ravi(82), Karan(82), Anita(95)]
By marks (factory): [Meera(70), Ravi(82), Karan(82), Anita(95)]
Marks high-to-low:  [Anita(95), Ravi(82), Karan(82), Meera(70)]
By name:            [Anita(95), Karan(82), Meera(70), Ravi(82)]
Marks desc, name:   [Anita(95), Karan(82), Ravi(82), Meera(70)]
Natural (case-sensitive): [Cherry, apple, banana, fig]
Case-insensitive:         [apple, banana, Cherry, fig]
By length:                [fig, apple, banana, Cherry]
*/
