import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 3 (Hard): the nightly sales report.
 *
 * THE SITUATION
 *   ReportService builds a sales report and emails it, but skips the email if the report is
 *   exactly the same as last night's. Three complaints came in:
 *     1. Building the report takes seconds, and it gets slower every month as sales grow.
 *     2. The header should be "[SALES REPORT]", but the opening "[" is missing.
 *     3. The "skip if unchanged" check never fires, so identical reports are emailed every night.
 *
 * TASK
 *   1. Run it and see the three failures.
 *   2. Find the bug behind each complaint. All three are in ReportService.
 *      Add a comment by each fix explaining what was wrong.
 *   3. Fix them. Keep the method signatures (build still returns a StringBuilder, so callers
 *      can append a footer). Don't change Sale or main.
 *
 * EXPECTED OUTPUT (after the fix; the time will differ)
 *   built 40000 rows in 12 ms
 *   fast enough: PASS
 *   header:      PASS
 *   unchanged:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - What happens to body on every iteration of the loop?
 *   - Which StringBuilder constructor does new StringBuilder('[') call? '[' is a char.
 *   - Does StringBuilder override equals? And can a String ever be equal to a StringBuilder?
 *
 * Run: java exercises/Exercise3_SlowReportBug.java
 */
public class Exercise3_SlowReportBug {

    record Sale(String region, String product, double amount) {}

    static class ReportService {
        private String lastReport;

        StringBuilder build(List<Sale> sales) {
            StringBuilder report = new StringBuilder('[');
            report.append("SALES REPORT]\n");

            String body = "";
            for (Sale sale : sales) {
                body += sale.region() + "," + sale.product() + "," + sale.amount() + "\n";
            }
            report.append(body);
            return report;
        }

        // True if this report is identical to the previous one. Remembers it for next time.
        boolean isUnchanged(StringBuilder report) {
            boolean same = lastReport != null && lastReport.equals(report);
            lastReport = report.toString();
            return same;
        }
    }

    public static void main(String[] args) {
        List<Sale> sales = new ArrayList<>();
        String[] regions = {"North", "South", "East", "West"};
        for (int i = 0; i < 40_000; i++) {
            sales.add(new Sale(regions[i % 4], "SKU-" + (i % 250), 100 + (i % 97)));
        }

        ReportService service = new ReportService();

        long start = System.nanoTime();
        StringBuilder tonight = service.build(sales);
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("built " + sales.size() + " rows in " + ms + " ms");

        boolean fast = ms < 250;
        boolean header = tonight.toString().startsWith("[SALES REPORT]\n")
                && tonight.toString().lines().count() == sales.size() + 1;

        service.isUnchanged(tonight);                            // first night: remembered
        boolean unchanged = service.isUnchanged(service.build(sales));   // same data again

        System.out.println("fast enough: " + (fast ? "PASS" : "FAIL (should take well under 250 ms)"));
        System.out.println("header:      " + (header ? "PASS" : "FAIL (starts with: "
                + tonight.toString().lines().findFirst().orElse("") + ")"));
        System.out.println("unchanged:   " + (unchanged ? "PASS" : "FAIL (identical report not detected)"));
        System.out.println(fast && header && unchanged ? "ALL PASS" : "SOME FAILED");
    }
}
