import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Example 3: the antisymmetry rule (swap the arguments, the sign must flip), and the two forms
 * of thenComparing.
 *
 * Run: java examples/Example3_ComparatorContract.java
 */
public class Example3_ComparatorContract {

    record Task(String name, boolean urgent, int estimateHours) { }

    public static void main(String[] args) {
        Task a = new Task("deploy", true, 3);
        Task b = new Task("hotfix", true, 1);

        // Broken: never returns a negative for two urgent tasks, so both orders say "after".
        Comparator<Task> broken = (x, y) -> x.urgent() ? 1 : -1;
        System.out.println("broken:  compare(a, b) = " + broken.compare(a, b) + ", compare(b, a) = " + broken.compare(b, a)
                + "  -> same sign: contract violated");

        // Fixed: urgent tasks first, built from a key, so swapping arguments flips the sign.
        Comparator<Task> urgentFirst = Comparator.comparing(Task::urgent).reversed();
        System.out.println("fixed:   compare(a, b) = " + urgentFirst.compare(a, b) + ", compare(b, a) = " + urgentFirst.compare(b, a)
                + "  -> both urgent: equal, consistent");

        List<Task> tasks = new ArrayList<>(List.of(
                new Task("report", false, 2), a, b, new Task("email", false, 1)));

        // thenComparing with a key extractor...
        Comparator<Task> byUrgencyThenEstimate = urgentFirst.thenComparingInt(Task::estimateHours);
        tasks.sort(byUrgencyThenEstimate);
        System.out.println("urgent first, then shortest: " + names(tasks));

        // ...or with a whole comparator you already have.
        Comparator<Task> byName = Comparator.comparing(Task::name);
        tasks.sort(urgentFirst.thenComparing(byName));
        System.out.println("urgent first, then by name:  " + names(tasks));
    }

    static List<String> names(List<Task> tasks) {
        List<String> names = new ArrayList<>();
        for (Task t : tasks) {
            names.add(t.name());
        }
        return names;
    }
}

/* Expected output:
broken:  compare(a, b) = 1, compare(b, a) = 1  -> same sign: contract violated
fixed:   compare(a, b) = 0, compare(b, a) = 0  -> both urgent: equal, consistent
urgent first, then shortest: [hotfix, deploy, email, report]
urgent first, then by name:  [deploy, hotfix, email, report]
*/
