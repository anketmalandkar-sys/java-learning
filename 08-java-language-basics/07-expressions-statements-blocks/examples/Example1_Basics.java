/**
 * Example 1: expressions and their types, the kinds of statements, and block scope.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        // Declaration statements.
        int qty = 3;
        double price = 49.5;
        String name = "Pen";

        // Expressions of different types.
        System.out.println("qty * 2        -> " + (qty * 2));
        System.out.println("price > 100    -> " + (price > 100));
        System.out.println("\"Item: \" + name -> " + ("Item: " + name));

        // An assignment is an expression: its value is the value assigned.
        int count;
        System.out.println("(count = 7)    -> " + (count = 7));

        // Chained assignment works because of that.
        int a;
        int b;
        a = b = 4;
        System.out.println("a=" + a + " b=" + b);

        // Parentheses change the grouping of a compound expression.
        System.out.println("10 + 20 / 5    -> " + (10 + 20 / 5));
        System.out.println("(10 + 20) / 5  -> " + ((10 + 20) / 5));

        // Expression statements: assignment, increment, method call, object creation.
        qty += 2;
        qty++;
        System.out.println("qty is now " + qty);
        new StringBuilder("unused");    // legal, though pointless here

        // A block creates a scope.
        if (qty > 5) {
            double discount = 0.1;      // only visible inside these braces
            price = price - price * discount;
        }
        // discount is out of scope here; using it would not compile.
        System.out.println("price after discount: " + price);

        // A bare block is legal too, and limits a temporary variable's life.
        {
            int temp = qty * 10;
            System.out.println("temp inside block: " + temp);
        }

        // Floating-point expressions round at each step.
        double sum = 0.1 + 0.2;
        System.out.println("0.1 + 0.2 == 0.3          -> " + (sum == 0.3));
        System.out.println("|0.1 + 0.2 - 0.3| < 1e-9  -> " + (Math.abs(sum - 0.3) < 1e-9));
    }
}

/* Expected output:
qty * 2        -> 6
price > 100    -> false
"Item: " + name -> Item: Pen
(count = 7)    -> 7
a=4 b=4
10 + 20 / 5    -> 14
(10 + 20) / 5  -> 6
qty is now 6
price after discount: 44.55
temp inside block: 60
0.1 + 0.2 == 0.3          -> false
|0.1 + 0.2 - 0.3| < 1e-9  -> true
*/
