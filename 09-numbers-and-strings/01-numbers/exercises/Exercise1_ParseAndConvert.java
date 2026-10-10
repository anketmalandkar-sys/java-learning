/**
 * Exercise 1 (Easy): parsing and converting with the wrapper classes.
 *
 * TASK
 *   Use wrapper-class methods (Integer.parseInt, Integer.toBinaryString, ...), not your own loops.
 *
 *     parseOrDefault(text, fallback)   the int in text (ignore spaces around it), or fallback if
 *                                      text is null or not a whole number
 *                                      parseOrDefault(" 42 ", 0) -> 42, parseOrDefault("4.5", -1) -> -1
 *     toBinary(n)                      n in base 2 as a String              toBinary(10) -> "1010"
 *     fromHex(text)                    a hex string like "ff" or "1A" to an int    fromHex("ff") -> 255
 *     colorToHex(r, g, b)              "#RRGGBB" with two lowercase hex digits each
 *                                      colorToHex(255, 136, 0) -> "#ff8800", colorToHex(0, 0, 10) -> "#00000a"
 *
 * EXPECTED OUTPUT
 *   parseOrDefault: PASS
 *   toBinary:       PASS
 *   fromHex:        PASS
 *   colorToHex:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - parseInt throws NumberFormatException for bad input. Catch it.
 *   - Integer.parseInt has an overload with a radix.
 *   - String.format("%02x", value) gives two lowercase hex digits.
 *
 * Run: java exercises/Exercise1_ParseAndConvert.java
 */
public class Exercise1_ParseAndConvert {

    static int parseOrDefault(String text, int fallback) {
        return 0; // TODO
    }

    static String toBinary(int n) {
        return ""; // TODO
    }

    static int fromHex(String text) {
        return 0; // TODO
    }

    static String colorToHex(int r, int g, int b) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("parseOrDefault:", parseOrDefault(" 42 ", 0) == 42 && parseOrDefault("-7", 0) == -7
                && parseOrDefault("4.5", -1) == -1 && parseOrDefault("abc", -1) == -1
                && parseOrDefault("", 9) == 9 && parseOrDefault(null, 9) == 9);
        allPass &= check("toBinary:", "1010".equals(toBinary(10)) && "0".equals(toBinary(0))
                && "11111111".equals(toBinary(255)));
        allPass &= check("fromHex:", fromHex("ff") == 255 && fromHex("1A") == 26 && fromHex("0") == 0);
        allPass &= check("colorToHex:", "#ff8800".equals(colorToHex(255, 136, 0))
                && "#00000a".equals(colorToHex(0, 0, 10)));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
