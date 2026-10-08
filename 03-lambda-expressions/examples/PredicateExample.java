import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PredicateExample {

    public static void main(String[] args) {
        var strings = List.of("one", "two", "three", "four");
        PredicateExample predicateExample = new PredicateExample();
        var stringsOfLength3 = predicateExample.retainStringsOflength3(strings);
        System.out.println("Original list: " + strings);
        System.out.println("Only elements of length 3: " + stringsOfLength3);

        Consumer<String> consumer = s -> System.out.println("Message : " + s);
        consumer.accept("Hi Anket!");
        consumer.accept("This is India!");

        Supplier<String> supplier = () -> "Anket Loves Java";
        System.out.println(supplier.get());

        Function<String, Integer> function = String::length;
        System.out.println(function.apply(supplier.get()));

        var animals = List.of("cat", "dog", "elephant", "ant", "bee", "butterfly");
        animals.forEach(System.out::println);

        // make animals modifiable
        animals = new ArrayList<>(animals);

        System.out.println(animals);
        animals.replaceAll(animal -> animal.toUpperCase());
        System.out.println(animals);

        animals.removeIf(animal -> animal.startsWith("B"));
        System.out.println(animals);
    }

    List<String> retainStringsOflength3(List<String> strings) {

        Predicate<String> predicate = s -> s.length() == 3;
        List<String> stringsOfLength3 = new ArrayList<>();
        for (String s : strings) {
            if (predicate.test(s)) {
               stringsOfLength3.add(s);
            }
        }
        return stringsOfLength3;
    }

}
