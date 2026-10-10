import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example 2: counting page views per user, the safe way and the risky way.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    public static void main(String[] args) {
        String[] visits = {"asha", "ravi", "asha", "meera", "asha", "ravi"};

        // Map values must be objects, so the counts are Integers.
        Map<String, Integer> views = new HashMap<>();
        for (String user : visits) {
            // getOrDefault avoids unboxing null for a user we haven't seen yet.
            views.put(user, views.getOrDefault(user, 0) + 1);
        }
        System.out.println("asha=" + views.get("asha") + " ravi=" + views.get("ravi")
                + " meera=" + views.get("meera"));

        // Risky: get() returns null for an unknown user, and unboxing null throws.
        try {
            int unknown = views.get("zoya");
            System.out.println("never printed " + unknown);
        } catch (NullPointerException e) {
            System.out.println("views.get(\"zoya\") unboxed null -> NullPointerException");
        }

        // Comparing two counts: equals, not ==.
        Map<String, Integer> lastWeek = new HashMap<>(Map.of("asha", 3));
        boolean same = views.get("asha").equals(lastWeek.get("asha"));
        System.out.println("asha same as last week? " + same);

        // remove(int) vs remove(Object) on a List<Integer>.
        List<Integer> userIds = new ArrayList<>(List.of(7, 1000, 42));
        userIds.remove(1);                       // INDEX 1: removes 1000, not a value 1
        System.out.println("after remove(1):                  " + userIds);
        userIds.remove(Integer.valueOf(7));      // the value 7
        System.out.println("after remove(Integer.valueOf(7)): " + userIds);

        // Big loops: a primitive accumulator avoids creating millions of Long objects.
        long total = 0;
        for (int i = 0; i < 1_000_000; i++) {
            total += i;
        }
        System.out.println("total = " + total);
    }
}

/* Expected output:
asha=3 ravi=2 meera=1
views.get("zoya") unboxed null -> NullPointerException
asha same as last week? true
after remove(1):                  [7, 42]
after remove(Integer.valueOf(7)): [42]
total = 499999500000
*/
