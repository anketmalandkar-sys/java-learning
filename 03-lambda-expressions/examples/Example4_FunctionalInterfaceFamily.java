import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Example 4: the primitive functional interfaces, collection methods that take lambdas,
 * and the extra combinators (Predicate.not / isEqual, Consumer.andThen, Function.identity).
 *
 * Run: java examples/Example4_FunctionalInterfaceFamily.java
 */
public class Example4_FunctionalInterfaceFamily {

    public static void main(String[] args) {
        System.out.println("--- Primitive versions: no boxing ---");
        int[] counter = {0};                                   // only to give the supplier something to count
        IntSupplier nextTicket = () -> ++counter[0];           // getAsInt()
        IntUnaryOperator twice = n -> n * 2;                   // int -> int, applyAsInt
        IntFunction<String> label = n -> "#" + n;              // int -> object, apply
        ToIntFunction<String> length = String::length;         // object -> int, applyAsInt
        IntToLongFunction cubed = n -> (long) n * n * n;       // int -> long, applyAsLong
        ToIntBiFunction<String, String> totalLength = (a, b) -> a.length() + b.length();
        System.out.println("tickets " + nextTicket.getAsInt() + ", " + nextTicket.getAsInt()
                + " | twice(21) = " + twice.applyAsInt(21)
                + " | label(7) = " + label.apply(7)
                + " | length(\"java\") = " + length.applyAsInt("java")
                + " | cubed(1000) = " + cubed.applyAsLong(1000)
                + " | totalLength = " + totalLength.applyAsInt("ab", "cde"));

        StringBuilder report = new StringBuilder();
        ObjIntConsumer<String> addLine = (item, qty) -> report.append(item).append(" x").append(qty).append("; ");
        addLine.accept("pen", 3);
        addLine.accept("ink", 1);
        BiPredicate<String, Integer> longerThan = (s, n) -> s.length() > n;   // no IntBiPredicate exists
        System.out.println("report: " + report + "| longerThan(\"lambda\", 4) = " + longerThan.test("lambda", 4));

        System.out.println("--- forEach / removeIf / replaceAll ---");
        List<String> tags = new ArrayList<>(List.of(" java ", "", " streams", "  "));
        tags.removeIf(String::isBlank);          // Predicate
        tags.replaceAll(String::trim);           // UnaryOperator<String>: the element type can't change
        StringBuilder shown = new StringBuilder();
        tags.forEach(t -> shown.append("[").append(t).append("]"));   // Consumer
        System.out.println(shown);

        List<String> fixed = Arrays.asList("a", "bb", "c");
        fixed.replaceAll(String::toUpperCase);   // fine: Arrays.asList allows set()
        System.out.println("Arrays.asList after replaceAll: " + fixed);
        System.out.println("Arrays.asList removeIf (no match): " + tryRemove(fixed, s -> s.length() > 5));
        System.out.println("Arrays.asList removeIf (match):    " + tryRemove(fixed, s -> s.length() > 1));
        System.out.println("List.of removeIf (no match):       " + tryRemove(List.of("a"), s -> false));

        System.out.println("--- More combinators ---");
        List<String> words = List.of("Duke", " ", "duke", "Java", "");
        System.out.println("Predicate.not(String::isBlank): " + filter(words, Predicate.not(String::isBlank)));
        System.out.println("Predicate.isEqual(\"Duke\"):      " + filter(words, Predicate.isEqual("Duke")));

        StringBuilder audit = new StringBuilder();
        Consumer<String> log = msg -> audit.append("LOG:").append(msg).append(' ');
        Consumer<String> shout = msg -> audit.append(msg.toUpperCase()).append(' ');
        log.andThen(shout).accept("saved");      // log first, then shout, with the same input
        System.out.println("Consumer.andThen: " + audit.toString().trim());

        // Function.identity(): "the element itself", e.g. as the key in toMap.
        Map<String, Integer> lengths = List.of("pen", "notebook").stream()
                .collect(Collectors.toMap(Function.identity(), String::length));
        System.out.println("Function.identity() as key: " + new java.util.TreeMap<>(lengths));

        Function<Integer, Integer> plusTax = p -> p + p / 10;
        Function<Integer, String> asRupees = p -> "Rs " + p;
        System.out.println("andThen == compose: " + plusTax.andThen(asRupees).apply(200)
                + " / " + asRupees.compose(plusTax).apply(200));

        System.out.println("--- Serializable lambdas ---");
        Runnable plain = () -> { };
        Runnable serial = (Runnable & Serializable) () -> { };
        System.out.println("plain is Serializable? " + (plain instanceof Serializable)
                + ", intersection cast? " + (serial instanceof Serializable));
    }

    static String tryRemove(List<String> list, Predicate<String> rule) {
        try {
            list.removeIf(rule);
            return "ok";
        } catch (UnsupportedOperationException e) {
            return "UnsupportedOperationException";
        }
    }

    static List<String> filter(List<String> items, Predicate<String> rule) {
        List<String> kept = new ArrayList<>();
        for (String item : items) {
            if (rule.test(item)) {
                kept.add(item);
            }
        }
        return kept;
    }
}

/* Expected output:
--- Primitive versions: no boxing ---
tickets 1, 2 | twice(21) = 42 | label(7) = #7 | length("java") = 4 | cubed(1000) = 1000000000 | totalLength = 5
report: pen x3; ink x1; | longerThan("lambda", 4) = true
--- forEach / removeIf / replaceAll ---
[java][streams]
Arrays.asList after replaceAll: [A, BB, C]
Arrays.asList removeIf (no match): ok
Arrays.asList removeIf (match):    UnsupportedOperationException
List.of removeIf (no match):       UnsupportedOperationException
--- More combinators ---
Predicate.not(String::isBlank): [Duke, duke, Java]
Predicate.isEqual("Duke"):      [Duke]
Consumer.andThen: LOG:saved SAVED
Function.identity() as key: {notebook=8, pen=3}
andThen == compose: Rs 220 / Rs 220
--- Serializable lambdas ---
plain is Serializable? false, intersection cast? true
*/
