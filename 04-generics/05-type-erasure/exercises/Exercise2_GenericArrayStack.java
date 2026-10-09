import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): a generic stack backed by an array.
 *
 * TASK
 *   ArrayStack<T> stores its items in an array. You can't write new T[2] (erasure), so the
 *   field is an Object[]. That's fine as long as the Object[] never leaks out as a T[].
 *   Implement:
 *
 *     push(item)         add on top. When the array is full, grow it to double the size.
 *     pop()              remove and return the top item. Throw IllegalStateException if empty.
 *                        Set the freed slot to null, so the stack doesn't keep the object alive.
 *     peek()             return the top item without removing it. Throw IllegalStateException if empty.
 *     size()             how many items are on the stack
 *     toArray(type)      a NEW, REAL T[] holding the items bottom-to-top, exactly size() long.
 *                        For String.class it must be a String[].
 *
 *   The trap: "return (T[]) elements;" compiles (with an unchecked warning), but it's an
 *   Object[]. The caller's String[] variable then throws ClassCastException. main checks for that.
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   push/peek: PASS
 *   grow:      PASS
 *   pop:       PASS
 *   empty:     PASS
 *   toArray:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - java.util.Arrays.copyOf(elements, newLength) makes a bigger copy.
 *   - Reading from elements gives an Object. Cast it to T: (T) elements[i]. That cast is unchecked
 *     but safe, because push() only ever stores Ts. Put @SuppressWarnings("unchecked") on the
 *     smallest scope that needs it.
 *   - toArray: java.lang.reflect.Array.newInstance(type, size), then copy the items in.
 *
 * Run: java exercises/Exercise2_GenericArrayStack.java
 */
public class Exercise2_GenericArrayStack {

    static class ArrayStack<T> {
        private Object[] elements = new Object[2];   // Object[], because new T[2] isn't allowed
        private int size = 0;

        void push(T item) {
            // TODO
        }

        T pop() {
            // TODO
            return null;
        }

        T peek() {
            // TODO
            return null;
        }

        int size() {
            // TODO
            return -1;
        }

        T[] toArray(Class<T> type) {
            // TODO
            return null;
        }

        int capacity() {   // used by main to check that the array grew
            return elements.length;
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("push/peek", () -> {
            ArrayStack<String> pages = new ArrayStack<>();
            pages.push("home");
            pages.push("cart");
            return pages.peek().equals("cart") && pages.size() == 2;
        });

        allPass &= check("grow", () -> {
            ArrayStack<Integer> ids = new ArrayStack<>();
            for (int i = 1; i <= 5; i++) {
                ids.push(i);
            }
            return ids.size() == 5 && ids.peek() == 5 && ids.capacity() >= 5;
        });

        allPass &= check("pop", () -> {
            ArrayStack<String> undo = new ArrayStack<>();
            undo.push("type A");
            undo.push("type B");
            undo.push("delete");
            String last = undo.pop();
            return last.equals("delete") && undo.size() == 2 && undo.peek().equals("type B");
        });

        allPass &= check("empty", () -> {
            ArrayStack<String> empty = new ArrayStack<>();
            boolean popThrew = false;
            boolean peekThrew = false;
            try { empty.pop(); } catch (IllegalStateException e) { popThrew = true; }
            try { empty.peek(); } catch (IllegalStateException e) { peekThrew = true; }
            return popThrew && peekThrew && empty.size() == 0;
        });

        allPass &= check("toArray", () -> {
            ArrayStack<String> history = new ArrayStack<>();
            history.push("home");
            history.push("search");
            history.push("product");
            String[] pages = history.toArray(String.class);   // ClassCastException if it's an Object[]
            return pages.getClass() == String[].class
                    && pages.length == 3
                    && String.join(" > ", pages).equals("home > search > product");
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. Unfinished TODOs return null, and a fake T[] fails with ClassCastException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-10s FAIL (still returning null?)%n", name + ":");
            return false;
        } catch (ClassCastException e) {
            System.out.printf("%-10s FAIL (ClassCastException: is that array really a T[]?)%n", name + ":");
            return false;
        }
        System.out.printf("%-10s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
