package oops.inheritance;

/*
 * ============================================================================
 *  OVERRIDING, HIDING AND THE FINER RULES OF INHERITANCE
 * ============================================================================
 *
 *  Source: https://dev.java/learn/language/oop/inheritance/what-is-inheritance
 *  Source: https://dev.java/learn/language/oop/inheritance/overriding
 *  Source: https://dev.java/learn/language/oop/inheritance/polymorphism
 *
 *  Fills the gaps next to InheritanceDemo.java and PolymorphismDemo.java:
 *
 *  1. ACCESS CAN BE WIDENED, NEVER NARROWED
 *     protected -> public is fine. public -> protected is a compile error.
 *
 *  2. INSTANCE vs STATIC MUST MATCH
 *     An instance method can't override a static one, or vice versa: compile error.
 *
 *  3. OVERLOADING IN A SUBCLASS IS A NEW METHOD
 *     Same name, different parameters: neither overrides nor hides. Which one
 *     runs is decided by the compiler from the REFERENCE type.
 *
 *  4. FIELDS ARE NOT POLYMORPHIC
 *     A subclass field with the same name HIDES the parent's (even with a
 *     different type). Which one you read depends on the reference type.
 *     Reach the parent's with super.field. Hiding fields is confusing: avoid it.
 *
 *  5. DON'T CALL OVERRIDABLE METHODS FROM A CONSTRUCTOR
 *     The parent constructor runs before the child's fields are initialized.
 *     If it calls a method the child overrides, the child's version sees
 *     default values (null, 0). Make such methods private or final.
 *
 *  6. PRIVATE MEMBERS ARE NOT INHERITED
 *     They exist inside the child object, but the child's code can only reach
 *     them through public/protected methods of the parent, OR through a
 *     public/protected NESTED class the child inherits: a nested class may read
 *     every private member of its enclosing class, so it's a back door.
 *
 *  6b. AN INHERITED METHOD CAN IMPLEMENT AN INTERFACE
 *     If the superclass already has a public method with the right signature,
 *     a subclass that adds `implements SomeInterface` needs no method of its own.
 *
 *  7. DEFAULT-METHOD CONFLICT RULES (interfaces)
 *     a) CLASS WINS: a method from a superclass beats a default from an interface.
 *     b) MORE SPECIFIC INTERFACE WINS: if B extends A and both provide the
 *        default, B's is used.
 *     c) Otherwise (two unrelated defaults) the class must override it and can
 *        pick one with X.super.method(). (Shown in AbstractionDemo.java.)
 *
 *  8. STATIC INTERFACE METHODS ARE NOT INHERITED
 *     Call them as InterfaceName.method(), never through an implementing class.
 */
class Account {
    private int pin = 1234;                 // not inherited: Savings can't touch it directly

    protected String type() {
        return "account";
    }

    static String bank() {
        return "Parent Bank";
    }

    String describe(Object detail) {
        return "Account.describe(Object)";
    }

    boolean checkPin(int attempt) {         // the only way a subclass can use the pin
        return attempt == pin;
    }
}

class Savings extends Account {
    @Override
    public String type() {                  // protected -> public: WIDENING is allowed
        return "savings";
    }

    // private String type() { ... }       // COMPILE ERROR: would narrow access
    // String bank() { ... }               // COMPILE ERROR: instance can't override static

    static String bank() {                  // static + static = HIDING, not overriding
        return "Child Bank";
    }

    String describe(String detail) {        // OVERLOAD: different parameter type
        return "Savings.describe(String)";
    }
}

class Engine {
    private int rpm = 900;                  // private: Turbo can't read it directly

    // Inherited by subclasses. As a nested class of Engine it may read Engine's privates.
    protected static class Gauge {
        int read(Engine engine) {
            return engine.rpm;
        }
    }
}

class Turbo extends Engine {
    int currentRpm() {
        // return rpm;                      // COMPILE ERROR: rpm is private in Engine
        return new Gauge().read(this);      // indirect access through the inherited nested class
    }
}

interface Runner {
    String run();
}

class Athlete {
    public String run() {
        return "Athlete.run()";
    }
}

// No run() here: the public run() inherited from Athlete implements Runner.run().
class Sprinter extends Athlete implements Runner { }

class Vehicle {
    String name = "vehicle";

    Vehicle() {
        // Calls an overridable method from a constructor: dangerous.
        System.out.println("  Vehicle() sees label = " + label());
    }

    String label() {
        return "Vehicle " + name;
    }
}

class Truck extends Vehicle {
    String name = "truck";                  // HIDES Vehicle.name
    // Not a compile-time constant on purpose (a `final` field with a literal would be
    // inlined and hide the problem). It is assigned only AFTER Vehicle() has finished.
    private String plate = "MH12-AB";

    Truck() {
        super();
        System.out.println("  Truck() sees label = " + label());
    }

    @Override
    String label() {
        return "Truck " + plate;            // plate is still null while Vehicle() runs
    }

    String bothNames() {
        return name + " / " + super.name;
    }
}

interface Greeter {
    default String greet() {
        return "Hello from Greeter";
    }

    static String helper() {
        return "Greeter.helper()";
    }
}

interface PoliteGreeter extends Greeter {
    @Override
    default String greet() {
        return "Good morning from PoliteGreeter";
    }
}

class Base {
    public String greet() {
        return "Hello from Base class";
    }
}

class ClassWins extends Base implements Greeter { }              // rule 7a

class SpecificWins implements Greeter, PoliteGreeter { }        // rule 7b

public class OverridingRules {

    public static void main(String[] args) {
        System.out.println("=== Widening access, static hiding ===");
        Account acc = new Savings();
        System.out.println("acc.type()       = " + acc.type() + "   (overridden, public in Savings)");
        System.out.println("Account.bank()   = " + Account.bank());
        System.out.println("Savings.bank()   = " + Savings.bank() + "   (hidden, not overridden)");

        System.out.println("\n=== Overloading in a subclass ===");
        Savings sav = new Savings();
        System.out.println("acc.describe(\"x\") = " + acc.describe("x") + "   (reference type Account: only describe(Object) exists)");
        System.out.println("sav.describe(\"x\") = " + sav.describe("x") + "   (reference type Savings: the String overload is more specific)");

        System.out.println("\n=== Private members ===");
        System.out.println("checkPin(1234) via inherited method = " + sav.checkPin(1234));
        System.out.println("Turbo reads Engine's private rpm via inherited Gauge = " + new Turbo().currentRpm());

        System.out.println("\n=== Inherited method implements an interface ===");
        Runner runner = new Sprinter();
        System.out.println("Sprinter as Runner: " + runner.run());

        System.out.println("\n=== Overridable call in a constructor ===");
        Truck truck = new Truck();

        System.out.println("\n=== Field hiding ===");
        Vehicle asVehicle = truck;
        System.out.println("truck.name     = " + truck.name);
        System.out.println("asVehicle.name = " + asVehicle.name + "   (fields use the REFERENCE type)");
        System.out.println("asVehicle.label() = " + asVehicle.label() + "   (methods use the OBJECT type)");
        System.out.println("bothNames()    = " + truck.bothNames());

        System.out.println("\n=== Default-method rules ===");
        System.out.println("ClassWins:    " + new ClassWins().greet());
        System.out.println("SpecificWins: " + new SpecificWins().greet());
        System.out.println("static interface method: " + Greeter.helper());
        // ClassWins.helper();     // COMPILE ERROR: static interface methods aren't inherited
        // PoliteGreeter.helper(); // COMPILE ERROR: not even by sub-interfaces
    }
}
