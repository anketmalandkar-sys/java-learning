/**
 * Exercise 1 (Easy): braces and the empty statement.
 *
 * TASK
 *   A login system logs and counts events. Two methods have block bugs. The indentation shows
 *   what the author MEANT; the code does something else. Fix both by adding/removing braces
 *   and semicolons only.
 *
 *     login(user, password)     only for a correct password: count a success AND reset failures.
 *                               For a wrong password: count a failure.
 *     checkLockout()            only when failures reach 3: lock the account.
 *
 * EXPECTED OUTPUT
 *   good login:     PASS
 *   bad login:      PASS
 *   lockout at 3:   PASS
 *   no early lock:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - Without braces, only ONE statement belongs to the if.
 *   - "if (cond);" ends the if right there.
 *   - Some checks may pass by accident before both bugs are fixed. Fix both anyway.
 *   - Bonus: once it works, rewrite login with if/else and braces.
 *
 * Run: java exercises/Exercise1_MissingBraces.java
 */
public class Exercise1_MissingBraces {

    static final String PASSWORD = "s3cret";
    static int successes = 0;
    static int failures = 0;
    static boolean locked = false;

    // TODO: fix the block bug
    static void login(String user, String password) {
        if (password.equals(PASSWORD))
            successes++;
            failures = 0;
        if (!password.equals(PASSWORD))
            failures++;
    }

    // TODO: fix the block bug
    static void checkLockout() {
        if (failures >= 3);
        {
            locked = true;
        }
    }

    static void reset() {
        successes = 0;
        failures = 0;
        locked = false;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        reset();
        login("asha", "s3cret");
        allPass &= check("good login:", successes == 1 && failures == 0);

        reset();
        login("asha", "oops");
        allPass &= check("bad login:", successes == 0 && failures == 1);

        reset();
        login("asha", "x");
        login("asha", "y");
        login("asha", "z");
        checkLockout();
        allPass &= check("lockout at 3:", locked);

        reset();
        login("asha", "x");
        checkLockout();
        allPass &= check("no early lock:", !locked);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
