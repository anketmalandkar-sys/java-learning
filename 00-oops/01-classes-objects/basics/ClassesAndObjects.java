package oops.basics;

/*
 * ============================================================================
 *  CLASSES AND OBJECTS — the foundation of OOP
 * ============================================================================
 *
 *  CLASS  = a blueprint / template. It describes WHAT data (fields) and
 *           WHAT behaviour (methods) its objects will have. No memory is
 *           allocated for instance fields until an object is created.
 *
 *  OBJECT = a concrete instance of a class, created with `new`. It lives on
 *           the heap and has:
 *             - STATE    -> the current values of its fields
 *             - BEHAVIOR -> the methods you can call on it
 *             - IDENTITY -> its own place in memory (two objects with equal
 *                           state are still two different objects)
 *
 *  Analogy: the class is the architect's drawing of a house; objects are the
 *  actual houses built from that drawing. One drawing, many houses.
 */
class Car {

    // ---------------------------------------------------------------------
    // INSTANCE FIELDS — every Car object gets its OWN copy of these.
    // ---------------------------------------------------------------------
    String brand;
    String color;
    int speed;

    // ---------------------------------------------------------------------
    // STATIC FIELD — ONE copy shared by the whole class (not per object).
    // Lives with the class itself, so we can read it as Car.totalCarsBuilt
    // without any object.
    // ---------------------------------------------------------------------
    static int totalCarsBuilt = 0;

    // ---------------------------------------------------------------------
    // CONSTRUCTORS — special methods that run when `new Car(...)` is called.
    //   * same name as the class, NO return type
    //   * if you write no constructor at all, Java silently adds a
    //     no-arg "default constructor". As soon as you write ANY
    //     constructor, that free default disappears.
    //   * constructors can be OVERLOADED (same name, different parameters)
    // ---------------------------------------------------------------------

    // No-arg constructor. `this(...)` calls ANOTHER constructor of the same
    // class (constructor chaining). It must be the FIRST statement.
    Car() {
        this("Unknown", "White");
    }

    // Two-arg constructor chains to the three-arg one.
    Car(String brand, String color) {
        this(brand, color, 0);
    }

    // The "main" constructor — all others funnel into this one, so the
    // initialisation logic is written exactly once.
    Car(String brand, String color, int speed) {
        // `this.brand` is the FIELD, plain `brand` is the PARAMETER.
        // `this` = a reference to the current object.
        this.brand = brand;
        this.color = color;
        this.speed = speed;
        totalCarsBuilt++;           // shared counter goes up for every object
    }

    // ---------------------------------------------------------------------
    // INSTANCE METHODS — operate on a specific object's state.
    // ---------------------------------------------------------------------
    void accelerate(int amount) {
        speed += amount;
    }

    // Method overloading: same name, different parameter list.
    void accelerate() {
        accelerate(10);
    }

    String describe() {
        return color + " " + brand + " going " + speed + " km/h";
    }

    // ---------------------------------------------------------------------
    // STATIC METHOD — belongs to the class; has NO `this`, so it cannot
    // touch instance fields directly (which car's speed would it mean?).
    // ---------------------------------------------------------------------
    static String factoryReport() {
        return "Cars built so far: " + totalCarsBuilt;
    }
}

/*
 * A tiny class used to show `==` vs `equals`.
 * It overrides equals/hashCode so two Points with the same x,y are "equal".
 * (Rule: if you override equals you MUST override hashCode as well, otherwise
 *  HashMap/HashSet break.)
 */
class Point {
    final int x;
    final int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                  // same object
        if (!(o instanceof Point other)) return false; // wrong type / null
        return x == other.x && y == other.y;         // same state
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {                       // used by println
        return "Point(" + x + ", " + y + ")";
    }
}

public class ClassesAndObjects {

    public static void main(String[] args) {
        System.out.println("=== Creating objects ===");
        Car a = new Car();                         // no-arg -> chains twice
        Car b = new Car("Tesla", "Red");           // 2-arg -> chains once
        Car c = new Car("BMW", "Black", 40);       // 3-arg directly

        b.accelerate(50);   // overloaded method with an int
        c.accelerate();     // overloaded method with no args (+10)

        System.out.println(a.describe());   // White Unknown going 0 km/h
        System.out.println(b.describe());   // Red Tesla going 50 km/h
        System.out.println(c.describe());   // Black BMW going 50 km/h

        // Static members are accessed through the CLASS name.
        System.out.println(Car.factoryReport());   // Cars built so far: 3

        System.out.println("\n=== References, not copies ===");
        // A variable of a class type holds a REFERENCE (an arrow) to the
        // object, not the object itself.
        Car alias = b;            // both arrows now point to the SAME object
        alias.accelerate(5);
        System.out.println("b via alias: " + b.describe()); // 55 km/h — b changed too!

        System.out.println("\n=== == vs equals ===");
        Point p1 = new Point(1, 2);
        Point p2 = new Point(1, 2);
        Point p3 = p1;
        // == compares IDENTITY (are these the same object in memory?)
        System.out.println("p1 == p2      : " + (p1 == p2));      // false
        System.out.println("p1 == p3      : " + (p1 == p3));      // true
        // equals compares STATE (as defined by the class)
        System.out.println("p1.equals(p2) : " + p1.equals(p2));   // true
        System.out.println("toString      : " + p1);              // Point(1, 2)
    }
}
