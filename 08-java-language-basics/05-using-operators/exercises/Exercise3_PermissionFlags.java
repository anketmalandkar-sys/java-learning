/**
 * Exercise 3 (Hard): permissions as bit flags.
 *
 * TASK
 *   A file's permissions are stored in ONE int, one bit per permission:
 *     READ = 1 (bit 0), WRITE = 2 (bit 1), EXECUTE = 4 (bit 2)
 *
 *   1. Define the three constants using the shift operator (1 << n), not the plain numbers.
 *   2. Write, using only bitwise operators (& | ^ ~ << >>) and comparisons:
 *        grant(perms, flag)     perms with flag turned ON         grant(READ, WRITE)          -> 3
 *        revoke(perms, flag)    perms with flag turned OFF        revoke(7, WRITE)            -> 5
 *        toggle(perms, flag)    flips flag                        toggle(5, READ)             -> 4
 *        has(perms, flag)       true if every bit of flag is on   has(5, READ | EXECUTE)      -> true
 *        format(perms)          "rwx" style, "-" for missing       format(5)                   -> "r-x"
 *        parse(text)            the reverse of format              parse("rw-")                -> 3
 *
 * EXPECTED OUTPUT
 *   constants: PASS
 *   grant:     PASS
 *   revoke:    PASS
 *   toggle:    PASS
 *   has:       PASS
 *   format:    PASS
 *   parse:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - Turning a bit OFF: AND with everything EXCEPT that bit. Which operator gives "everything except"?
 *   - has() with a multi-bit flag: (perms & flag) must equal flag, not just be non-zero.
 *   - In parse, charAt(i) == '-' tells you a bit is missing.
 *
 * Run: java exercises/Exercise3_PermissionFlags.java
 */
public class Exercise3_PermissionFlags {

    static final int READ = 0;      // TODO: use 1 << n
    static final int WRITE = 0;     // TODO
    static final int EXECUTE = 0;   // TODO

    static int grant(int perms, int flag) {
        return perms; // TODO
    }

    static int revoke(int perms, int flag) {
        return perms; // TODO
    }

    static int toggle(int perms, int flag) {
        return perms; // TODO
    }

    static boolean has(int perms, int flag) {
        return false; // TODO
    }

    static String format(int perms) {
        return "---"; // TODO
    }

    static int parse(String text) {
        return 0; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("constants:", READ == 1 && WRITE == 2 && EXECUTE == 4);
        allPass &= check("grant:", grant(1, 2) == 3 && grant(3, 2) == 3 && grant(0, 7) == 7);
        allPass &= check("revoke:", revoke(7, 2) == 5 && revoke(5, 2) == 5 && revoke(7, 7) == 0);
        allPass &= check("toggle:", toggle(5, 1) == 4 && toggle(4, 1) == 5);
        allPass &= check("has:", has(5, 1 | 4) && !has(5, 2) && !has(1, 1 | 2) && has(7, 7));
        allPass &= check("format:", "r-x".equals(format(5)) && "rwx".equals(format(7))
                && "---".equals(format(0)) && "-w-".equals(format(2)));
        allPass &= check("parse:", parse("rw-") == 3 && parse("---") == 0 && parse("r-x") == 5
                && parse(format(6)) == 6);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-10s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
