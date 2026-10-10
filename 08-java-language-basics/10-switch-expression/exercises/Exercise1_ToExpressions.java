/**
 * Exercise 1 (Easy): from switch statement to switch expression.
 *
 * TASK
 *   Each "Old" method below is a working classic switch statement. Write the matching "New"
 *   method as a single "return switch (...) { ... };" using arrow labels, with several
 *   constants per case where they share a result. No break, no extra variables.
 *
 *     sizeNameOld / sizeNameNew         'S' -> "Small", 'M' -> "Medium", 'L' -> "Large", else "Unknown"
 *     vowelOld / vowelNew               true for a, e, i, o, u (lowercase only)
 *     httpStatusOld / httpStatusNew     message for an HTTP status code
 *
 *   The checks compare New with Old for many inputs.
 *
 * EXPECTED OUTPUT
 *   sizeName:   PASS
 *   vowel:      PASS
 *   httpStatus: PASS
 *   ALL PASS
 *
 * Run: java exercises/Exercise1_ToExpressions.java
 */
public class Exercise1_ToExpressions {

    static String sizeNameOld(char code) {
        String name;
        switch (code) {
            case 'S':
                name = "Small";
                break;
            case 'M':
                name = "Medium";
                break;
            case 'L':
                name = "Large";
                break;
            default:
                name = "Unknown";
        }
        return name;
    }

    static String sizeNameNew(char code) {
        return ""; // TODO
    }

    static boolean vowelOld(char c) {
        boolean result;
        switch (c) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                result = true;
                break;
            default:
                result = false;
        }
        return result;
    }

    static boolean vowelNew(char c) {
        return false; // TODO
    }

    static String httpStatusOld(int code) {
        String message;
        switch (code) {
            case 200:
            case 201:
            case 204:
                message = "Success";
                break;
            case 301:
            case 302:
                message = "Redirect";
                break;
            case 400:
                message = "Bad request";
                break;
            case 401:
            case 403:
                message = "Not allowed";
                break;
            case 404:
                message = "Not found";
                break;
            case 500:
                message = "Server error";
                break;
            default:
                message = "Other";
        }
        return message;
    }

    static String httpStatusNew(int code) {
        return ""; // TODO
    }

    public static void main(String[] args) {
        boolean sizes = true;
        for (char c : "SMLXsm ".toCharArray()) {
            sizes &= sizeNameOld(c).equals(sizeNameNew(c));
        }
        boolean vowels = true;
        for (char c = 'a'; c <= 'z'; c++) {
            vowels &= vowelOld(c) == vowelNew(c);
        }
        vowels &= vowelOld('A') == vowelNew('A');
        boolean statuses = true;
        for (int code : new int[] {200, 201, 204, 301, 302, 400, 401, 403, 404, 500, 418, 0}) {
            statuses &= httpStatusOld(code).equals(httpStatusNew(code));
        }

        boolean allPass = true;
        allPass &= check("sizeName:", sizes);
        allPass &= check("vowel:", vowels);
        allPass &= check("httpStatus:", statuses);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-11s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
