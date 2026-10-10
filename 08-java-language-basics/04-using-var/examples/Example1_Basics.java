import java.util.ArrayList;
import java.util.List;

/**
 * Example 1: what var infers, and that the type stays fixed.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        var count = 10;
        var price = 9.99;
        var initial = 'J';
        var language = "Java";
        var bigNumber = 10_000_000_000L;

        // Boxing to Object lets us ask the runtime what each value really is.
        System.out.println("count     -> " + typeOf(count));
        System.out.println("price     -> " + typeOf(price));
        System.out.println("initial   -> " + typeOf(initial));
        System.out.println("language  -> " + typeOf(language));
        System.out.println("bigNumber -> " + typeOf(bigNumber));

        // The type is fixed: count is an int forever.
        count = count + 5;
        // count = "fifteen";   // would not compile: incompatible types
        System.out.println("count is now " + count);

        // var with generics: the type argument comes from the right side.
        var languages = new ArrayList<String>();
        languages.add("Java");
        languages.add("Kotlin");

        // var works for loop variables too.
        for (var name : languages) {
            System.out.println("  " + name + " has " + name.length() + " letters");
        }

        // List.of has an obvious type, so var reads well here.
        var primes = List.of(2, 3, 5, 7);
        var sum = 0;
        for (var p : primes) {
            sum += p;
        }
        System.out.println("sum of " + primes + " = " + sum);
    }

    static String typeOf(Object value) {
        return value.getClass().getSimpleName();
    }
}

/* Expected output:
count     -> Integer
price     -> Double
initial   -> Character
language  -> String
bigNumber -> Long
count is now 15
  Java has 4 letters
  Kotlin has 6 letters
sum of [2, 3, 5, 7] = 17
*/
