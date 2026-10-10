import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Exercise 4 (Medium): pick the right kind of method reference.
 *
 * TASK
 *   Each method below returns a lambda. Replace EVERY lambda with a method reference
 *   (no "->" may remain in this class's methods, except in main). Next to each one, write a
 *   comment naming its kind: static, bound, unbound, or constructor.
 *
 *   The checks call the functions, so the behaviour must stay exactly the same.
 *
 * EXPECTED OUTPUT
 *   larger:        PASS
 *   startsWith:    PASS
 *   isBanned:      PASS
 *   byLength:      PASS
 *   concat:        PASS
 *   newList:       PASS
 *   copyList:      PASS
 *   makeTicket:    PASS
 *   no lambdas:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - Unbound: the object the method is called on is the FIRST argument of the function.
 *     (s, prefix) -> s.startsWith(prefix) is String::startsWith.
 *   - isBanned checks membership in ONE particular list: that list is the bound target.
 *   - byLength: Comparator.comparingInt takes a ToIntFunction<String>.
 *   - Constructor references pick the constructor from the target type.
 *   - The "no lambdas" check reads this file's source and counts "->" outside main.
 *
 * Run: java exercises/Exercise4_MethodReferences.java
 */
public class Exercise4_MethodReferences {

    record Ticket(String event, int seat) { }

    static final List<String> BANNED = List.of("spam", "scam");

    static IntBinaryOperator larger() {
        return (a, b) -> Math.max(a, b);
    }

    static BiPredicate<String, String> startsWith() {
        return (s, prefix) -> s.startsWith(prefix);
    }

    static Predicate<String> isBanned() {
        return word -> BANNED.contains(word);
    }

    static Comparator<String> byLength() {
        return Comparator.comparingInt(s -> s.length());
    }

    static BinaryOperator<String> concat() {
        return (a, b) -> a.concat(b);
    }

    static Supplier<List<String>> newList() {
        return () -> new ArrayList<>();
    }

    static Function<Collection<String>, List<String>> copyList() {
        return c -> new ArrayList<>(c);
    }

    static BiFunction<String, Integer, Ticket> makeTicket() {
        return (event, seat) -> new Ticket(event, seat);
    }

    public static void main(String[] args) throws Exception {
        boolean allPass = true;
        allPass &= check("larger:", larger().applyAsInt(4, 11) == 11);
        allPass &= check("startsWith:", startsWith().test("java", "ja") && !startsWith().test("java", "va"));
        allPass &= check("isBanned:", isBanned().test("spam") && !isBanned().test("ham"));
        List<String> words = new ArrayList<>(List.of("kiwi", "fig", "banana"));
        words.sort(byLength());
        allPass &= check("byLength:", words.equals(List.of("fig", "kiwi", "banana")));
        allPass &= check("concat:", "abcd".equals(concat().apply("ab", "cd")));
        List<String> fresh = newList().get();
        fresh.add("ok");
        allPass &= check("newList:", fresh.equals(List.of("ok")));
        List<String> source = List.of("x", "y");
        List<String> copied = copyList().apply(source);
        copied.add("z");
        allPass &= check("copyList:", copied.equals(List.of("x", "y", "z")) && source.size() == 2);
        allPass &= check("makeTicket:", new Ticket("Concert", 12).equals(makeTicket().apply("Concert", 12)));
        allPass &= check("no lambdas:", lambdasOutsideMain() == 0);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Counts "->" in this file before main() starts (comments in the header are skipped).
    static int lambdasOutsideMain() throws Exception {
        // Works from the lesson folder (terminal) and from the repo root (IntelliJ's default).
        String[] candidates = {
            "exercises/Exercise4_MethodReferences.java",
            "03-lambda-expressions/exercises/Exercise4_MethodReferences.java",
            "Exercise4_MethodReferences.java",
        };
        java.nio.file.Path self = null;
        for (String c : candidates) {
            if (java.nio.file.Files.exists(java.nio.file.Path.of(c))) {
                self = java.nio.file.Path.of(c);
                break;
            }
        }
        if (self == null) {
            System.out.println("(couldn't find the source file to count lambdas; run from the lesson folder)");
            return -1;
        }
        String code = java.nio.file.Files.readString(self);
        String body = code.substring(code.indexOf("public class"), code.indexOf("public static void main"));
        int count = 0;
        for (int i = body.indexOf("->"); i >= 0; i = body.indexOf("->", i + 2)) {
            count++;
        }
        return count;
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-14s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
