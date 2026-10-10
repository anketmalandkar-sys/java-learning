/**
 * Exercise 4 (Medium): Character helpers and escapes.
 *
 * TASK
 *   1. checkPassword(pw) returns a list of the rules that FAIL, in this order, joined by ", "
 *      (empty string if all pass). Use Character helper methods, not regex.
 *        "too short"       fewer than 8 characters
 *        "no uppercase"    no uppercase letter
 *        "no lowercase"    no lowercase letter
 *        "no digit"        no digit
 *        "no symbol"       no character that is neither a letter, a digit, nor whitespace
 *        "has space"       contains any whitespace (space, tab, newline, ...)
 *      checkPassword("abc")         -> "too short, no uppercase, no digit, no symbol"
 *      checkPassword("Secret#2024") -> ""
 *
 *   2. digitSum(text): the sum of the VALUES of all digits in the text.
 *        digitSum("a1b2c3") -> 6     (not 49 + 50 + 51)
 *
 *   3. Return these exact strings by writing string literals with escape sequences
 *      (no concatenation with variables, no String.format):
 *        windowsPath()   C:\Users\asha\notes.txt
 *        quoted()        She said "it's fine"
 *        twoColumns()    the word Name, a TAB, the word Asha, a NEWLINE, the word Age, a TAB, 30
 *
 * EXPECTED OUTPUT
 *   checkPassword: PASS
 *   digitSum:      PASS
 *   windowsPath:   PASS
 *   quoted:        PASS
 *   twoColumns:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - Character.isLetterOrDigit and Character.isWhitespace together define "symbol".
 *   - Character.getNumericValue('3') is 3.
 *   - String.join(", ", list) joins with a separator.
 *
 * Run: java exercises/Exercise4_PasswordRules.java
 */
public class Exercise4_PasswordRules {

    static String checkPassword(String pw) {
        return "TODO"; // TODO
    }

    static int digitSum(String text) {
        return 0; // TODO
    }

    static String windowsPath() {
        return ""; // TODO
    }

    static String quoted() {
        return ""; // TODO
    }

    static String twoColumns() {
        return ""; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("checkPassword:", "too short, no uppercase, no digit, no symbol".equals(checkPassword("abc"))
                && "".equals(checkPassword("Secret#2024"))
                && "no symbol, has space".equals(checkPassword("Secret 2024"))
                && "no lowercase".equals(checkPassword("SECRET#2024"))
                && "has space".equals(checkPassword("Secret#2024\t")));
        allPass &= check("digitSum:", digitSum("a1b2c3") == 6 && digitSum("none") == 0 && digitSum("99") == 18);
        allPass &= check("windowsPath:", windowsPath().length() == 23 && windowsPath().startsWith("C:")
                && windowsPath().chars().filter(c -> c == 92).count() == 3 && windowsPath().endsWith("notes.txt"));
        allPass &= check("quoted:", quoted().length() == 20 && quoted().charAt(9) == 34
                && quoted().charAt(12) == 39 && quoted().charAt(19) == 34);
        allPass &= check("twoColumns:", twoColumns().length() == 16 && twoColumns().indexOf(9) == 4
                && twoColumns().indexOf(10) == 9 && twoColumns().lastIndexOf(9) == 13);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-14s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
