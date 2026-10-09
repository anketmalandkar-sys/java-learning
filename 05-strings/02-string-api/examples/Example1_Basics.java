import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Example 1: the everyday String methods, grouped by job.
 *   1. Comparing      2. Searching      3. Slicing and split
 *   4. Cleaning       5. Joining and repeating      6. Formatting and text blocks
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        System.out.println("--- 1. Comparing ---");
        System.out.println("equalsIgnoreCase: " + "Pune".equalsIgnoreCase("pune"));
        System.out.println("\"Zebra\" vs \"apple\": compareTo " + Integer.signum("Zebra".compareTo("apple"))
                + ", ignoring case " + Integer.signum("Zebra".compareToIgnoreCase("apple")));
        List<String> names = new ArrayList<>(List.of("meera", "Arjun", "banu", "ravi", "Kavya"));
        names.sort(null);                                   // natural order: capitals first
        System.out.println("natural order:    " + names);
        names.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("case-insensitive: " + names);

        System.out.println();
        System.out.println("--- 2. Searching ---");
        String path = "/home/ravi/report.final.pdf";
        System.out.println("indexOf('/', 1) = " + path.indexOf('/', 1));
        System.out.println("lastIndexOf('.') = " + path.lastIndexOf('.'));
        System.out.println("indexOf(\"xyz\") = " + path.indexOf("xyz"));
        System.out.println("contains/startsWith/endsWith: " + path.contains("ravi") + " "
                + path.startsWith("/home") + " " + path.endsWith(".pdf"));

        System.out.println();
        System.out.println("--- 3. Slicing and split ---");
        String fileName = path.substring(path.lastIndexOf('/') + 1);
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        System.out.println("file name: " + fileName + ", extension: " + extension);
        System.out.println("substring(0, 6) of \"report.pdf\": " + "report.pdf".substring(0, 6));
        System.out.println("split(\",\")           " + Arrays.toString("a,b,,".split(",")));
        System.out.println("split(\",\", -1)       " + Arrays.toString("a,b,,".split(",", -1)));
        System.out.println("split(\".\")           " + Arrays.toString("1.2.3".split(".")));
        System.out.println("split(\"\\\\.\")         " + Arrays.toString("1.2.3".split("\\.")));
        System.out.println("split(\"\\\\s*,\\\\s*\")   " + Arrays.toString("a , b,c ".split("\\s*,\\s*")));

        System.out.println();
        System.out.println("--- 4. Cleaning ---");
        String emSpace = String.valueOf((char) 0x2003);       // U+2003 EM SPACE: Unicode whitespace
        String messy = emSpace + " Ravi Kumar " + emSpace;
        System.out.println(("trim():  [" + messy.trim() + "]").replace(emSpace, "<em>") + "  (trim misses the em space)");
        System.out.println("strip(): [" + messy.strip() + "]");
        System.out.println("\"   \".isEmpty() = " + "   ".isEmpty() + ", isBlank() = " + "   ".isBlank());
        System.out.println("replace:    " + "v1.2.3".replace(".", "-"));
        System.out.println("replaceAll: " + "v1.2.3".replaceAll(".", "-") + "   (\".\" is a regex: any char)");
        System.out.println("replaceAll(\"[0-9]+\", \"#\"): " + "a1b22c333".replaceAll("[0-9]+", "#"));
        System.out.println("toLowerCase(ROOT): " + "TITLE".toLowerCase(Locale.ROOT)
                + ", Turkish: " + ("TITLE".toLowerCase(Locale.forLanguageTag("tr")).equals("title") ? "title" : "not \"title\" (dotless i)"));

        System.out.println();
        System.out.println("--- 5. Joining and repeating ---");
        System.out.println(String.join(" | ", List.of("Pune", "Goa", "Delhi")));
        System.out.println("=".repeat(20));
        System.out.println("lines in a block: " + "one\ntwo\nthree".lines().count());

        System.out.println();
        System.out.println("--- 6. Formatting and text blocks ---");
        System.out.println(String.format("[%-8s|%6.2f|%03d]", "Pens", 4.5, 7));
        System.out.println("%s owes %,d".formatted("Ravi", 1250000));
        String json = """
                {
                  "id": "A101",
                  "total": %.1f
                }""".formatted(2499.5);
        System.out.println(json);
    }
}

/* Expected output:
--- 1. Comparing ---
equalsIgnoreCase: true
"Zebra" vs "apple": compareTo -1, ignoring case 1
natural order:    [Arjun, Kavya, banu, meera, ravi]
case-insensitive: [Arjun, banu, Kavya, meera, ravi]

--- 2. Searching ---
indexOf('/', 1) = 5
lastIndexOf('.') = 23
indexOf("xyz") = -1
contains/startsWith/endsWith: true true true

--- 3. Slicing and split ---
file name: report.final.pdf, extension: pdf
substring(0, 6) of "report.pdf": report
split(",")           [a, b]
split(",", -1)       [a, b, , ]
split(".")           []
split("\\.")         [1, 2, 3]
split("\\s*,\\s*")   [a, b, c ]

--- 4. Cleaning ---
trim():  [<em> Ravi Kumar <em>]  (trim misses the em space)
strip(): [Ravi Kumar]
"   ".isEmpty() = false, isBlank() = true
replace:    v1-2-3
replaceAll: ------   ("." is a regex: any char)
replaceAll("[0-9]+", "#"): a#b#c#
toLowerCase(ROOT): title, Turkish: not "title" (dotless i)

--- 5. Joining and repeating ---
Pune | Goa | Delhi
====================
lines in a block: 3

--- 6. Formatting and text blocks ---
[Pens    |  4.50|007]
Ravi owes 1,250,000
{
  "id": "A101",
  "total": 2499.5
}
*/
