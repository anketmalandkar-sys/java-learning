import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;

/**
 * Example 2: customer names on their way to and from a database.
 *   1. Encoding: how many bytes a name takes in UTF-8 vs UTF-16.
 *   2. Mojibake: decoding UTF-8 bytes with the wrong charset (no exception, just garbage).
 *   3. Normalization: two "Jose"s with an accent that aren't equals until normalized.
 *   4. Fitting a name into a byte-limited column without splitting a character.
 *
 * Non-ASCII text is printed as U+XXXX codes so the output is the same in any console.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    static final String ACUTE = Character.toString(0x00E9);              // e with acute, precomposed
    static final String COMBINING_ACUTE = String.valueOf((char) 0x0301);   // accent that attaches to the letter before

    public static void main(String[] args) {
        String jose = "Jos" + ACUTE;                              // "Jose" with an accent, NFC form

        System.out.println("--- 1. Bytes per encoding ---");
        System.out.println("default charset: " + Charset.defaultCharset() + " (UTF-8 by default since Java 18)");
        for (String name : new String[] {"Ravi", jose, "Priya " + Character.toString(0x1F60A)}) {
            System.out.printf("%-22s chars=%d  UTF-8=%d bytes  UTF-16=%d bytes%n", show(name), name.length(),
                    name.getBytes(StandardCharsets.UTF_8).length,
                    name.getBytes(StandardCharsets.UTF_16BE).length);
        }

        System.out.println();
        System.out.println("--- 2. Decoding with the wrong charset ---");
        byte[] fromDatabase = jose.getBytes(StandardCharsets.UTF_8);          // stored as UTF-8
        String right = new String(fromDatabase, StandardCharsets.UTF_8);
        String wrong = new String(fromDatabase, StandardCharsets.ISO_8859_1); // read as Latin-1: no error!
        System.out.println("bytes:        " + hex(fromDatabase));
        System.out.println("as UTF-8:     " + show(right) + "  equals original? " + right.equals(jose));
        System.out.println("as ISO-8859-1: " + show(wrong) + "  <- mojibake, shown on screen as JosA~(c)");

        System.out.println();
        System.out.println("--- 3. Normalization ---");
        String typedOnMac = "Jos" + "e" + COMBINING_ACUTE;          // NFD: e + combining accent
        System.out.println("NFC: " + show(jose) + "  NFD: " + show(typedOnMac));
        System.out.println("equals:           " + jose.equals(typedOnMac));
        System.out.println("equalsIgnoreCase: " + jose.equalsIgnoreCase(typedOnMac));
        String a = Normalizer.normalize(jose, Normalizer.Form.NFC);
        String b = Normalizer.normalize(typedOnMac, Normalizer.Form.NFC);
        System.out.println("after NFC:        " + a.equals(b));

        System.out.println();
        System.out.println("--- 4. Fit into a 6-byte column ---");
        String status = "OK " + Character.toString(0x1F44D) + Character.toString(0x1F44D);   // OK + 2 thumbs up
        System.out.println("full:      " + show(status) + " = " + status.getBytes(StandardCharsets.UTF_8).length + " bytes");
        System.out.println("bytes cut: " + show(new String(status.getBytes(StandardCharsets.UTF_8), 0, 6, StandardCharsets.UTF_8))
                + "  <- half an emoji became U+FFFD (replacement char)");
        System.out.println("safe cut:  [" + show(fitBytes(status, 6)) + "]  <- a 4-byte emoji does not fit in the 3 bytes left, so it is left out whole");
    }

    // Longest prefix of whole code points whose UTF-8 form fits in maxBytes.
    static String fitBytes(String text, int maxBytes) {
        StringBuilder out = new StringBuilder();
        int used = 0;
        for (int cp : text.codePoints().toArray()) {
            int size = Character.toString(cp).getBytes(StandardCharsets.UTF_8).length;
            if (used + size > maxBytes) {
                break;
            }
            out.appendCodePoint(cp);
            used += size;
        }
        return out.toString();
    }

    static String show(String text) {
        StringBuilder out = new StringBuilder();
        text.codePoints().forEach(cp -> out.append(cp < 128 ? Character.toString(cp) : String.format("[U+%04X]", cp)));
        return out.toString();
    }

    static String hex(byte[] bytes) {
        StringBuilder out = new StringBuilder();
        for (byte b : bytes) {
            out.append(String.format("%02X ", b));
        }
        return out.toString().strip();
    }
}

/* Expected output:
--- 1. Bytes per encoding ---
default charset: UTF-8 (UTF-8 by default since Java 18)
Ravi                   chars=4  UTF-8=4 bytes  UTF-16=8 bytes
Jos[U+00E9]            chars=4  UTF-8=5 bytes  UTF-16=8 bytes
Priya [U+1F60A]        chars=8  UTF-8=10 bytes  UTF-16=16 bytes

--- 2. Decoding with the wrong charset ---
bytes:        4A 6F 73 C3 A9
as UTF-8:     Jos[U+00E9]  equals original? true
as ISO-8859-1: Jos[U+00C3][U+00A9]  <- mojibake, shown on screen as JosA~(c)

--- 3. Normalization ---
NFC: Jos[U+00E9]  NFD: Jose[U+0301]
equals:           false
equalsIgnoreCase: false
after NFC:        true

--- 4. Fit into a 6-byte column ---
full:      OK [U+1F44D][U+1F44D] = 11 bytes
bytes cut: OK [U+FFFD]  <- half an emoji became U+FFFD (replacement char)
safe cut:  [OK ]  <- a 4-byte emoji does not fit in the 3 bytes left, so it is left out whole
*/
