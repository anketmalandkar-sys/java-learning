import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Exercise 2 (Medium): rank products by price, then by name.
 *
 * TASK
 *   1. Implement compareTo in Product so the natural order is:
 *        - price ascending (cheapest first)
 *        - if two products cost the same, by name A to Z
 *   2. In main, fill in the three TODOs:
 *        - put all products into a TreeSet
 *        - find the cheapest product with Collections.min
 *        - find the most expensive product with Collections.max
 *
 * INPUT (name, price)
 *   Mouse 799.0, Keyboard 1499.0, Cable 199.0, Webcam 1499.0, Headset 2499.0, Adapter 199.0
 *
 * EXPECTED OUTPUT
 *   Catalog:   [Adapter 199.0, Cable 199.0, Mouse 799.0, Keyboard 1499.0, Webcam 1499.0, Headset 2499.0]
 *   Cheapest:  Adapter 199.0
 *   Priciest:  Headset 2499.0
 *   PASS
 *
 * HINTS
 *   - price is a double. Don't write (int) (price - other.price). Why not? 0.5 - 0.2 cast to int is 0.
 *     Use Double.compare.
 *   - Compare price first. Only if the result is 0, compare names.
 *   - Without the name tie-breaker, the TreeSet would drop one of the products that share a price.
 *     Try it after you finish to see this happen.
 *
 * Uses records (Java 16+).
 * Run: java exercises/Exercise2_ProductRanking.java
 */
public class Exercise2_ProductRanking {

    record Product(String name, double price) implements Comparable<Product> {

        @Override
        public int compareTo(Product other) {
            int res = Double.compare(this.price, other.price);
            if (res != 0) {
                return res;
            }
            return this.name.compareTo(other.name);
        }

        @Override
        public String toString() {
            return name + " " + price;
        }
    }

    public static void main(String[] args) {
        List<Product> products = List.of(
                new Product("Mouse", 799.0),
                new Product("Keyboard", 1499.0),
                new Product("Cable", 199.0),
                new Product("Webcam", 1499.0),
                new Product("Headset", 2499.0),
                new Product("Adapter", 199.0)
        );

        // TODO: create a TreeSet containing all products
        Set<Product> catalog = new TreeSet<>(products);

        // TODO: use Collections.min to find the cheapest product
        Product cheapest = Collections.min(catalog);

        // TODO: use Collections.max to find the most expensive product
        Product priciest = Collections.max(catalog);

        System.out.println("Catalog:   " + catalog);
        System.out.println("Cheapest:  " + cheapest);
        System.out.println("Priciest:  " + priciest);

        String expectedCatalog = "[Adapter 199.0, Cable 199.0, Mouse 799.0, Keyboard 1499.0, Webcam 1499.0, Headset 2499.0]";
        boolean pass = expectedCatalog.equals(catalog.toString())
                && new Product("Adapter", 199.0).equals(cheapest)
                && new Product("Headset", 2499.0).equals(priciest);
        System.out.println(pass ? "PASS" : "FAIL: expected catalog " + expectedCatalog
                + ", cheapest Adapter 199.0, priciest Headset 2499.0");
    }
}
