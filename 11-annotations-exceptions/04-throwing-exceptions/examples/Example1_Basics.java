import java.util.Objects;

/**
 * Example 1: throw and throws, standard exceptions, chaining, and reading a stack trace.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static class Account {
        private final String owner;
        private int balance;
        private boolean closed;

        Account(String owner, int balance) {
            this.owner = Objects.requireNonNull(owner, "owner");          // NullPointerException with a message
            if (balance < 0) {
                throw new IllegalArgumentException("balance must not be negative: " + balance);
            }
            this.balance = balance;
        }

        void withdraw(int amount) {
            if (closed) {
                throw new IllegalStateException("account of " + owner + " is closed");
            }
            if (amount <= 0) {
                throw new IllegalArgumentException("amount must be positive: " + amount);
            }
            balance -= amount;
        }

        void close() {
            closed = true;
        }
    }

    // A checked exception: declared with throws.
    static class ReportException extends Exception {
        ReportException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static int parsePercent(String text) {
        return Integer.parseInt(text.replace("%", ""));
    }

    static int discountFor(String text) throws ReportException {
        try {
            return parsePercent(text);
        } catch (NumberFormatException e) {
            throw new ReportException("bad discount in report: " + text, e);   // chained: keeps the cause
        }
    }

    public static void main(String[] args) {
        System.out.println("--- Standard exceptions, thrown early with clear messages ---");
        tryIt(() -> new Account(null, 10));
        tryIt(() -> new Account("asha", -5));
        Account acc = new Account("asha", 100);
        tryIt(() -> acc.withdraw(0));
        acc.close();
        tryIt(() -> acc.withdraw(10));

        System.out.println("--- Chained exception ---");
        try {
            discountFor("ten%");
        } catch (ReportException e) {
            System.out.println("  " + e.getMessage());
            System.out.println("  caused by " + e.getCause().getClass().getSimpleName() + ": " + e.getCause().getMessage());

            // The stack trace as data: the first frame is where the cause was created.
            StackTraceElement top = e.getCause().getStackTrace()[0];
            System.out.println("  cause created in " + top.getClassName().replaceAll(".*\\.", "") + "." + top.getMethodName());
            boolean fromParsePercent = false;
            for (StackTraceElement frame : e.getCause().getStackTrace()) {
                if (frame.getMethodName().equals("parsePercent")) {
                    fromParsePercent = true;
                }
            }
            System.out.println("  passed through parsePercent? " + fromParsePercent);
        }
    }

    static void tryIt(Runnable action) {
        try {
            action.run();
            System.out.println("  ok");
        } catch (RuntimeException e) {
            System.out.println("  " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}

/* Expected output:
--- Standard exceptions, thrown early with clear messages ---
  NullPointerException: owner
  IllegalArgumentException: balance must not be negative: -5
  IllegalArgumentException: amount must be positive: 0
  IllegalStateException: account of asha is closed
--- Chained exception ---
  bad discount in report: ten%
  caused by NumberFormatException: For input string: "ten"
  cause created in NumberFormatException.forInputString
  passed through parsePercent? true
*/
