import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Solution to Exercise 3 (Hard): a nightly report job that silently loses its numbers.
 *
 * THE SITUATION
 *   JobRunner has two overloads:
 *     submit(Runnable job)        runs the job, records nothing
 *     submit(Callable<T> job)     runs the job and records its result in results()
 *
 *   The nightly job submits four tasks and expects four results:
 *     [orders today: 128, revenue today: 45200.0, refunds today: 3, backup done]
 *
 *   But running it prints only TWO results, and the check fails.
 *   Nothing throws, and the code compiles without warnings.
 *
 * TASK
 *   1. Run it. Work out, for each of the four submit(...) calls in nightlyJob(),
 *      which overload the compiler picks and why.
 *   2. Fix the call sites in nightlyJob() so all four results are recorded.
 *      Change ONLY nightlyJob(). Don't touch JobRunner, the Reports class or main.
 *   3. Bonus: there's a second way to fix a call that doesn't touch the lambda body.
 *      Add a comment in nightlyJob() describing it.
 *
 * EXPECTED OUTPUT (after the fix)
 *   results = [orders today: 128, revenue today: 45200.0, refunds today: 3, backup done]
 *   PASS
 *
 * HINTS
 *   - A lambda whose body is a block { ... } returns a value only if it has a return statement.
 *     Without one it can only be a Runnable.
 *   - A lambda whose body is an expression can fit both, if the expression has a value.
 *     Then Callable wins, because it's more specific.
 *   - Look closely at the last call: does Reports.runBackup() return anything?
 *
 * Run: java solutions/Solution3_TaskRunnerBug.java
 */
public class Solution3_TaskRunnerBug {

    static class JobRunner {
        private final List<Object> results = new ArrayList<>();

        void submit(Runnable job) {
            job.run();
        }

        <T> void submit(Callable<T> job) throws Exception {
            results.add(job.call());
        }

        List<Object> results() {
            return results;
        }
    }

    static class Reports {
        static String countOrders() { return "orders today: 128"; }
        static double revenue() { return 45200.0; }
        static String countRefunds() {  System.out.print(""); return "refunds today: 3"; }
        static void runBackup() { /* copies files; returns nothing */ }
    }

    // Why results went missing: a block lambda { ... } with no return statement returns nothing,
    // so it only fits Runnable, and submit(Runnable) records no result. An expression lambda
    // whose expression has a value fits both overloads, and Callable wins because it's more specific.
    //
    // Bonus, a fix that doesn't touch the lambda: cast it to pick the overload yourself, e.g.
    //     runner.submit((Callable<String>) () -> { ...; return "x"; });
    // A cast can't add a missing return, though. The lambda body must still produce the value.
    static void nightlyJob(JobRunner runner) throws Exception {
        runner.submit(() -> Reports.countOrders());

        runner.submit(() -> "revenue today: " + Reports.revenue());

        runner.submit(() -> {
            System.out.print("");   // pretend this is some logging
            return Reports.countRefunds();   // the missing return: now it's a Callable
        });

        runner.submit(() -> {
            Reports.runBackup();
            return "backup done";
        });
    }

    public static void main(String[] args) throws Exception {
        JobRunner runner = new JobRunner();
        nightlyJob(runner);

        List<Object> expected = List.of(
                "orders today: 128", "revenue today: 45200.0", "refunds today: 3", "backup done");
        System.out.println("results = " + runner.results());
        System.out.println(runner.results().equals(expected) ? "PASS" : "FAIL");
    }
}
