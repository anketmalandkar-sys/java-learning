/**
 * Example 1: each operator group in a few lines.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        System.out.println("Arithmetic:");
        System.out.println("  7 / 2   = " + (7 / 2) + "    (int division drops the fraction)");
        System.out.println("  7 % 2   = " + (7 % 2));
        System.out.println("  7 / 2.0 = " + (7 / 2.0));
        System.out.println("  -7 % 2  = " + (-7 % 2) + "   (sign of the left operand)");
        // Parentheses matter: without them, + concatenates left to right.
        System.out.println("  \"sum: \" + 2 + 3   = " + ("sum: " + 2 + 3));
        System.out.println("  \"sum: \" + (2 + 3) = " + ("sum: " + (2 + 3)));

        System.out.println("Compound assignment:");
        int stock = 10;
        stock -= 3;
        stock *= 2;
        System.out.println("  stock = " + stock);

        System.out.println("Prefix vs postfix:");
        int x = 5;
        int a = x++;    // a gets the OLD value
        int b = ++x;    // b gets the NEW value
        System.out.println("  a=" + a + " b=" + b + " x=" + x);

        System.out.println("Relational and conditional:");
        int age = 20;
        boolean hasTicket = true;
        System.out.println("  age >= 18 && hasTicket = " + (age >= 18 && hasTicket));
        System.out.println("  age < 13 || age > 65   = " + (age < 13 || age > 65));
        System.out.println("  !hasTicket             = " + !hasTicket);

        System.out.println("Ternary:");
        int seatsLeft = 0;
        String status = seatsLeft > 0 ? "Book now" : "Sold out";
        System.out.println("  " + status);

        System.out.println("instanceof:");
        Object value = "hello";
        Object nothing = null;
        System.out.println("  \"hello\" instanceof String = " + (value instanceof String));
        System.out.println("  null instanceof String    = " + (nothing instanceof String));

        System.out.println("Bitwise:");
        int p = 0b1100;
        int q = 0b1010;
        System.out.println("  p & q = " + Integer.toBinaryString(p & q));
        System.out.println("  p | q = " + Integer.toBinaryString(p | q));
        System.out.println("  p ^ q = " + Integer.toBinaryString(p ^ q));
        System.out.println("  1 << 3 = " + (1 << 3));
        System.out.println("  -16 >> 2 = " + (-16 >> 2) + ", -16 >>> 28 = " + (-16 >>> 28));
    }
}

/* Expected output:
Arithmetic:
  7 / 2   = 3    (int division drops the fraction)
  7 % 2   = 1
  7 / 2.0 = 3.5
  -7 % 2  = -1   (sign of the left operand)
  "sum: " + 2 + 3   = sum: 23
  "sum: " + (2 + 3) = sum: 5
Compound assignment:
  stock = 14
Prefix vs postfix:
  a=5 b=7 x=7
Relational and conditional:
  age >= 18 && hasTicket = true
  age < 13 || age > 65   = false
  !hasTicket             = false
Ternary:
  Sold out
instanceof:
  "hello" instanceof String = true
  null instanceof String    = false
Bitwise:
  p & q = 1000
  p | q = 1110
  p ^ q = 110
  1 << 3 = 8
  -16 >> 2 = -4, -16 >>> 28 = 15
*/
