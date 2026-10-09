import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 1 (Easy): StringBuilder basics.
 *
 * TASK
 *   Implement the four methods with a StringBuilder. Don't use += on a String inside a loop,
 *   and don't use String.join (that's for later).
 *
 *     reverseWords(sentence)   the words in reverse order, separated by single spaces.
 *                              "the quick brown fox" -> "fox brown quick the"
 *
 *     csvRow(fields)           the fields joined by commas, with NO trailing comma.
 *                              ["A101", "Ravi", "2499.5"] -> "A101,Ravi,2499.5"; [] -> ""
 *
 *     progressBar(percent)     a 10-character bar: one '#' per full 10%, '-' for the rest,
 *                              then a space and the percentage.
 *                              45 -> "[####------] 45%",  100 -> "[##########] 100%"
 *
 *     compress(text)           runs of the same letter become letter + count; single letters
 *                              stay as they are.
 *                              "aaabccdddd" -> "a3bc2d4",  "abc" -> "abc",  "" -> ""
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   reverseWords: PASS
 *   csvRow:       PASS
 *   progressBar:  PASS
 *   compress:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - reverseWords: split("\\s+"), then loop from the last word to the first.
 *   - csvRow: append a comma BEFORE every field except the first (if (i > 0) ...).
 *   - progressBar: "#".repeat(n) is fine inside one append.
 *   - compress: walk the text, counting how many times the current letter repeats.
 *
 * Run: java exercises/Exercise1_BuilderBasics.java
 */
public class Exercise1_BuilderBasics {

    static String reverseWords(String sentence) {
        // TODO
        return null;
    }

    static String csvRow(List<String> fields) {
        // TODO
        return null;
    }

    static String progressBar(int percent) {
        // TODO
        return null;
    }

    static String compress(String text) {
        // TODO
        return null;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("reverseWords", () ->
                reverseWords("the quick brown fox").equals("fox brown quick the")
                        && reverseWords("hello").equals("hello"));

        allPass &= check("csvRow", () ->
                csvRow(List.of("A101", "Ravi", "2499.5")).equals("A101,Ravi,2499.5")
                        && csvRow(List.of("only")).equals("only")
                        && csvRow(List.of()).equals(""));

        allPass &= check("progressBar", () ->
                progressBar(45).equals("[####------] 45%")
                        && progressBar(0).equals("[----------] 0%")
                        && progressBar(100).equals("[##########] 100%"));

        allPass &= check("compress", () ->
                compress("aaabccdddd").equals("a3bc2d4")
                        && compress("abc").equals("abc")
                        && compress("zzzzzzzzzzzz").equals("z12")
                        && compress("").equals(""));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-13s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-13s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
