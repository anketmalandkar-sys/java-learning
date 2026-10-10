import java.util.List;
import java.util.Map;

/**
 * Example 2: the same payment logic with error codes, then with exceptions.
 *
 * Shows the three advantages of exceptions:
 *   1. the main logic isn't buried under "if (code == -1)" checks
 *   2. the error travels up to whoever handles it, without every method passing codes along
 *   3. a hierarchy lets one handler catch a whole family of problems
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    // ---------- Version 1: error codes ----------
    static final int OK = 0;
    static final int NO_ACCOUNT = -1;
    static final int NO_FUNDS = -2;

    static int payWithCodes(Map<String, Integer> balances, String account, int amount) {
        Integer balance = balances.get(account);
        if (balance == null) {
            return NO_ACCOUNT;
        }
        if (balance < amount) {
            return NO_FUNDS;
        }
        balances.put(account, balance - amount);
        return OK;
    }

    static String checkoutWithCodes(Map<String, Integer> balances, String account, int amount) {
        int code = payWithCodes(balances, account, amount);
        if (code == NO_ACCOUNT) {                 // every caller repeats this ladder
            return "failed: unknown account";
        } else if (code == NO_FUNDS) {
            return "failed: insufficient funds";
        }
        return "paid " + amount;                  // ...and it's easy to forget a code
    }

    // ---------- Version 2: exceptions, with a small hierarchy ----------
    static class PaymentException extends Exception {          // checked: the caller can recover
        PaymentException(String message) {
            super(message);
        }
    }

    static class UnknownAccountException extends PaymentException {
        UnknownAccountException(String account) {
            super("unknown account " + account);
        }
    }

    static class InsufficientFundsException extends PaymentException {
        InsufficientFundsException(int missing) {
            super("short by " + missing);
        }
    }

    static void pay(Map<String, Integer> balances, String account, int amount) throws PaymentException {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be positive: " + amount);   // a caller bug: unchecked
        }
        Integer balance = balances.get(account);
        if (balance == null) {
            throw new UnknownAccountException(account);
        }
        if (balance < amount) {
            throw new InsufficientFundsException(amount - balance);
        }
        balances.put(account, balance - amount);
    }

    // The main logic reads straight through; one handler covers the whole family.
    static String checkout(Map<String, Integer> balances, String account, int amount) {
        try {
            pay(balances, account, amount);
            return "paid " + amount;
        } catch (PaymentException e) {
            return "failed (" + e.getClass().getSimpleName() + "): " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        Map<String, Integer> balances = new java.util.HashMap<>(Map.of("asha", 500, "ravi", 50));

        System.out.println("Error codes:");
        for (String account : List.of("asha", "ravi", "zoya")) {
            System.out.println("  " + account + " -> " + checkoutWithCodes(balances, account, 100));
        }

        balances = new java.util.HashMap<>(Map.of("asha", 500, "ravi", 50));
        System.out.println("Exceptions:");
        for (String account : List.of("asha", "ravi", "zoya")) {
            System.out.println("  " + account + " -> " + checkout(balances, account, 100));
        }

        try {
            checkout(balances, "asha", -5);
        } catch (IllegalArgumentException e) {
            System.out.println("  programming error, not handled as a payment failure: " + e.getMessage());
        }
    }
}

/* Expected output:
Error codes:
  asha -> paid 100
  ravi -> failed: insufficient funds
  zoya -> failed: unknown account
Exceptions:
  asha -> paid 100
  ravi -> failed (InsufficientFundsException): short by 50
  zoya -> failed (UnknownAccountException): unknown account zoya
  programming error, not handled as a payment failure: amount must be positive: -5
*/
