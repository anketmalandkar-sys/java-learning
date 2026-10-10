package oops.encapsulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * ============================================================================
 *  ENCAPSULATION — "bundle data with the code that guards it, and hide it"
 * ============================================================================
 *
 *  1. Make fields PRIVATE so nobody outside can change them directly.
 *  2. Expose only the operations that make sense (deposit, withdraw...),
 *     and VALIDATE inside them.
 *  3. The object is therefore always in a valid state — this guarantee is
 *     called an INVARIANT. Here: "balance is never negative".
 *
 *  Why it matters: if `balance` were public, any code anywhere could write
 *  `account.balance = -1_000_000;` and the bug would be impossible to track.
 *  With encapsulation there is exactly ONE place that changes the balance.
 *
 *  Note: "getters and setters for every field" is NOT automatically
 *  encapsulation. A setter like setBalance(double) would expose the field
 *  just as badly. Expose BEHAVIOUR (deposit/withdraw), not raw data.
 */
public class BankAccount {

    // private -> visible only inside this class
    private final String accountNumber;   // final -> can never be reassigned
    private final String owner;
    private double balance;
    private final List<String> history = new ArrayList<>();

    public BankAccount(String accountNumber, String owner, double openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }
        this.accountNumber = accountNumber;
        this.owner = owner;
        this.balance = openingBalance;
        history.add("OPEN " + openingBalance);
    }

    // ---- Read-only access (getters) ----------------------------------------
    public String getAccountNumber() { return accountNumber; }
    public String getOwner()         { return owner; }
    public double getBalance()       { return balance; }

    /*
     * Don't leak the internal mutable list! If we returned `history` itself,
     * a caller could do getHistory().clear() and wipe our records.
     * Return an unmodifiable VIEW instead (a "defensive" approach).
     */
    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }

    // ---- Behaviour with validation -----------------------------------------
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit must be positive");
        }
        balance += amount;
        history.add("DEPOSIT " + amount);
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal must be positive");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance -= amount;
        history.add("WITHDRAW " + amount);
    }

    // private helper — an implementation detail callers never see
    private String mask(String number) {
        return "****" + number.substring(number.length() - 4);
    }

    @Override
    public String toString() {
        return owner + " [" + mask(accountNumber) + "] balance=" + balance;
    }
}
