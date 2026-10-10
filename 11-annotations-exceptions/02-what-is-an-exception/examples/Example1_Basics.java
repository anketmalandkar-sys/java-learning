import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Example 1: how an exception travels up the call stack, the three kinds of exceptions,
 * and catch-or-specify.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // Specifies (declares) the checked exception: its callers must deal with it.
    static void loadStock(String path) throws IOException {
        System.out.println("    loadStock: opening " + path);
        new FileReader(path).close();          // throws FileNotFoundException for a missing file
        System.out.println("    loadStock: done");   // skipped when the line above throws
    }

    // No handler here either: it just passes the exception on (also declares it).
    static void processOrder(String path) throws IOException {
        System.out.println("  processOrder: start");
        loadStock(path);
        System.out.println("  processOrder: end");   // skipped too
    }

    static String kind(Class<? extends Throwable> type) {
        if (Error.class.isAssignableFrom(type)) {
            return "error (unchecked)";
        }
        if (RuntimeException.class.isAssignableFrom(type)) {
            return "runtime exception (unchecked)";
        }
        return "checked exception";
    }

    public static void main(String[] args) {
        System.out.println("--- The call stack is searched backwards for a handler ---");
        try {
            processOrder("no-such-stock-file.csv");
        } catch (IOException e) {                       // matches FileNotFoundException (a subclass)
            System.out.println("main: handled " + e.getClass().getSimpleName());
        }

        System.out.println("--- The three kinds ---");
        for (Class<? extends Throwable> t : java.util.List.of(
                FileNotFoundException.class, IOException.class, NullPointerException.class,
                NumberFormatException.class, StackOverflowError.class)) {
            System.out.println("  " + t.getSimpleName() + ": " + kind(t));
        }

        System.out.println("--- Unchecked: no throws needed, but the program still has to cope ---");
        String[] quantities = {"3", "abc", "7"};
        int total = 0;
        for (String q : quantities) {
            try {
                total += Integer.parseInt(q);           // NumberFormatException is unchecked
            } catch (NumberFormatException e) {
                System.out.println("  skipping bad quantity \"" + q + "\"");
            }
        }
        System.out.println("  total = " + total);

        System.out.println("--- A runtime exception usually means: fix the bug ---");
        String customer = null;
        // customer.length() would throw NullPointerException: check instead of catching.
        System.out.println("  name length: " + (customer == null ? "unknown" : customer.length()));
    }
}

/* Expected output:
--- The call stack is searched backwards for a handler ---
  processOrder: start
    loadStock: opening no-such-stock-file.csv
main: handled FileNotFoundException
--- The three kinds ---
  FileNotFoundException: checked exception
  IOException: checked exception
  NullPointerException: runtime exception (unchecked)
  NumberFormatException: runtime exception (unchecked)
  StackOverflowError: error (unchecked)
--- Unchecked: no throws needed, but the program still has to cope ---
  skipping bad quantity "abc"
  total = 10
--- A runtime exception usually means: fix the bug ---
  name length: unknown
*/
