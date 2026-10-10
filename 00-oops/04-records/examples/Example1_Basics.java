import java.util.HashSet;
import java.util.Set;

/**
 * Example 1: what a record gives you, and its three kinds of constructors.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    record Point(int x, int y) {
        // A static field and a static factory are allowed.
        static final Point ORIGIN = new Point(0, 0);

        static Point onDiagonal(int n) {
            return new Point(n, n);
        }

        // An ordinary method using the components.
        double distanceTo(Point other) {
            return Math.hypot(x - other.x, y - other.y);
        }
    }

    record Range(int start, int end) {
        // Compact canonical constructor: validate; fields are assigned after this block.
        Range {
            if (start > end) {
                throw new IllegalArgumentException("start " + start + " > end " + end);
            }
        }

        // Any other constructor must call the canonical one.
        Range(int single) {
            this(single, single);
        }

        int length() {
            return end - start;
        }
    }

    record Email(String value) {
        // Normalising: reassign the parameter, not the field.
        Email {
            value = value.strip().toLowerCase();
        }
    }

    public static void main(String[] args) {
        Point a = new Point(3, 4);
        Point b = new Point(3, 4);
        System.out.println("toString:  " + a);
        System.out.println("accessors: x()=" + a.x() + " y()=" + a.y());
        System.out.println("a.equals(b)? " + a.equals(b) + ", a == b? " + (a == b)
                + ", same hashCode? " + (a.hashCode() == b.hashCode()));

        Set<Point> visited = new HashSet<>();
        visited.add(a);
        System.out.println("set contains an equal Point? " + visited.contains(new Point(3, 4)));
        System.out.println("distance to origin: " + a.distanceTo(Point.ORIGIN));
        System.out.println("onDiagonal(2): " + Point.onDiagonal(2));

        System.out.println("Range(2, 7).length() = " + new Range(2, 7).length());
        System.out.println("Range(5) = " + new Range(5));
        try {
            new Range(9, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("Range(9, 1) rejected: " + e.getMessage());
        }

        System.out.println("Email(\"  Asha@Mail.COM \") = " + new Email("  Asha@Mail.COM "));

        // Every record extends java.lang.Record and is final.
        System.out.println("superclass: " + Point.class.getSuperclass().getSimpleName()
                + ", isRecord: " + Point.class.isRecord());
    }
}

/* Expected output:
toString:  Point[x=3, y=4]
accessors: x()=3 y()=4
a.equals(b)? true, a == b? false, same hashCode? true
set contains an equal Point? true
distance to origin: 5.0
onDiagonal(2): Point[x=2, y=2]
Range(2, 7).length() = 5
Range(5) = Range[start=5, end=5]
Range(9, 1) rejected: start 9 > end 1
Email("  Asha@Mail.COM ") = Email[value=asha@mail.com]
superclass: Record, isRecord: true
*/
