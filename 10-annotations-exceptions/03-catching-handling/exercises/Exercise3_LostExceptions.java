import java.io.IOException;

/**
 * Exercise 3 (Hard): stop losing the real error.
 *
 * TASK
 *   1. upload(data) writes to a Channel and closes it in a finally block. When the write fails
 *      ("network down") AND close() fails too ("close failed"), the caller only sees
 *      "close failed": the real problem is lost.
 *      Rewrite upload with try-with-resources so that:
 *        - the caller receives the "network down" exception
 *        - "close failed" is available from e.getSuppressed()
 *        - when only close() fails, the caller receives "close failed"
 *
 *   2. tryCharge(amount) has a `return` inside finally. It always returns "done", even when
 *      the charge throws, so a failed payment looks successful. Remove the return from finally,
 *      so that:
 *        - a successful charge returns "charged <amount>"
 *        - a failing charge throws its IllegalStateException to the caller
 *        - ATTEMPTS still counts every call (keep the finally for that)
 *
 * EXPECTED OUTPUT
 *   both fail:        PASS
 *   suppressed:       PASS
 *   only close fails: PASS
 *   all good:         PASS
 *   charge ok:        PASS
 *   charge fails:     PASS
 *   attempts:         PASS
 *   ALL PASS
 *
 * HINTS
 *   - try (Channel ch = new Channel(...)) { ch.write(data); }
 *   - upload keeps "throws IOException": the caller decides what to do.
 *
 * Run: java exercises/Exercise3_LostExceptions.java
 */
public class Exercise3_LostExceptions {

    static class Channel implements AutoCloseable {
        private final boolean failWrite;
        private final boolean failClose;
        boolean wrote = false;

        Channel(boolean failWrite, boolean failClose) {
            this.failWrite = failWrite;
            this.failClose = failClose;
        }

        void write(String data) throws IOException {
            if (failWrite) {
                throw new IOException("network down");
            }
            wrote = true;
        }

        @Override
        public void close() throws IOException {
            if (failClose) {
                throw new IOException("close failed");
            }
        }
    }

    // TODO 1
    static void upload(String data, boolean failWrite, boolean failClose) throws IOException {
        Channel ch = new Channel(failWrite, failClose);
        try {
            ch.write(data);
        } finally {
            ch.close();
        }
    }

    static int ATTEMPTS = 0;

    static void charge(int amount) {
        if (amount > 1000) {
            throw new IllegalStateException("limit exceeded");
        }
    }

    // TODO 2
    @SuppressWarnings("finally")
    static String tryCharge(int amount) {
        try {
            charge(amount);
            return "charged " + amount;
        } finally {
            ATTEMPTS++;
            return "done";
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        IOException both = catchIo(() -> upload("x", true, true));
        allPass &= check("both fail:", both != null && "network down".equals(both.getMessage()));
        allPass &= check("suppressed:", both != null && both.getSuppressed().length == 1
                && "close failed".equals(both.getSuppressed()[0].getMessage()));

        IOException onlyClose = catchIo(() -> upload("x", false, true));
        allPass &= check("only close fails:", onlyClose != null && "close failed".equals(onlyClose.getMessage()));

        allPass &= check("all good:", catchIo(() -> upload("x", false, false)) == null);

        ATTEMPTS = 0;
        allPass &= check("charge ok:", "charged 500".equals(tryCharge(500)));
        boolean threw;
        try {
            tryCharge(5000);
            threw = false;
        } catch (IllegalStateException e) {
            threw = "limit exceeded".equals(e.getMessage());
        }
        allPass &= check("charge fails:", threw);
        allPass &= check("attempts:", ATTEMPTS == 2);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    interface IoAction {
        void run() throws IOException;
    }

    static IOException catchIo(IoAction action) {
        try {
            action.run();
            return null;
        } catch (IOException e) {
            return e;
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-17s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
