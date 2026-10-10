import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Example 3: which casts to parameterized types are checked (no warning) and which aren't.
 *
 * Run: java examples/Example3_ProvablySafeCasts.java
 * To see that the "checked" casts really produce no warning, compile with:
 *   javac -Xlint:unchecked -d out examples/Example3_ProvablySafeCasts.java
 * Only the cast inside uncheckedCasts() is reported (it is suppressed for the plain run).
 */
public class Example3_ProvablySafeCasts {

    static void checkedCasts(Collection<String> coll, Object obj) {
        // The type argument <String> is already known; only the class part is checked at runtime.
        List<String> asList = (List<String>) coll;
        System.out.println("  (List<String>) coll       -> ok, " + asList.size() + " item(s)");

        try {
            ArrayList<String> asArrayList = (ArrayList<String>) asList;   // a real runtime check on ArrayList
            System.out.println("  (ArrayList<String>) list  -> ok, " + asArrayList);
        } catch (ClassCastException e) {
            System.out.println("  (ArrayList<String>) list  -> ClassCastException: it's a "
                    + asList.getClass().getSimpleName() + ", caught right at the cast");
        }

        List<?> anyList = (List<?>) obj;      // unbounded wildcard: nothing about the elements to check
        System.out.println("  (List<?>) obj             -> ok, " + anyList.size() + " item(s)");
    }

    @SuppressWarnings("unchecked")
    static void uncheckedCasts(Object obj) {
        // The runtime can only check "is it a List?"; the <Integer> is a promise nobody verifies.
        List<Integer> numbers = (List<Integer>) obj;
        System.out.println("  (List<Integer>) obj       -> 'succeeds', size " + numbers.size());
        try {
            int first = numbers.get(0);       // the failure shows up here, far from the cast
            System.out.println("never printed " + first);
        } catch (ClassCastException e) {
            System.out.println("  numbers.get(0)            -> ClassCastException, away from the cast");
        }
    }

    public static void main(String[] args) {
        List<String> names = new LinkedList<>(List.of("Asha", "Ravi"));

        System.out.println("Checked casts (no warning):");
        checkedCasts(names, names);

        System.out.println("Unchecked cast (warning, suppressed here):");
        uncheckedCasts(names);
    }
}

/* Expected output:
Checked casts (no warning):
  (List<String>) coll       -> ok, 2 item(s)
  (ArrayList<String>) list  -> ClassCastException: it's a LinkedList, caught right at the cast
  (List<?>) obj             -> ok, 2 item(s)
Unchecked cast (warning, suppressed here):
  (List<Integer>) obj       -> 'succeeds', size 2
  numbers.get(0)            -> ClassCastException, away from the cast
*/
