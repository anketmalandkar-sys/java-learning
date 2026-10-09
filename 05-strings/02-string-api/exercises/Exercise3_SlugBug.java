import java.util.Locale;

/**
 * Exercise 3 (Hard): release-note URLs that break on the production server.
 *
 * THE SITUATION
 *   The release-notes tool turns a version and a title into a URL slug:
 *       slugFor("2.14.0", "Improve INDEXING speed.")  ->  "v2-14-improve-indexing-speed"
 *   The rules:
 *     - "v" + major + "-" + minor   (the patch number is dropped)
 *     - then "-" + the title in lower case, with every "." removed, and each run of spaces
 *       turned into a single "-"
 *
 *   It worked on the developer's laptop once. On the production server (whose default locale
 *   is Turkish, simulated at the top of main) it does three different wrong things.
 *
 * TASK
 *   1. Run it and read each failure.
 *   2. Find the THREE bugs in slugFor. For each one, add a comment saying what the code
 *      actually does and why.
 *   3. Fix them. Change only slugFor. Keep the Turkish locale line in main: the fix must
 *      work on that server.
 *
 * EXPECTED OUTPUT (after the fix)
 *   v2-14-improve-indexing-speed       PASS
 *   v3-0-fix-login-on-ios-17           PASS
 *   v10-2-new-billing-ui               PASS
 *   ALL PASS
 *
 * HINTS
 *   - Which String methods take a regex? What does "." mean in a regex?
 *   - What does "INDEXING".toLowerCase() return when the default locale is Turkish?
 *   - Not every bug throws an exception. One of them just produces a wrong slug.
 *
 * Run: java exercises/Exercise3_SlugBug.java
 */
public class Exercise3_SlugBug {

    static String slugFor(String version, String title) {
        String[] parts = version.split(".");
        String prefix = "v" + parts[0] + "-" + parts[1];

        String words = title.toLowerCase()
                .replaceAll(".", "")
                .strip()
                .replaceAll("\\s+", "-");

        return prefix + "-" + words;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.forLanguageTag("tr-TR"));   // the production server. Keep this line.

        String[][] cases = {
                {"2.14.0", "Improve INDEXING speed.", "v2-14-improve-indexing-speed"},
                {"3.0.1", "Fix login on iOS 17", "v3-0-fix-login-on-ios-17"},
                {"10.2.0", "  New   billing UI ", "v10-2-new-billing-ui"},
        };

        boolean allPass = true;
        for (String[] c : cases) {
            String result;
            try {
                result = slugFor(c[0], c[1]);
            } catch (RuntimeException e) {
                result = "crashed: " + e.getClass().getSimpleName();
            }
            boolean ok = result.equals(c[2]);
            allPass &= ok;
            // Print non-ASCII characters as \\uXXXX so the console shows them reliably.
            System.out.printf("%-34s %s%n", ascii(result), ok ? "PASS" : "FAIL (expected " + c[2] + ")");
        }
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static String ascii(String text) {
        StringBuilder out = new StringBuilder();
        for (char ch : text.toCharArray()) {
            out.append(ch < 128 ? String.valueOf(ch) : String.format("\\u%04X", (int) ch));
        }
        return out.toString();
    }
}
