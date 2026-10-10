package oops.basics;

import java.util.Arrays;

/*
 * ============================================================================
 *  PARAMETERS, ARGUMENTS AND OBJECT LIFETIME
 * ============================================================================
 *
 *  Source: https://dev.java/learn/language/oop/classes-objects/calling-methods-constructors
 *  Source: https://dev.java/learn/language/oop/classes-objects/defining-constructors
 *  Source: https://dev.java/learn/language/oop/classes-objects/creating-objects
 *
 *  Fills the gaps next to ClassesAndObjects.java:
 *
 *  1. PARAMETER vs ARGUMENT: the parameter is the variable in the declaration
 *     (`int qty`), the argument is the value passed at the call (`3`).
 *
 *  2. VARARGS: `int... values` accepts zero or more ints. Inside the method it
 *     is just an int[]. It must be the LAST parameter, and there can be only one.
 *
 *  3. JAVA IS ALWAYS PASS-BY-VALUE.
 *       - primitive argument: the method gets a copy; changing it does nothing
 *         to the caller's variable.
 *       - reference argument: the method gets a copy of the REFERENCE. Both
 *         point at the same object, so changing the object's fields IS visible
 *         to the caller. Reassigning the parameter to a new object is NOT.
 *
 *  4. SHADOWING: a parameter with the same name as a field hides the field.
 *     Use `this.field` to reach it. Keep this to constructors and setters.
 *
 *  5. CONSTRUCTOR ACCESS: a `private` constructor stops other classes from
 *     calling `new`. Combine it with a static factory method to validate,
 *     cache, or give creation a meaningful name.
 *
 *  6. OBJECT LIFETIME: an object becomes eligible for garbage collection when
 *     no live reference points to it any more: the variable went out of scope,
 *     or was set to null / to another object. The GC reclaims it later, at a
 *     time you don't control. There is no "delete".
 */
class Wallet {
    int balance;

    Wallet(int balance) {
        this.balance = balance;
    }

    // Shadowing: the parameter `balance` hides the field.
    void reset(int balance) {
        balance = 0;               // changes only the PARAMETER (a common bug)
    }

    void resetProperly(int balance) {
        this.balance = balance;    // `this.balance` is the field
    }
}

final class Temperature {
    private final double celsius;

    // private: nobody outside this class can call `new Temperature(...)`.
    private Temperature(double celsius) {
        this.celsius = celsius;
    }

    // Static factories with meaningful names, both validated in one place.
    static Temperature ofCelsius(double c) {
        if (c < -273.15) {
            throw new IllegalArgumentException("below absolute zero: " + c);
        }
        return new Temperature(c);
    }

    static Temperature ofFahrenheit(double f) {
        return ofCelsius((f - 32) * 5 / 9);
    }

    double celsius() {
        return celsius;
    }
}

public class ParametersAndObjects {

    // Varargs: zero or more ints. `values` is an int[] inside the method.
    static int sum(int... values) {
        int total = 0;
        for (int v : values) {
            total += v;
        }
        return total;
    }

    // A required first parameter plus varargs: "at least one value".
    static int max(int first, int... rest) {
        int best = first;
        for (int v : rest) {
            best = Math.max(best, v);
        }
        return best;
    }

    static void tryToChange(int number) {
        number = 99;                      // the caller's variable is untouched
    }

    static void deposit(Wallet wallet, int amount) {
        wallet.balance += amount;         // same object as the caller's: visible
    }

    static void replace(Wallet wallet) {
        wallet = new Wallet(1_000_000);   // only the local copy of the reference changes
    }

    public static void main(String[] args) {
        System.out.println("=== Varargs ===");
        System.out.println("sum()            = " + sum());
        System.out.println("sum(4, 5, 6)     = " + sum(4, 5, 6));
        System.out.println("sum(new int[]{10, 20}) = " + sum(new int[] {10, 20}));
        System.out.println("max(7)           = " + max(7));
        System.out.println("max(7, 2, 9, 1)  = " + max(7, 2, 9, 1));
        // String.format and printf are varargs methods too.
        System.out.println(String.format("%s has %d items", "cart", 3));

        System.out.println("\n=== Pass-by-value ===");
        int n = 5;
        tryToChange(n);
        System.out.println("primitive after tryToChange: " + n);

        Wallet w = new Wallet(100);
        deposit(w, 50);
        System.out.println("after deposit (object changed): " + w.balance);
        replace(w);
        System.out.println("after replace (reference NOT changed): " + w.balance);

        int[] scores = {1, 2, 3};
        Arrays.fill(scores, 0);   // arrays are objects: the method changes the caller's array
        System.out.println("array after Arrays.fill: " + Arrays.toString(scores));

        System.out.println("\n=== Shadowing ===");
        w.reset(0);
        System.out.println("after reset(0) (shadowing bug): " + w.balance);
        w.resetProperly(0);
        System.out.println("after resetProperly(0): " + w.balance);

        System.out.println("\n=== Private constructor + static factory ===");
        // Temperature t = new Temperature(20);   // COMPILE ERROR: constructor is private
        System.out.printf("ofFahrenheit(212) = %.1f C%n", Temperature.ofFahrenheit(212).celsius());
        try {
            Temperature.ofCelsius(-300);
        } catch (IllegalArgumentException e) {
            System.out.println("rejected: " + e.getMessage());
        }

        System.out.println("\n=== Object lifetime ===");
        Wallet first = new Wallet(1);
        Wallet second = first;      // two references, one object
        first = null;               // the object is still reachable through `second`
        System.out.println("still reachable via second: " + second.balance);
        second = new Wallet(2);     // now nothing points at Wallet(1): eligible for GC
        System.out.println("Wallet(1) is now unreachable; the GC may reclaim it whenever it likes.");
        {
            Wallet temp = new Wallet(3);
            System.out.println("temp in scope: " + temp.balance);
        }
        // `temp` is out of scope here, so its Wallet is unreachable too.
    }
}
