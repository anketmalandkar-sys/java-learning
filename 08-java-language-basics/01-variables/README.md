# Variables and Naming

Source: https://dev.java/learn/language/constructs/basics/variables

← Back to [Java Language Basics](../README.md)

## 1. What it is

A **variable** is a named slot that holds a value. Java has four kinds, and the kind depends on *where* you declare it and whether you add `static`: **instance fields**, **static fields**, **local variables**, and **parameters**. Naming rules decide what the compiler accepts; naming conventions decide what other Java developers expect to read.

## 2. Why it exists

Programs need to remember things: an account's balance, how many accounts exist, a running total inside a loop. Each kind of variable answers a different question: *whose* value is it (one object's, or the whole class's), and *how long* does it live (as long as the object, or just one method call)? Picking the wrong kind is a common source of bugs, like two shopping carts that share one total.

## 3. Core concepts

**Instance field (non-static field)**: each object gets its own copy.
```java
class Car {
    int speed;          // car A's speed is independent of car B's
}
```

**Static field (class variable)**: one copy shared by all objects of the class.
```java
class Car {
    static int carsBuilt;            // one counter for every Car
    static final int WHEEL_COUNT = 4; // static + final = a constant
}
```

**Local variable**: declared inside a method (or block). Exists only while that method runs; nothing outside can see it.
```java
void drive() {
    int distance = 0;   // no keyword needed, just declared in a method
}
```

**Parameter**: a value handed to a method or constructor. It's a variable, not a field.
```java
void accelerate(int amount) { speed += amount; }   // amount is a parameter
```

> "Field" means instance or static field. "Variable" covers all four kinds. A class's **members** are its fields, methods and nested types.

**Naming rules (the compiler enforces these)**
- Case-sensitive: `total` and `Total` are different variables.
- Start with a letter, `$` or `_`; then letters, digits, `$`, `_`. No spaces.
- Can't be a keyword (`class`, `int`, `new`, ...).

**Naming conventions (people expect these)**
- Start with a letter. Avoid `$` (generated code uses it) and a leading `_`.
- Use whole words: `cadence`, not `c`.
- One word → lowercase: `speed`. Several words → camelCase: `currentGear`.
- Constants (`static final`) → UPPER_SNAKE_CASE: `MAX_SPEED`. That's the only place underscores belong.

## 4. How it works under the hood

- **Instance fields** live inside each object on the heap. They get a **default value** if you don't set one: `0`, `false`, `null`, etc.
- **Static fields** live once per class, created when the class is loaded. They also get defaults.
- **Local variables and parameters** live in the method's stack frame and vanish when the method returns. Locals get **no default**: the compiler refuses to let you read one before you assign it.

```java
class Demo {
    int count;                 // defaults to 0

    void run() {
        int local;
        // System.out.println(local);   // compile error: might not have been initialized
        System.out.println(count);      // fine: prints 0
    }
}
```

## 5. Common mistakes and gotchas

**Using `static` for per-object data.** Every object then shares one value.
```java
// WRONG: all carts share one total
class Cart { static double total; }

// RIGHT: each cart has its own total
class Cart { double total; }
```

**A parameter shadowing a field.** Inside the constructor, `name` means the parameter, so the field is never set.
```java
// WRONG: assigns the parameter to itself; the field stays null
Customer(String name) { name = name; }

// RIGHT: this.name is the field
Customer(String name) { this.name = name; }
```

**Reading a local before assigning it.** Fields default to 0, locals don't.
```java
int sum;                // WRONG: sum += x; won't compile
int sum = 0;            // RIGHT
```

**Cryptic or unconventional names.**
```java
int d; int Max_Items; int $count;            // WRONG
int daysUntilDue; final int maxItems; int count;  // RIGHT
static final int MAX_ITEMS = 10;             // constant: UPPER_SNAKE_CASE
```

## 6. When to use / when not to

| Use a... | When the value... |
|---|---|
| Instance field | describes one object (an account's balance, a car's speed) |
| Static field | belongs to the class as a whole (a counter of all objects, shared config) |
| `static final` constant | never changes (`MAX_RETRIES`, `TAX_RATE`) |
| Local variable | is only needed while one method runs (loop counter, temp result) |
| Parameter | is input the caller provides |

Prefer the **narrowest** scope that works: a local over a field, an instance field over a static one. Avoid mutable static fields in general; they're shared global state.

## 7. Interview angle

1. **What are the four kinds of variables in Java?** Instance fields, static fields, local variables, parameters.
2. **Difference between an instance and a static variable?** Instance: one copy per object. Static: one copy per class, shared by all objects.
3. **Do local variables get default values?** No. The compiler requires them to be definitely assigned before use. Fields do get defaults (`0`, `false`, `null`).
4. **What does `this.name = name` do, and why is `this` needed?** The parameter `name` shadows the field; `this.name` refers to the field.
5. **Naming convention for constants?** `static final` with UPPER_SNAKE_CASE, e.g. `MAX_SIZE`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_VisitorBadge.java` | Use a static counter and an instance field to give each visitor a unique badge number | Easy |
| 2 | `exercises/Exercise2_ProductShadowing.java` | Fix constructors broken by parameter shadowing, and add a constant | Medium |
| 3 | `exercises/Exercise3_ScoreboardBug.java` | Find why two players share a score, fix it, and finish the scoring with locals and a cap | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
