import java.nio.charset.StandardCharsets;

/**
 * Exercise 3 (Hard): a sign-up service that doesn't understand Unicode.
 *
 * THE SITUATION
 *   SignupService has three rules, and support tickets about all three:
 *     1. "Usernames must be 3 to 12 characters, and an emoji counts as one character."
 *        Ticket: "My username is Priya plus 6 emoji, which is 11 characters. It says it's too long!"
 *     2. "The mobile app sends the display name as UTF-8 bytes."
 *        Ticket: "My profile says JosA~(c) instead of my name."
 *     3. "Display names are unique, ignoring case."
 *        Ticket: "I registered as 'jose' with an accent from my Mac. My friend registered
 *         'JOSE' with an accent from Windows. The system says these are different names."
 *
 * TASK
 *   1. Run it and read the failures (non-ASCII is printed as U+XXXX).
 *   2. Find the bug behind each ticket. All three are in SignupService.
 *      Add a comment by each fix explaining what went wrong.
 *   3. Fix them. Don't change main or the test data.
 *
 * EXPECTED OUTPUT (after the fix)
 *   length:  PASS
 *   decode:  PASS
 *   unique:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - What does length() count?
 *   - Decoding with the wrong charset never throws. It just produces different characters.
 *   - A Mac often sends an accented letter as the plain letter followed by a combining accent
 *     (U+0301). java.text.Normalizer can turn both forms into the same one.
 *
 * Run: java exercises/Exercise3_UsernameBug.java
 */
public class Exercise3_UsernameBug {

    static class SignupService {

        static boolean validLength(String username) {
            int length = username.length();
            return length >= 3 && length <= 12;
        }

        static String decodeDisplayName(byte[] requestBody) {
            return new String(requestBody, StandardCharsets.ISO_8859_1);
        }

        static boolean sameDisplayName(String a, String b) {
            return a.equalsIgnoreCase(b);
        }
    }

    // ---- Don't change anything below this line. ----

    static final String SMILE = Character.toString(0x1F60A);
    static final String E_ACUTE = Character.toString(0x00E9);           // precomposed (Windows, most apps)
    static final String E_ACUTE_UPPER = Character.toString(0x00C9);
    static final String COMBINING_ACUTE = String.valueOf((char) 0x0301);  // e + this = accented e (Mac)

    public static void main(String[] args) {
        String priya = "Priya" + SMILE.repeat(6);                 // 11 characters to a person
        String tooLong = "abcdefghijk" + SMILE.repeat(2);         // 13
        boolean length = SignupService.validLength(priya)
                && SignupService.validLength("abc")
                && !SignupService.validLength("ab")
                && !SignupService.validLength(tooLong);
        System.out.println("length:  " + (length ? "PASS" : "FAIL (Priya + 6 emoji should be valid)"));

        String jose = "Jos" + E_ACUTE;
        byte[] fromApp = jose.getBytes(StandardCharsets.UTF_8);
        String decoded = SignupService.decodeDisplayName(fromApp);
        boolean decode = decoded.equals(jose);
        System.out.println("decode:  " + (decode ? "PASS" : "FAIL (got " + show(decoded) + ")"));

        String fromMac = "jos" + "e" + COMBINING_ACUTE;
        String fromWindows = "JOS" + E_ACUTE_UPPER;
        boolean unique = SignupService.sameDisplayName(fromMac, fromWindows)
                && SignupService.sameDisplayName("Ravi", "rAVI")
                && !SignupService.sameDisplayName("Ravi", "Ravi2");
        System.out.println("unique:  " + (unique ? "PASS" : "FAIL (" + show(fromMac) + " vs " + show(fromWindows) + ")"));

        System.out.println(length && decode && unique ? "ALL PASS" : "SOME FAILED");
    }

    static String show(String text) {
        StringBuilder out = new StringBuilder();
        text.codePoints().forEach(cp -> out.append(cp < 128 ? Character.toString(cp) : String.format("[U+%04X]", cp)));
        return out.toString();
    }
}
