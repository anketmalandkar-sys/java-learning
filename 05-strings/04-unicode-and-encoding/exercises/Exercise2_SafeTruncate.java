import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): cutting text without breaking characters.
 *
 * TASK
 *   A notification service shows previews of chat messages and stores them in a database
 *   column limited by BYTES. Messages are full of emoji. Implement:
 *
 *     preview(text, maxCodePoints)   if text has at most maxCodePoints code points, return it
 *                                    unchanged. Otherwise return its first maxCodePoints code
 *                                    points followed by "..." (the dots don't count).
 *                                    It must NEVER cut an emoji in half.
 *
 *     fitUtf8(text, maxBytes)        the longest start of text, made of WHOLE code points,
 *                                    whose UTF-8 encoding is at most maxBytes bytes.
 *
 *     initial(name)                  the first code point of the stripped name, upper-cased,
 *                                    as a String (for an avatar circle). "" for a blank name.
 *                                    " ravi" -> "R";  a name starting with an emoji -> that emoji.
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   preview: PASS
 *   fitUtf8: PASS
 *   initial: PASS
 *   ALL PASS
 *
 * HINTS
 *   - text.offsetByCodePoints(0, n) is the char index where the (n+1)th code point starts:
 *     substring(0, that) keeps exactly n whole code points.
 *   - fitUtf8: walk text.codePoints(), adding each one's UTF-8 size until the next would overflow.
 *     StringBuilder.appendCodePoint adds a whole code point.
 *   - initial: Character.toString(name.strip().codePointAt(0)), then toUpperCase(Locale.ROOT).
 *
 * Run: java exercises/Exercise2_SafeTruncate.java
 */
public class Exercise2_SafeTruncate {

    static final String SMILE = Character.toString(0x1F60A);      // 4 bytes in UTF-8
    static final String RUPEE = Character.toString(0x20B9);       // 3 bytes in UTF-8
    static final String E_ACUTE = Character.toString(0x00E9);     // 2 bytes in UTF-8

    static String preview(String text, int maxCodePoints) {
        // TODO
        return null;
    }

    static String fitUtf8(String text, int maxBytes) {
        // TODO
        return null;
    }

    static String initial(String name) {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("preview", () -> {
            String message = "Hi" + SMILE + SMILE + " see you";
            return preview(message, 3).equals("Hi" + SMILE + "...")     // the cut lands right after an emoji
                    && preview(message, 4).equals("Hi" + SMILE + SMILE + "...")
                    && preview("short", 10).equals("short")
                    && preview("exact", 5).equals("exact");
        });

        allPass &= check("fitUtf8", () -> {
            String price = "Rs" + RUPEE + "99";                       // 2 + 3 + 2 = 7 bytes
            return fitUtf8(price, 7).equals(price)
                    && fitUtf8(price, 4).equals("Rs")                 // the rupee needs 3 more bytes: left out
                    && fitUtf8(price, 5).equals("Rs" + RUPEE)
                    && fitUtf8("caf" + E_ACUTE, 4).equals("caf")
                    && fitUtf8(SMILE + SMILE, 7).equals(SMILE)
                    && fitUtf8("abc", 0).equals("");
        });

        allPass &= check("initial", () ->
                initial(" ravi kumar").equals("R")
                        && initial(E_ACUTE + "lodie").equals(Character.toString(0x00C9))   // upper-case E with acute
                        && initial(SMILE + " team").equals(SMILE)
                        && initial("   ").equals(""));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-8s FAIL (still returning null?)%n", name + ":");
            return false;
        } catch (RuntimeException e) {
            System.out.printf("%-8s FAIL (%s)%n", name + ":", e);
            return false;
        }
        System.out.printf("%-8s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
