import java.util.ArrayList;
import java.util.List;

/**
 * Example 3: Class<?> for "any class", Class<? extends Number> for a bounded family,
 * and why a wildcard takes only one bound.
 *
 * Run: java examples/Example3_ClassWildcardAndBounds.java
 */
public class Example3_ClassWildcardAndBounds {

    // Class<?>: none of these Class methods depend on the type argument.
    static String describe(Class<?> type) {
        String kind = type.isPrimitive() ? "primitive"
                : type.isInterface() ? "interface"
                : type.isArray() ? "array"
                : "class";
        return type.getSimpleName() + " (" + kind + ")";
    }

    // Class<? extends Number>: only class objects of number types are accepted.
    static boolean isWholeNumber(Class<? extends Number> type) {
        return type == Integer.class || type == Long.class || type == Short.class || type == Byte.class;
    }

    // ONE bound per wildcard is all you get. For "Number AND Comparable", use a type parameter:
    // static double maxOf(List<? extends Number & Comparable<?>> values)   // compile error
    static <T extends Number & Comparable<T>> T maxOf(List<T> values) {
        T best = values.get(0);
        for (T v : values) {
            if (v.compareTo(best) > 0) {
                best = v;
            }
        }
        return best;
    }

    public static void main(String[] args) {
        System.out.println("--- Class<?> ---");
        List<Class<?>> types = List.of(String.class, List.class, int.class, int[].class);
        for (Class<?> t : types) {
            System.out.println("  " + describe(t));
        }

        Object payload = new ArrayList<String>();
        Class<?> runtime = payload.getClass();      // the usual way to hold "the class of some object"
        System.out.println("  runtime class of payload: " + describe(runtime));

        System.out.println("--- Class<? extends Number> ---");
        System.out.println("  Integer whole? " + isWholeNumber(Integer.class));
        System.out.println("  Double whole?  " + isWholeNumber(Double.class));
        // isWholeNumber(String.class);             // compile error: String isn't a Number

        System.out.println("--- One bound per wildcard; several for a type parameter ---");
        System.out.println("  maxOf([3, 9, 4]) = " + maxOf(List.of(3, 9, 4)));
    }
}

/* Expected output:
--- Class<?> ---
  String (class)
  List (interface)
  int (primitive)
  int[] (array)
  runtime class of payload: ArrayList (class)
--- Class<? extends Number> ---
  Integer whole? true
  Double whole?  false
--- One bound per wildcard; several for a type parameter ---
  maxOf([3, 9, 4]) = 9
*/
