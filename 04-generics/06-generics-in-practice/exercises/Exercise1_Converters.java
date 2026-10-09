import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 1 (Easy): one generic interface, implemented three ways.
 *
 * TASK
 *   Converter<S, T> turns an S into a T. Implement:
 *
 *     TrimUpper       implements Converter<String, String>      (fixes both types)
 *                     "  pune " -> "PUNE"
 *     ParseQuantity   implements Converter<String, Integer>     (fixes both types)
 *                     " 12 " -> 12   (trim first; you can assume the text is a valid number)
 *     EachOf<S, T>    implements Converter<List<S>, List<T>>    (stays generic)
 *                     applies an element converter to every item of a list, in order
 *
 *   Then implement the default method andThen(next) in the interface. It returns a NEW
 *   Converter<S, R> that converts with this one first, then with next:
 *       trimUpper.andThen(s -> s.length())   turns "  pune " into 4
 *
 *   The checks in main are commented out, because they can't compile until your classes
 *   implement Converter. When a class is ready, uncomment its block and delete the
 *   "return false;" under it. Don't change anything else in main.
 *
 * EXPECTED OUTPUT
 *   TrimUpper:     PASS
 *   ParseQuantity: PASS
 *   EachOf:        PASS
 *   andThen:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - When a class implements Converter<String, Integer>, its method is
 *     public Integer convert(String source). The types are fixed, so there's no <...> on the class.
 *   - EachOf needs a field holding the element converter: Converter<S, T>.
 *   - andThen can return a lambda: source -> next.convert(convert(source)).
 *     Why is next a Converter<? super T, ? extends R>? (PECS: next consumes Ts and produces Rs.)
 *
 * Run: java exercises/Exercise1_Converters.java
 */
public class Exercise1_Converters {

    interface Converter<S, T> {
        T convert(S source);

        default <R> Converter<S, R> andThen(Converter<? super T, ? extends R> next) {
            // TODO
            return null;
        }
    }

    // TODO: make TrimUpper implement Converter<String, String>
    static class TrimUpper {
    }

    // TODO: make ParseQuantity implement Converter<String, Integer>
    static class ParseQuantity {
    }

    // TODO: make EachOf<S, T> implement Converter<List<S>, List<T>>
    static class EachOf<S, T> {
        // TODO: a field for the element converter, and a constructor that takes it
    }

    public static void main(String[] args) {
        boolean allPass = true;

        // These lines are commented out because they won't compile until the classes implement
        // Converter. Uncomment each block when its class is ready.

        allPass &= check("TrimUpper", () -> {
            // Converter<String, String> trimUpper = new TrimUpper();
            // return trimUpper.convert("  pune ").equals("PUNE") && trimUpper.convert("Goa").equals("GOA");
            return false;
        });

        allPass &= check("ParseQuantity", () -> {
            // Converter<String, Integer> parse = new ParseQuantity();
            // return parse.convert(" 12 ") == 12 && parse.convert("0") == 0;
            return false;
        });

        allPass &= check("EachOf", () -> {
            // Converter<List<String>, List<Integer>> parseAll = new EachOf<>(new ParseQuantity());
            // Converter<List<String>, List<String>> shoutAll = new EachOf<>(new TrimUpper());
            // return parseAll.convert(List.of("1", " 2", "30 ")).equals(List.of(1, 2, 30))
            //         && shoutAll.convert(List.of("a ", " b")).equals(List.of("A", "B"))
            //         && parseAll.convert(List.of()).isEmpty();
            return false;
        });

        allPass &= check("andThen", () -> {
            // Converter<String, Integer> nameLength = new TrimUpper().andThen(s -> s.length());
            // Converter<String, Integer> doubled = new ParseQuantity().andThen(n -> n * 2);
            // Converter<String, String> label = new ParseQuantity().andThen(n -> "qty=" + n);
            // return nameLength.convert("  pune ") == 4
            //         && doubled.convert(" 21") == 42
            //         && label.convert("3").equals("qty=3");
            return false;
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-14s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-14s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
