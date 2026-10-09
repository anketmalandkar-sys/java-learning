import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 3 (Hard): report helpers that let customer names into the sales figures.
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
 *      "DELETE once it stops compiling".
 *   5. Check: javac -Xlint:all exercises/Exercise3_ReportUtilsBug.java gives no warnings.
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
 * Run: java exercises/Exercise3_ReportUtilsBug.java
 */
public class Exercise3_ReportUtilsBug {

    static List<? extends Number> loadDailySales() {
        return new ArrayList<>(List.of(1200.0, 860.5, 990.0));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void moveAll(List<?> from, List<?> to) {
        ((List) to).addAll(from);
        from.clear();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    static void swapFirstAndLast(List<?> list) {
        List raw = list;
        Object first = raw.get(0);
        raw.set(0, raw.get(raw.size() - 1));
        raw.set(raw.size() - 1, first);
    }

    static double total(List<Double> sales) {
        double sum = 0;
        for (Double sale : sales) {
            sum += sale;
        }
        return sum;
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List<Double> sales = (List<Double>) loadDailySales();   // FIX: no cast needed once loadDailySales is fixed
        sales.add(1430.0);

        List<Double> pending = new ArrayList<>(List.of(300.0));
        moveAll(pending, sales);

        List<Number> allFigures = new ArrayList<>();
        moveAll(new ArrayList<>(List.of(5.5)), allFigures);   // Doubles into a List<Number>: must keep working

        List<String> customers = new ArrayList<>(List.of("Ravi", "Meera"));
        moveAll(customers, sales);   // DELETE once it stops compiling

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
