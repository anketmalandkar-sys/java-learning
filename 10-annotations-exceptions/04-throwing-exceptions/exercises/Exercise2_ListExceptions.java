import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Exercise 2 (Medium): a family of custom exceptions (the dev.java linked-list example).
 *
 * TASK
 *   SimpleList returns null or -1 when something is wrong. Replace that with exceptions:
 *
 *   1. Create a CHECKED base class ListException (with a (String message) constructor), and
 *      three subclasses of it:
 *        InvalidIndexException     message "no element at <n> (size <size>)"
 *        EmptyListException        message "list is empty"
 *        ObjectNotFoundException   message "<object> not found"
 *
 *   2. Change the methods to throw them, and declare `throws` with the most SPECIFIC type:
 *        objectAt(n)      when n < 0 or n >= size      -> InvalidIndexException
 *        firstObject()    when the list is empty       -> EmptyListException
 *        indexOf(o)       when o isn't in the list     -> ObjectNotFoundException
 *
 *   3. Write describe(list, n): return "item: <objectAt(n)>", or, if ANY ListException is
 *      thrown, "problem: <message>". Use ONE catch block for the whole family.
 *
 * EXPECTED OUTPUT
 *   hierarchy:  PASS
 *   declared:   PASS
 *   objectAt:   PASS
 *   first:      PASS
 *   indexOf:    PASS
 *   describe:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - class InvalidIndexException extends ListException { InvalidIndexException(int n, int size) {...} }
 *   - The checks find your classes by name with reflection, so spell them exactly as above.
 *
 * Run: java exercises/Exercise2_ListExceptions.java
 */
public class Exercise2_ListExceptions {

    // TODO: ListException and its three subclasses

    static class SimpleList {
        private final List<Object> items = new ArrayList<>();

        void add(Object o) {
            items.add(o);
        }

        Object objectAt(int n) {
            return (n < 0 || n >= items.size()) ? null : items.get(n);   // TODO
        }

        Object firstObject() {
            return items.isEmpty() ? null : items.get(0);                  // TODO
        }

        int indexOf(Object o) {
            return items.indexOf(o);                                       // TODO: -1 means "not found"
        }
    }

    static String describe(SimpleList list, int n) {
        return "item: " + list.objectAt(n);                                // TODO
    }

    public static void main(String[] args) throws Exception {
        boolean allPass = true;

        Class<?> base = find("ListException");
        Class<?> invalid = find("InvalidIndexException");
        Class<?> empty = find("EmptyListException");
        Class<?> notFound = find("ObjectNotFoundException");
        boolean hierarchy = base != null && invalid != null && empty != null && notFound != null
                && Exception.class.isAssignableFrom(base) && !RuntimeException.class.isAssignableFrom(base)
                && base.isAssignableFrom(invalid) && base.isAssignableFrom(empty) && base.isAssignableFrom(notFound);
        allPass &= check("hierarchy:", hierarchy);

        allPass &= check("declared:", hierarchy
                && declares("objectAt", invalid, int.class)
                && declares("firstObject", empty)
                && declares("indexOf", notFound, Object.class));

        SimpleList list = new SimpleList();
        Method objectAt = SimpleList.class.getDeclaredMethod("objectAt", int.class);
        Method firstObject = SimpleList.class.getDeclaredMethod("firstObject");
        Method indexOf = SimpleList.class.getDeclaredMethod("indexOf", Object.class);

        allPass &= check("first:", thrown(firstObject, list) instanceof Exception e
                && e.getClass() == empty && "list is empty".equals(e.getMessage()));

        list.add("pen");
        list.add("ink");
        allPass &= check("objectAt:", "ink".equals(objectAt.invoke(list, 1))
                && thrown(objectAt, list, 2) instanceof Exception e && e.getClass() == invalid
                && "no element at 2 (size 2)".equals(e.getMessage()));
        allPass &= check("indexOf:", Integer.valueOf(1).equals(indexOf.invoke(list, "ink"))
                && thrown(indexOf, list, "glue") instanceof Exception e && e.getClass() == notFound
                && "glue not found".equals(e.getMessage()));

        allPass &= check("describe:", "item: pen".equals(describe(list, 0))
                && "problem: no element at 7 (size 2)".equals(describe(list, 7)));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static Class<?> find(String name) {
        for (Class<?> c : Exercise2_ListExceptions.class.getDeclaredClasses()) {
            if (c.getSimpleName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    static boolean declares(String method, Class<?> exception, Class<?>... params) throws Exception {
        return Arrays.asList(SimpleList.class.getDeclaredMethod(method, params).getExceptionTypes()).equals(List.of(exception));
    }

    // Invokes the method and returns what it threw (or null).
    static Throwable thrown(Method m, Object target, Object... args) {
        try {
            m.invoke(target, args);
            return null;
        } catch (java.lang.reflect.InvocationTargetException e) {
            return e.getCause();
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-11s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
