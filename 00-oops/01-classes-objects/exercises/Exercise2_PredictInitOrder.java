package oops.exercises;

import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 2 (Medium): predict initialization order.
 * Goes with basics/InitializationOrder.java.
 *
 * TASK
 *   Read the Store / CornerStore / Prices classes below WITHOUT running anything, and fill in:
 *
 *   1. FIRST: the events logged by the first `new CornerStore("A")`, in order.
 *   2. SECOND: the events logged by a second `new CornerStore("B")`.
 *   3. READING_CONSTANT_INITIALIZES: does reading Prices.MIN_ORDER log "Prices static"?
 *   4. READING_LIST_INITIALIZES: does reading Prices.COUPONS log "Prices static"?
 *
 *   Then run. Wrong predictions are printed with the real order, so you can see what you missed.
 *
 * EXPECTED OUTPUT
 *   first object:    PASS
 *   second object:   PASS
 *   constant read:   PASS
 *   list read:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - Static parts run once, parent before child.
 *   - For each object: parent fields+blocks, parent constructor, child fields+blocks, child constructor.
 *   - Which kind of static final field is a compile-time constant?
 *
 * Run: press the green run button next to main in IntelliJ, or from the repo root:
 *   javac -d out $(find 00-oops/01-classes-objects -name "*.java") && java -cp out oops.exercises.Exercise2_PredictInitOrder
 */
public class Exercise2_PredictInitOrder {

    // TODO: your predictions
    static final List<String> FIRST = List.of();
    static final List<String> SECOND = List.of();
    static final boolean READING_CONSTANT_INITIALIZES = true;
    static final boolean READING_LIST_INITIALIZES = false;

    static final List<String> LOG = new ArrayList<>();

    static class Store {
        static {
            LOG.add("Store static");
        }

        String city = log("Store field");

        Store() {
            LOG.add("Store()");
        }

        {
            LOG.add("Store block");
        }
    }

    static class CornerStore extends Store {
        {
            LOG.add("CornerStore block");
        }

        static {
            LOG.add("CornerStore static");
        }

        final String id;

        CornerStore(String id) {
            this.id = log("CornerStore(" + id + ")");
        }
    }

    static class Prices {
        static final int MIN_ORDER = 99;
        static final List<String> COUPONS = List.of("SAVE10");

        static {
            LOG.add("Prices static");
        }
    }

    static String log(String event) {
        LOG.add(event);
        return event;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        LOG.clear();
        new CornerStore("A");
        allPass &= compare("first object:", FIRST, LOG);

        LOG.clear();
        new CornerStore("B");
        allPass &= compare("second object:", SECOND, LOG);

        LOG.clear();
        int min = Prices.MIN_ORDER;
        boolean constantInit = LOG.contains("Prices static");
        allPass &= check("constant read:", constantInit == READING_CONSTANT_INITIALIZES);

        LOG.clear();
        List<String> coupons = Prices.COUPONS;
        boolean listInit = LOG.contains("Prices static");
        allPass &= check("list read:", listInit == READING_LIST_INITIALIZES);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
        if (min < 0 || coupons == null) {
            System.out.println("unreachable");
        }
    }

    static boolean compare(String name, List<String> predicted, List<String> actual) {
        boolean ok = predicted.equals(actual);
        check(name, ok);
        if (!ok) {
            System.out.println("    actual: " + actual);
        }
        return ok;
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-16s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
