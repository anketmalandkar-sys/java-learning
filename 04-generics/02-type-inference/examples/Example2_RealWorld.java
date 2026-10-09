import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Example 2: target typing in a small order-processing app.
 *   1. The same lambda text becoming different types, depending on the target.
 *   2. Lambdas in a return, a ?: and an array initializer.
 *   3. The comparator-chain trap, and three ways out.
 *   4. Overloads: which lambdas become a Runnable and which a Callable.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    record Order(String id, String customer, double total) {}

    // The return statement is the target: the lambda becomes a Predicate<Order>.
    static Predicate<Order> biggerThan(double limit) {
        return order -> order.total() > limit;
    }

    // Two overloads. Which one a lambda picks depends on whether it returns a value.
    static class TaskRunner {
        void submit(Runnable task) {
            task.run();
            System.out.println("  ran a Runnable (no result)");
        }

        <T> void submit(Callable<T> task) throws Exception {
            T result = task.call();
            System.out.println("  ran a Callable, result = " + result);
        }
    }

    public static void main(String[] args) throws Exception {
        List<Order> orders = List.of(
                new Order("A1", "Ravi", 250.0),
                new Order("A2", "Meera", 80.0),
                new Order("A3", "Ravi", 1200.0),
                new Order("A4", "Arjun", 80.0));

        System.out.println("--- 1. Same text, different target types ---");
        Predicate<Order> isSmall = order -> order.total() < 100;            // a Predicate
        Function<Order, Boolean> isSmallFn = order -> order.total() < 100;    // a Function
        System.out.println("A2 small? " + isSmall.test(orders.get(1)) + " / " + isSmallFn.apply(orders.get(1)));

        System.out.println();
        System.out.println("--- 2. Return, ?: and array initializer as targets ---");
        System.out.println("Over 200: " + orders.stream().filter(biggerThan(200)).map(Order::id).toList());

        boolean newestFirst = true;
        Comparator<Order> byId = newestFirst
                ? (a, b) -> b.id().compareTo(a.id())    // both branches get the target Comparator<Order>
                : (a, b) -> a.id().compareTo(b.id());
        System.out.println("By id, newest first: " + orders.stream().sorted(byId).map(Order::id).toList());

        List<String> log = new ArrayList<>();
        Runnable[] closingSteps = {                      // each element's target is Runnable
                () -> log.add("lock tills"),
                () -> log.add("print report")
        };
        for (Runnable step : closingSteps) {
            step.run();
        }
        System.out.println("Closing steps: " + log);

        System.out.println();
        System.out.println("--- 3. Comparator chains ---");
        // Comparator<Order> broken = Comparator.comparing(o -> o.total()).reversed();
        //   won't compile: comparing(...) is the receiver of .reversed(), so it has no target and o is Object.
        Comparator<Order> fix1 = Comparator.comparing(Order::total).reversed();               // method ref names the type
        Comparator<Order> fix2 = Comparator.comparing((Order o) -> o.total()).reversed();     // explicitly typed lambda
        Comparator<Order> fix3 = Comparator.<Order, Double>comparing(o -> o.total()).reversed();   // type witness
        System.out.println("fix1: " + orders.stream().sorted(fix1).map(Order::id).toList());
        System.out.println("fix2: " + orders.stream().sorted(fix2).map(Order::id).toList());
        System.out.println("fix3: " + orders.stream().sorted(fix3).map(Order::id).toList());

        // thenComparing is also a chain: Order::customer keeps the type known.
        Comparator<Order> byTotalThenCustomer = Comparator.comparing(Order::total)
                .thenComparing(Order::customer);
        System.out.println("By total, then customer: "
                + orders.stream().sorted(byTotalThenCustomer).map(Order::id).toList());

        System.out.println();
        System.out.println("--- 4. Runnable or Callable? ---");
        TaskRunner runner = new TaskRunner();
        double[] runningTotal = {0};

        System.out.println("() -> \"report ready\"");
        runner.submit(() -> "report ready");                            // returns a value: Callable

        System.out.println("() -> { System.out.print(\"\"); }");
        runner.submit(() -> { System.out.print(""); });                 // block with no return: Runnable

        System.out.println("() -> runningTotal[0] += 250");
        runner.submit(() -> runningTotal[0] += 250);                    // an expression that has a value: both fit, Callable wins

        System.out.println("() -> { runningTotal[0] += 250; }");
        runner.submit(() -> { runningTotal[0] += 250; });               // same work in a block, no return: Runnable

        System.out.println("(Runnable) () -> runningTotal[0] += 250");
        runner.submit((Runnable) () -> runningTotal[0] += 250);         // a cast forces the choice
    }
}

/* Expected output:
--- 1. Same text, different target types ---
A2 small? true / true

--- 2. Return, ?: and array initializer as targets ---
Over 200: [A1, A3]
By id, newest first: [A4, A3, A2, A1]
Closing steps: [lock tills, print report]

--- 3. Comparator chains ---
fix1: [A3, A1, A2, A4]
fix2: [A3, A1, A2, A4]
fix3: [A3, A1, A2, A4]
By total, then customer: [A4, A2, A1, A3]

--- 4. Runnable or Callable? ---
() -> "report ready"
  ran a Callable, result = report ready
() -> { System.out.print(""); }
  ran a Runnable (no result)
() -> runningTotal[0] += 250
  ran a Callable, result = 250.0
() -> { runningTotal[0] += 250; }
  ran a Runnable (no result)
(Runnable) () -> runningTotal[0] += 250
  ran a Runnable (no result)
*/
