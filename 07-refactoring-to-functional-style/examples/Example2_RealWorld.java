import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Example 2: a daily sales report read from a CSV file, refactored from loops to streams.
 *   1. The imperative version: BufferedReader, while loop, if, a running total.
 *   2. The same report with Files.lines + filter + map + sum, in try-with-resources.
 *   3. More questions answered from the same data: read the file ONCE into a list of records,
 *      then stream that list as often as needed (a stream itself can only be used once).
 *
 * The example writes its own CSV to a temp file, so it runs anywhere.
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    record Sale(String orderId, String city, int amount, String status) {
        static Sale parse(String line) {
            String[] parts = line.split(",");
            return new Sale(parts[0], parts[1], Integer.parseInt(parts[2]), parts[3]);
        }
    }

    // 1. Imperative: how to read, how to skip, how to add up.
    static int paidRevenueImperative(Path file) throws IOException {
        int total = 0;
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            reader.readLine();                       // skip the header
            String line;
            while ((line = reader.readLine()) != null) {
                Sale sale = Sale.parse(line);
                if (sale.status().equals("PAID")) {
                    total += sale.amount();
                }
            }
        }
        return total;
    }

    // 2. Functional: the same steps, each one named. try-with-resources closes the file.
    static int paidRevenue(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            return lines.skip(1)                     // the header
                    .map(Sale::parse)
                    .filter(sale -> sale.status().equals("PAID"))
                    .mapToInt(Sale::amount)
                    .sum();
        }
    }

    // 3. Read once, then ask many questions.
    static List<Sale> readSales(Path file) throws IOException {
        try (Stream<String> lines = Files.lines(file)) {
            return lines.skip(1).map(Sale::parse).toList();
        }
    }

    public static void main(String[] args) throws IOException {
        Path file = Files.createTempFile("sales", ".csv");
        Files.write(file, List.of(
                "orderId,city,amount,status",
                "A1,Pune,1200,PAID",
                "A2,Mumbai,450,PAID",
                "A3,Pune,3000,REFUNDED",
                "A4,Delhi,2500,PAID",
                "A5,Mumbai,1800,PAID",
                "A6,Delhi,300,FAILED",
                "A7,Pune,700,PAID"));

        try {
            System.out.println("--- Paid revenue ---");
            System.out.println("imperative: " + paidRevenueImperative(file));
            System.out.println("functional: " + paidRevenue(file));

            List<Sale> sales = readSales(file);

            System.out.println();
            System.out.println("--- Cities with a paid order over 1000 (sorted, no duplicates) ---");
            List<String> bigCities = sales.stream()
                    .filter(sale -> sale.status().equals("PAID") && sale.amount() > 1000)
                    .map(Sale::city)
                    .distinct()
                    .sorted()
                    .toList();
            System.out.println(bigCities);

            System.out.println();
            System.out.println("--- Orders per status ---");
            // replaces: Map<String, Long> m = new TreeMap<>(); for (...) m.merge(status, 1L, Long::sum);
            Map<String, Long> perStatus = sales.stream()
                    .collect(Collectors.groupingBy(Sale::status, TreeMap::new, Collectors.counting()));
            System.out.println(perStatus);

            System.out.println();
            System.out.println("--- Paid revenue per city ---");
            Map<String, Integer> perCity = sales.stream()
                    .filter(sale -> sale.status().equals("PAID"))
                    .collect(Collectors.groupingBy(Sale::city, TreeMap::new, Collectors.summingInt(Sale::amount)));
            perCity.forEach((city, revenue) -> System.out.println(city + ": " + revenue));

            System.out.println();
            System.out.println("--- First two paid orders over 1000 ---");
            // filter BEFORE limit: "the first 2 that match", not "the matches among the first 2"
            sales.stream()
                    .filter(sale -> sale.status().equals("PAID") && sale.amount() > 1000)
                    .limit(2)
                    .map(Sale::orderId)
                    .forEach(System.out::println);
        } finally {
            Files.delete(file);
        }
    }
}

/* Expected output:
--- Paid revenue ---
imperative: 6650
functional: 6650

--- Cities with a paid order over 1000 (sorted, no duplicates) ---
[Delhi, Mumbai, Pune]

--- Orders per status ---
{FAILED=1, PAID=5, REFUNDED=1}

--- Paid revenue per city ---
Delhi: 2500
Mumbai: 2250
Pune: 1900

--- First two paid orders over 1000 ---
A1
A4
*/
