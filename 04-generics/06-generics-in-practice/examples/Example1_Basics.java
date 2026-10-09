import java.util.EnumMap;
import java.util.Map;

/**
 * Example 1: generic interfaces and recursive bounds.
 *   1. Three ways to implement a generic interface Pair<K, V>.
 *   2. Enum<E extends Enum<E>>: an enum can only be compared with its own kind.
 *   3. A self-typed builder: Builder<B extends Builder<B>>.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    interface Pair<K, V> {
        K key();
        V value();

        default String describe() {
            return key() + " -> " + value();
        }
    }

    // 1a. Stays generic: the caller chooses K and V.
    record OrderedPair<K, V>(K key, V value) implements Pair<K, V> {}

    // 1b. Fixes both: not generic any more.
    record StockLine(String key, Integer value) implements Pair<String, Integer> {
        boolean isLow() { return value < 10; }
    }

    // 1c. Fixes K, keeps V generic.
    record Labelled<V>(String key, V value) implements Pair<String, V> {}

    enum Size { SMALL, MEDIUM, LARGE }
    enum Colour { RED, GREEN }

    // 3. B is "the concrete builder class", so inherited setters return the subclass.
    abstract static class Builder<B extends Builder<B>> {
        protected String name = "unnamed";

        B name(String name) {
            this.name = name;
            return self();
        }

        abstract B self();
    }

    static class PizzaBuilder extends Builder<PizzaBuilder> {
        private Size size = Size.MEDIUM;
        private boolean extraCheese;

        PizzaBuilder size(Size size) {
            this.size = size;
            return this;
        }

        PizzaBuilder extraCheese() {
            this.extraCheese = true;
            return this;
        }

        @Override
        PizzaBuilder self() { return this; }

        String build() {
            return size + " " + name + (extraCheese ? " + extra cheese" : "");
        }
    }

    public static void main(String[] args) {
        System.out.println("--- 1. Implementing Pair<K, V> three ways ---");
        Pair<String, Double> price = new OrderedPair<>("Coffee", 3.5);
        StockLine pens = new StockLine("Pens", 4);
        Pair<String, Boolean> flag = new Labelled<>("gift-wrap", true);
        System.out.println(price.describe());
        System.out.println(pens.describe() + (pens.isLow() ? " (low stock)" : ""));
        System.out.println(flag.describe());

        Pair<String, Integer> asPair = pens;   // a StockLine is a Pair<String, Integer>
        int count = asPair.value();            // through the interface, still an Integer
        System.out.println("via the interface: " + count);

        System.out.println();
        System.out.println("--- 2. Enum<E extends Enum<E>> ---");
        System.out.println("SMALL vs LARGE: " + Size.SMALL.compareTo(Size.LARGE));
        // Size.SMALL.compareTo(Colour.RED);   // won't compile: compareTo takes a Size
        Map<Size, Integer> stockBySize = new EnumMap<>(Size.class);   // EnumMap<K extends Enum<K>, V>
        stockBySize.put(Size.LARGE, 3);
        stockBySize.put(Size.SMALL, 12);
        System.out.println("stock by size: " + stockBySize);   // kept in declaration order

        System.out.println();
        System.out.println("--- 3. Self-typed builder ---");
        String pizza = new PizzaBuilder()
                .name("Margherita")    // from Builder, but returns PizzaBuilder...
                .extraCheese()         // ...so PizzaBuilder methods are still available
                .size(Size.LARGE)
                .build();
        System.out.println(pizza);
    }
}

/* Expected output:
--- 1. Implementing Pair<K, V> three ways ---
Coffee -> 3.5
Pens -> 4 (low stock)
gift-wrap -> true
via the interface: 4

--- 2. Enum<E extends Enum<E>> ---
SMALL vs LARGE: -2
stock by size: {SMALL=12, LARGE=3}

--- 3. Self-typed builder ---
LARGE Margherita + extra cheese
*/
