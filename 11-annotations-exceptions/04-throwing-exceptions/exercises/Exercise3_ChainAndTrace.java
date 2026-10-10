import java.util.function.Supplier;

/**
 * Exercise 3 (Hard): chain causes, find the root cause, read the trace, log lazily.
 *
 * TASK
 *   1. loadConfig(text) translates a low-level NumberFormatException into a ConfigException,
 *      but throws away the original. Keep it as the CAUSE (add a constructor if needed).
 *
 *   2. rootCause(t): follow getCause() to the end of the chain and return the last Throwable.
 *      A Throwable without a cause is its own root cause.
 *
 *   3. origin(t): return "<SimpleClassName>.<methodName>" of the FIRST stack frame of the ROOT
 *      cause whose class name starts with "Exercise3_ChainAndTrace" (i.e. the first frame in
 *      our own code, skipping JDK frames like Integer.parseInt).
 *        For the config failure this is "Exercise3_ChainAndTrace.parsePort".
 *
 *   4. warn(...) builds its message even when warnings are switched off, which is wasteful.
 *      Change warn to take a Supplier<String> and only call get() when WARNINGS_ON is true,
 *      then update the call in startServer.
 *
 * EXPECTED OUTPUT
 *   cause kept:     PASS
 *   root cause:     PASS
 *   no cause:       PASS
 *   origin:         PASS
 *   lazy logging:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - new ConfigException("...", e) needs a constructor that passes the cause to super.
 *   - getStackTrace() returns StackTraceElement[]; getClassName() gives the binary name, e.g.
 *     "Exercise3_ChainAndTrace" (nested classes would add "$Inner").
 *
 * Run: java exercises/Exercise3_ChainAndTrace.java
 */
public class Exercise3_ChainAndTrace {

    static class ConfigException extends Exception {
        ConfigException(String message) {
            super(message);
        }
        // TODO: a constructor that also takes the cause
    }

    static int parsePort(String value) {
        return Integer.parseInt(value.trim());
    }

    static int loadConfig(String text) throws ConfigException {
        String value = text.substring(text.indexOf('=') + 1);
        try {
            return parsePort(value);
        } catch (NumberFormatException e) {
            throw new ConfigException("bad port in config: " + text);   // TODO: keep the cause
        }
    }

    static Throwable rootCause(Throwable t) {
        return t; // TODO
    }

    static String origin(Throwable t) {
        return ""; // TODO
    }

    static boolean WARNINGS_ON = false;
    static int MESSAGES_BUILT = 0;

    static String describeEnvironment() {
        MESSAGES_BUILT++;                    // pretend this is expensive
        return "env=prod, host=app-7";
    }

    // TODO: take a Supplier<String> instead
    static void warn(String message) {
        if (WARNINGS_ON) {
            System.out.println("WARN " + message);
        }
    }

    static void startServer() {
        warn("starting with " + describeEnvironment());   // TODO: pass a lambda
    }

    public static void main(String[] args) {
        boolean allPass = true;

        ConfigException failure = null;
        try {
            loadConfig("port=80x");
        } catch (ConfigException e) {
            failure = e;
        }
        allPass &= check("cause kept:", failure != null && failure.getCause() instanceof NumberFormatException);
        allPass &= check("root cause:", failure != null && rootCause(failure) instanceof NumberFormatException
                && rootCause(new RuntimeException("a", new IllegalStateException("b", new ArithmeticException("c"))))
                        .getMessage().equals("c"));
        IllegalStateException alone = new IllegalStateException("alone");
        allPass &= check("no cause:", rootCause(alone) == alone);
        allPass &= check("origin:", failure != null && "Exercise3_ChainAndTrace.parsePort".equals(origin(failure)));

        WARNINGS_ON = false;
        MESSAGES_BUILT = 0;
        startServer();
        boolean lazyOff = MESSAGES_BUILT == 0;
        WARNINGS_ON = true;
        startServer();
        allPass &= check("lazy logging:", lazyOff && MESSAGES_BUILT == 1);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
