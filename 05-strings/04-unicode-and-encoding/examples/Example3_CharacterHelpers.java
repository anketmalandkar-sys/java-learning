/**
 * Example 3: Character helper methods and escape sequences.
 *
 * Run: java examples/Example3_CharacterHelpers.java
 */
public class Example3_CharacterHelpers {

    public static void main(String[] args) {
        String sample = "Hi 5x_Q";
        System.out.println("Classifying \"" + sample + "\":");
        for (char c : sample.toCharArray()) {
            System.out.printf("  '%s' letter=%-5b digit=%-5b space=%-5b upper=%b%n",
                    c, Character.isLetter(c), Character.isDigit(c),
                    Character.isWhitespace(c), Character.isUpperCase(c));
        }

        System.out.println("Converting:");
        System.out.println("  toUpperCase('q')       = " + Character.toUpperCase('q'));
        System.out.println("  toLowerCase('Q')       = " + Character.toLowerCase('Q'));
        System.out.println("  (int) '7'              = " + (int) '7' + "   (the character code)");
        System.out.println("  getNumericValue('7')   = " + Character.getNumericValue('7') + "    (the digit's value)");
        System.out.println("  '7' - '0'              = " + ('7' - '0'));

        // Character is the wrapper class; autoboxing converts char <-> Character.
        Character boxed = 'z';
        char unboxed = boxed;
        System.out.println("  boxed 'z' compareTo 'a' > 0? " + (boxed.compareTo('a') > 0) + ", unboxed=" + unboxed);

        System.out.println("Escape sequences:");
        System.out.println("  tab:[\t]");
        System.out.println("  quotes: \"double\" and 'single' and " + '\'');
        System.out.println("  backslash: C:\\temp\\notes.txt");
        System.out.println("  \\s is a space: [a\sb]");
        System.out.println("  length of \"\\r\\n\" = " + "\r\n".length());

        // In a text block, \s keeps a trailing space and a trailing \ joins two lines.
        String block = """
                Name:\s
                Very long \
                line
                """;
        System.out.print(block.replace(" ", "."));
    }
}

/* Expected output:
Classifying "Hi 5x_Q":
  'H' letter=true  digit=false space=false upper=true
  'i' letter=true  digit=false space=false upper=false
  ' ' letter=false digit=false space=true  upper=false
  '5' letter=false digit=true  space=false upper=false
  'x' letter=true  digit=false space=false upper=false
  '_' letter=false digit=false space=false upper=false
  'Q' letter=true  digit=false space=false upper=true
Converting:
  toUpperCase('q')       = Q
  toLowerCase('Q')       = q
  (int) '7'              = 55   (the character code)
  getNumericValue('7')   = 7    (the digit's value)
  '7' - '0'              = 7
  boxed 'z' compareTo 'a' > 0? true, unboxed=z
Escape sequences:
  tab:[	]
  quotes: "double" and 'single' and '
  backslash: C:\temp\notes.txt
  \s is a space: [a b]
  length of "\r\n" = 2
Name:.
Very.long.line
*/
