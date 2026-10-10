import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Exercise 2 (Medium): formatting numbers for people.
 *
 * TASK
 *   Use String.format (always with Locale.US) or DecimalFormat (always with the US symbols
 *   from usSymbols()), so results don't depend on the computer's language settings.
 *
 *     money(amount)           grouping and exactly 2 decimals      money(1234567.5) -> "1,234,567.50"
 *     percentChange(oldV, newV)
 *                             signed, 1 decimal, with a % sign      percentChange(200, 230) -> "+15.0%"
 *                                                                   percentChange(200, 150) -> "-25.0%"
 *     invoiceNumber(n)        "INV-" plus n zero-padded to 6 digits invoiceNumber(42) -> "INV-000042"
 *     reportLine(item, qty, price)
 *                             item left-aligned in 10 chars, qty right-aligned in 4, then
 *                             price as money right-aligned in 12
 *                             reportLine("Pen", 10, 1250.5) -> "Pen         10    1,250.50"
 *     compact(amount)         a DecimalFormat with pattern "#,##0.##": up to 2 decimals,
 *                             no trailing zeros       compact(1500) -> "1,500", compact(2.5) -> "2.5"
 *
 * EXPECTED OUTPUT
 *   money:         PASS
 *   percentChange: PASS
 *   invoiceNumber: PASS
 *   reportLine:    PASS
 *   compact:       PASS
 *   ALL PASS
 *
 * HINTS
 *   - %,.2f   grouping + 2 decimals.   %+.1f  sign always shown.   %06d  zero-padded width 6.
 *   - A literal % in a format string is written %%.
 *   - %-10s is left-aligned, %4d / %12s are right-aligned.
 *
 * Run: java exercises/Exercise2_FormatReport.java
 */
public class Exercise2_FormatReport {

    static DecimalFormatSymbols usSymbols() {
        return DecimalFormatSymbols.getInstance(Locale.US);
    }

    static String money(double amount) {
        return ""; // TODO
    }

    static String percentChange(double oldValue, double newValue) {
        return ""; // TODO
    }

    static String invoiceNumber(int n) {
        return ""; // TODO
    }

    static String reportLine(String item, int qty, double price) {
        return ""; // TODO
    }

    static String compact(double amount) {
        return ""; // TODO: new DecimalFormat("#,##0.##", usSymbols())
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("money:", "1,234,567.50".equals(money(1234567.5)) && "0.05".equals(money(0.049))
                && "-12.00".equals(money(-12)));
        allPass &= check("percentChange:", "+15.0%".equals(percentChange(200, 230))
                && "-25.0%".equals(percentChange(200, 150)) && "+0.0%".equals(percentChange(80, 80)));
        allPass &= check("invoiceNumber:", "INV-000042".equals(invoiceNumber(42))
                && "INV-123456".equals(invoiceNumber(123456)));
        allPass &= check("reportLine:", "Pen         10    1,250.50".equals(reportLine("Pen", 10, 1250.5))
                && "Notebook     3      136.50".equals(reportLine("Notebook", 3, 136.5)));
        allPass &= check("compact:", "1,500".equals(compact(1500)) && "2.5".equals(compact(2.5))
                && "3.14".equals(compact(3.14159)));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-14s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
