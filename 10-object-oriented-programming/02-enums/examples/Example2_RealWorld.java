import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Example 2: subscription plans and order statuses in a SaaS app.
 *
 *   - Plan carries its own data (price, user limit), so no switch is needed to look it up.
 *   - OrderStatus has a transition rule per constant.
 *   - EnumSet and EnumMap group and count by enum.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    enum Plan {
        FREE(0, 1), PRO(499, 5), TEAM(1999, 50);

        private final int priceRupees;
        private final int maxUsers;

        Plan(int priceRupees, int maxUsers) {
            this.priceRupees = priceRupees;
            this.maxUsers = maxUsers;
        }

        boolean allows(int users) {
            return users <= maxUsers;
        }

        // The cheapest plan that fits a team: loops over values() in declaration order.
        static Plan cheapestFor(int users) {
            for (Plan plan : values()) {
                if (plan.allows(users)) {
                    return plan;
                }
            }
            throw new IllegalArgumentException("No plan for " + users + " users");
        }
    }

    enum OrderStatus {
        PLACED, PACKED, SHIPPED, DELIVERED, CANCELLED;

        boolean canMoveTo(OrderStatus next) {
            return switch (this) {
                case PLACED -> next == PACKED || next == CANCELLED;
                case PACKED -> next == SHIPPED || next == CANCELLED;
                case SHIPPED -> next == DELIVERED;
                case DELIVERED, CANCELLED -> false;
            };
        }
    }

    public static void main(String[] args) {
        for (int users : new int[] {1, 3, 20}) {
            Plan plan = Plan.cheapestFor(users);
            System.out.println(users + " user(s) -> " + plan + " at Rs " + plan.priceRupees);
        }

        System.out.println("PLACED -> SHIPPED allowed? " + OrderStatus.PLACED.canMoveTo(OrderStatus.SHIPPED));
        System.out.println("PACKED -> SHIPPED allowed? " + OrderStatus.PACKED.canMoveTo(OrderStatus.SHIPPED));

        // EnumSet: a compact set of enum constants.
        EnumSet<OrderStatus> finished = EnumSet.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED);
        EnumSet<OrderStatus> open = EnumSet.complementOf(finished);
        System.out.println("open statuses: " + open);

        // EnumMap: counts per status, always iterated in declaration order.
        List<OrderStatus> today = List.of(OrderStatus.SHIPPED, OrderStatus.PLACED, OrderStatus.SHIPPED,
                OrderStatus.CANCELLED, OrderStatus.PLACED, OrderStatus.SHIPPED);
        Map<OrderStatus, Integer> counts = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : today) {
            counts.merge(status, 1, Integer::sum);
        }
        System.out.println("today: " + counts);

        // Store the NAME, not the ordinal, when saving to a file or database.
        String saved = OrderStatus.SHIPPED.name();
        System.out.println("saved \"" + saved + "\", loaded back: " + OrderStatus.valueOf(saved));
    }
}

/* Expected output:
1 user(s) -> FREE at Rs 0
3 user(s) -> PRO at Rs 499
20 user(s) -> TEAM at Rs 1999
PLACED -> SHIPPED allowed? false
PACKED -> SHIPPED allowed? true
open statuses: [PLACED, PACKED, SHIPPED]
today: {PLACED=2, SHIPPED=3, CANCELLED=1}
saved "SHIPPED", loaded back: SHIPPED
*/
