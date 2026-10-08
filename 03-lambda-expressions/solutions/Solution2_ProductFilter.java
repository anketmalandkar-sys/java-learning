import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Exercise 2 (Medium): build an online shop's search filters from lambdas.
 *
 * TASK
 *   Part A. Write two reusable helpers that take behaviour as a parameter:
 *     filter(items, rule)        keep only the items where rule.test(item) is true
 *     map(items, transformer)    turn each item into something else
 *   (Write them with a plain for loop. Don't use streams yet.)
 *
 *   Part B. Write the rules:
 *     IN_STOCK          stock > 0
 *     IS_ELECTRONICS    category equals "Electronics"
 *     priceUnder(max)   a METHOD that returns a Predicate: price < max
 *
 *   Part C. Write the price pipeline, using andThen (not one big lambda):
 *     SALE_PRICE        first take 20% off, then add Rs 49 shipping
 *
 *   Then main uses them to answer three searches. Don't change main.
 *
 * EXPECTED OUTPUT
 *   Electronics in stock:            [Headphones, Phone]
 *   In stock under Rs 1000:          [Headphones, Notebook, Water bottle]
 *   Sale prices of those:            [769.0, 209.0, 409.0]
 *   Out of stock OR over Rs 20000:   [Laptop, Phone, Desk lamp]
 *   PASS
 *
 * HINTS
 *   - filter: new ArrayList, loop, if (rule.test(item)) add.
 *   - map's signature is generic: <T, R> List<R> map(List<T> items, Function<T, R> transformer).
 *   - priceUnder(max) returns a lambda that captures max: return product -> product.getPrice() < max;
 *   - Combine rules with and(), or(), negate().
 *   - For SALE_PRICE, declare the two steps as their own Function constants first.
 *     (p -> p * 0.8).andThen(...) won't compile: a bare lambda has no type until it's assigned.
 *
 * Run: java solutions/Solution2_ProductFilter.java
 */
public class Solution2_ProductFilter {

    static class Product {
        private final String name;
        private final String category;
        private final double price;
        private final int stock;

        Product(String name, String category, double price, int stock) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }

        String getName() { return name; }
        String getCategory() { return category; }
        double getPrice() { return price; }
        int getStock() { return stock; }

        @Override
        public String toString() {
            return name;
        }
    }

    // ---- Part A ----

    static <T> List<T> filter(List<T> items, Predicate<T> rule) {
        List<T> filteredItems = new ArrayList<>();
        for (T item : items) {
            if (rule.test(item)) {
                filteredItems.add(item);
            }
        }
        return filteredItems;
    }

    static <T, R> List<R> map(List<T> items, Function<T, R> transformer) {
        List<R> result = new ArrayList<>();
        for (T item : items) {
            result.add(transformer.apply(item));
        }
        return result;
    }

    // ---- Part B ----

    static final Predicate<Product> IN_STOCK = product -> product.getStock() > 0;

    static final Predicate<Product> IS_ELECTRONICS = product -> "Electronics".equals(product.getCategory());

    static Predicate<Product> priceUnder(double max) {
        // return a lambda that captures max
        return product -> product.getPrice() < max;
    }

    // ---- Part C ----

    // Two small steps joined with andThen: discount first, then shipping.
    static final Function<Double, Double> DISCOUNTED_PRICE = price -> price * 0.8;
    static final Function<Double, Double> ADD_SHIPPING = price -> price + 49;
    static final Function<Double, Double> SALE_PRICE = DISCOUNTED_PRICE.andThen(ADD_SHIPPING);

    public static void main(String[] args) {
        List<Product> catalog = List.of(
                new Product("Laptop", "Electronics", 55000, 0),
                new Product("Headphones", "Electronics", 900, 12),
                new Product("Notebook", "Stationery", 200, 40),
                new Product("Phone", "Electronics", 24999, 3),
                new Product("Water bottle", "Home", 450, 25),
                new Product("Desk lamp", "Home", 1299, 0)
        );

        if (IN_STOCK == null || IS_ELECTRONICS == null || priceUnder(1) == null || SALE_PRICE == null) {
            System.out.println("FAIL: fill in the rules and SALE_PRICE first");
            return;
        }

        List<Product> electronicsInStock = filter(catalog, IS_ELECTRONICS.and(IN_STOCK));
        System.out.println("Electronics in stock:            " + electronicsInStock);

        List<Product> cheapInStock = filter(catalog, IN_STOCK.and(priceUnder(1000)));
        System.out.println("In stock under Rs 1000:          " + cheapInStock);

        List<Double> salePrices = map(map(cheapInStock, Product::getPrice), SALE_PRICE);
        System.out.println("Sale prices of those:            " + salePrices);

        List<Product> unusual = filter(catalog, IN_STOCK.negate().or(priceUnder(20000).negate()));
        System.out.println("Out of stock OR over Rs 20000:   " + unusual);

        boolean pass = electronicsInStock.toString().equals("[Headphones, Phone]")
                && cheapInStock.toString().equals("[Headphones, Notebook, Water bottle]")
                && salePrices.equals(List.of(769.0, 209.0, 409.0))
                && unusual.toString().equals("[Laptop, Phone, Desk lamp]");
        System.out.println(pass ? "PASS" : "FAIL");
    }
}
