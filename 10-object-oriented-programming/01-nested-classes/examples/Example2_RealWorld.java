import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Example 2: a shopping cart that uses nested classes the way real code does.
 *
 *   Cart.Item      static nested: a plain data type that belongs with Cart
 *   Cart.Builder   static nested: the builder pattern
 *   ItemIterator   private inner: walks the cart's private list, hidden from callers
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static class Cart implements Iterable<Cart.Item> {
        private final String owner;
        private final List<Item> items;

        private Cart(String owner, List<Item> items) {
            this.owner = owner;
            this.items = items;
        }

        static class Item {
            final String name;
            final int priceRupees;
            final int qty;

            Item(String name, int priceRupees, int qty) {
                this.name = name;
                this.priceRupees = priceRupees;
                this.qty = qty;
            }

            int total() {
                return priceRupees * qty;
            }
        }

        static class Builder {
            private final String owner;
            private final List<Item> items = new ArrayList<>();

            Builder(String owner) {
                this.owner = owner;
            }

            Builder add(String name, int price, int qty) {
                items.add(new Item(name, price, qty));
                return this;
            }

            Cart build() {
                return new Cart(owner, List.copyOf(items));
            }
        }

        // Private inner class: callers only ever see it as an Iterator<Item>.
        private class ItemIterator implements Iterator<Item> {
            private int next = 0;

            @Override
            public boolean hasNext() {
                return next < items.size();      // the outer cart's private list
            }

            @Override
            public Item next() {
                return items.get(next++);
            }
        }

        @Override
        public Iterator<Item> iterator() {
            return new ItemIterator();          // inside an instance method: this.new ItemIterator()
        }

        int grandTotal() {
            int sum = 0;
            for (Item item : this) {
                sum += item.total();
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        Cart cart = new Cart.Builder("Asha")
                .add("Notebook", 45, 3)
                .add("Pen", 12, 10)
                .build();

        for (Cart.Item item : cart) {
            System.out.printf("%-9s %3d x %d = %d%n", item.name, item.priceRupees, item.qty, item.total());
        }
        System.out.println(cart.owner + " pays " + cart.grandTotal());

        // An anonymous class for a one-off discount rule that keeps a counter.
        DiscountRule bulkRule = new DiscountRule() {
            private int applied = 0;

            @Override
            public int discount(Cart.Item item) {
                if (item.qty >= 10) {
                    applied++;
                    return item.total() / 10;
                }
                return 0;
            }

            @Override
            public String describe() {
                return "10% off lines of 10+, applied " + applied + " time(s)";
            }
        };
        int saved = 0;
        for (Cart.Item item : cart) {
            saved += bulkRule.discount(item);
        }
        System.out.println("saved " + saved + ": " + bulkRule.describe());
    }

    // Two abstract methods, so a lambda can't implement it; an anonymous class can.
    interface DiscountRule {
        int discount(Cart.Item item);

        String describe();
    }
}

/* Expected output:
Notebook   45 x 3 = 135
Pen        12 x 10 = 120
Asha pays 255
saved 12: 10% off lines of 10+, applied 1 time(s)
*/
