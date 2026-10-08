import javax.annotation.processing.SupportedSourceVersion;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * Example 2: a food-delivery app's checkout, built from small lambdas.
 *
 * Instead of one big method full of if-statements, each business rule is a
 * small named lambda. Rules are combined with and()/or()/andThen(), and the
 * notification step is just a list of Consumers that can grow without changing
 * the checkout code.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static class Order {
        private final String customer;
        private final double amount;
        private final boolean premiumMember;
        private final int distanceKm;

        Order(String customer, double amount, boolean premiumMember, int distanceKm) {
            this.customer = customer;
            this.amount = amount;
            this.premiumMember = premiumMember;
            this.distanceKm = distanceKm;
        }

        String getCustomer() { return customer; }
        double getAmount() { return amount; }
        boolean isPremiumMember() { return premiumMember; }
        int getDistanceKm() { return distanceKm; }
    }

    // ---- Rules as named Predicates: each one reads like a sentence. ----
    static final Predicate<Order> IS_PREMIUM = Order::isPremiumMember;
    static final Predicate<Order> IS_BIG_ORDER = order -> order.getAmount() >= 500;
    static final Predicate<Order> IS_NEARBY = order -> order.getDistanceKm() <= 5;

    // Free delivery for premium members, or for big orders that are nearby.
    static final Predicate<Order> FREE_DELIVERY = IS_PREMIUM.or(IS_BIG_ORDER.and(IS_NEARBY));

    // ---- Price steps as Functions, chained in a fixed order. ----
    static final Function<Double, Double> APPLY_DISCOUNT = price -> price >= 1000 ? price * 0.9 : price;
    static final Function<Double, Double> ADD_GST = price -> price * 1.05;
    static final Function<Double, Double> ROUND = price -> Math.round(price * 100) / 100.0;

    // andThen: discount first, then tax on the discounted price, then round.
    static final Function<Double, Double> FINAL_PRICE = APPLY_DISCOUNT.andThen(ADD_GST).andThen(ROUND);

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("Aarav", 1200, false, 3),
                new Order("Sana", 300, true, 12),
                new Order("Kabir", 650, false, 9),
                new Order("Diya", 150, false, 2)
        );

        // ---- Notifications: a list of Consumers. Adding a channel = adding a lambda. ----
        List<String> sentMessages = new ArrayList<>();
        List<Consumer<String>> notifiers = List.of(
                message -> sentMessages.add("SMS:   " + message),
                message -> sentMessages.add("Email: " + message)
        );

        for (Order order : orders) {
            double deliveryFee = FREE_DELIVERY.test(order) ? 0 : 40;
            double total = FINAL_PRICE.apply(order.getAmount()) + deliveryFee;
            System.out.printf("%-6s food %.2f + delivery %.0f = %.2f%n",
                    order.getCustomer(), FINAL_PRICE.apply(order.getAmount()), deliveryFee, total);

            String message = "Hi " + order.getCustomer() + ", your order of Rs " + total + " is confirmed";
            notifiers.forEach(notify -> notify.accept(message));
        }

        System.out.println();
        System.out.println("First two notifications sent:");
        sentMessages.subList(0, 2).forEach(System.out::println);

        // ---- computeIfAbsent takes a Function: it builds the value only if the key is missing. ----
        Map<Boolean, List<String>> byFreeDelivery = new HashMap<>();
        for (Order order : orders) {
            byFreeDelivery.computeIfAbsent(FREE_DELIVERY.test(order), key -> new ArrayList<>())
                    .add(order.getCustomer());
        }
        System.out.println();
        System.out.println("Free delivery: " + byFreeDelivery.get(true));
        System.out.println("Paid delivery: " + byFreeDelivery.get(false));

        // ---- removeIf takes a Predicate. negate() flips a rule without writing a new one. ----
        List<Order> nearbyOrders = new ArrayList<>(orders);
        nearbyOrders.removeIf(IS_NEARBY.negate());
        System.out.print("Nearby orders dispatched first: ");
        nearbyOrders.forEach(order -> System.out.print(order.getCustomer() + " "));
        System.out.println();

        Random random = new Random(314L);
        IntSupplier intSupplier = random::nextInt;
        System.out.println(intSupplier.getAsInt());

        Predicate<String> nonNull = s -> s != null;
        Predicate<String> nonEmpty = s -> !s.isEmpty();
        Predicate<String> shorterThan5 = s -> s.length() < 5;

        Predicate<String> p = nonNull.and(nonEmpty).and(shorterThan5);

    }
}

/* Expected output:
Aarav  food 1134.00 + delivery 0 = 1134.00
Sana   food 315.00 + delivery 0 = 315.00
Kabir  food 682.50 + delivery 40 = 722.50
Diya   food 157.50 + delivery 40 = 197.50

First two notifications sent:
SMS:   Hi Aarav, your order of Rs 1134.0 is confirmed
Email: Hi Aarav, your order of Rs 1134.0 is confirmed

Free delivery: [Aarav, Sana]
Paid delivery: [Kabir, Diya]
Nearby orders dispatched first: Aarav Diya
*/
