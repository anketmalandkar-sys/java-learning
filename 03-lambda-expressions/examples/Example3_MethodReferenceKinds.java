import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Example 3: the four kinds of method reference, with several arguments, and constructor
 * references that change with the target type.
 *
 * Run: java examples/Example3_MethodReferenceKinds.java
 */
public class Example3_MethodReferenceKinds {

    record User(String name, int age) { }

    public static void main(String[] args) {
        System.out.println("--- Static: Type::staticMethod ---");
        IntBinaryOperator max = Math::max;                    // (a, b) -> Math.max(a, b)
        Function<String, Integer> parse = Integer::parseInt;  // s -> Integer.parseInt(s)
        System.out.println("max(3, 9) = " + max.applyAsInt(3, 9) + ", parse(\"42\") + 1 = " + (parse.apply("42") + 1));

        System.out.println("--- Unbound: Type::instanceMethod (object = first argument) ---");
        ToIntFunction<String> length = String::length;                  // s -> s.length()
        BinaryOperator<String> join = String::concat;                   // (s, t) -> s.concat(t)
        Comparator<String> ignoreCase = String::compareToIgnoreCase;    // (s, t) -> s.compareToIgnoreCase(t)
        Function<User, String> name = User::name;                       // u -> u.name()
        System.out.println("length(\"lambda\") = " + length.applyAsInt("lambda"));
        System.out.println("join(\"ab\", \"cd\") = " + join.apply("ab", "cd"));
        List<String> words = new ArrayList<>(List.of("pear", "Apple", "banana"));
        words.sort(ignoreCase);
        System.out.println("sorted ignoring case = " + words);
        System.out.println("name of User = " + name.apply(new User("Asha", 31)));

        System.out.println("--- Bound: expr::instanceMethod (object fixed in the reference) ---");
        Function<String, String> greet = "Hello, "::concat;   // name -> "Hello, ".concat(name)
        StringBuilder log = new StringBuilder();
        Function<String, StringBuilder> append = log::append; // text -> log.append(text)
        append.apply("first;");
        append.apply("second;");
        System.out.println(greet.apply("Ravi") + " / log = " + log);

        // The target of a bound reference is evaluated ONCE, when the reference is created.
        User user = new User("Meera", 28);
        Supplier<Integer> nameLength = user.name()::length;   // captures "Meera" now
        user = new User("Christopher", 40);                   // too late: the reference keeps "Meera"
        System.out.println("nameLength.get() = " + nameLength.get() + " (still Meera's name)");

        System.out.println("--- Constructor: Type::new picks a constructor from the target ---");
        Supplier<List<String>> empty = ArrayList::new;                      // new ArrayList<>()
        Function<Collection<String>, List<String>> copy = ArrayList::new;   // new ArrayList<>(collection)
        BiFunction<String, Integer, User> makeUser = User::new;             // (n, a) -> new User(n, a)
        Supplier<List<String>> typed = ArrayList<String>::new;              // a type argument is allowed...
        // Supplier<List<String>> diamond = ArrayList<>::new;               // ...the diamond is not: compile error
        System.out.println("empty = " + empty.get() + ", copy = " + copy.apply(List.of("x", "y"))
                + ", typed = " + typed.get());
        System.out.println("makeUser = " + makeUser.apply("Zoya", 25));
    }
}

/* Expected output:
--- Static: Type::staticMethod ---
max(3, 9) = 9, parse("42") + 1 = 43
--- Unbound: Type::instanceMethod (object = first argument) ---
length("lambda") = 6
join("ab", "cd") = abcd
sorted ignoring case = [Apple, banana, pear]
name of User = Asha
--- Bound: expr::instanceMethod (object fixed in the reference) ---
Hello, Ravi / log = first;second;
nameLength.get() = 5 (still Meera's name)
--- Constructor: Type::new picks a constructor from the target ---
empty = [], copy = [x, y], typed = []
makeUser = User[name=Zoya, age=25]
*/
