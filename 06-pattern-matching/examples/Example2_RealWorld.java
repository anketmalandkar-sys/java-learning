import java.util.List;

/**
 * Example 2: pricing and routing orders with a sealed hierarchy (Java 21).
 *   1. A sealed interface of records: the compiler knows every payment type.
 *   2. An exhaustive switch with NO default: fees per payment type.
 *   3. Nested record patterns + guards: rules that depend on the whole order's shape.
 *   4. Results as a sealed type too: Approved / Declined, handled with a switch.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    sealed interface Payment permits Card, Upi, Wallet {}
    record Card(String network, String last4) implements Payment {}
    record Upi(String vpa) implements Payment {}
    record Wallet(String provider, double balance) implements Payment {}

    record Customer(String name, boolean premium) {}
    record Order(String id, Customer customer, double amount, Payment payment) {}

    sealed interface Decision permits Approved, Declined {}
    record Approved(String orderId, double fee) implements Decision {}
    record Declined(String orderId, String reason) implements Decision {}

    // 2. Exhaustive: if a new Payment type is added to "permits", this stops compiling
    //    until it gets a case. That's why there's no default.
    static double feePercent(Payment payment) {
        return switch (payment) {
            case Card(String network, var last4) when network.equals("AMEX") -> 2.5;
            case Card c   -> 1.8;
            case Upi u    -> 0.0;
            case Wallet w -> 1.0;
        };
    }

    // 3. Rules over the whole order, matched structurally. First match wins, so strict rules go first.
    static Decision decide(Order order) {
        return switch (order) {
            case Order(var id, var customer, var amount, Wallet(var provider, var balance))
                    when balance < amount ->
                    new Declined(id, provider + " wallet has only " + balance);
            case Order(var id, Customer(var name, var premium), var amount, Card card)
                    when amount > 50_000 && !premium ->
                    new Declined(id, name + " needs premium for card orders over 50000");
            case Order(var id, Customer(var name, var premium), var amount, var payment) ->
                    new Approved(id, premium ? 0.0 : amount * feePercent(payment) / 100);
        };
    }

    // 4. Handling the result: also exhaustive, also no default.
    static String report(Decision decision) {
        return switch (decision) {
            case Approved(var id, var fee) when fee == 0 -> id + ": approved, no fee";
            case Approved(var id, var fee)               -> id + ": approved, fee " + String.format("%.2f", fee);
            case Declined(var id, var reason)            -> id + ": DECLINED (" + reason + ")";
        };
    }

    public static void main(String[] args) {
        Customer ravi = new Customer("Ravi", false);
        Customer meera = new Customer("Meera", true);

        List<Order> orders = List.of(
                new Order("A1", ravi, 2_000, new Card("VISA", "4242")),
                new Order("A2", ravi, 2_000, new Card("AMEX", "0005")),
                new Order("A3", ravi, 750, new Upi("ravi@okbank")),
                new Order("A4", meera, 1_200, new Wallet("PayCash", 500)),
                new Order("A5", ravi, 80_000, new Card("VISA", "4242")),
                new Order("A6", meera, 80_000, new Card("VISA", "1111")));

        System.out.println("--- Fees by payment type ---");
        for (Payment p : List.of(new Card("VISA", "4242"), new Card("AMEX", "0005"), new Upi("a@b"), new Wallet("PayCash", 0))) {
            System.out.println(p + " -> " + feePercent(p) + "%");
        }

        System.out.println();
        System.out.println("--- Decisions ---");
        for (Order order : orders) {
            System.out.println(report(decide(order)));
        }
    }
}

/* Expected output:
--- Fees by payment type ---
Card[network=VISA, last4=4242] -> 1.8%
Card[network=AMEX, last4=0005] -> 2.5%
Upi[vpa=a@b] -> 0.0%
Wallet[provider=PayCash, balance=0.0] -> 1.0%

--- Decisions ---
A1: approved, fee 36.00
A2: approved, fee 50.00
A3: approved, no fee
A4: DECLINED (PayCash wallet has only 500.0)
A5: DECLINED (Ravi needs premium for card orders over 50000)
A6: approved, no fee
*/
