package oops.abstraction;

/*
 * ============================================================================
 *  ABSTRACTION — show WHAT an object does, hide HOW it does it
 * ============================================================================
 *
 *  You drive a car with a steering wheel and pedals; you don't need to know
 *  about fuel injection. Abstraction gives the caller a simple contract and
 *  hides the implementation behind it.
 *
 *  Encapsulation vs Abstraction (common interview question):
 *     Encapsulation = HIDING DATA (private fields, guarded access).
 *     Abstraction   = HIDING COMPLEXITY (exposing only the essential
 *                     operations through abstract classes / interfaces).
 *
 *  Java tools:
 *    ABSTRACT CLASS
 *      - declared with `abstract`, cannot be instantiated
 *      - can have abstract methods (no body) AND concrete methods
 *      - can have fields, constructors, any access modifier
 *      - a class can extend only ONE abstract class
 *      - use it when subclasses share STATE and CODE ("is-a" family)
 *
 *    INTERFACE
 *      - a pure contract: "anything that implements me can do X"
 *      - methods are public abstract by default; since Java 8 they can have
 *        `default` and `static` methods, since Java 9 `private` helpers
 *      - fields are always public static final (constants)
 *      - a class can implement MANY interfaces
 *      - use it for CAPABILITIES shared by unrelated classes ("can-do")
 */

// --------------------------------------------------------------------------
// Abstract class + TEMPLATE METHOD pattern:
// the parent fixes the algorithm's skeleton, children fill in the steps.
// --------------------------------------------------------------------------
abstract class Vehicle {
    private final String model;            // abstract classes CAN hold state

    protected Vehicle(String model) {      // ...and have constructors
        this.model = model;
    }

    // The fixed algorithm. final so no subclass can break the order.
    public final void startJourney() {
        System.out.println("[" + model + "]");
        checkFuel();
        startEngine();
        System.out.println("  Journey started.");
    }

    // Steps every vehicle MUST implement in its own way.
    protected abstract void checkFuel();
    protected abstract void startEngine();
}

class PetrolCar extends Vehicle {
    PetrolCar() { super("Petrol Car"); }

    @Override protected void checkFuel()   { System.out.println("  Checking petrol tank..."); }
    @Override protected void startEngine() { System.out.println("  Turning key, engine roars."); }
}

class ElectricCar extends Vehicle {
    ElectricCar() { super("Electric Car"); }

    @Override protected void checkFuel()   { System.out.println("  Checking battery %..."); }
    @Override protected void startEngine() { System.out.println("  Pressing START, silent motor on."); }
}

// --------------------------------------------------------------------------
// Interfaces — multiple inheritance of TYPE.
// Both declare a default method with the same signature -> the "diamond".
// Java forces the implementing class to resolve the conflict explicitly.
// --------------------------------------------------------------------------
interface Payable {
    double TAX_RATE = 0.18;               // implicitly public static final

    double amount();                      // implicitly public abstract

    default String receipt() {            // default method (Java 8+)
        return "Paid " + withTax();
    }

    private double withTax() {            // private helper (Java 9+)
        return amount() * (1 + TAX_RATE);
    }

    static Payable of(double amount) {    // static factory method
        return () -> amount;              // lambda: Payable has 1 abstract method
    }
}

interface Refundable {
    default String receipt() {
        return "Refundable within 30 days";
    }
}

class Order implements Payable, Refundable {
    private final double price;

    Order(double price) { this.price = price; }

    @Override
    public double amount() { return price; }

    // Both parents give receipt() -> we MUST override and choose.
    // Interface.super.method() calls a specific parent's default.
    @Override
    public String receipt() {
        return Payable.super.receipt() + " | " + Refundable.super.receipt();
    }
}

// --------------------------------------------------------------------------
// Sealed interface (Java 17+): restricts WHO may implement it.
// The compiler then knows every possible subtype, so a switch can be
// exhaustive without a `default` branch.
// --------------------------------------------------------------------------
sealed interface PaymentMethod permits Card, Upi, Cash {}
record Card(String last4) implements PaymentMethod {}
record Upi(String vpa)    implements PaymentMethod {}
record Cash()             implements PaymentMethod {}

public class AbstractionDemo {

    static String describe(PaymentMethod pm) {
        // Pattern-matching switch (Java 21). No default needed: sealed!
        return switch (pm) {
            case Card c -> "Card ending " + c.last4();
            case Upi u  -> "UPI " + u.vpa();
            case Cash c -> "Cash";
        };
    }

    public static void main(String[] args) {
        System.out.println("=== Abstract class + template method ===");
        // Vehicle v = new Vehicle("x");   // COMPILE ERROR: Vehicle is abstract
        Vehicle[] fleet = { new PetrolCar(), new ElectricCar() };
        for (Vehicle v : fleet) {
            v.startJourney();     // caller only knows "start journey"
        }

        System.out.println("\n=== Interfaces & the diamond ===");
        Order order = new Order(100);
        System.out.println(order.receipt());

        // An Order can be viewed through either interface:
        Payable p = order;
        Refundable r = order;
        System.out.println("As Payable   : " + p.amount());
        System.out.println("As Refundable: " + r.receipt());

        Payable quick = Payable.of(50);    // static interface method + lambda
        System.out.println("Quick payable: " + quick.receipt());

        System.out.println("\n=== Sealed types ===");
        PaymentMethod[] methods = { new Card("4242"), new Upi("anket@upi"), new Cash() };
        for (PaymentMethod pm : methods) {
            System.out.println(describe(pm));
        }
    }
}
