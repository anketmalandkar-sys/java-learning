package oops.abstraction;

/*
 * ============================================================================
 *  INTERFACES IN DEPTH, AND ABSTRACT CLASSES THAT IMPLEMENT THEM
 * ============================================================================
 *
 *  Source: https://dev.java/learn/language/oop/inheritance/abstract-classes
 *  Source: https://dev.java/learn/language/oop/interfaces/defining-interfaces
 *  Source: https://dev.java/learn/language/oop/interfaces/examples
 *
 *  Fills the gaps next to AbstractionDemo.java (template method, the default
 *  diamond, sealed types). Comparator's static/default helpers (comparing,
 *  thenComparing, reversed) are covered in 02-comparator.
 *
 *  INTERFACE MEMBERS
 *     - abstract methods: implicitly public abstract
 *     - default methods (Java 8): have a body, inherited by implementers
 *     - static methods (Java 8): called as InterfaceName.method()
 *     - private methods (Java 9): shared helper code for defaults/statics,
 *       invisible to implementers
 *     - constants: every field is implicitly public static final
 *     - no instance fields, no constructors, and members can't be protected
 *
 *  AN INTERFACE CAN EXTEND SEVERAL INTERFACES
 *     interface SmartDevice extends Switchable, Connectable { ... }
 *
 *  A SUB-INTERFACE HAS THREE CHOICES FOR AN INHERITED DEFAULT METHOD
 *     - say nothing: it inherits the default as is
 *     - override it with a new default body
 *     - REDECLARE IT ABSTRACT (no body): every class implementing the
 *       sub-interface must now write the method itself
 *
 *  NESTED TYPES IN AN INTERFACE
 *     Classes, records, enums and interfaces declared inside an interface are
 *     implicitly public and static, e.g. Map.Entry. Good for small types that
 *     only make sense together with the interface.
 *
 *  EVOLVING A PUBLISHED INTERFACE
 *     Adding an ABSTRACT method breaks every existing implementer (they no
 *     longer compile). Options that don't break them:
 *       - add a DEFAULT method with a sensible implementation
 *       - add a STATIC helper method
 *       - create a SUB-INTERFACE with the new method; new code implements that
 *
 *  INTERFACE-TYPED PARAMETERS
 *     A method like isLargerThan(Relatable other) accepts ANY Relatable. To
 *     reach class-specific data it must cast, and the cast fails at runtime if
 *     a different implementation is passed. Generics (Comparable<T>) avoid it.
 *
 *  ABSTRACT CLASSES
 *     - A subclass that doesn't implement every inherited abstract method must
 *       itself be declared abstract.
 *     - An abstract class can implement an interface PARTIALLY; its first
 *       concrete subclass must supply the rest.
 *     - Abstract classes can have static fields and methods, called through
 *       the class name.
 *     - JDK example: AbstractMap implements most of Map; HashMap and TreeMap
 *       extend it and add the rest.
 */
interface Switchable {
    int MAX_WATTS = 2000;                    // implicitly public static final

    void turnOn();

    void turnOff();

    boolean isOn();

    // Added AFTER the interface was published: a default method keeps old
    // implementers compiling.
    default void toggle() {
        log("toggle");
        if (isOn()) {
            turnOff();
        } else {
            turnOn();
        }
    }

    default String status() {
        log("status");
        return isOn() ? "ON" : "OFF";
    }

    // private helper shared by the default methods; implementers can't see it.
    private void log(String action) {
        DeviceLog.record(getClass().getSimpleName() + "." + action);
    }

    static String describeLimit() {
        return "max " + MAX_WATTS + " W";
    }
}

interface Connectable {
    String network();
}

// One interface extending two.
interface SmartDevice extends Switchable, Connectable {
    default String summary() {
        return network() + ":" + status();
    }
}

final class DeviceLog {
    static final StringBuilder LINES = new StringBuilder();

    static void record(String line) {
        LINES.append(line).append(' ');
    }
}

// Abstract class implementing the interface PARTIALLY: isOn/turnOn/turnOff,
// but not network(). It also has state and a static member.
abstract class AbstractDevice implements SmartDevice {
    private static int created = 0;
    private boolean on;
    protected final String name;

    protected AbstractDevice(String name) {
        this.name = name;
        created++;
    }

    @Override
    public void turnOn() {
        on = true;
    }

