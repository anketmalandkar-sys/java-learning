import java.util.*;
import java.util.function.Function;

/**
 * Solution to Exercise 2 (Medium): build a generic undo history, like the one in a text editor.
 *
 * TASK
 *   History<T> remembers every state you record, oldest first. Implement:
 *     record(state)   add a new state at the end
 *     current()       the latest state, or Optional.empty() if nothing is recorded
 *     undo()          remove the latest state and return the new current one.
 *                     If there's nothing to go back to (0 or 1 states), change nothing
 *                     and return Optional.empty().
 *     size()          how many states are stored
 *     map(converter)  a NEW History<R> holding converter applied to each state, same order.
 *                     The original history must not change.
 *
 *   map is a generic method inside a generic class: the class has T, and the method
 *   adds its own type parameter R. The signature is already written for you.
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   Editor:   History[Hello, Hello wor, Hello world]
 *   Undo ->   Optional[Hello wor]
 *   Lengths:  History[5, 9]
 *   Counter:  History[0, 1, 2]
 *   Undo x3:  Optional[1], Optional[0], Optional.empty
 *   PASS
 *
 * HINTS
 *   - Store the states in a List<T>.
 *   - current(): Optional.of(...) or Optional.empty().
 *   - undo(): check size() first. Remove the last item, then return current().
 *   - map(): create new History<R>(), then call record(converter.apply(state)) for each state.
 *
 * Run: java solutions/Solution2_UndoHistory.java
 */
public class Solution2_UndoHistory {

    static class History<T> {
        // private: callers must go through record/undo, so they can't clear or reorder the states.
        private final List<T> states = new ArrayList<>();

        void record(T state) {
            states.add(state);
        }

        Optional<T> current() {
            if (states.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(states.get(states.size() - 1));   // getLast() also works on Java 21+
        }

        Optional<T> undo() {
            if (states.size() <= 1) {
                return Optional.empty();   // nothing to go back to: leave the history alone
            }
            states.remove(states.size() - 1);
            return current();              // reuse current() instead of repeating its logic
        }

        int size() {
            return states.size();
        }

        // Function<T, R> is enough here. Lesson 04 shows why the JDK writes
        // Function<? super T, ? extends R>: it also accepts more general functions.
        <R> History<R> map(Function<T, R> converter) {
            History<R> mapped = new History<>();
            for (T state : states) {       // oldest to newest, so the order is kept
                mapped.record(converter.apply(state));
            }
            return mapped;                 // a new History: this one is unchanged
        }

        @Override
        public String toString() {
            return "History" + states;
        }
    }

    public static void main(String[] args) {
        History<String> editor = new History<>();
        editor.record("Hello");
        editor.record("Hello wor");
        editor.record("Hello world");
        String editorBefore = editor.toString();
        System.out.println("Editor:   " + editorBefore);

        Optional<String> afterUndo = editor.undo();
        System.out.println("Undo ->   " + afterUndo);

        History<Integer> lengths = editor.map(String::length);
        System.out.println("Lengths:  " + lengths);

        History<Integer> counter = new History<>();
        for (int i = 0; i < 3; i++) {
            counter.record(i);
        }
        System.out.println("Counter:  " + counter);

        Optional<Integer> u1 = counter.undo();
        Optional<Integer> u2 = counter.undo();
        Optional<Integer> u3 = counter.undo();   // only one state left: nothing to go back to
        System.out.println("Undo x3:  " + u1 + ", " + u2 + ", " + u3);

        boolean pass = editorBefore.equals("History[Hello, Hello wor, Hello world]")
                && afterUndo.equals(Optional.of("Hello wor"))
                && editor.size() == 2
                && editor.current().equals(Optional.of("Hello wor"))
                && lengths.toString().equals("History[5, 9]")
                && editor.size() == 2                       // map must not change the original
                && u1.equals(Optional.of(1))
                && u2.equals(Optional.of(0))
                && u3.isEmpty()
                && counter.size() == 1
                && new History<String>().current().isEmpty();
        System.out.println(pass ? "PASS" : "FAIL");
    }
}
