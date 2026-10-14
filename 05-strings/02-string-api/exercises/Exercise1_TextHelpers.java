import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Exercise 1 (Easy): everyday text helpers.
 *
 * TASK
 *   Implement the four helpers. Use String methods; no regex is needed except where noted.
 *
 *     initials(fullName)       first letter of each word, upper case.
 *                              "ravi kumar sharma" -> "RKS",  "  meera   nair " -> "MN"
 *                              (words can be separated by several spaces: split("\\s+") on the
 *                              stripped name handles that)
 *
 *     maskCard(cardNumber)     keep the last 4 digits, replace the rest with '*'.
 *                              "4111222233334444" -> "************4444"
 *                              Numbers of 4 digits or fewer are returned unchanged.
 *
 *     isPalindrome(text)       true if it reads the same backwards, ignoring case and anything
 *                              that isn't a letter or digit.
 *                              "Never odd or even" -> true,  "Java" -> false
 *
 *     countOccurrences(text, word)   how many times word appears in text (case-sensitive),
 *                              using indexOf in a loop. Matches may not overlap:
 *                              countOccurrences("aaaa", "aa") -> 2
 *
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   initials:         PASS
 *   maskCard:         PASS
 *   isPalindrome:     PASS
 *   countOccurrences: PASS
 *   ALL PASS
 *
 * HINTS
 *   - "*".repeat(n) builds a run of stars. substring(length - 4) is the last 4 characters.
 *   - For isPalindrome: replaceAll("[^A-Za-z0-9]", "") keeps letters and digits,
 *     then compare characters from both ends with charAt.
 *   - indexOf(word, fromIndex) returns -1 when there are no more matches.
 *
 * Run: java exercises/Exercise1_TextHelpers.java
 */
public class Exercise1_TextHelpers {

    static String initials(String fullName) {
        // TODO
        return Arrays.stream(fullName.strip().split("\\s+"))
                .filter(word -> !word.isEmpty())
                .map(word -> String.valueOf(Character.toUpperCase(word.charAt(0))))
                .collect(Collectors.joining());
    }

    static String maskCard(String cardNumber) {
        // TODO
        int length = cardNumber.length();
        return "*".repeat(length - 4).concat(cardNumber.substring(length - 4));
    }

    static boolean isPalindrome(String text) {
        // TODO
        if (text == null) {
            return false;
        }

        // Keep only letters and digits, and convert to lowercase for case-insensitive check
        String cleaned = text.replaceAll("[^A-Za-z0-9]", "").toLowerCase();

        int left = 0;
        int right = cleaned.length() - 1;

        // Compare characters from both ends moving inward
        while (left < right) {
            if (cleaned.charAt(left) != cleaned.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    static int countOccurrences(String text, String word) {
        if (text == null || word == null || word.isEmpty() || text.isEmpty()) {
            return 0;
        }

        int count = 0;
        int index = 0;

        // Use indexOf in a loop, advancing by word.length() to prevent overlapping
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length(); // Move past the current match
        }

        return count;
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("initials", () ->
                initials("ravi kumar sharma").equals("RKS")
                        && initials("  meera   nair ").equals("MN")
                        && initials("Arjun").equals("A"));

        allPass &= check("maskCard", () ->
                maskCard("4111222233334444").equals("************4444")
                        && maskCard("12345").equals("*2345")
                        && maskCard("1234").equals("1234"));

        allPass &= check("isPalindrome", () ->
                isPalindrome("Never odd or even")
                        && isPalindrome("A man, a plan, a canal: Panama")
                        && isPalindrome("12321")
                        && !isPalindrome("Java")
                        && isPalindrome(""));

        allPass &= check("countOccurrences", () ->
                countOccurrences("the cat sat on the mat with the hat", "the") == 3
                        && countOccurrences("aaaa", "aa") == 2
                        && countOccurrences("The end", "the") == 0
                        && countOccurrences("", "x") == 0);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-17s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-17s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
