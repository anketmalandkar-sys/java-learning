import java.time.LocalDate;                 // single-type import
import java.util.*;                         // wildcard import: List, Map, ArrayList ...
import java.util.function.Function;         // NOT covered by java.util.*: separate package
import java.util.Map.Entry;                 // a NESTED type: Entry instead of Map.Entry
                                            // (import java.util.Map.*; would import Entry but NOT Map)

import static java.lang.Math.PI;            // static import of one constant
import static java.lang.Math.max;           // static import of one method

/**
 * Example 1: ways to refer to types in other packages, ambiguity, and static imports.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        // Imported by the single-type import.
        LocalDate releaseDay = LocalDate.of(2024, 3, 19);
        System.out.println("Java 22 released: " + releaseDay);

        // Fully qualified name, no import needed: fine for a one-off use.
        java.time.DayOfWeek day = releaseDay.getDayOfWeek();
        System.out.println("day of week: " + day);

        // From the java.util.* wildcard.
        List<String> pkgs = new ArrayList<>(List.of("java.util", "java.time", "java.io"));
        Collections.sort(pkgs);
        System.out.println("sorted: " + pkgs);

        // java.util.function needed its own import.
        Function<String, Integer> length = String::length;
        System.out.println("length of \"package\": " + length.apply("package"));

        // Entry was imported on its own; Map itself comes from the java.util.* wildcard.
        Map<String, Integer> stock = new TreeMap<>(Map.of("pen", 10, "ink", 3));
        for (Entry<String, Integer> e : stock.entrySet()) {
            System.out.println("  " + e.getKey() + "=" + e.getValue());
        }

        System.out.println("Entry is really " + Entry.class.getName() + " ($ marks a nested class)");

        // java.lang is imported automatically: String, Math, Integer ...
        System.out.println("Integer.MAX_VALUE = " + Integer.MAX_VALUE);

        // Static imports: no "Math." prefix.
        System.out.printf("area of r=2: %.2f, max(3, 8) = %d%n", PI * 2 * 2, max(3, 8));

        // Two Date classes: java.util.Date (from the wildcard) and java.sql.Date.
        // The simple name means java.util.Date here; the other needs its full name.
        java.sql.Date sqlDate = java.sql.Date.valueOf(releaseDay);
        Date utilDate = new Date(sqlDate.getTime());
        System.out.println("same instant? " + (utilDate.getTime() == sqlDate.getTime()));

        // Every class knows its package.
        System.out.println("LocalDate is in " + LocalDate.class.getPackageName());
        System.out.println("Function is in " + Function.class.getPackageName());
        System.out.println("this class is in the unnamed package? " + Example1_Basics.class.getPackageName().isEmpty());
    }
}

/* Expected output:
Java 22 released: 2024-03-19
day of week: TUESDAY
sorted: [java.io, java.time, java.util]
length of "package": 7
  ink=3
  pen=10
Entry is really java.util.Map$Entry ($ marks a nested class)
Integer.MAX_VALUE = 2147483647
area of r=2: 12.57, max(3, 8) = 8
same instant? true
LocalDate is in java.time
Function is in java.util.function
this class is in the unnamed package? true
*/
