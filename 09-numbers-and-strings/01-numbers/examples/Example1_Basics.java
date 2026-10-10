import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Example 1: wrapper conversions, printf specifiers, DecimalFormat, and Math.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        System.out.println("Text -> number:");
        System.out.println("  parseInt(\"42\")        = " + Integer.parseInt("42"));
        System.out.println("  parseInt(\"ff\", 16)    = " + Integer.parseInt("ff", 16));
        System.out.println("  valueOf(\"333\", 8)     = " + Integer.valueOf("333", 8));
        System.out.println("  decode(\"0x1F\")        = " + Integer.decode("0x1F"));
        System.out.println("  parseDouble(\"3.75\")   = " + Double.parseDouble("3.75"));

        System.out.println("Number -> text:");
        System.out.println("  toBinaryString(10)    = " + Integer.toBinaryString(10));
        System.out.println("  toHexString(255)      = " + Integer.toHexString(255));
        System.out.println("  toString(255, 8)      = " + Integer.toString(255, 8));

        System.out.println("Wrapper methods:");
        Integer boxed = 300;
        System.out.println("  300 as byteValue      = " + boxed.byteValue() + "  (only the low 8 bits survive)");
        System.out.println("  compare(5, 7)         = " + Integer.compare(5, 7));
        System.out.println("  Integer 5 equals 5L?  = " + Integer.valueOf(5).equals(5L));
        System.out.println("  Integer.MAX_VALUE     = " + Integer.MAX_VALUE);

        // Locale.US so the output is the same on every machine.
        System.out.println("printf:");
        System.out.printf(Locale.US, "  [%.2f] [%08.2f] [%+d] [%,d]%n", Math.PI, Math.PI, 5, 1234567);
        System.out.printf(Locale.US, "  [%-6s] [%6s] [%10.3f]%n", "ab", "cd", Math.E);
        System.out.println("  France: " + String.format(Locale.FRANCE, "%.2f", 3.5));

        System.out.println("DecimalFormat:");
        System.out.println("  " + usFormat("###,###.###").format(123456.789));
        System.out.println("  " + usFormat("###.##").format(123456.789));
        System.out.println("  " + usFormat("000000.000").format(123.78));

        System.out.println("Math:");
        System.out.println("  ceil(2.1)=" + Math.ceil(2.1) + " floor(2.9)=" + Math.floor(2.9)
                + " rint(2.5)=" + Math.rint(2.5) + " round(2.5)=" + Math.round(2.5)
                + " round(-2.5)=" + Math.round(-2.5));
        System.out.println("  pow(2,10)=" + Math.pow(2, 10) + " sqrt(144)=" + Math.sqrt(144)
                + " abs(-7)=" + Math.abs(-7) + " max(3,8)=" + Math.max(3, 8));
        System.out.printf(Locale.US, "  sin(30 degrees)=%.4f  log(e)=%.1f%n",
                Math.sin(Math.toRadians(30)), Math.log(Math.E));
    }

    // US symbols so the output is the same on every machine.
    static DecimalFormat usFormat(String pattern) {
        return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.US));
    }
}

/* Expected output:
Text -> number:
  parseInt("42")        = 42
  parseInt("ff", 16)    = 255
  valueOf("333", 8)     = 219
  decode("0x1F")        = 31
  parseDouble("3.75")   = 3.75
Number -> text:
  toBinaryString(10)    = 1010
  toHexString(255)      = ff
  toString(255, 8)      = 377
Wrapper methods:
  300 as byteValue      = 44  (only the low 8 bits survive)
  compare(5, 7)         = -1
  Integer 5 equals 5L?  = false
  Integer.MAX_VALUE     = 2147483647
printf:
  [3.14] [00003.14] [+5] [1,234,567]
  [ab    ] [    cd] [     2.718]
  France: 3,50
DecimalFormat:
  123,456.789
  123456.79
  000123.780
Math:
  ceil(2.1)=3.0 floor(2.9)=2.0 rint(2.5)=2.0 round(2.5)=3 round(-2.5)=-2
  pow(2,10)=1024.0 sqrt(144)=12.0 abs(-7)=7 max(3,8)=8
  sin(30 degrees)=0.5000  log(e)=1.0
*/