    @Override
    public void turnOff() {
        on = false;
    }

    @Override
    public boolean isOn() {
        return on;
    }

    static int createdCount() {
        return created;
    }

    // network() is still abstract here.
}

class SmartBulb extends AbstractDevice {
    SmartBulb(String name) {
        super(name);
    }

    @Override
    public String network() {
        return "wifi/" + name;
    }
}

// abstract class HalfPlug extends AbstractDevice { }   // fine: still abstract
// class BrokenPlug extends AbstractDevice { }          // COMPILE ERROR: doesn't implement network()

interface Shape {
    default String describe() {
        return "some shape";
    }

    // Nested types: implicitly public static. Used as Shape.Size and Shape.Kind.
    record Size(int width, int height) { }

    enum Kind { ROUND, ANGULAR }

    Kind kind();
}

// Re-abstracts the inherited default: implementers can no longer rely on "some shape".
interface DescribedShape extends Shape {
    @Override
    String describe();
}

class Circle implements Shape {               // keeps Shape's default describe()
    @Override
    public Kind kind() {
        return Kind.ROUND;
    }
}

class Square implements DescribedShape {      // MUST write describe()
    private final Shape.Size size = new Shape.Size(3, 3);

    @Override
    public String describe() {
        return "square " + size.width() + "x" + size.height();
    }

    @Override
    public Kind kind() {
        return Kind.ANGULAR;
    }
}

// class Blob implements DescribedShape {     // COMPILE ERROR: doesn't implement describe()
//     public Kind kind() { return Kind.ROUND; }
// }

// Interface-typed parameter: the cast is the weak spot.
interface Relatable {
    int isLargerThan(Relatable other);
}

class Box implements Relatable {
    final int volume;

    Box(int volume) {
        this.volume = volume;
    }

    @Override
    public int isLargerThan(Relatable other) {
        Box otherBox = (Box) other;          // ClassCastException if it's not a Box
        return Integer.compare(volume, otherBox.volume);
    }
}

class Parcel implements Relatable {
    @Override
    public int isLargerThan(Relatable other) {
        return 0;
    }
}

public class InterfaceAndAbstractRules {

    // Works for ANY Relatable: the interface used as a type.
    static Relatable larger(Relatable a, Relatable b) {
        return a.isLargerThan(b) >= 0 ? a : b;
    }

    public static void main(String[] args) {
        System.out.println("=== Interface members ===");
        SmartBulb bulb = new SmartBulb("desk");
        bulb.toggle();
        System.out.println("after toggle: " + bulb.status());
        System.out.println("summary(): " + bulb.summary());
        System.out.println("constant: Switchable.MAX_WATTS = " + Switchable.MAX_WATTS);
        System.out.println("static: " + Switchable.describeLimit());
        System.out.println("private helper logged: " + DeviceLog.LINES.toString().trim());
        // bulb.log("x");                       // COMPILE ERROR: private interface method

        System.out.println("\n=== One object, many types ===");
        Switchable s = bulb;
        Connectable c = bulb;
        System.out.println("as Switchable: " + s.isOn() + ", as Connectable: " + c.network());
        System.out.println("AbstractDevice.createdCount() = " + AbstractDevice.createdCount());

        System.out.println("\n=== Interface-typed parameter and its cast ===");
        Box small = new Box(2);
        Box big = new Box(9);
        System.out.println("larger box volume: " + ((Box) larger(small, big)).volume);
        try {
            small.isLargerThan(new Parcel());
        } catch (ClassCastException e) {
            System.out.println("Box.isLargerThan(Parcel) -> ClassCastException: generics (Comparable<Box>) would catch this at compile time");
        }

        System.out.println("\n=== Re-abstracted default, nested types ===");
        Shape circle = new Circle();
        Shape square = new Square();
        System.out.println("Circle (inherits the default): " + circle.describe() + ", " + circle.kind());
        System.out.println("Square (had to write it):      " + square.describe() + ", " + square.kind());
        Shape.Size size = new Shape.Size(4, 2);       // nested record: no Shape object needed (static)
        System.out.println("Shape.Size is static? " + java.lang.reflect.Modifier.isStatic(Shape.Size.class.getModifiers())
                + ", public? " + java.lang.reflect.Modifier.isPublic(Shape.Size.class.getModifiers()) + ", " + size);
    }
}
