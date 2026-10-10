/**
 * Exercise 4 (Medium): edit a StringBuilder in place.
 *
 * TASK
 *   Each method must work on ONE StringBuilder made from the input (new StringBuilder(input))
 *   and change it with the editing methods named below. No substring, no +, no String.replace.
 *
 *     maskCard(number)          keep the last 4 digits, replace every other digit with '*',
 *                               keep the spaces.  Use setCharAt.
 *                               "4111 1111 1111 1234" -> "**** **** **** 1234"
 *     withSeparators(digits)    insert ',' every 3 digits from the right.  Use insert.
 *                               "1234567" -> "1,234,567",  "999" -> "999",  "1000" -> "1,000"
 *     removeVowels(text)        delete a, e, i, o, u (any case).  Use deleteCharAt, looping backwards.
 *                               "Education" -> "dctn"
 *     shorten(text, max)        if longer than max, cut to max - 3 characters and add "...".
 *                               Use setLength and append. Shorter text is returned unchanged.
 *                               shorten("Quarterly sales report", 12) -> "Quarterly..."
 *     swapDomain(email, domain) replace everything after '@' with domain.  Use indexOf and replace.
 *                               swapDomain("asha@old.com", "new.org") -> "asha@new.org"
 *
 * EXPECTED OUTPUT
 *   maskCard:       PASS
 *   withSeparators: PASS
 *   removeVowels:   PASS
 *   shorten:        PASS
 *   swapDomain:     PASS
 *   ALL PASS
 *
 * HINTS
 *   - maskCard: count digits from the END; only the last 4 digits survive.
 *   - withSeparators: start at length() - 3 and step back by 3 while the position is > 0.
 *   - Deleting while looping forwards skips the character that slides into the deleted spot.
 *
 * Run: java exercises/Exercise4_EditInPlace.java
 */
public class Exercise4_EditInPlace {

    static String maskCard(String number) {
        StringBuilder sb = new StringBuilder(number);
        // TODO
        return sb.toString();
    }

    static String withSeparators(String digits) {
        StringBuilder sb = new StringBuilder(digits);
        // TODO
        return sb.toString();
    }

    static String removeVowels(String text) {
        StringBuilder sb = new StringBuilder(text);
        // TODO
        return sb.toString();
    }

    static String shorten(String text, int max) {
        StringBuilder sb = new StringBuilder(text);
        // TODO
        return sb.toString();
    }

    static String swapDomain(String email, String domain) {
        StringBuilder sb = new StringBuilder(email);
        // TODO
        return sb.toString();
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("maskCard:", "**** **** **** 1234".equals(maskCard("4111 1111 1111 1234"))
                && "**3456".equals(maskCard("123456")) && "1234".equals(maskCard("1234")));
        allPass &= check("withSeparators:", "1,234,567".equals(withSeparators("1234567"))
                && "999".equals(withSeparators("999")) && "1,000".equals(withSeparators("1000"))
                && "12,345".equals(withSeparators("12345")) && "".equals(withSeparators("")));
        allPass &= check("removeVowels:", "dctn".equals(removeVowels("Education"))
                && "".equals(removeVowels("aeiouAEIOU")) && "rhythm".equals(removeVowels("rhythm"))
                && "bk".equals(removeVowels("book")));
        allPass &= check("shorten:", "Quarterly...".equals(shorten("Quarterly sales report", 12))
                && "Short".equals(shorten("Short", 12)) && "Exactly12chr".equals(shorten("Exactly12chr", 12)));
        allPass &= check("swapDomain:", "asha@new.org".equals(swapDomain("asha@old.com", "new.org"))
                && "a@b.io".equals(swapDomain("a@verylongdomain.example.com", "b.io")));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
