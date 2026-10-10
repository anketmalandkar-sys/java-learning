import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Exercise 5 (Medium): primitive interfaces, combinators, and the removeIf traps.
 *
 * TASK
 *   1. PRIMITIVES. Return lambdas (or method references) of the primitive types given, so no
 *      boxing happens:
 *        isEven()       an IntPredicate: true for even numbers (negatives too)
 *        wordLength()   a ToIntFunction<String>: the length of the stripped string
 *        maxOf()        an IntBinaryOperator: the larger of two ints
 *
 *   2. COMBINATORS. Using ONLY Predicate.not, Predicate.isEqual, .and/.or/.negate and method
 *      references (no "->" in these two methods):
 *        usableTag()    keeps tags that are not blank AND not equal to "draft"
 *        auditTrail(record, notify)
 *                       returns ONE Consumer<String> that runs `record` first, then `notify`,
 *                       both receiving the same message (Consumer.andThen)
 *
 *   3. IDENTITY. indexByName(names) returns a Map from each name to itself in upper case,
 *      using Function.identity() for the keys.
 *                                     ["asha", "ravi"] -> {asha=ASHA, ravi=RAVI}
 *
 *   4. SAFE CLEANUP. cleanTags(tags) must remove blank tags and strip the rest, and must work
 *      for ANY list it's given, including List.of(...) and Arrays.asList(...), without throwing
 *      and without changing the caller's list. Return the cleaned list.
 *
 * EXPECTED OUTPUT
 *   isEven:       PASS
 *   wordLength:   PASS
 *   maxOf:        PASS
 *   usableTag:    PASS
 *   auditTrail:   PASS
 *   indexByName:  PASS
 *   cleanTags:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - -3 % 2 is -1, not 1.
 *   - Predicate.not(String::isBlank).and(Predicate.not(Predicate.isEqual("draft")))
 *   - removeIf mutates, and List.of / Arrays.asList refuse that. Copy into a new ArrayList first.
 *
 * Run: java exercises/Exercise5_CombineAndSpecialize.java
 */
public class Exercise5_CombineAndSpecialize {

    static IntPredicate isEven() {
        return n -> false; // TODO
    }

    static ToIntFunction<String> wordLength() {
        return s -> -1; // TODO
    }

    static IntBinaryOperator maxOf() {
        return (a, b) -> 0; // TODO
    }

    static Predicate<String> usableTag() {
        return Predicate.isEqual("TODO"); // TODO
    }

    static Consumer<String> auditTrail(Consumer<String> record, Consumer<String> notify) {
        return record; // TODO
    }

    static Map<String, String> indexByName(List<String> names) {
        return Map.of(); // TODO
    }

    static List<String> cleanTags(List<String> tags) {
        tags.removeIf(String::isBlank);      // TODO: crashes for List.of / Arrays.asList, and mutates the caller's list
        tags.replaceAll(String::strip);
        return tags;
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("isEven:", isEven().test(4) && isEven().test(-6) && !isEven().test(-3) && isEven().test(0));
        allPass &= check("wordLength:", wordLength().applyAsInt("  java ") == 4 && wordLength().applyAsInt("") == 0);
        allPass &= check("maxOf:", maxOf().applyAsInt(3, 9) == 9 && maxOf().applyAsInt(-1, -5) == -1);

        Predicate<String> usable = usableTag();
        allPass &= check("usableTag:", usable.test("java") && !usable.test("  ") && !usable.test("draft")
                && usable.test("Draft"));

        StringBuilder trail = new StringBuilder();
        Consumer<String> record = msg -> trail.append("REC:").append(msg).append(' ');
        Consumer<String> notify = msg -> trail.append("NOTIFY:").append(msg).append(' ');
        auditTrail(record, notify).accept("saved");
        allPass &= check("auditTrail:", "REC:saved NOTIFY:saved".equals(trail.toString().trim()));

        allPass &= check("indexByName:", Map.of("asha", "ASHA", "ravi", "RAVI").equals(indexByName(List.of("asha", "ravi"))));

        boolean cleanOk;
        try {
            List<String> mine = new ArrayList<>(List.of(" a ", " "));
            List<String> fromMine = cleanTags(mine);
            cleanOk = cleanTags(List.of(" java", "", "x ")).equals(List.of("java", "x"))
                    && cleanTags(Arrays.asList(" ", "y")).equals(List.of("y"))
                    && fromMine.equals(List.of("a"))
                    && mine.equals(List.of(" a ", " "));          // the caller's list is untouched
        } catch (UnsupportedOperationException e) {
            cleanOk = false;
        }
        allPass &= check("cleanTags:", cleanOk);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-13s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
