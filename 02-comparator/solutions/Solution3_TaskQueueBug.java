import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Solution 3 (Hard): find and fix the bugs in a task scheduler.
 *
 * A teammate wrote this scheduler. It compiles and runs, but the output is wrong.
 *
 * REQUIRED ORDER
 *   1. priority HIGHEST first (3 = critical, 1 = low)
 *   2. within the same priority, EARLIEST due time first
 *   3. if priority and due time are both equal, name A to Z
 *
 * The same SCHEDULE comparator drives two things:
 *   - a PriorityQueue that hands out the next task to work on
 *   - a TreeSet that keeps a sorted, de-duplicated view of all tasks
 *
 * EXPECTED OUTPUT (after your fix)
 *   Work order: [Security patch, Fix login bug, Plan sprint, Renew SSL cert, Archive logs, Write docs]
 *   Task board: 6 tasks
 *   PASS
 *
 * There are THREE separate bugs, all in the comparator code. Fix only the comparators;
 * the data and the main method are correct.
 *
 * HINTS (read one at a time if you're stuck)
 *   1. What happens when you cast a very large long difference to int?
 *   2. What exactly does .reversed() reverse when it's at the end of a chain?
 *   3. How does a TreeSet decide that two elements are duplicates?
 *
 * Run: java exercises/Solution3_TaskQueueBug.java
 */
public class Solution3_TaskQueueBug {

    static final long HOUR = 60L * 60 * 1000;
    static final long DAY = 24 * HOUR;
    static final long NOW = 1_780_000_000_000L;   // a fixed "current time" in epoch millis

    static class Task {
        private final String name;
        private final int priority;
        private final long dueAtMillis;

        Task(String name, int priority, long dueAtMillis) {
            this.name = name;
            this.priority = priority;
            this.dueAtMillis = dueAtMillis;
        }

        String getName() { return name; }
        int getPriority() { return priority; }
        long getDueAtMillis() { return dueAtMillis; }

        @Override
        public String toString() {
            return name;
        }
    }

    // ---- Fixed comparators ----

    // Bug 1: (int) (a - b) on epoch millis overflows for gaps over ~24.8 days, flipping the sign.
    static final Comparator<Task> BY_DUE = Comparator.comparingLong(Task::getDueAtMillis);

    // Bug 2: .reversed() at the end flipped the due-time order too. Reverse only the priority key.
    // Bug 3: without a name tie-breaker, TreeSet treated Plan sprint and Renew SSL cert as duplicates.
    static final Comparator<Task> SCHEDULE =
            Comparator.comparingInt(Task::getPriority).reversed()
                    .thenComparing(BY_DUE)
                    .thenComparing(Task::getName);

    // ---- The code below is correct. ----

    public static void main(String[] args) {
        List<Task> tasks = List.of(
                new Task("Write docs", 1, NOW + 40 * DAY),
                new Task("Renew SSL cert", 2, NOW + DAY),
                new Task("Fix login bug", 3, NOW + 2 * DAY),
                new Task("Archive logs", 1, NOW + 3 * DAY),
                new Task("Security patch", 3, NOW + HOUR),
                new Task("Plan sprint", 2, NOW + DAY)
        );

        PriorityQueue<Task> queue = new PriorityQueue<>(SCHEDULE);
        queue.addAll(tasks);
        List<String> workOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            workOrder.add(queue.poll().getName());
        }
        System.out.println("Work order: " + workOrder);

        Set<Task> board = new TreeSet<>(SCHEDULE);
        board.addAll(tasks);
        System.out.println("Task board: " + board.size() + " tasks");

        List<String> expected = List.of("Security patch", "Fix login bug", "Plan sprint",
                "Renew SSL cert", "Archive logs", "Write docs");
        boolean pass = workOrder.equals(expected) && board.size() == tasks.size();
        System.out.println(pass ? "PASS" : "FAIL: expected " + expected + " and " + tasks.size() + " tasks");
    }
}
