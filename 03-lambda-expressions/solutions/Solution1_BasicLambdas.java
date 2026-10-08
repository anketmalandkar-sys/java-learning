import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Exercise 1 (Easy): write lambdas for the core functional interfaces.
 *
 * TASK
 *   Replace every null below with a lambda or method reference that does what
 *   its comment says. Don't change main.
 *
 * EXPECTED OUTPUT
 *   isPositive:  PASS
 *   toInitials:  PASS
 *   collectInto: PASS
 *   emptyCart:   PASS
 *   capitalise:  PASS
 *   cheaper:     PASS
 *   repeat:      PASS
 *   parse:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - Predicate returns boolean, Function returns a value, Consumer returns nothing,
 *     Supplier takes nothing.
 *   - Initials of "Ada Lovelace": first char of each word. name.charAt(0) and name.indexOf(' ') help.
 *   - At least one of these should be a method reference (e.g. Integer::parseInt).
 *
 * Run: java solutions/Solution1_BasicLambdas.java
 */
public class Solution1_BasicLambdas {

    // true if the number is greater than zero
    static Predicate<Integer> isPositive = i -> i > 0;

    // "Ada Lovelace" -> "AL" (always exactly two words)
    static Function<String, String> toInitials = name -> name.substring(0, 1) + name.charAt(name.indexOf(' ') + 1);

    // add the given text to the 'log' list below
    static List<String> log = new ArrayList<>();
    static Consumer<String> collectInto = log::add;

    // return a new, empty ArrayList each time it's called
    static Supplier<List<String>> emptyCart = ArrayList::new;

    // "java" -> "Java" (first letter upper case, rest unchanged; input is never empty)
    static UnaryOperator<String> capitalise = s -> s.substring(0, 1).toUpperCase() + s.substring(1);

    // return the smaller of two prices
    static BinaryOperator<Double> cheaper = Math::min;

    // repeat the text n times: ("ab", 3) -> "ababab"
    static BiFunction<String, Integer, String> repeat = String::repeat;

    // "42" -> 42 (use a method reference)
    static Function<String, Integer> parse = Integer::parseInt;

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("isPositive", () -> isPositive.test(5) && !isPositive.test(0) && !isPositive.test(-3));
        allPass &= check("toInitials", () -> toInitials.apply("Ada Lovelace").equals("AL")
                && toInitials.apply("Grace Hopper").equals("GH"));
        allPass &= check("collectInto", () -> {
            collectInto.accept("first");
            collectInto.accept("second");
            return log.equals(List.of("first", "second"));
        });
        allPass &= check("emptyCart", () -> {
            List<String> a = emptyCart.get();
            List<String> b = emptyCart.get();
            a.add("apple");
            return a.size() == 1 && b.isEmpty();   // must be two different lists
        });
        allPass &= check("capitalise", () -> capitalise.apply("java").equals("Java")
                && capitalise.apply("x").equals("X"));
        allPass &= check("cheaper", () -> cheaper.apply(99.5, 120.0) == 99.5 && cheaper.apply(10.0, 3.0) == 3.0);
        allPass &= check("repeat", () -> repeat.apply("ab", 3).equals("ababab") && repeat.apply("x", 0).isEmpty());
        allPass &= check("parse", () -> parse.apply("42") == 42);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. A null lambda causes a NullPointerException, which we report as "not done yet".
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-12s FAIL (still null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-12s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
