import java.util.function.Supplier;

/**
 * Exercise 1 (Easy): counting characters properly.
 *
 * TASK
 *   Implement the four methods.
 *
 *     codePointLength(text)   the number of code points (an emoji counts as 1)
 *     utf8Size(text)          the number of bytes text takes in UTF-8
 *     isAscii(text)           true if every character is below 128 ("" counts as ASCII)
 *     describe(text)          every code point as "U+XXXX" (upper-case hex, at least 4 digits),
 *                             separated by single spaces.
 *                             "H" + rupee + grinning face  ->  "U+0048 U+20B9 U+1F600"
 *
 *   The test strings are built from code point numbers, e.g. Character.toString(0x1F600),
 *   so this file is plain ASCII.
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   codePointLength: PASS
 *   utf8Size:        PASS
 *   isAscii:         PASS
 *   describe:        PASS
 *   ALL PASS
 *
 * HINTS
 *   - codePointCount(0, length()), or codePoints().count().
 *   - getBytes(StandardCharsets.UTF_8).length.
 *   - String.format("U+%04X", codePoint); join with String.join or a StringBuilder.
 *
 * Run: java exercises/Exercise1_CountingCharacters.java
 */
public class Exercise1_CountingCharacters {

    static final String RUPEE = Character.toString(0x20B9);
    static final String GRINNING = Character.toString(0x1F600);
    static final String E_ACUTE = Character.toString(0x00E9);

    static int codePointLength(String text) {
        // TODO
        return -1;
    }

    static int utf8Size(String text) {
        // TODO
        return -1;
    }

    static boolean isAscii(String text) {
        // TODO
        return false;
    }

    static String describe(String text) {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("codePointLength", () ->
                codePointLength("Ravi") == 4
                        && codePointLength("Ravi" + GRINNING) == 5
                        && codePointLength(GRINNING + GRINNING + GRINNING) == 3
                        && codePointLength("") == 0);

        allPass &= check("utf8Size", () ->
                utf8Size("Ravi") == 4
                        && utf8Size("Jos" + E_ACUTE) == 5
                        && utf8Size("100" + RUPEE) == 6
                        && utf8Size(GRINNING) == 4);

        allPass &= check("isAscii", () ->
                isAscii("Hello, World! 123")
                        && isAscii("")
                        && !isAscii("Jos" + E_ACUTE)
                        && !isAscii("ok" + GRINNING));

        allPass &= check("describe", () ->
                describe("H" + RUPEE + GRINNING).equals("U+0048 U+20B9 U+1F600")
                        && describe("A").equals("U+0041")
                        && describe("").equals(""));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-16s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-16s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
