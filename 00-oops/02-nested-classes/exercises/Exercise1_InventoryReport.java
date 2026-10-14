import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 1 (Easy): a static nested class and an inner class.
 *
 * TASK
 *   Complete the Warehouse class:
 *
 *   1. Add a STATIC nested class Product with final fields name (String) and stock (int),
 *      a constructor, and a method isLow() that returns true when stock < 5.
 *
 *   2. Make addProduct(name, stock) create a Product and add it to the private list.
 *
 *   3. Add an INNER (non-static) class LowStockReport with a method lines() that returns a
 *      List<String> like "Warehouse Pune: Glue (2)" for every low-stock product. It must read
 *      the outer Warehouse's private city and products fields directly.
 *
 *   4. Make report() return a new LowStockReport for this warehouse.
 *
 *   Then uncomment the lines in main marked "uncomment" and run.
 *
 * EXPECTED OUTPUT
 *   product:  PASS
 *   isLow:    PASS
 *   report:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - Creating an inner object inside an instance method: just "new LowStockReport()".
 *   - From outside: warehouse.new LowStockReport() works too.
 *
 * Run: java exercises/Exercise1_InventoryReport.java
 */
public class Exercise1_InventoryReport {

    static class Warehouse {
        private final String city;
        private final List<Object> products = new ArrayList<>();   // TODO: List<Product>

        Warehouse(String city) {
            this.city = city;
        }

        // TODO: static nested class Product
        static class Product {
            final String name;
            final int stock;
            Product(String name, int stock) {
                this.name = name;
                this.stock = stock;
            }

            public Boolean isLow() {
                return stock < 5;
            }

            @Override
            public String toString() {
                return "%s (%s)".formatted(name, stock);
            }
        }

        void addProduct(String name, int stock) {
            // TODO
            products.add(new Product(name, stock));
        }

        // TODO: inner class LowStockReport
        class LowStockReport {
            public List<String> lines() {
                List<String> lines = new ArrayList<>();
                for (Object product : products) {
                    lines.add("Warehouse %s: %s".formatted(city, product));
                }
                return lines;
            }
        }


        LowStockReport report() {   // TODO: return type LowStockReport
            return new LowStockReport();
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        // uncomment:
         Warehouse.Product glue = new Warehouse.Product("Glue", 2);
         allPass &= check("product:", "Glue".equals(glue.name) && glue.stock == 2);
         allPass &= check("isLow:", glue.isLow() && !new Warehouse.Product("Tape", 5).isLow());

         Warehouse pune = new Warehouse("Pune");
         pune.addProduct("Glue", 2);
         //pune.addProduct("Tape", 40);
         pune.addProduct("Ink", 0);
         List<String> lines = pune.report().lines();
         allPass &= check("report:", lines.equals(List.of("Warehouse Pune: Glue (2)", "Warehouse Pune: Ink (0)")));
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-9s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
