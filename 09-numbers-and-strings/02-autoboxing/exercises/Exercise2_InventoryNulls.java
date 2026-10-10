import java.util.HashMap;
import java.util.Map;

/**
 * Exercise 2 (Medium): null-safe unboxing.
 *
 * TASK
 *   A shop keeps stock counts in a Map<String, Integer>. Products that were never stocked
 *   aren't in the map at all. Every method below crashes for an unknown product. Fix them so
 *   an unknown product counts as 0 stock. Keep using the map; don't add products to it unless
 *   the method is meant to change stock.
 *
 *     stockOf(product)              current stock, 0 if unknown
 *     canSell(product, qty)         true if stock >= qty
 *     restock(product, qty)         add qty to the stock (works for new products too)
 *     sell(product, qty)            if canSell, subtract and return true; otherwise false and
 *                                   change nothing
 *     totalStock()                  sum of all values. The map may hold an explicit null for a
 *                                   product that was discontinued: treat it as 0.
 *
 * EXPECTED OUTPUT
 *   stockOf:    PASS
 *   canSell:    PASS
 *   restock:    PASS
 *   sell:       PASS
 *   totalStock: PASS
 *   ALL PASS
 *
 * HINTS
 *   - map.getOrDefault(key, 0) helps for missing keys, but not for a key mapped to null.
 *   - map.merge(key, qty, Integer::sum) adds to an existing value or inserts qty.
 *
 * Run: java exercises/Exercise2_InventoryNulls.java
 */
public class Exercise2_InventoryNulls {

    static Map<String, Integer> stock = new HashMap<>();

    static int stockOf(String product) {
        return stock.get(product); // TODO: crashes for unknown products
    }

    static boolean canSell(String product, int qty) {
        return stock.get(product) >= qty; // TODO
    }

    static void restock(String product, int qty) {
        stock.put(product, stock.get(product) + qty); // TODO
    }

    static boolean sell(String product, int qty) {
        Integer current = stock.get(product);
        if (current >= qty) { // TODO
            stock.put(product, current - qty);
            return true;
        }
        return false;
    }

    static int totalStock() {
        int total = 0;
        for (Integer count : stock.values()) {
            total += count; // TODO
        }
        return total;
    }

    static void reset() {
        stock.clear();
        stock.put("pen", 10);
        stock.put("notebook", 3);
        stock.put("fax-paper", null);   // discontinued
    }

    public static void main(String[] args) {
        boolean allPass = true;

        reset();
        allPass &= check("stockOf:", safe(() -> stockOf("pen") == 10 && stockOf("eraser") == 0
                && stockOf("fax-paper") == 0));

        reset();
        allPass &= check("canSell:", safe(() -> canSell("pen", 10) && !canSell("pen", 11)
                && !canSell("eraser", 1) && canSell("eraser", 0)));

        reset();
        allPass &= check("restock:", safe(() -> {
            restock("pen", 5);
            restock("eraser", 4);
            return stockOf("pen") == 15 && stockOf("eraser") == 4;
        }));

        reset();
        allPass &= check("sell:", safe(() -> sell("notebook", 2) && stockOf("notebook") == 1
                && !sell("notebook", 2) && stockOf("notebook") == 1
                && !sell("eraser", 1) && !stock.containsKey("eraser")));

        reset();
        allPass &= check("totalStock:", safe(() -> totalStock() == 13));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    interface Check {
        boolean run();
    }

    static boolean safe(Check check) {
        try {
            return check.run();
        } catch (NullPointerException e) {
            return false;
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-11s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
