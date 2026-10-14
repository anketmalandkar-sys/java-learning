import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): a PECS-style TaskQueue, and a wildcard capture helper.
 *
 * TASK
 *   Part A. TaskQueue<T> holds tasks in order. Four of its methods take a parameter
 *   with a bare ? that you have to fix. For each one, decide whether it should be
 *   ? extends T or ? super T, change the type, then implement the method:
 *
 *     addAll(source)     add every item from source to the end of the queue
 *     drainTo(target)    move every item into target (in order), empty the queue,
 *                        and return how many items were moved
 *     removeIf(rule)     remove every item the rule matches; return how many were removed
 *     forEach(action)    run action on every item, in order
 *
 *   main uses them like a real app would: it adds UrgentTasks to a TaskQueue<Task>,
 *   drains into a List<Object> archive, and passes a Predicate<Object> and a Consumer<Object>.
 *
 *   Part B. rotate(list) moves the FIRST element to the END, for a list of any type.
 *   Keep its List<?> signature. That means you need wildcard capture: write a private
 *   helper <T> void rotateHelper(List<T> list) and call it.
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   addAll:   PASS
 *   removeIf: PASS
 *   forEach:  PASS
 *   drainTo:  PASS
 *   rotate:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - PECS: if the method reads Ts out of the parameter, it's a producer. If it hands Ts to it, it's a consumer.
 *     A Predicate or Consumer is handed Ts, so it's a consumer.
 *   - The capture error looks like "incompatible types: Object cannot be converted to CAP#1".
 *
 * Run: java exercises/Exercise2_TaskQueue.java
 */
public class Exercise2_TaskQueue {

    static class Task {
        final String title;

        Task(String title) {
            this.title = title;
        }

        @Override
        public String toString() { return title; }
    }

    static class UrgentTask extends Task {
        UrgentTask(String title) {
            super(title);
        }

        @Override
        public String toString() { return "!" + title; }
    }

    static class TaskQueue<T> {
        private final List<T> items = new ArrayList<>();

        void add(T item) {
            items.add(item);
        }

        List<T> snapshot() {
            return List.copyOf(items);
        }

        // Producer: we read Ts out of source.
        void addAll(Collection<? extends T> source) {
            items.addAll(source);
        }

        // Consumer: we hand Ts to target.
        int drainTo(Collection<? super T> target) {
            int moved = items.size();
            target.addAll(items);
            items.clear();
            return moved;
        }

        // Consumer: the rule is handed Ts.
        int removeIf(Predicate<? super T> rule) {
            int before = items.size();
            items.removeIf(rule);
            return before - items.size();
        }

        // Consumer: the action is handed Ts.
        void forEach(Consumer<? super T> action) {
            for (T item : items) {
                action.accept(item);
            }
        }
    }

    static void rotate(List<?> list) {
        rotateHelper(list);
    }

    // Captures the ? as a named T, so we can put back an element we took out.
    private static <T> void rotateHelper(List<T> list) {
        if (!list.isEmpty()) {
            list.add(list.remove(0));
        }
    }

    public static void main(String[] args) {
        TaskQueue<Task> queue = new TaskQueue<>();
        queue.add(new Task("reply to email"));

        List<UrgentTask> urgent = List.of(new UrgentTask("server down"), new UrgentTask("refund"));
        List<Task> normal = List.of(new Task("spam: win a prize"), new Task("update docs"));

        boolean allPass = true;

        allPass &= check("addAll", () -> {
            queue.addAll(urgent);   // UrgentTasks into a queue of Task
            queue.addAll(normal);
            return queue.snapshot().toString()
                    .equals("[reply to email, !server down, !refund, spam: win a prize, update docs]");
        });

        allPass &= check("removeIf", () -> {
            Predicate<Object> looksLikeSpam = item -> item.toString().startsWith("spam");   // a general rule
            int removed = queue.removeIf(looksLikeSpam);
            return removed == 1 && queue.snapshot().size() == 4;
        });

        allPass &= check("forEach", () -> {
            List<String> log = new ArrayList<>();
            Consumer<Object> logger = item -> log.add("seen " + item);   // a general handler
            queue.forEach(logger);
            return log.equals(List.of("seen reply to email", "seen !server down", "seen !refund", "seen update docs"));
        });

        allPass &= check("drainTo", () -> {
            List<Object> archive = new ArrayList<>(List.of("--- archive ---"));
            int moved = queue.drainTo(archive);
            return moved == 4
                    && archive.toString().equals("[--- archive ---, reply to email, !server down, !refund, update docs]")
                    && queue.snapshot().isEmpty();
        });

        allPass &= check("rotate", () -> {
            List<String> shifts = new ArrayList<>(List.of("night", "morning", "evening"));
            List<Integer> ids = new ArrayList<>(List.of(1, 2, 3));
            rotate(shifts);
            rotate(ids);
            return shifts.equals(List.of("morning", "evening", "night")) && ids.equals(List.of(2, 3, 1));
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok = test.get();
        System.out.printf("%-9s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
