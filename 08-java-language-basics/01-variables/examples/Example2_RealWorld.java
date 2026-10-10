/**
 * Example 2: a bank account, showing which kind of variable fits which job.
 *
 *   accountNumber, owner, balance   -> instance fields (each account has its own)
 *   nextAccountNumber               -> static field (shared counter for all accounts)
 *   MINIMUM_BALANCE                 -> constant (same rule for everyone, never changes)
 *   amount                          -> parameter (input from the caller)
 *   balanceAfter                    -> local variable (only needed inside one method)
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static class BankAccount {
        static final double MINIMUM_BALANCE = 100.0;
        private static int nextAccountNumber = 1001;

        private final int accountNumber;
        private final String owner;
        private double balance;

        BankAccount(String owner, double openingDeposit) {
            // Each account takes the next number, then bumps the shared counter.
            this.accountNumber = nextAccountNumber++;
            this.owner = owner;
            this.balance = openingDeposit;
        }

        boolean withdraw(double amount) {
            double balanceAfter = balance - amount;
            if (balanceAfter < MINIMUM_BALANCE) {
                System.out.printf("  #%d %s: withdraw %.2f refused (would leave %.2f)%n",
                        accountNumber, owner, amount, balanceAfter);
                return false;
            }
            balance = balanceAfter;
            System.out.printf("  #%d %s: withdrew %.2f, balance %.2f%n",
                    accountNumber, owner, amount, balance);
            return true;
        }
    }

    public static void main(String[] args) {
        BankAccount asha = new BankAccount("Asha", 500.0);
        BankAccount ravi = new BankAccount("Ravi", 150.0);

        System.out.println("Withdrawals:");
        asha.withdraw(200.0);
        ravi.withdraw(200.0);   // refused: Ravi's balance is separate from Asha's
        ravi.withdraw(40.0);

        System.out.println("Minimum balance for every account: " + BankAccount.MINIMUM_BALANCE);
        System.out.println("Next account number: " + BankAccount.nextAccountNumber);
    }
}

/* Expected output:
Withdrawals:
  #1001 Asha: withdrew 200.00, balance 300.00
  #1002 Ravi: withdraw 200.00 refused (would leave -50.00)
  #1002 Ravi: withdrew 40.00, balance 110.00
Minimum balance for every account: 100.0
Next account number: 1003
*/
