/**
 * Example 3: string/number conversion, regionMatches, matches, and text-block escapes.
 *
 * Run: java examples/Example3_ConversionsAndRegions.java
 */
public class Example3_ConversionsAndRegions {

    public static void main(String[] args) {
        System.out.println("String -> number:");
        String[] inputs = {"12", "19.99", " 7", "abc"};
        for (String input : inputs) {
            try {
                int whole = Integer.parseInt(input);
                System.out.println("  \"" + input + "\" -> int " + whole);
            } catch (NumberFormatException e) {
                // Not a whole number: try a decimal after trimming.
                try {
                    double decimal = Double.parseDouble(input.strip());
                    System.out.println("  \"" + input + "\" -> double " + decimal);
                } catch (NumberFormatException e2) {
                    System.out.println("  \"" + input + "\" -> not a number");
                }
            }
        }
        System.out.println("  parseBoolean(\"TRUE\") = " + Boolean.parseBoolean("TRUE")
                + ", parseBoolean(\"yes\") = " + Boolean.parseBoolean("yes"));

        System.out.println("Number -> String:");
        String a = String.valueOf(42);
        String b = Integer.toString(255, 2);
        String c = Double.toString(0.5);
        System.out.println("  " + a + " " + b + " " + c + " " + String.valueOf((Object) null));

        System.out.println("Regions:");
        String log = "2024-03-09 ERROR disk full";
        System.out.println("  startsWith(\"ERROR\", 11)           = " + log.startsWith("ERROR", 11));
        System.out.println("  regionMatches(11, \"error\", 0, 5)  = " + log.regionMatches(11, "error", 0, 5));
        System.out.println("  regionMatches(true, 11, ...)       = " + log.regionMatches(true, 11, "error", 0, 5));

        System.out.println("matches (whole string only):");
        System.out.println("  \"ORD-0042\"   " + "ORD-0042".matches("[A-Z]{3}-\\d{4}"));
        System.out.println("  \"x ORD-0042\" " + "x ORD-0042".matches("[A-Z]{3}-\\d{4}"));

        System.out.println("Small helpers:");
        String file = "report.final.pdf";
        int dot = file.lastIndexOf('.');
        System.out.println("  extension of " + file + " = " + file.substring(dot + 1));
        System.out.println("  subSequence(0, 6)   = " + file.subSequence(0, 6));
        System.out.println("  replaceFirst        = " + "a-b-c".replaceFirst("-", "+"));
        char[] buffer = new char[5];
        file.getChars(0, 5, buffer, 0);
        System.out.println("  getChars into array = " + new String(buffer));

        String block = """
                Name:\s
                One long \
                line
                """;
        System.out.println("Text block, spaces shown as dots: " + block.replace(" ", ".").replace("\n", "|"));
    }
}

/* Expected output:
String -> number:
  "12" -> int 12
  "19.99" -> double 19.99
  " 7" -> double 7.0
  "abc" -> not a number
  parseBoolean("TRUE") = true, parseBoolean("yes") = false
Number -> String:
  42 11111111 0.5 null
Regions:
  startsWith("ERROR", 11)           = true
  regionMatches(11, "error", 0, 5)  = false
  regionMatches(true, 11, ...)       = true
matches (whole string only):
  "ORD-0042"   true
  "x ORD-0042" false
Small helpers:
  extension of report.final.pdf = pdf
  subSequence(0, 6)   = report
  replaceFirst        = a+b-c
  getChars into array = repor
Text block, spaces shown as dots: Name:.|One.long.line|
*/
