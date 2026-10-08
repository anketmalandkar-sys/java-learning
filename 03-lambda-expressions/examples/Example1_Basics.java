import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Example 1: the basics of lambda expressions.
 *
 * Shows: an anonymous class vs a lambda, the six core functional interfaces,
 * method references, and capturing a local variable.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // Our own functional interface. @FunctionalInterface makes the compiler
    // reject it if someone adds a second abstract method.
    @FunctionalInterface
    interface Greeter {
        String greet(String name);
    }

    public static void main(String[] args) {
        // 1. Anonymous class vs lambda: same behaviour, far less code.
        Greeter formal = new Greeter() {
            @Override
            public String greet(String name) {
                return "Good morning, " + name + ".";
            }
        };
        Greeter casual = name -> "Hey " + name + "!";
        System.out.println(formal.greet("Priya"));
        System.out.println(casual.greet("Priya"));

        // 2. The six core interfaces from java.util.function.
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Function<String, Integer> wordLength = word -> word.length();
        Consumer<String> shout = text -> System.out.println(text.toUpperCase() + "!");
        Supplier<List<String>> newList = () -> new ArrayList<>();
        UnaryOperator<String> trim = text -> text.trim();
        BinaryOperator<Integer> larger = (a, b) -> a >= b ? a : b;

        System.out.println("isEven(4): " + isEven.test(4));
        System.out.println("wordLength(\"lambda\"): " + wordLength.apply("lambda"));
        shout.accept("hello");
        System.out.println("new list is empty: " + newList.get().isEmpty());
        System.out.println("trim: [" + trim.apply("   spaced out   ") + "]");
        System.out.println("larger(7, 12): " + larger.apply(7, 12));

        // 3. Block body: braces plus an explicit return.
        Function<Integer, String> grade = marks -> {
            if (marks >= 90) {
                return "A";
            }
            if (marks >= 75) {
                return "B";
            }
            return "C";
        };
        System.out.println("grade(82): " + grade.apply(82));

        // 4. Method references: shorter when the lambda only calls one method.
        Function<String, Integer> parse = Integer::parseInt;        // static method
        Function<String, String> upper = String::toUpperCase;       // method on the parameter
        Consumer<String> print = System.out::println;               // method on a specific object
        Supplier<List<String>> listMaker = ArrayList::new;          // constructor
        print.accept("parse(\"42\") + 1 = " + (parse.apply("42") + 1));
        print.accept("upper: " + upper.apply("java"));
        print.accept("listMaker gives: " + listMaker.get());

        // 5. Capturing a local variable. passMark is never reassigned, so it's effectively final.
        int passMark = 40;
        Predicate<Integer> passed = marks -> marks >= passMark;
        System.out.println("passed(35): " + passed.test(35) + ", passed(55): " + passed.test(55));

        // 6. Passing behaviour into a method: the same method, two different rules.
        List<String> cities = List.of("Pune", "Mumbai", "Goa", "Bengaluru");
        System.out.println("Short names: " + keepIf(cities, city -> city.length() <= 4));
        System.out.println("Starts with B: " + keepIf(cities, city -> city.startsWith("B")));
    }

    // A tiny "filter" written by hand. The rule comes in as a Predicate.
    static List<String> keepIf(List<String> items, Predicate<String> rule) {
        List<String> result = new ArrayList<>();
        for (String item : items) {
            if (rule.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
}

/* Expected output:
Good morning, Priya.
Hey Priya!
isEven(4): true
wordLength("lambda"): 6
HELLO!
new list is empty: true
trim: [spaced out]
larger(7, 12): 12
grade(82): B
parse("42") + 1 = 43
upper: JAVA
listMaker gives: []
passed(35): false, passed(55): true
Short names: [Pune, Goa]
Starts with B: [Bengaluru]
*/
