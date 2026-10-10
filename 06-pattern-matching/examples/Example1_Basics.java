import java.util.List;

/**
 * Example 1: the three kinds of pattern (Java 21).
 *   1. instanceof with a type pattern: scope after && and after an early return.
 *   2. A cleaner equals() with instanceof.
 *   3. switch with type patterns, case null, and when guards.
 *   4. Record patterns: deconstruction, var, and nesting.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // ---------- 1. instanceof patterns ----------

    static String shout(Object obj) {
        if (obj instanceof String s && !s.isBlank()) {   // s is usable right after &&
            return s.toUpperCase() + "!";
        }
        return "(nothing to shout)";
    }

    static String doubled(Object obj) {
        if (!(obj instanceof Integer n)) {
            return "not a number";                        // leave early...
        }
        return "doubled: " + n * 2;                       // ...so n is in scope from here on
    }

    // ---------- 2. equals with a pattern ----------

    static final class Point2D {
        private final int x;
        private final int y;

        Point2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            // test, cast and compare in one expression; false for null and other types
            return o instanceof Point2D other && x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    // ---------- 3. switch with type patterns and guards ----------

    static String describe(Object obj) {
        return switch (obj) {
            case null                       -> "null";
            case String s when s.isBlank()  -> "blank text";
            case String s                   -> "text \"" + s + "\" (" + s.length() + " chars)";
            case Integer i when i < 0       -> "negative int " + i;
            case Integer i                  -> "int " + i;
            case List<?> list               -> "list of " + list.size();
            case int[] numbers              -> "int[] of " + numbers.length;
            default                         -> "other: " + obj.getClass().getSimpleName();
        };
    }

    // ---------- 4. record patterns ----------

    record Point(int x, int y) {}
    record Line(Point start, Point end) {}

    static String classify(Object obj) {
        return switch (obj) {
            case Point(var x, var y) when x == 0 && y == 0       -> "the origin";
            case Point(int x, int y) when x == y                 -> "a point on the diagonal at " + x;
            case Point(int x, int y)                             -> "a point at (" + x + ", " + y + ")";
            // nested: take the Line apart AND both Points inside it
            case Line(Point(var x1, var y1), Point(var x2, var y2)) when x1 == x2 -> "a vertical line at x=" + x1;
            case Line(Point(var x1, var y1), Point(var x2, var y2)) when y1 == y2 -> "a horizontal line at y=" + y1;
            case Line(Point start, Point end)                    -> "a sloped line from " + start + " to " + end;
            default                                              -> "not geometry";
        };
    }

    public static void main(String[] args) {
        System.out.println("--- 1. instanceof patterns ---");
        System.out.println(shout("hello"));
        System.out.println(shout("   "));
        System.out.println(shout(42));
        System.out.println(doubled(21));
        System.out.println(doubled("21"));

        System.out.println();
        System.out.println("--- 2. equals ---");
        Point2D a = new Point2D(1, 2);
        System.out.println("equal points:    " + a.equals(new Point2D(1, 2)));
        System.out.println("different point: " + a.equals(new Point2D(2, 1)));
        System.out.println("a String:        " + a.equals("(1, 2)"));
        System.out.println("null:            " + a.equals(null));

        System.out.println();
        System.out.println("--- 3. switch with type patterns ---");
        Object[] things = {"Pune", "  ", -5, 42, List.of(1, 2, 3), new int[4], 3.14, null};
        for (Object thing : things) {
            System.out.println(describe(thing));
        }

        System.out.println();
        System.out.println("--- 4. record patterns ---");
        Object[] shapes = {
                new Point(0, 0), new Point(3, 3), new Point(2, 5),
                new Line(new Point(1, 0), new Point(1, 9)),
                new Line(new Point(0, 4), new Point(7, 4)),
                new Line(new Point(0, 0), new Point(2, 3)),
                "circle"};
        for (Object shape : shapes) {
            System.out.println(classify(shape));
        }
    }
}

/* Expected output:
--- 1. instanceof patterns ---
HELLO!
(nothing to shout)
(nothing to shout)
doubled: 42
not a number

--- 2. equals ---
equal points:    true
different point: false
a String:        false
null:            false

--- 3. switch with type patterns ---
text "Pune" (4 chars)
blank text
negative int -5
int 42
list of 3
int[] of 4
other: Double
null

--- 4. record patterns ---
the origin
a point on the diagonal at 3
a point at (2, 5)
a vertical line at x=1
a horizontal line at y=4
a sloped line from Point[x=0, y=0] to Point[x=2, y=3]
not geometry
*/
