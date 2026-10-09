import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Solution to Exercise 1 (Easy): one generic interface, implemented three ways.
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
 * Run: java solutions/Solution1_Converters.java
 */
public class Solution1_Converters {

    interface Converter<S, T> {
        T convert(S source);

        // Convert with this converter first, then feed the result to next.
        default <R> Converter<S, R> andThen(Converter<? super T, ? extends R> next) {
            return source -> next.convert(convert(source));
        }
    }

    // Fixes both types: the class isn't generic, and convert takes a String and returns a String.
    static class TrimUpper implements Converter<String, String> {
        @Override
        public String convert(String source) {
            return source.trim().toUpperCase();
        }
    }

    static class ParseQuantity implements Converter<String, Integer> {
        @Override
        public Integer convert(String source) {
            return Integer.parseInt(source.trim());
        }
    }

    // Stays generic: whatever an element converter does to one item, EachOf does to a whole list.
    static class EachOf<S, T> implements Converter<List<S>, List<T>> {
        private final Converter<S, T> elementConverter;

        EachOf(Converter<S, T> elementConverter) {
            this.elementConverter = elementConverter;
        }

        @Override
        public List<T> convert(List<S> source) {
            List<T> converted = new ArrayList<>();
            for (S item : source) {
                converted.add(elementConverter.convert(item));
            }
            return converted;
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("TrimUpper", () -> {
            Converter<String, String> trimUpper = new TrimUpper();
            return trimUpper.convert("  pune ").equals("PUNE") && trimUpper.convert("Goa").equals("GOA");
        });

        allPass &= check("ParseQuantity", () -> {
            Converter<String, Integer> parse = new ParseQuantity();
            return parse.convert(" 12 ") == 12 && parse.convert("0") == 0;
        });

        allPass &= check("EachOf", () -> {
            Converter<List<String>, List<Integer>> parseAll = new EachOf<>(new ParseQuantity());
            Converter<List<String>, List<String>> shoutAll = new EachOf<>(new TrimUpper());
            return parseAll.convert(List.of("1", " 2", "30 ")).equals(List.of(1, 2, 30))
                    && shoutAll.convert(List.of("a ", " b")).equals(List.of("A", "B"))
                    && parseAll.convert(List.of()).isEmpty();
        });

        allPass &= check("andThen", () -> {
            Converter<String, Integer> nameLength = new TrimUpper().andThen(s -> s.length());
            Converter<String, Integer> doubled = new ParseQuantity().andThen(n -> n * 2);
            Converter<String, String> label = new ParseQuantity().andThen(n -> "qty=" + n);
            return nameLength.convert("  pune ") == 4
                    && doubled.convert(" 21") == 42
                    && label.convert("3").equals("qty=3");
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
