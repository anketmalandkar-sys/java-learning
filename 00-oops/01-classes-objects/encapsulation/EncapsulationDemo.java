package oops.encapsulation;

/*
 * ACCESS MODIFIERS (from most to least restrictive)
 *
 *   Modifier     | same class | same package | subclass (other pkg) | everywhere
 *   -------------+------------+--------------+----------------------+-----------
 *   private      |    yes     |      no      |          no          |    no
 *   (default)    |    yes     |     yes      |          no          |    no
 *   protected    |    yes     |     yes      |         yes          |    no
 *   public       |    yes     |     yes      |         yes          |   yes
 *
 * Rule of thumb: start with private and only open up when you must.
 */

/*
 * IMMUTABILITY — the strongest form of encapsulation.
 * A `record` (Java 16+) gives you private final fields, a constructor,
 * accessors (amount(), currency()), equals, hashCode and toString for free.
 * Once created, a Money can never change — so it is automatically
 * thread-safe and can be shared freely.
 *
 * The "compact constructor" below runs validation before the fields are set.
 */
record Money(double amount, String currency) {
    Money {
        if (amount < 0) throw new IllegalArgumentException("amount < 0");
        if (currency == null || currency.length() != 3)
            throw new IllegalArgumentException("currency must be a 3-letter code");
    }

    // "Changing" an immutable object = returning a NEW object.
    Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("currency mismatch");
        }
        return new Money(amount + other.amount, currency);
    }
}

public class EncapsulationDemo {

    public static void main(String[] args) {
        BankAccount acc = new BankAccount("1234567890", "Anket", 1000);

        acc.deposit(500);
        acc.withdraw(200);
        System.out.println(acc);                       // Anket [****7890] balance=1300.0

        // acc.balance = -5;   // COMPILE ERROR: balance has private access

        // The invariant is protected — invalid operations are rejected:
        try {
            acc.withdraw(10_000);
        } catch (IllegalStateException e) {
            System.out.println("Rejected: " + e.getMessage());   // Insufficient funds
        }

        try {
            acc.getHistory().clear();                  // try to tamper with history
        } catch (UnsupportedOperationException e) {
            System.out.println("Rejected: history is read-only");
        }
        System.out.println("History: " + acc.getHistory());

        System.out.println("\n=== Immutable value object ===");
        Money m1 = new Money(10, "INR");
        Money m2 = m1.add(new Money(5, "INR"));
        System.out.println("m1 = " + m1);   // unchanged: Money[amount=10.0, currency=INR]
        System.out.println("m2 = " + m2);   // new object: Money[amount=15.0, currency=INR]
    }
}
