package oops.polymorphism;

import java.util.List;

/*
 * ============================================================================
 *  POLYMORPHISM — "one name, many forms"
 * ============================================================================
 *
 *  1. COMPILE-TIME (static) polymorphism = METHOD OVERLOADING
 *     Same method name, different PARAMETER LIST (number/type/order).
 *     The compiler picks which one to call by looking at the arguments.
 *     Return type alone is NOT enough to overload.
 *
 *  2. RUNTIME (dynamic) polymorphism = METHOD OVERRIDING
 *     A subclass redefines a parent method with the same signature.
 *     When you call it through a PARENT-type reference, the JVM looks at the
 *     ACTUAL object at runtime and runs that object's version.
 *     This is called DYNAMIC DISPATCH (or late binding).
 *
 *  Overriding rules:
 *     - same name + same parameters
 *     - return type: same or a SUBTYPE (covariant return)
 *     - access cannot be NARROWED (public -> private is an error)
 *     - cannot throw broader CHECKED exceptions
 *     - static, private and final methods are NOT overridden
 *       (static methods are "hidden" — see the gotcha below)
 */

class Shape {
    String name() { return "Shape"; }

    double area() { return 0; }

    static String kind() { return "static Shape.kind()"; }
}

class Circle extends Shape {
    private final double r;
    Circle(double r) { this.r = r; }

    @Override String name()  { return "Circle"; }
    @Override double area()  { return Math.PI * r * r; }

    // This HIDES Shape.kind(); it does NOT override it.
    static String kind() { return "static Circle.kind()"; }
}

class Rectangle extends Shape {
    private final double w, h;
    Rectangle(double w, double h) { this.w = w; this.h = h; }

    @Override String name() { return "Rectangle"; }
    @Override double area() { return w * h; }

    // Method only rectangles have.
    boolean isSquare() { return w == h; }
}

class Triangle extends Shape {
    private final double b, h;
    Triangle(double b, double h) { this.b = b; this.h = h; }

    @Override String name() { return "Triangle"; }
    @Override double area() { return 0.5 * b * h; }
}

/* Compile-time polymorphism: one name, several parameter lists. */
class AreaCalculator {
    static double area(double side)                { return side * side; }       // square
    static double area(double w, double h)         { return w * h; }             // rectangle
    static double area(double a, double b, double c) {                            // triangle (Heron)
        double s = (a + b + c) / 2;
        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }
    static String area(String shapeName)           { return "Unknown area for " + shapeName; }
}

public class PolymorphismDemo {

    public static void main(String[] args) {
        System.out.println("=== Compile-time: overloading ===");
        System.out.println(AreaCalculator.area(4));          // 16.0
        System.out.println(AreaCalculator.area(4, 5));       // 20.0
        System.out.println(AreaCalculator.area(3, 4, 5));    // 6.0
        System.out.println(AreaCalculator.area("blob"));

        System.out.println("\n=== Runtime: overriding + dynamic dispatch ===");
        // UPCASTING: a Circle IS-A Shape, so it can be stored in a Shape
        // variable. This is automatic and always safe.
        List<Shape> shapes = List.of(
                new Circle(1),
                new Rectangle(2, 3),
                new Triangle(4, 5),
                new Rectangle(2, 2));

        // ONE loop, written only against Shape, works for every subclass —
        // including ones written next year. That is the real power of
        // polymorphism: code depends on the abstraction, not the details.
        for (Shape s : shapes) {
            System.out.printf("%-10s area = %.2f%n", s.name(), s.area());
        }

        System.out.println("\n=== Downcasting & instanceof pattern matching ===");
        for (Shape s : shapes) {
            // s.isSquare();   // COMPILE ERROR: the reference type is Shape
            // We must DOWNCAST to reach Rectangle-only methods. Checking with
            // instanceof first avoids a ClassCastException.
            if (s instanceof Rectangle rect) {          // Java 16+: test + cast + bind
                System.out.println("Rectangle, square? " + rect.isSquare());
            }
        }
        try {
            Shape s = new Circle(2);
            Rectangle r = (Rectangle) s;                // compiles, fails at runtime
            System.out.println(r);
        } catch (ClassCastException e) {
            System.out.println("ClassCastException: a Circle is not a Rectangle");
        }

        System.out.println("\n=== Gotcha: static methods are NOT polymorphic ===");
        Shape ref = new Circle(1);
        System.out.println(ref.name());   // "Circle"  -> instance method, uses the OBJECT's type
        System.out.println(Shape.kind()); // static, resolved by the CLASS named at compile time
        System.out.println(Circle.kind());
        // (Calling ref.kind() would also print "static Shape.kind()", because
        //  static calls are bound to the REFERENCE type, not the object.)
    }
}
