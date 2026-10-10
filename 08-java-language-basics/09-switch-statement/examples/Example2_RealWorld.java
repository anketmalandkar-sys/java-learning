/**
 * Example 2: an order-status handler with a String switch and an enum switch.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    enum PaymentMethod { CARD, UPI, CASH, WALLET }

    public static void main(String[] args) {
        String[] statuses = {"placed", "SHIPPED", "Delivered", "lost", null};
        for (String status : statuses) {
            System.out.println(status + " -> " + customerMessage(status));
        }

        for (PaymentMethod method : PaymentMethod.values()) {
            System.out.println(method + " fee: " + feePercent(method) + "%");
        }
    }

    static String customerMessage(String status) {
        // Guard: a classic switch throws NullPointerException on a null String.
        if (status == null) {
            return "No status yet";
        }
        String message;
        // Normalise case so "SHIPPED" and "shipped" match the same label.
        switch (status.toLowerCase()) {
            case "placed":
                message = "We got your order";
                break;
            case "shipped":
                message = "On its way";
                break;
            case "delivered":
                message = "Enjoy!";
                break;
            default:
                message = "Please contact support (status: " + status + ")";
                break;
        }
        return message;
    }

    static int feePercent(PaymentMethod method) {
        int fee;
        // With an enum selector, the labels are the bare constant names: CARD, not PaymentMethod.CARD.
        switch (method) {
            case CARD:
                fee = 2;
                break;
            case UPI:
            case CASH:
                fee = 0;
                break;
            default:
                fee = 1;
                break;
        }
        return fee;
    }
}

/* Expected output:
placed -> We got your order
SHIPPED -> On its way
Delivered -> Enjoy!
lost -> Please contact support (status: lost)
null -> No status yet
CARD fee: 2%
UPI fee: 0%
CASH fee: 0%
WALLET fee: 1%
*/
