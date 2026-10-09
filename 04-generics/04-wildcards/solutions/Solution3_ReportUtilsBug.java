import java.util.ArrayList;
import java.util.List;

/**
 * Solution to Exercise 3 (Hard): report helpers that let customer names into the sales figures.
 *
 * THE SITUATION
 *   The end-of-day report adds up sales. It crashes with a ClassCastException inside total().
 *   The code compiles, apart from "unchecked" notes. Three helpers were written in a hurry:
 *
 *     loadDailySales()     returns List<? extends Number>, so main had to cast its result
 *     moveAll(from, to)    takes two List<?>s and uses a raw-type cast to add to 'to'
 *     swapFirstAndLast()   uses a raw-type cast because list.set(...) didn't compile on a List<?>
 *
 * TASK
 *   1. Run it. Find which line puts a String into the List<Double>, and why the compiler didn't stop it.
 *   2. Fix the three helpers so there are NO raw types, NO casts and NO @SuppressWarnings left:
 *        - loadDailySales: wildcards don't belong in return types. Return the concrete type.
 *        - moveAll: give it a type parameter and PECS wildcards so 'from' and 'to' are tied together.
 *        - swapFirstAndLast: keep the List<?> signature and use a capture helper.
 *   3. Fix main's line marked "FIX" so it doesn't need a cast any more.
 *   4. The bad call should now be a compile error. Delete the line marked
 *   5. Check: javac -Xlint:all exercises/Solution3_ReportUtilsBug.java gives no warnings.
 *
 * EXPECTED OUTPUT (after the fix)
 *   sales:   PASS
 *   pending: PASS
 *   total:   PASS
 *   swap:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - moveAll's 'from' produces Ts and its 'to' consumes them.
 *   - Moving Doubles into a List<Number> should still compile after your fix.
 *     Moving Strings into a List<Double> should not.
 *
 * Run: java solutions/Solution3_ReportUtilsBug.java
 */
public class Solution3_ReportUtilsBug {

    // What went wrong: moveAll took two unrelated List<?>s and used a raw-type cast, so the compiler
    // couldn't connect them. moveAll(customers, sales) put Strings into the List<Double>, and the crash
    // only came later, in total(), when a String was unboxed as a Double.

    // Fix 1: no wildcard in the return type. Callers get a List<Double> they can add to, with no cast.
    static List<Double> loadDailySales() {
        return new ArrayList<>(List.of(1200.0, 860.5, 990.0));
    }

    // Fix 2: T ties the two lists together. 'from' produces Ts (extends), 'to' consumes them (super).
    // Doubles into a List<Number> still compiles (T = Double); Strings into a List<Double> doesn't.
    static <T> void moveAll(List<? extends T> from, List<? super T> to) {
        to.addAll(from);
        from.clear();
    }

    // Fix 3: keep the List<?> signature, and let a helper capture ? as T. No raw types needed.
    static void swapFirstAndLast(List<?> list) {
        swapFirstAndLastHelper(list);
    }

    private static <T> void swapFirstAndLastHelper(List<T> list) {
        T first = list.get(0);
        list.set(0, list.get(list.size() - 1));
        list.set(list.size() - 1, first);
    }

    static double total(List<Double> sales) {
        double sum = 0;
        for (Double sale : sales) {
            sum += sale;
        }
        return sum;
    }

    public static void main(String[] args) {
        List<Double> sales = loadDailySales();   // no cast any more
        sales.add(1430.0);

        List<Double> pending = new ArrayList<>(List.of(300.0));
        moveAll(pending, sales);

        List<Number> allFigures = new ArrayList<>();
        moveAll(new ArrayList<>(List.of(5.5)), allFigures);   // Doubles into a List<Number>: must keep working

        boolean allPass = true;
        allPass &= report("sales", sales.equals(List.of(1200.0, 860.5, 990.0, 1430.0, 300.0)));
        allPass &= report("pending", pending.isEmpty() && allFigures.equals(List.of(5.5)));
        allPass &= report("total", total(sales) == 4780.5);

        List<String> shifts = new ArrayList<>(List.of("night", "morning", "evening"));
        swapFirstAndLast(shifts);
        allPass &= report("swap", shifts.equals(List.of("evening", "morning", "night")));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean report(String name, boolean ok) {
        System.out.printf("%-8s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
