# Enums

Source: https://dev.java/learn/language/oop/classes-objects/enums

← Back to [Object Oriented Programming](../README.md)

## 1. What it is

An **enum** is a class with a **fixed, known set of instances**, listed when it's declared: `enum Size { SMALL, MEDIUM, LARGE }`. Those constants are the only objects of that type that can ever exist. Enums can still have fields, constructors and methods, and every enum implicitly extends `java.lang.Enum`.

## 2. Why it exists

Before enums, people used `int` or `String` constants: `static final int SMALL = 0;`. Nothing stopped you passing `42` or `"smal"` where a size was expected, and the compiler couldn't tell you a `switch` missed a case. An enum makes invalid values impossible to write, gives each value a name that prints nicely, and lets the compiler check switches for completeness.

## 3. Core concepts

**Declaring and using**
```java
enum Size { SMALL, MEDIUM, LARGE }

Size s = Size.MEDIUM;
if (s == Size.MEDIUM) { ... }      // == is safe: there's only one MEDIUM object
```

**Built-in methods**
```java
s.name()                 // "MEDIUM"
s.ordinal()              // 1 (position in the declaration, from 0)
Size.values()            // [SMALL, MEDIUM, LARGE], a NEW array each call
Size.valueOf("LARGE")    // Size.LARGE; IllegalArgumentException for an unknown name
s.compareTo(Size.LARGE)  // negative: enums compare by ordinal
```

**Switch** (exhaustive in a switch expression, no `default` needed)
```java
int ml = switch (s) {
    case SMALL -> 250;
    case MEDIUM -> 350;
    case LARGE -> 500;
};
```

**Fields, constructor, methods**: the constant list ends with `;`, and each constant passes its constructor arguments.
```java
enum Plan {
    FREE(0, 1), PRO(499, 5), TEAM(1999, 50);   // ; ends the constants

    private final int priceRupees;
    private final int maxUsers;

    Plan(int priceRupees, int maxUsers) {      // implicitly private
        this.priceRupees = priceRupees;
        this.maxUsers = maxUsers;
    }

    int priceRupees() { return priceRupees; }
    boolean allows(int users) { return users <= maxUsers; }
}
```

**Constant-specific behaviour**: declare an `abstract` method; every constant must implement it in its own body.
```java
enum Operation {
    ADD { int apply(int a, int b) { return a + b; } },
    MULTIPLY { int apply(int a, int b) { return a * b; } };

    abstract int apply(int a, int b);
}
```

**Singleton**: an enum with one constant is the simplest thread-safe singleton.
```java
enum AppConfig { INSTANCE; String region() { return "ap-south-1"; } }
```

**Enum collections**: `EnumSet` and `EnumMap` are fast, compact `Set`/`Map` implementations for enum keys.
```java
EnumSet<Size> drinkable = EnumSet.of(Size.MEDIUM, Size.LARGE);
EnumMap<Size, Integer> stock = new EnumMap<>(Size.class);
```

## 4. How it works under the hood

- The compiler turns `enum Size` into a `final class Size extends Enum<Size>` with one `public static final` field per constant, created once when the class loads.
- Constructors are always private, so no code can create extra instances. You also can't subclass an enum (constant-specific bodies are compiler-generated anonymous subclasses).
- `values()` returns a fresh copy of the internal array every call; cache it if you call it in a hot loop.
- Enums implement `Comparable` (by ordinal) and `Serializable`; serialization writes only the **name**, so deserialized constants are the same objects.
- Since enum constants are unique, `==` and `equals` give the same answer; `==` also avoids a `NullPointerException` and is checked at compile time.

## 5. Common mistakes and gotchas

**Storing `ordinal()`**
```java
db.save(order.size().ordinal());      // WRONG: reordering or inserting a constant changes every number
db.save(order.size().name());         // RIGHT (or a dedicated code field)
```

**`valueOf` with user input**
```java
Size s = Size.valueOf(input);                    // WRONG: "small" or "XL" throws IllegalArgumentException
Size s = parseSize(input.strip().toUpperCase()); // RIGHT: normalise, and catch/validate
```

**A `default` that hides a new constant**
```java
switch (plan) { case FREE -> ...; case PRO -> ...; default -> ...; }   // RISKY: a new ENTERPRISE silently hits default
switch (plan) { case FREE -> ...; case PRO -> ...; case TEAM -> ...; } // BETTER: adding a constant breaks the build
```

**Forgetting the `;`** after the constants when the enum has fields or methods → compile error.

**Changing constants in shared code**: removing or renaming one breaks callers at compile time, and values stored in files/databases no longer match. Review every user of the enum before changing it.

## 6. When to use / when not to

- **Use an enum** for a small, fixed set known at compile time: days, statuses, sizes, plans, directions, HTTP methods.
- **Use fields on the enum** instead of parallel `switch`es scattered around the code (price, label, limits belong to the constant).
- **Don't use an enum** when the set changes often or is defined by users/config (product categories, countries from a database): use a class or a config file.
- For sets/maps keyed by an enum, use `EnumSet`/`EnumMap`.

## 7. Interview angle

1. **Can an enum have a constructor? Can you call it?** Yes, it can have one (implicitly private). Only the enum's own constants call it; `new` on an enum is a compile error.
2. **Can an enum extend a class? Implement an interface?** It can't extend anything (it already extends `Enum`), but it can implement interfaces.
3. **`==` or `equals` for enums?** Either works, `==` is preferred: null-safe and type-checked at compile time.
4. **Why not persist `ordinal()`?** It depends on declaration order, so adding or reordering constants silently changes the stored meaning.
5. **Why is an enum a good singleton?** The JVM guarantees one instance, thread-safe creation, and correct serialization; reflection can't create another.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_TrafficLight.java` | A basic enum with `next()`, `values()`, `ordinal()` and an exhaustive switch | Easy |
| 2 | `exercises/Exercise2_CoffeeSizes.java` | An enum with fields, a constructor and safe parsing of user input | Medium |
| 3 | `exercises/Exercise3_Calculator.java` | Constant-specific methods, an `EnumMap` usage counter, and a symbol lookup | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
