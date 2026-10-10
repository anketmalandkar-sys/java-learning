import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOError;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Exercise 1 (Easy): checked, runtime exception, or error?
 *
 * TASK
 *   1. kind(type) returns "checked", "runtime" or "error" for any Throwable class:
 *        - Error or a subclass                         -> "error"
 *        - RuntimeException or a subclass              -> "runtime"
 *        - everything else (incl. Exception, Throwable) -> "checked"
 *      Use Class.isAssignableFrom (or walk getSuperclass()), not a hard-coded list of names.
 *
 *   2. mustCatchOrSpecify(type): true only for checked exceptions.
 *
 *   3. handledBy(thrown, handler): true if a `catch (handler e)` block would catch an exception
 *      of type `thrown`, i.e. the handler type is the thrown type or one of its superclasses.
 *        handledBy(FileNotFoundException.class, IOException.class) -> true
 *        handledBy(IOException.class, FileNotFoundException.class) -> false
 *
 * EXPECTED OUTPUT
 *   kind:        PASS
 *   catchOrSpec: PASS
 *   handledBy:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - A.class.isAssignableFrom(B.class) is true when B is A or a subclass of A.
 *   - Throwable itself counts as checked: `throw new Throwable()` must be caught or declared.
 *
 * Run: java exercises/Exercise1_ClassifyExceptions.java
 */
public class Exercise1_ClassifyExceptions {

    static String kind(Class<? extends Throwable> type) {
        return ""; // TODO
    }

    static boolean mustCatchOrSpecify(Class<? extends Throwable> type) {
        return false; // TODO
    }

    static boolean handledBy(Class<? extends Throwable> thrown, Class<? extends Throwable> handler) {
        return false; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("kind:", "checked".equals(kind(IOException.class))
                && "checked".equals(kind(FileNotFoundException.class))
                && "checked".equals(kind(InterruptedException.class))
                && "checked".equals(kind(Exception.class))
                && "checked".equals(kind(Throwable.class))
                && "runtime".equals(kind(NumberFormatException.class))
                && "runtime".equals(kind(UncheckedIOException.class))
                && "error".equals(kind(StackOverflowError.class))
                && "error".equals(kind(IOError.class)));

        List<Class<? extends Throwable>> mustHandle = List.of(IOException.class, EOFException.class, Exception.class);
        List<Class<? extends Throwable>> free = List.of(IllegalStateException.class, OutOfMemoryError.class, RuntimeException.class);
        boolean ok = true;
        for (Class<? extends Throwable> t : mustHandle) {
            ok &= mustCatchOrSpecify(t);
        }
        for (Class<? extends Throwable> t : free) {
            ok &= !mustCatchOrSpecify(t);
        }
        allPass &= check("catchOrSpec:", ok);

        allPass &= check("handledBy:", handledBy(FileNotFoundException.class, IOException.class)
                && !handledBy(IOException.class, FileNotFoundException.class)
                && handledBy(NumberFormatException.class, IllegalArgumentException.class)
                && handledBy(NullPointerException.class, Exception.class)
                && !handledBy(StackOverflowError.class, Exception.class)
                && handledBy(EOFException.class, EOFException.class));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
