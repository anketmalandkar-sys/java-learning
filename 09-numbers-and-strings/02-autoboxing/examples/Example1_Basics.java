import java.util.ArrayList;
import java.util.List;

/**
 * Example 1: where boxing and unboxing happen, and the Integer cache.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        // Boxing: an int goes into a List<Integer>.
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            numbers.add(i * 10);        // compiled as numbers.add(Integer.valueOf(i * 10))
        }
        System.out.println("numbers = " + numbers);

        // Unboxing: % and += don't work on objects, so the compiler calls intValue().
        System.out.println("sum of even-indexed = " + sumAtEvenIndexes(numbers));

        // Unboxing when assigning to a primitive.
        int first = numbers.get(0);
        System.out.println("first + 1 = " + (first + 1));

        // The Integer cache: -128..127 share objects; larger values usually don't.
        Integer small1 = 127;
        Integer small2 = 127;
        Integer big1 = 128;
        Integer big2 = 128;
        System.out.println("127 == 127 (Integer): " + (small1 == small2));
        System.out.println("128 == 128 (Integer): " + (big1 == big2));
        System.out.println("128 equals 128:       " + big1.equals(big2));

        // Wrapper vs primitive: the wrapper is unboxed, so values are compared.
        int plain = 128;
        System.out.println("Integer 128 == int 128: " + (big1 == plain));

        // Unboxing null fails.
        Integer missing = null;
        try {
            int value = missing;
            System.out.println("never printed " + value);
        } catch (NullPointerException e) {
            System.out.println("unboxing null -> NullPointerException");
        }
    }

    static int sumAtEvenIndexes(List<Integer> values) {
        Integer total = 0;                  // boxed on purpose, to show unbox-add-rebox
        for (int i = 0; i < values.size(); i++) {
            if (i % 2 == 0) {
                total += values.get(i);     // total = Integer.valueOf(total.intValue() + values.get(i).intValue())
            }
        }
        return total;                       // unboxed to int on return
    }
}

/* Expected output:
numbers = [10, 20, 30, 40, 50]
sum of even-indexed = 90
first + 1 = 11
127 == 127 (Integer): true
128 == 128 (Integer): false
128 equals 128:       true
Integer 128 == int 128: true
unboxing null -> NullPointerException
*/
