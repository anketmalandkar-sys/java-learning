package oops.inheritance;

/*
 * ============================================================================
 *  INHERITANCE — "IS-A" relationship, code reuse through `extends`
 * ============================================================================
 *
 *  A subclass (child) inherits the fields and methods of its superclass
 *  (parent) and can:
 *     - ADD new fields/methods
 *     - OVERRIDE inherited methods to change behaviour
 *     - call the parent's version with `super.method()`
 *
 *  Test before using it: "Is a Dog an Animal?" -> yes -> inheritance is OK.
 *                        "Is a Car an Engine?" -> no  -> use composition.
 *
 *  TYPES in Java:
 *     Single       : Dog extends Animal
 *     Multilevel   : Puppy extends Dog extends Animal
 *     Hierarchical : Dog and Cat both extend Animal
 *     Multiple     : NOT allowed with classes (class C extends A, B is an
 *                    error) because of the "diamond problem" — if A and B
 *                    both define run(), which one does C get? Java allows
 *                    multiple inheritance of TYPE via interfaces instead
 *                    (see the abstraction package).
 *
 *  Every class implicitly extends java.lang.Object, which is why every
 *  object has toString(), equals(), hashCode(), getClass() ...
 */

class Animal {
    // protected -> visible to subclasses (even in other packages) and to
    // classes in the same package.
    protected String name;
    private int heartBeats;            // private -> NOT accessible in Dog

    Animal(String name) {
        this.name = name;
        System.out.println("  1. Animal constructor (" + name + ")");
    }

    void eat() {
        System.out.println(name + " is eating.");
    }

    void makeSound() {
        System.out.println(name + " makes a generic sound.");
    }

    // final method -> subclasses CANNOT override it.
    // (A final CLASS cannot be extended at all, e.g. java.lang.String.)
    final void breathe() {
        heartBeats++;
        System.out.println(name + " breathes. (heart beats: " + heartBeats + ")");
    }
}

class Dog extends Animal {
    private final String breed;

    Dog(String name, String breed) {
        // The parent MUST be constructed first. super(...) must be the first
        // statement. If you omit it, Java inserts super() (no-arg) — which
        // would fail here because Animal has no no-arg constructor.
        super(name);
        this.breed = breed;
        System.out.println("  2. Dog constructor (" + breed + ")");
    }

    // OVERRIDING: same signature as the parent method.
    // @Override asks the compiler to check that we really override something
    // (catches typos like makeSond()).
    @Override
    void makeSound() {
        System.out.println(name + " says: Woof!");
    }

    // New behaviour only dogs have.
    void fetch() {
        System.out.println(name + " the " + breed + " fetches the ball.");
    }

    // @Override void breathe() {}   // COMPILE ERROR: breathe() is final
}

// Multilevel: Puppy -> Dog -> Animal
class Puppy extends Dog {

    Puppy(String name, String breed) {
        super(name, breed);
        System.out.println("  3. Puppy constructor");
    }

    @Override
    void makeSound() {
        super.makeSound();                       // reuse Dog's version...
        System.out.println(name + " also whimpers."); // ...and extend it
    }
}

// Hierarchical: Cat is a sibling of Dog
class Cat extends Animal {

    Cat(String name) {
        super(name);
    }

    @Override
    void makeSound() {
        System.out.println(name + " says: Meow!");
    }
}

public class InheritanceDemo {

    public static void main(String[] args) {
        System.out.println("=== Constructor order (parent first!) ===");
        Puppy puppy = new Puppy("Bruno", "Labrador");
        // Prints 1. Animal, 2. Dog, 3. Puppy — construction goes top-down.

        System.out.println("\n=== Inherited, overridden and new methods ===");
        puppy.eat();        // inherited from Animal unchanged
        puppy.breathe();    // inherited final method
        puppy.makeSound();  // Puppy's override, which calls Dog's via super
        puppy.fetch();      // inherited from Dog

        System.out.println("\n=== Hierarchical ===");
        Cat cat = new Cat("Kitty");
        cat.makeSound();

        System.out.println("\n=== instanceof follows the IS-A chain ===");
        System.out.println("puppy instanceof Dog    : " + (puppy instanceof Dog));
        System.out.println("puppy instanceof Animal : " + (puppy instanceof Animal));
        System.out.println("puppy instanceof Object : " + (puppy instanceof Object));
        System.out.println("Superclass of Puppy     : " + Puppy.class.getSuperclass().getSimpleName());
    }
}
