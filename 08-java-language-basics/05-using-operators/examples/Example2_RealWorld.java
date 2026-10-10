/**
 * Example 2: an online checkout, using operators the way real code does.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    record Customer(String name, boolean isMember) { }

    static final int FREE_SHIPPING_FROM = 500;
    static final int SHIPPING_FEE = 40;

    public static void main(String[] args) {
        checkout(new Customer("Asha", true), new int[] {120, 80, 60}, 7);
        checkout(new Customer("Ravi", false), new int[] {300, 250}, 0);
        checkout(null, new int[] {99}, 3);
    }

    static void checkout(Customer customer, int[] itemPrices, int couponPercent) {
        // Short-circuit: customer.name() is never called when customer is null.
        String name = customer != null && customer.name() != null ? customer.name() : "Guest";

        int subtotal = 0;
        for (int price : itemPrices) {
            subtotal += price;
        }

        // Members get free shipping; everyone else gets it above a threshold.
        boolean member = customer != null && customer.isMember();
        int shipping = member || subtotal >= FREE_SHIPPING_FROM ? 0 : SHIPPING_FEE;

        // Integer math first, then a single division at the end, so nothing is lost early.
        int discount = subtotal * couponPercent / 100;

        int total = subtotal - discount + shipping;

        // / and % split the total into hundreds and the remainder.
        int hundreds = total / 100;
        int rest = total % 100;

        System.out.printf("%-5s subtotal=%d discount=%d shipping=%d total=%d (%d x 100 + %d)%n",
                name, subtotal, discount, shipping, total, hundreds, rest);
    }
}

/* Expected output:
Asha  subtotal=260 discount=18 shipping=0 total=242 (2 x 100 + 42)
Ravi  subtotal=550 discount=0 shipping=0 total=550 (5 x 100 + 50)
Guest subtotal=99 discount=2 shipping=40 total=137 (1 x 100 + 37)
*/
