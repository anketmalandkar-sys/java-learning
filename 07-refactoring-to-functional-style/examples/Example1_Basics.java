import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Example 1: each common loop next to its functional equivalent. Both versions print the same thing.
 *   1. Simple loop             -> IntStream.range / rangeClosed
 *   2. Loop with a step        -> IntStream.iterate(seed, hasNext, next)       (Java 9+)
 *   3. Loop with a break       -> IntStream.iterate(seed, next) + takeWhile     (Java 9+)
 *   4. foreach with an if      -> stream().filter(...)
 *   5. foreach with a transform-> stream().map(...)
 *   6. Accumulator variable    -> a terminal operation (sum, count, ...)
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static final List<String> NAMES = List.of("Ravi", "Meera", "Arun", "Sanjana", "Kiran", "Joel");

    public static void main(String[] args) {
        simpleLoops();
        loopsWithSteps();
        loopsWithBreak();
        foreachWithIf();
        foreachWithTransformation();
        accumulators();
    }

    static void simpleLoops() {
        System.out.println("--- 1. Simple loops ---");

        List<Integer> imperative = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            imperative.add(i);
        }
        // range excludes the end, like "i < 5"
        List<Integer> functional = IntStream.range(0, 5).boxed().toList();
        System.out.println("range(0, 5):       " + imperative + " = " + functional);

        // rangeClosed includes the end, like "i <= 5"
        System.out.println("rangeClosed(1, 5): " + IntStream.rangeClosed(1, 5).boxed().toList());
    }

    static void loopsWithSteps() {
        System.out.println();
        System.out.println("--- 2. Loops with steps ---");

        List<Integer> imperative = new ArrayList<>();
        for (int i = 0; i <= 15; i += 3) {
            imperative.add(i);
        }
        // the three parts of the for header become three arguments
        List<Integer> functional = IntStream.iterate(0, i -> i <= 15, i -> i + 3).boxed().toList();
        System.out.println("step 3:  " + imperative + " = " + functional);

        // a step that isn't "+ k": filter() on a range couldn't express this one
        System.out.println("times 3: " + IntStream.iterate(1, i -> i < 500, i -> i * 3).boxed().toList());
    }

    static void loopsWithBreak() {
        System.out.println();
        System.out.println("--- 3. Loops with break ---");

        List<Integer> imperative = new ArrayList<>();
        for (int p = 1; ; p *= 2) {
            if (p > 1000) {
                break;
            }
            imperative.add(p);
        }
        // iterate(seed, next) is infinite; takeWhile is the "break"
        List<Integer> functional = IntStream.iterate(1, p -> p * 2).takeWhile(p -> p <= 1000).boxed().toList();
        System.out.println(imperative);
        System.out.println(functional);
    }

    static void foreachWithIf() {
        System.out.println();
        System.out.println("--- 4. foreach with if -> filter ---");

        System.out.print("imperative:");
        for (String name : NAMES) {
            if (name.length() == 4) {
                System.out.print(" " + name);
            }
        }
        System.out.println();

        System.out.print("functional:");
        NAMES.stream()
                .filter(name -> name.length() == 4)
                .forEach(name -> System.out.print(" " + name));
        System.out.println();
    }

    static void foreachWithTransformation() {
        System.out.println();
        System.out.println("--- 5. foreach with transformation -> map ---");

        List<String> imperative = new ArrayList<>();
        for (String name : NAMES) {
            if (name.length() == 4) {
                imperative.add(name.toUpperCase());
            }
        }
        // filter first, then map: transform only what you keep
        List<String> functional = NAMES.stream()
                .filter(name -> name.length() == 4)
                .map(String::toUpperCase)
                .toList();
        System.out.println(imperative + " = " + functional);

        // map can change the type too: String -> Integer
        System.out.println("lengths: " + NAMES.stream().map(String::length).toList());
    }

    static void accumulators() {
        System.out.println();
        System.out.println("--- 6. Accumulators -> terminal operations ---");

        int total = 0;
        for (String name : NAMES) {
            total += name.length();
        }
        // "total += ..." inside a lambda wouldn't compile (total isn't effectively final),
        // and it doesn't need to: sum() is the accumulator.
        int functionalTotal = NAMES.stream().mapToInt(String::length).sum();
        System.out.println("total letters: " + total + " = " + functionalTotal);

        long longNames = NAMES.stream().filter(name -> name.length() > 4).count();
        System.out.println("names longer than 4: " + longNames);

        String joined = NAMES.stream().filter(name -> name.startsWith("S") || name.startsWith("K"))
                .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("S or K names: " + joined);
    }
}

/* Expected output:
--- 1. Simple loops ---
range(0, 5):       [0, 1, 2, 3, 4] = [0, 1, 2, 3, 4]
rangeClosed(1, 5): [1, 2, 3, 4, 5]

--- 2. Loops with steps ---
step 3:  [0, 3, 6, 9, 12, 15] = [0, 3, 6, 9, 12, 15]
times 3: [1, 3, 9, 27, 81, 243]

--- 3. Loops with break ---
[1, 2, 4, 8, 16, 32, 64, 128, 256, 512]
[1, 2, 4, 8, 16, 32, 64, 128, 256, 512]

--- 4. foreach with if -> filter ---
imperative: Ravi Arun Joel
functional: Ravi Arun Joel

--- 5. foreach with transformation -> map ---
[RAVI, ARUN, JOEL] = [RAVI, ARUN, JOEL]
lengths: [4, 5, 4, 7, 5, 4]

--- 6. Accumulators -> terminal operations ---
total letters: 29 = 29
names longer than 4: 3
S or K names: [Sanjana, Kiran]
*/
