/**
 * Exercise 3 (Hard): block scope and assignment-as-expression.
 *
 * TASK
 *   TokenReader hands out words one at a time from a sentence, and returns null when done.
 *
 *   1. countLongWords() has a scope bug: it always returns 0 or 1. Fix it by moving ONE
 *      declaration to the right block.
 *
 *   2. Write stats(reader) using the read-loop idiom:
 *          String token;
 *          while ((token = reader.next()) != null) { ... }
 *      Return an int[] of three values: {number of words, total letters, length of longest word}.
 *        "to be or not" -> {4, 9, 3}
 *
 *   3. Write firstRepeated(reader): the first word that equals the word right before it
 *      (case-insensitive), or null if there isn't one. Use the same loop idiom.
 *        "the cat the the end" -> "the"      "a b c" -> null
 *
 * EXPECTED OUTPUT
 *   countLongWords: PASS
 *   stats:          PASS
 *   firstRepeated:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - A variable declared inside the loop body starts over on every pass.
 *   - In firstRepeated, you need a variable that remembers the previous word across passes.
 *     Which block must it be declared in?
 *
 * Run: java exercises/Exercise3_TokenReader.java
 */
public class Exercise3_TokenReader {

    static class TokenReader {
        private final String[] words;
        private int position = 0;

        TokenReader(String sentence) {
            this.words = sentence.isBlank() ? new String[0] : sentence.trim().split("\\s+");
        }

        String next() {
            return position < words.length ? words[position++] : null;
        }
    }

    // TODO: fix the scope bug
    static int countLongWords(TokenReader reader, int minLength) {
        String token;
        int count = 0;
        while ((token = reader.next()) != null) {
            int longWords = 0;
            if (token.length() >= minLength) {
                longWords++;
            }
            count = longWords;
        }
        return count;
    }

    static int[] stats(TokenReader reader) {
        return new int[] {0, 0, 0}; // TODO
    }

    static String firstRepeated(TokenReader reader) {
        return "TODO"; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("countLongWords:",
                countLongWords(new TokenReader("Java blocks create a scope for locals"), 5) == 4
                        && countLongWords(new TokenReader("hi"), 5) == 0);

        int[] s = stats(new TokenReader("to be or not"));
        int[] empty = stats(new TokenReader(""));
        allPass &= check("stats:", s[0] == 4 && s[1] == 9 && s[2] == 3
                && empty[0] == 0 && empty[1] == 0 && empty[2] == 0);

        allPass &= check("firstRepeated:", "the".equals(firstRepeated(new TokenReader("the cat the the end")))
                && "go".equals(firstRepeated(new TokenReader("ready set Go go")))
                && firstRepeated(new TokenReader("a b c")) == null);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
