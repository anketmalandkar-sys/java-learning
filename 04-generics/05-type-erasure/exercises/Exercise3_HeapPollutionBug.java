import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Exercise 3 (Hard): two ClassCastExceptions that blow up far from their cause.
 *
 * THE SITUATION
 *   1. Settings.get(key, type) promises an Optional<T>: empty if the key is missing OR holds
 *      the wrong type. But get("timeout", Integer.class) hands back an Optional<Integer>
 *      that actually contains the String "30s". Nothing fails inside get(). The crash comes
 *      later, wherever the value is used as an Integer.
 *
 *   2. firstTwo(a, b, c) is supposed to return the first two items. Calling it with Strings
 *      throws ClassCastException, even though every argument is a String.
 *
 *   Both helpers were written with @SuppressWarnings / @SafeVarargs to make the warnings go away.
 *
 * TASK
 *   1. Run it. For each bug, explain in a comment WHY it happens. Both come down to erasure:
 *      what does the (T) cast in get() check at runtime? What kind of array does
 *      arrayOf(a, b) create when it's called from inside a generic method?
 *   2. Fix Settings.get so it really checks the type, using the Class<T> it's given.
 *      Keep its signature.
 *   3. Fix firstTwo so it can't fail: return a List<T> instead of a T[]. Delete arrayOf,
 *      which can't be made safe. Then update main's line marked "FIX".
 *   4. When you're done, there should be no @SuppressWarnings, no @SafeVarargs and no casts
 *      to T or T[] left, and javac -Xlint:all exercises/Exercise3_HeapPollutionBug.java
 *      should print no warnings.
 *   5. Bonus: add a second method  static String describe(List<Integer> ids)  next to the
 *      existing describe(List<String>). Read the compile error, explain it, then delete it.
 *
 * EXPECTED OUTPUT (after the fix)
 *   retries:   PASS
 *   timeout:   PASS
 *   missing:   PASS
 *   firstTwo:  PASS
 *   describe:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - Class<T> has isInstance(obj) and cast(obj). Unlike (T), they really check at runtime.
 *   - The README's section 5 has both bugs in miniature.
 *
 * Run: java exercises/Exercise3_HeapPollutionBug.java
 */
public class Exercise3_HeapPollutionBug {

    static class Settings {
        private final Map<String, Object> values = new HashMap<>();

        void put(String key, Object value) {
            values.put(key, value);
        }

        @SuppressWarnings("unchecked")
        <T> Optional<T> get(String key, Class<T> type) {
            return Optional.ofNullable((T) values.get(key));
        }
    }

    @SafeVarargs
    static <T> T[] arrayOf(T... items) {
        return items;
    }

    static <T> T[] firstTwo(T a, T b, T c) {
        return arrayOf(a, b);
    }

    static String describe(List<String> names) {
        return names.size() + " names";
    }

    public static void main(String[] args) {
        Settings settings = new Settings();
        settings.put("retries", 3);
        settings.put("timeout", "30s");   // someone stored the timeout as text

        boolean allPass = true;

        Optional<Integer> retries = settings.get("retries", Integer.class);
        allPass &= report("retries", retries.isPresent() && retries.get() == 3);

        Optional<Integer> timeout = settings.get("timeout", Integer.class);
        allPass &= report("timeout", timeout.isEmpty());   // wrong type: must be empty, not a disguised String

        allPass &= report("missing", settings.get("colour", String.class).isEmpty());

        boolean firstTwoOk;
        try {
            String[] pair = firstTwo("Pune", "Goa", "Delhi");   // FIX: use the new List<String> return type
            firstTwoOk = pair.length == 2 && pair[0].equals("Pune") && pair[1].equals("Goa");
        } catch (ClassCastException e) {
            System.out.println("  (firstTwo: " + e.getMessage().split(" \\(")[0] + ")");
            firstTwoOk = false;
        }
        allPass &= report("firstTwo", firstTwoOk);

        allPass &= report("describe", describe(List.of("Ravi", "Meera")).equals("2 names"));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean report(String name, boolean ok) {
        System.out.printf("%-10s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
