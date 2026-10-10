import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Example 2: an inventory service with a family of custom exceptions, translating low-level
 * errors into its own vocabulary (with the cause chained), and lazy log messages.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    // ---- The exception family: one checked superclass, specific subclasses ----
    static class InventoryException extends Exception {
        InventoryException(String message) {
            super(message);
        }

        InventoryException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    static class UnknownSkuException extends InventoryException {
        UnknownSkuException(String sku) {
            super("unknown SKU " + sku);
        }
    }

    static class OutOfStockException extends InventoryException {
        final int available;

        OutOfStockException(String sku, int wanted, int available) {
            super(sku + ": wanted " + wanted + ", " + available + " left");
            this.available = available;
        }
    }

    // ---- A tiny logger that only builds messages for enabled levels ----
    static class Log {
        static boolean debugEnabled = false;
        static int messagesBuilt = 0;

        static void debug(Supplier<String> message) {
            if (debugEnabled) {
                System.out.println("  DEBUG " + message.get());
            }
        }

        static String expensive(String text) {
            messagesBuilt++;
            return text;
        }
    }

    // ---- A pretend storage layer with its own low-level exception ----
    static class StorageDriver {
        Map<String, String> rows = new HashMap<>(Map.of("PEN", "10", "INK", "x2"));   // INK is corrupt

        String read(String key) {
            String raw = rows.get(key);
            return raw;
        }
    }

    static class Inventory {
        private final StorageDriver driver = new StorageDriver();

        int stockOf(String sku) throws InventoryException {
            String raw = driver.read(sku);
            if (raw == null) {
                throw new UnknownSkuException(sku);
            }
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                // Translate the low-level problem into this layer's language, keeping the cause.
                throw new InventoryException("stock record for " + sku + " is corrupt", e);
            }
        }

        void reserve(String sku, int qty) throws InventoryException {
            if (qty <= 0) {
                throw new IllegalArgumentException("qty must be positive: " + qty);   // misuse: unchecked
            }
            int available = stockOf(sku);
            Log.debug(() -> Log.expensive("reserving " + qty + " of " + sku + " (" + available + " available)"));
            if (qty > available) {
                throw new OutOfStockException(sku, qty, available);
            }
            driver.rows.put(sku, String.valueOf(available - qty));
        }
    }

    public static void main(String[] args) {
        Inventory inventory = new Inventory();
        List<String> results = new ArrayList<>();

        for (String[] request : new String[][] {{"PEN", "3"}, {"PEN", "50"}, {"GLUE", "1"}, {"INK", "1"}}) {
            String sku = request[0];
            int qty = Integer.parseInt(request[1]);
            try {
                inventory.reserve(sku, qty);
                results.add(sku + " x" + qty + ": reserved");
            } catch (OutOfStockException e) {                      // a specific member of the family
                results.add(sku + " x" + qty + ": only " + e.available + " left, offering a backorder");
            } catch (InventoryException e) {                       // the rest of the family
                String cause = e.getCause() == null ? "" : " (cause: " + e.getCause().getClass().getSimpleName() + ")";
                results.add(sku + " x" + qty + ": " + e.getMessage() + cause);
            }
        }
        results.forEach(r -> System.out.println("  " + r));

        System.out.println("debug off -> messages built: " + Log.messagesBuilt);
        Log.debugEnabled = true;
        try {
            inventory.reserve("PEN", 1);
        } catch (InventoryException e) {
            System.out.println("unexpected: " + e.getMessage());
        }
        System.out.println("debug on  -> messages built: " + Log.messagesBuilt);
    }
}

/* Expected output:
  PEN x3: reserved
  PEN x50: only 7 left, offering a backorder
  GLUE x1: unknown SKU GLUE
  INK x1: stock record for INK is corrupt (cause: NumberFormatException)
debug off -> messages built: 0
  DEBUG reserving 1 of PEN (7 available)
debug on  -> messages built: 1
*/
