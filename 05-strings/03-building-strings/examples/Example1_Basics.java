/**
 * Example 1: StringBuilder, and why += in a loop is slow.
 *   1. The cost of += in a loop, measured against StringBuilder.
 *   2. StringBuilder operations: append (chained), insert, reverse, delete, setLength.
 *   3. Gotchas: new StringBuilder(char), and equals on builders.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    static final int LINES = 20_000;

    static String withPlusEquals() {
        String text = "";
        for (int i = 0; i < LINES; i++) {
            text += "order-" + i + ",paid\n";       // copies all of text, every time
        }
        return text;
    }

    static String withBuilder() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < LINES; i++) {
            text.append("order-").append(i).append(",paid\n");   // appends in place
        }
        return text.toString();
    }

    public static void main(String[] args) {
        System.out.println("--- 1. += vs StringBuilder, " + LINES + " lines ---");
        long start = System.nanoTime();
        String slow = withPlusEquals();
        long plusMs = (System.nanoTime() - start) / 1_000_000;

        start = System.nanoTime();
        String fast = withBuilder();
        long builderMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("same text? " + slow.equals(fast) + ", " + fast.length() + " characters");
        System.out.println("+= took " + plusMs + " ms, StringBuilder took " + builderMs + " ms");
        System.out.println("StringBuilder at least 10x faster? " + (builderMs * 10 <= plusMs));

        System.out.println();
        System.out.println("--- 2. StringBuilder operations ---");
        StringBuilder sb = new StringBuilder("Order");
        sb.append(' ').append(101).append(": ").append(2499.5);   // append takes any type
        System.out.println("append:       " + sb);
        sb.insert(0, "[").append(']');
        System.out.println("insert:       " + sb);
        sb.replace(1, 6, "Invoice");
        System.out.println("replace:      " + sb);
        sb.deleteCharAt(0).deleteCharAt(sb.length() - 1);
        System.out.println("deleteCharAt: " + sb);
        System.out.println("reverse:      " + new StringBuilder("stressed").reverse());
        sb.setLength(0);                                            // reuse the same builder
        System.out.println("after setLength(0): [" + sb + "], length " + sb.length());

        System.out.println();
        System.out.println("--- 3. Gotchas ---");
        StringBuilder fromChar = new StringBuilder('[');            // '[' is 91: used as the CAPACITY
        fromChar.append("items");
        System.out.println("new StringBuilder('[') + items:  " + fromChar);
        System.out.println("new StringBuilder(\"[\") + items: " + new StringBuilder("[").append("items"));

        StringBuilder a = new StringBuilder("same");
        StringBuilder b = new StringBuilder("same");
        System.out.println("a.equals(b):              " + a.equals(b) + "  (identity, not content)");
        System.out.println("a.compareTo(b) == 0:      " + (a.compareTo(b) == 0));
        System.out.println("\"same\".equals(a):         " + "same".equals(a));
        System.out.println("\"same\".contentEquals(a):  " + "same".contentEquals(a));
    }
}

/* Expected output (the timings vary by machine; the rest is fixed):
--- 1. += vs StringBuilder, 20000 lines ---
same text? true, 328890 characters
+= took 531 ms, StringBuilder took 2 ms
StringBuilder at least 10x faster? true

--- 2. StringBuilder operations ---
append:       Order 101: 2499.5
insert:       [Order 101: 2499.5]
replace:      [Invoice 101: 2499.5]
deleteCharAt: Invoice 101: 2499.5
reverse:      desserts
after setLength(0): [], length 0

--- 3. Gotchas ---
new StringBuilder('[') + items:  items
new StringBuilder("[") + items: [items
a.equals(b):              false  (identity, not content)
a.compareTo(b) == 0:      true
"same".equals(a):         false
"same".contentEquals(a):  true
*/
