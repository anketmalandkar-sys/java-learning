import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Example 1: chars, code points, graphemes and bytes.
 *   1. The same "one character" counted four ways.
 *   2. Code point APIs: codePointAt, Character.getName, isLetter(int).
 *   3. Surrogate pairs: what charAt returns inside an emoji.
 *   4. Safe vs unsafe substring.
 *
 * Non-ASCII characters are built from their code point numbers and printed as U+XXXX,
 * so this file and its output are plain ASCII and look the same in any console.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static final String E_ACUTE = Character.toString(0x00E9);            // e with acute accent, one code point
    static final String RUPEE = Character.toString(0x20B9);              // Indian rupee sign
    static final String GRINNING = Character.toString(0x1F600);          // grinning face emoji, above U+FFFF
    static final String INDIA_FLAG = Character.toString(0x1F1EE) + Character.toString(0x1F1F3);
    static final String E_COMBINING = "e" + (char) 0x0301;               // e + combining acute accent

    public static void main(String[] args) {
        System.out.println("--- 1. Four ways to count ---");
        System.out.println("text                 chars  codepoints  graphemes  utf8-bytes");
        row("A", "A");
        row("e-acute (U+00E9)", E_ACUTE);
        row("rupee (U+20B9)", RUPEE);
        row("grinning (U+1F600)", GRINNING);
        row("India flag", INDIA_FLAG);
        row("e + U+0301", E_COMBINING);
        row("\"Ravi\" + grinning", "Ravi" + GRINNING);

        System.out.println();
        System.out.println("--- 2. Code point APIs ---");
        String name = "Ravi" + GRINNING;
        int lastCodePoint = name.codePointAt(4);
        System.out.println("codePointAt(4): " + describe(lastCodePoint) + " " + Character.getName(lastCodePoint));
        System.out.println("isLetter('R'): " + Character.isLetter(name.codePointAt(0))
                + ", isLetter(emoji): " + Character.isLetter(lastCodePoint));
        System.out.print("codePoints():  ");
        name.codePoints().forEach(cp -> System.out.print(describe(cp) + " "));
        System.out.println(" <- one per character");
        System.out.print("chars():       ");
        name.chars().forEach(ch -> System.out.print(describe(ch) + " "));
        System.out.println("  <- the emoji shows up as 2 surrogates");

        System.out.println();
        System.out.println("--- 3. Surrogate pairs ---");
        char high = GRINNING.charAt(0);
        char low = GRINNING.charAt(1);
        System.out.println("charAt(0) = " + describe(high) + ", high surrogate? " + Character.isHighSurrogate(high));
        System.out.println("charAt(1) = " + describe(low) + ", low surrogate?  " + Character.isLowSurrogate(low));
        System.out.println("together they are " + describe(Character.toCodePoint(high, low)));

        System.out.println();
        System.out.println("--- 4. Safe substring ---");
        String status = "Hi" + GRINNING + GRINNING;
        String unsafe = status.substring(0, 3);                                // cuts the first emoji in half
        String safe = status.substring(0, status.offsetByCodePoints(0, 3));    // first 3 code points
        System.out.println("substring(0, 3):                 " + show(unsafe) + " <- a lone surrogate");
        System.out.println("substring(0, offsetByCodePoints): " + show(safe));
    }

    static void row(String label, String text) {
        long graphemes = Pattern.compile("\\X").matcher(text).results().count();
        System.out.printf("%-20s %5d %11d %10d %11d%n", label, text.length(),
                text.codePointCount(0, text.length()), graphemes,
                text.getBytes(StandardCharsets.UTF_8).length);
    }

    static String describe(int codePoint) {
        return String.format("U+%04X", codePoint);
    }

    // ASCII stays as it is; everything else is shown as U+XXXX (per code point).
    static String show(String text) {
        StringBuilder out = new StringBuilder();
        text.codePoints().forEach(cp -> out.append(cp < 128 ? Character.toString(cp) : "[" + describe(cp) + "]"));
        return out.toString();
    }
}

/* Expected output:
--- 1. Four ways to count ---
text                 chars  codepoints  graphemes  utf8-bytes
A                        1           1          1           1
e-acute (U+00E9)         1           1          1           2
rupee (U+20B9)           1           1          1           3
grinning (U+1F600)       2           1          1           4
India flag               4           2          1           8
e + U+0301               2           2          1           3
"Ravi" + grinning        6           5          5           8

--- 2. Code point APIs ---
codePointAt(4): U+1F600 GRINNING FACE
isLetter('R'): true, isLetter(emoji): false
codePoints():  U+0052 U+0061 U+0076 U+0069 U+1F600  <- one per character
chars():       U+0052 U+0061 U+0076 U+0069 U+D83D U+DE00   <- the emoji shows up as 2 surrogates

--- 3. Surrogate pairs ---
charAt(0) = U+D83D, high surrogate? true
charAt(1) = U+DE00, low surrogate?  true
together they are U+1F600

--- 4. Safe substring ---
substring(0, 3):                 Hi[U+D83D] <- a lone surrogate
substring(0, offsetByCodePoints): Hi[U+1F600]
*/
