package oops.basics;

import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================================
 *  INITIALIZATION: static blocks, instance blocks, and the order things run
 * ============================================================================
 *
 *  Source: https://dev.java/learn/language/oop/classes-objects/more-on-classes
 *
 *  Fills the gaps next to ClassesAndObjects.java (which covers static vs
 *  instance members, `this`, and constructor chaining):
 *
 *  STATIC INITIALIZER BLOCK   static { ... }
 *     Runs ONCE, when the class is initialized (first real use), in source
 *     order with the static field initializers. Use it for setup that doesn't
 *     fit on one line. If it throws, the class can't be used at all
 *     (ExceptionInInitializerError, then NoClassDefFoundError).
 *
 *  INSTANCE INITIALIZER BLOCK   { ... }
 *     Runs on EVERY `new`, before the constructor body, in source order with
 *     the instance field initializers. The compiler copies it into every
 *     constructor, so it's a way to share code between constructors.
 *     (A private/final helper method called from each constructor is often clearer.)
 *
 *  ORDER for `new Child()` when Child extends Parent:
 *     1. Parent static fields/blocks   (once, first time the class is used)
 *     2. Child static fields/blocks    (once)
 *     3. Parent instance fields/blocks
 *     4. Parent constructor body
 *     5. Child instance fields/blocks
 *     6. Child constructor body
 *
 *  COMPILE-TIME CONSTANTS are INLINED
 *     `static final int MAX = 5;` (a primitive or String with a constant
 *     value) is copied into every class that uses it, at compile time.
 *     So (a) reading it doesn't even initialize the declaring class, and
 *     (b) if you change its value, classes that use it keep the OLD value
 *     until they're recompiled.
 *
 *  STATIC MEMBERS VIA AN INSTANCE
 *     `obj.staticMethod()` compiles, but the object is ignored: it's really
 *     `ClassName.staticMethod()`. It even works when obj is null. Always use
 *     the class name, so readers don't think it depends on the object.
 *
 *  FORWARD REFERENCES
 *     A field must be declared before an initializer that reads it:
 *        int a = b + 1;  int b = 2;   // COMPILE ERROR: illegal forward reference
 */
class InitLog {
    static final List<String> EVENTS = new ArrayList<>();

    static String log(String event) {
        EVENTS.add(event);
        return event;
    }
}

class Parent {
    static String parentStatic = InitLog.log("1 Parent static field");

    static {
        InitLog.log("1 Parent static block");
    }

    String parentField = InitLog.log("3 Parent instance field");

    {
        InitLog.log("3 Parent instance block");
    }

    Parent() {
        InitLog.log("4 Parent constructor");
    }
}

class Child extends Parent {
    static {
        InitLog.log("2 Child static block");
    }

    String childField = InitLog.log("5 Child instance field");

    {
        InitLog.log("5 Child instance block");
    }

    Child() {
        super();
        InitLog.log("6 Child constructor");
    }
}

class Settings {
    static final int MAX_USERS = 50;              // compile-time constant: inlined
    static final Integer BOXED_MAX = 50;          // NOT a compile-time constant (an object)
    static final long STARTED_AT = System.nanoTime();   // not constant either

    static {
        InitLog.log("Settings initialized");
    }

    static String describe() {
        return "Settings v1";
    }
}

public class InitializationOrder {

    public static void main(String[] args) {
        System.out.println("=== First new Child() ===");
        new Child();
        InitLog.EVENTS.forEach(e -> System.out.println("  " + e));

        System.out.println("=== Second new Child(): no static steps this time ===");
        InitLog.EVENTS.clear();
        new Child();
        InitLog.EVENTS.forEach(e -> System.out.println("  " + e));

        System.out.println("\n=== Compile-time constants are inlined ===");
        InitLog.EVENTS.clear();
        int max = Settings.MAX_USERS;               // copied in at compile time
        System.out.println("read MAX_USERS = " + max + ", class initialized? " + !InitLog.EVENTS.isEmpty());
        Integer boxed = Settings.BOXED_MAX;         // a real field read: triggers initialization
        System.out.println("read BOXED_MAX = " + boxed + ", class initialized? " + !InitLog.EVENTS.isEmpty());

        System.out.println("\n=== Static method through an instance reference ===");
        Settings none = null;
        // Compiles (with a warning) and does NOT throw: the reference is ignored.
        @SuppressWarnings({"static", "AccessStaticViaInstance"})
        String text = none.describe();
        System.out.println("null.describe() returned \"" + text + "\": always write Settings.describe()");
    }
}
