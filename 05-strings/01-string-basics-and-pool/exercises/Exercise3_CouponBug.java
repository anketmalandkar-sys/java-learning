import java.util.Map;

/**
 * Exercise 3 (Hard): the coupon service that only works in tests.
 *
 * THE SITUATION
 *   CouponService works out a customer's discount from a coupon code. Its unit tests pass,
 *   and they use literals like discountFor("SAVE10", "ACTIVE"). But in production EVERY
 *   real customer gets 0% off. A few requests even crash the service.
 *
 *   Real requests arrive as text, e.g. "code= save10 ;status=ACTIVE", and are parsed with
 *   split() and substring(). So every value is built at runtime, may have stray spaces or
 *   lower case, and status can be missing (null).
 *
 * TASK
 *   1. Run it. The "unit tests" pass and the "real requests" fail.
 *   2. Find the THREE string bugs in CouponService (all in discountFor and isExpired).
 *      For each one, add a comment explaining why the literal-based tests didn't catch it.
 *   3. Fix them. Change only CouponService. Don't touch parseRequest or main.
 *
 * EXPECTED OUTPUT (after the fix)
 *   unit tests:    PASS
 *   real requests: PASS
 *   ALL PASS
 *
 * HINTS
 *   - What does code.trim() do to code?
 *   - "SAVE10" from a Map key and "SAVE10" built by toUpperCase() are equal, but are they
 *     the same object?
 *   - Which side of status.equals("EXPIRED") can be null?
 *
 * Run: java exercises/Exercise3_CouponBug.java
 */
public class Exercise3_CouponBug {

    static class CouponService {
        private static final Map<String, Integer> DISCOUNTS = Map.of(
                "SAVE10", 10,
                "WELCOME20", 20);

        // Percentage discount for a coupon code, or 0 if the code is unknown or expired.
        static int discountFor(String rawCode, String status) {
            if (isExpired(status)) {
                return 0;
            }
            String code = rawCode;
            code.trim();
            code.toUpperCase();
            for (String known : DISCOUNTS.keySet()) {
                if (known == code) {
                    return DISCOUNTS.get(known);
                }
            }
            return 0;
        }

        static boolean isExpired(String status) {
            return status.equals("EXPIRED");
        }
    }

    // ---- Don't change anything below this line. ----

    // Parses "code=<code>;status=<status>". status=... may be missing.
    static int handleRequest(String request) {
        String[] parts = request.split(";");
        String code = parts[0].substring(parts[0].indexOf('=') + 1);
        String status = parts.length > 1 ? parts[1].substring(parts[1].indexOf('=') + 1) : null;
        return CouponService.discountFor(code, status);
    }

    public static void main(String[] args) {
        boolean unitTests = CouponService.discountFor("SAVE10", "ACTIVE") == 10
                && CouponService.discountFor("WELCOME20", "ACTIVE") == 20
                && CouponService.discountFor("SAVE10", "EXPIRED") == 0
                && CouponService.discountFor("FAKE", "ACTIVE") == 0;
        System.out.println("unit tests:    " + (unitTests ? "PASS" : "FAIL"));

        boolean realRequests;
        try {
            realRequests = handleRequest("code=SAVE10;status=ACTIVE") == 10
                    && handleRequest("code= save10 ;status=ACTIVE") == 10
                    && handleRequest("code=Welcome20") == 20             // no status: treat as active
                    && handleRequest("code=SAVE10;status=EXPIRED") == 0
                    && handleRequest("code=save99;status=ACTIVE") == 0;
        } catch (NullPointerException e) {
            System.out.println("  (crashed: NullPointerException)");
            realRequests = false;
        }
        System.out.println("real requests: " + (realRequests ? "PASS" : "FAIL"));

        System.out.println(unitTests && realRequests ? "ALL PASS" : "SOME FAILED");
    }
}
