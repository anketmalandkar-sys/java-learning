/**
 * Example 1: how precedence and associativity group an expression.
 *
 * Each line prints the expression without parentheses, then with the parentheses
 * Java effectively adds. Both columns always print the same value.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        int a = 10;
        int b = 4;
        int c = 3;

        System.out.println("Precedence:");
        show("a + b * c", a + b * c, "a + (b * c)", a + (b * c));
        show("a - b + c", a - b + c, "(a - b) + c", (a - b) + c);
        show("a % b * c", a % b * c, "(a % b) * c", (a % b) * c);
        show("1 << c + 1", 1 << c + 1, "1 << (c + 1)", 1 << (c + 1));
        show("-a * -b", -a * -b, "(-a) * (-b)", (-a) * (-b));

        System.out.println("Comparisons before logic, && before ||:");
        boolean x = a > b || b > c && c > a;
        boolean xGrouped = (a > b) || ((b > c) && (c > a));
        System.out.println("  a > b || b > c && c > a  = " + x + "   grouped: " + xGrouped);

        System.out.println("Bitwise vs ==:");
        int flags = 0b0110;
        int mask = 0b0100;
        // flags & mask == mask   would not compile: == binds tighter than &
        System.out.println("  (flags & mask) == mask = " + ((flags & mask) == mask));

        System.out.println("Right-associative assignment:");
        int p;
        int q;
        int r;
        p = q = r = 7;
        System.out.println("  p=" + p + " q=" + q + " r=" + r);

        System.out.println("Strings and + (left to right):");
        System.out.println("  \"x\" + 1 + 2 = " + ("x" + 1 + 2));
        System.out.println("  1 + 2 + \"x\" = " + (1 + 2 + "x"));

        System.out.println("Operands are still evaluated left to right:");
        int result = trace("first", 1) + trace("second", 2) * trace("third", 3);
        System.out.println("  result = " + result);
    }

    static void show(String plain, int plainValue, String grouped, int groupedValue) {
        System.out.printf("  %-12s = %-4d %-14s = %d%n", plain, plainValue, grouped, groupedValue);
    }

    static int trace(String label, int value) {
        System.out.println("  evaluating " + label);
        return value;
    }
}

/* Expected output:
Precedence:
  a + b * c    = 22   a + (b * c)    = 22
  a - b + c    = 9    (a - b) + c    = 9
  a % b * c    = 6    (a % b) * c    = 6
  1 << c + 1   = 16   1 << (c + 1)   = 16
  -a * -b      = 40   (-a) * (-b)    = 40
Comparisons before logic, && before ||:
  a > b || b > c && c > a  = true   grouped: true
Bitwise vs ==:
  (flags & mask) == mask = true
Right-associative assignment:
  p=7 q=7 r=7
Strings and + (left to right):
  "x" + 1 + 2 = x12
  1 + 2 + "x" = 3x
Operands are still evaluated left to right:
  evaluating first
  evaluating second
  evaluating third
  result = 7
*/
