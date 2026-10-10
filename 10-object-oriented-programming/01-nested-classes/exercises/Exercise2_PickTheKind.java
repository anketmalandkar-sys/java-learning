import java.util.List;
import java.util.function.Predicate;

/**
 * Exercise 2 (Medium): the same job three ways.
 *
 * TASK
 *   A sign-up form accepts a username if it has 3 to 15 characters, starts with a letter, and
 *   isn't in a list of reserved names (case-insensitive).
 *
 *   Write it three times, each returning a Predicate<String>:
 *     asLambda(reserved)          a lambda
 *     asAnonymous(reserved)       an anonymous class that ALSO counts how many names it rejected,
 *                                 and returns "rejected=N" from toString()
 *     asLocalClass(reserved, min) a LOCAL class named LengthRule with a constructor taking the
 *                                 minimum length (instead of the fixed 3). Create and return it.
 *
 *   The reserved list must be captured from the method's parameter (it's effectively final).
 *
 * EXPECTED OUTPUT
 *   lambda:    PASS
 *   anonymous: PASS
 *   local:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - Character.isLetter(s.charAt(0)) checks the first character.
 *   - reserved.stream().anyMatch(r -> r.equalsIgnoreCase(name)) checks the reserved list.
 *   - Only the anonymous class can hold the counter: a lambda has no fields.
 *
 * Run: java exercises/Exercise2_PickTheKind.java
 */
public class Exercise2_PickTheKind {

    static Predicate<String> asLambda(List<String> reserved) {
        return name -> false; // TODO
    }

    static Predicate<String> asAnonymous(List<String> reserved) {
        return name -> false; // TODO: replace with an anonymous class
    }

    static Predicate<String> asLocalClass(List<String> reserved, int minLength) {
        return name -> false; // TODO: declare class LengthRule here and return an instance
    }

    public static void main(String[] args) {
        List<String> reserved = List.of("admin", "root");
        List<String> good = List.of("asha", "Ravi_2024", "bob");
        List<String> bad = List.of("al", "9lives", "ADMIN", "averyveryverylongname");

        boolean allPass = true;
        allPass &= check("lambda:", allMatch(asLambda(reserved), good, bad));

        Predicate<String> anonymous = asAnonymous(reserved);
        boolean anonOk = allMatch(anonymous, good, bad);
        allPass &= check("anonymous:", anonOk && "rejected=4".equals(anonymous.toString()));

        Predicate<String> local = asLocalClass(reserved, 4);
        allPass &= check("local:", local.test("asha") && !local.test("bob") && !local.test("root")
                && local.getClass().getSimpleName().equals("LengthRule"));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean allMatch(Predicate<String> rule, List<String> good, List<String> bad) {
        boolean ok = true;
        for (String g : good) {
            ok &= rule.test(g);
        }
        for (String b : bad) {
            ok &= !rule.test(b);
        }
        return ok;
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-10s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
