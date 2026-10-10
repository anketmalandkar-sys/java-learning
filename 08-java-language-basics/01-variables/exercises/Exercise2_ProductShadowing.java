/**
 * Exercise 2 (Medium): fix parameter shadowing and add a constant.
 *
 * TASK
 *   The Product class compiles, but the checks fail: the constructor and setPrice() never
 *   change the fields. Find out why and fix it (don't rename the parameters).
 *
 *   Then add a constant for the 18% tax rate, named by the Java convention, and use it in
 *   priceWithTax() instead of the magic number. Name it TAX_RATE.
 *
 * EXPECTED OUTPUT
 *   name set:        PASS
 *   price set:       PASS
 *   setPrice works:  PASS
 *   price with tax:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - Inside the constructor, which variable does the plain word 'price' refer to?
 *   - A constant is 'static final'.
 *
 * Run: java exercises/Exercise2_ProductShadowing.java
 */
public class Exercise2_ProductShadowing {

    static class Product {
        // TODO: add the TAX_RATE constant (0.18)

        String name;
        double price;

        Product(String name, double price) {
            name = name;     // TODO: bug
            price = price;   // TODO: bug
        }

        void setPrice(double price) {
            price = price;   // TODO: bug
        }

        double priceWithTax() {
            return price + price * 0.18;   // TODO: use the constant
        }
    }

    public static void main(String[] args) {
        Product pen = new Product("Pen", 50.0);

        boolean allPass = true;
        allPass &= check("name set:", "Pen".equals(pen.name));
        allPass &= check("price set:", pen.price == 50.0);

        pen.setPrice(100.0);
        allPass &= check("setPrice works:", pen.price == 100.0);
        allPass &= check("price with tax:", Math.abs(pen.priceWithTax() - 118.0) < 0.001);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-16s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
