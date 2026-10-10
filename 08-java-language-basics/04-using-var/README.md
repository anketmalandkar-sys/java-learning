# Using `var` (Local Variable Type Inference)

Source: https://dev.java/learn/language/constructs/basics/using-var

← Back to [Java Language Basics](../README.md)

## 1. What it is

Since **Java 10**, you can write `var` instead of a type when declaring a **local variable** that has an initializer. The compiler looks at the right-hand side and fills in the type for you. The variable is still statically typed: its type is fixed at compile time and never changes.

## 2. Why it exists

Some declarations repeat the type twice, and the repeat adds noise without information:
```java
Map<String, List<Order>> ordersByCustomer = new HashMap<String, List<Order>>();
var ordersByCustomer = new HashMap<String, List<Order>>();   // same type, less noise
```
`var` lets the variable *name* carry the meaning, and keeps long generic types from pushing the important part off the screen.

## 3. Core concepts

**The type comes from the initializer**
```java
var count = 10;                  // int
var price = 9.99;                // double
var name = "Asha";               // String
var names = new ArrayList<String>();   // ArrayList<String>
```

**It's still static typing**
```java
var name = "Asha";
name = 42;                       // compile error: name is a String
```

**Where `var` works**: local variables in methods, constructors and initializer blocks, including loop variables and try-with-resources.
```java
for (var item : cart) { ... }
for (var i = 0; i < 10; i++) { ... }
try (var in = Files.newInputStream(path)) { ... }
```

**Where it doesn't**
```java
class Shop {
    var stock = 5;               // ERROR: not for fields
    void sell(var qty) { }       // ERROR: not for parameters
    var total() { return 0; }    // ERROR: not for return types
}
var x;                           // ERROR: no initializer, nothing to infer from
var y = null;                    // ERROR: null has no type
var arr = {1, 2, 3};             // ERROR: array initializer needs an explicit type
var a = 1, b = 2;                // ERROR: one variable per var declaration
```

`var` isn't a keyword; it's a *reserved type name*. You can still have a variable called `var` (please don't), but not a class called `var`.

## 4. How it works under the hood

- Inference happens **only at compile time**. The `.class` file contains the real type; at runtime there's no difference between `var` and an explicit type.
- The inferred type is the **exact static type of the initializer**. `var list = new ArrayList<String>();` gives `ArrayList<String>`, not `List<String>`.
- With the diamond, there's nothing to infer the element type from: `var list = new ArrayList<>();` gives `ArrayList<Object>`.
- Numeric literals follow normal literal rules: `var n = 0;` is an `int`, even if you later want to add decimals to it.

## 5. Common mistakes and gotchas

**`var` with a 0 that should be a double.** `+=` hides the problem with a silent cast.
```java
var total = 0;            // WRONG: int
total += 19.99;           // compiles! total becomes 19 (truncated)

var total = 0.0;          // RIGHT: double
```

**Integer division in the initializer**
```java
var rate = 15 / 100;      // WRONG: int 0
var rate = 15 / 100.0;    // RIGHT: double 0.15
```

**Diamond with `var`**
```java
var tags = new ArrayList<>();          // WRONG: ArrayList<Object>
var tags = new ArrayList<String>();    // RIGHT
```

**Hiding a type the reader needs**
```java
var result = service.process(data);    // UNCLEAR: what is result?
Invoice invoice = service.process(data);   // CLEARER here
```

## 6. When to use / when not to

**Use `var` when** the type is obvious from the right side (`new`, a literal, a well-named factory like `List.of`), when the type is long and noisy (nested generics), and for loop variables.

**Avoid `var` when** the initializer is a method call whose return type isn't obvious, when the exact numeric type matters (`0` vs `0.0` vs `0L`), or when you want the variable to have an interface type (`List<String> x = new ArrayList<>()`).

Rule of thumb: if a reader can't tell the type in two seconds, write it out.

## 7. Interview angle

1. **Does `var` make Java dynamically typed?** No. The type is inferred once, at compile time, and fixed. Assigning a different type is a compile error.
2. **Where can't you use `var`?** Fields, method parameters, return types, without an initializer, with a `null` initializer, with a bare array initializer `{...}`, and in multi-variable declarations.
3. **What type does `var list = new ArrayList<>();` have?** `ArrayList<Object>`: the diamond has nothing to infer from.
4. **Which Java version added `var`?** Java 10 (local variables). Java 11 allowed it on lambda parameters.
5. **Is there any runtime cost?** None. The compiled bytecode is identical to writing the type explicitly.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_CompileQuiz.java` | Decide which `var` declarations compile | Easy |
| 2 | `exercises/Exercise2_PredictTheType.java` | Predict the type and value `var` infers; the program checks you against the real answer | Medium |
| 3 | `exercises/Exercise3_InvoiceBugs.java` | Fix an invoice calculator broken by two `var` inference traps | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
