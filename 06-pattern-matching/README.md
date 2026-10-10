# Pattern matching

Source: https://dev.java/learn/language/fp/pattern-matching

## 1. What it is

**Pattern matching** lets you test an object's shape and pull data out of it in one step. Each match has three parts:
- the **target**: the object you're testing
- the **pattern**: a type, or a record shape like `Point(int x, int y)`
- the **pattern variables**: names bound to the matched parts when the test succeeds

Java has three kinds of pattern:

| Feature | Looks like | Final in |
|---------|-----------|----------|
| Type pattern in `instanceof` | `if (obj instanceof String s)` | Java 16 |
| Patterns in `switch`, with `when` guards | `case Circle c when c.radius() > 10 ->` | Java 21 |
| Record patterns (deconstruction, nesting) | `case Line(Point(var x1, var y1), Point p2) ->` | Java 21 |

## 2. Why it exists

Before patterns, checking a type meant testing it, casting it, then pulling fields out by hand:

```java
if (shape instanceof Circle) {
    Circle c = (Circle) shape;            // the same type written three times
    double r = c.radius();
    return Math.PI * r * r;
} else if (shape instanceof Rectangle) {
    Rectangle rect = (Rectangle) shape;
    return rect.width() * rect.height();
} else {
    throw new IllegalArgumentException("unknown shape");   // forget a type and you only find out at runtime
}
```

With patterns:

```java
return switch (shape) {
    case Circle(double r)               -> Math.PI * r * r;
    case Rectangle(double w, double h)  -> w * h;
};   // with a sealed Shape, the compiler checks every type is covered: no default needed
```

That means no casts, no repeated types, and the compiler tells you when a case is missing.

## 3. Core concepts

**Type pattern with `instanceof` (Java 16).** `s` exists only where the match is known to have succeeded:

```java
if (obj instanceof String s && !s.isEmpty()) {   // s can be used after && ...
    System.out.println(s.length());
}

if (!(obj instanceof Integer n)) {
    return "not a number";                       // ... and after an if that leaves the method
}
return "doubled: " + n * 2;                      // n is in scope here

if (obj instanceof String s || s.isEmpty())      // compile error: with ||, s may not be bound
```

It cleans up `equals` too:

```java
@Override
public boolean equals(Object o) {
    return o instanceof Point other && x == other.x && y == other.y;
}
```

**Type patterns in `switch` (Java 21).** Any reference type can be the selector, and the cases are types:

```java
static String describe(Object obj) {
    return switch (obj) {
        case null                -> "nothing";              // without this, null throws NullPointerException
        case Integer i           -> "int " + i;
        case String s            -> "text of length " + s.length();
        case int[] array         -> "int array of " + array.length;
        default                  -> "something else: " + obj.getClass().getSimpleName();
    };
}
```

**Guards with `when`.** A case label can't hold a boolean condition, so you add one after `when`:

```java
case String s when s.isBlank()   -> "blank text";
case String s                    -> "text: " + s;            // the unguarded case comes after
case Integer i when i < 0        -> "negative";
case Integer i                   -> "non-negative";
```

Cases are checked **top to bottom**, and the first match wins.

**Record patterns (Java 21).** They take a record apart into its components, following the canonical constructor's order:

```java
record Point(int x, int y) {}

if (obj instanceof Point(int x, int y)) {    // x and y come from p.x() and p.y()
    System.out.println(x + y);
}

case Point(var x, var y) when x == y -> "on the diagonal";   // var: let the compiler infer the type
```

**Nested patterns.** Patterns can contain patterns, matching deep structures in one line:

```java
record Line(Point start, Point end) {}

case Line(Point(var x1, var y1), Point(var x2, var y2)) when x1 == x2 -> "vertical line";
```

**Sealed types make switches exhaustive.** If the selector's type is a `sealed` interface, the compiler knows every permitted subtype. A switch that covers them all needs no `default`. Adding a new subtype later turns every switch that misses it into a **compile error**, which is exactly what you want:

```java
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}
record Triangle(double base, double height) implements Shape {}

double area(Shape shape) {
    return switch (shape) {               // no default: the compiler checks all three are covered
        case Circle c    -> Math.PI * c.radius() * c.radius();
        case Rectangle r -> r.width() * r.height();
        case Triangle t  -> 0.5 * t.base() * t.height();
    };
}
```

## 4. How it works under the hood

- **It's compiled to ordinary checks.** `obj instanceof String s` compiles to an `instanceof` test plus a cast. A record pattern calls the record's accessor methods. A pattern `switch` is compiled to an `invokedynamic` call (`SwitchBootstraps.typeSwitch`) that picks the first matching case. Guards are then checked in order.
- **Dominance.** The compiler rejects a case that can never be reached, because an earlier case already matches everything it would:
  ```java
  case CharSequence cs -> ...
  case String s        -> ...      // compile error: dominated, every String is a CharSequence
  ```
  Guarded cases don't dominate (the guard might fail), so ordering *between guards* is up to you, and the compiler can't catch mistakes there.
- **Exhaustiveness.** A `switch` *expression*, or any `switch` that uses patterns, must cover every possible value. For a sealed hierarchy the permitted subtypes are enough. For `Object` you need a `default` or a total pattern like `case Object o`.
- **`null`.** A pattern switch throws `NullPointerException` on `null` unless there's a `case null` (it can be combined: `case null, default ->`). `instanceof` is simply `false` for `null`.
- **Impossible matches don't compile.** `Integer i` against a `String` selector, or `str instanceof Integer n`, is a compile error.

**What's newer than Java 21:**
- **Unnamed patterns `_`** (final in Java 22). `case Line(Point p, _) ->` ignores a component you don't need. On Java 21 use `var ignored` instead.
- **Primitive types in patterns** (`case int i when i > 0`) are a preview feature in Java 23 and later.
- **Record patterns in enhanced `for` loops** were previewed in Java 20 and **removed** before Java 21. Some older articles still show them.

## 5. Common mistakes and gotchas

**Guards in the wrong order.** The first matching case wins, and the compiler can't check guard logic:

```java
case Payment p when p.amount() > 1_000  -> "review";
case Payment p when p.amount() > 50_000 -> "block";      // wrong: never reached for big payments
// right: put the most specific (strictest) guard first
case Payment p when p.amount() > 50_000 -> "block";
case Payment p when p.amount() > 1_000  -> "review";
```

**Adding a `default` to a sealed switch.** It "works" today and silently swallows tomorrow's new subtype:

```java
case Circle c -> ...;  case Rectangle r -> ...;
default -> 0;                       // wrong: a new Triangle quietly gets area 0
// right: no default. Adding Triangle makes this a compile error until you handle it.
```

**Forgetting `null`.**

```java
switch (payment) { case Card c -> ...; ... }    // wrong if payment can be null: NullPointerException
switch (payment) { case null -> "missing"; case Card c -> ...; ... }   // right
```

**Using a pattern variable where it might not be bound.**

```java
if (obj instanceof String s || s.length() > 3)     // wrong: doesn't compile
if (obj instanceof String s && s.length() > 3)     // right
```

**Shadowing a field with a pattern variable.** `if (o instanceof Point x)` inside a class with a field `x` hides the field in that block. Choose distinct names.

**Expecting record patterns to box or convert.** A pattern must match the component type: `Point(Integer x, ...)` doesn't match an `int` component, and `Box(String s)` doesn't match a `Box(Object o)` that holds an `Integer`.

## 6. When to use / when not to

**Use:**
- `instanceof` patterns everywhere you used to test and then cast, including `equals`.
- A pattern `switch` over a **sealed** hierarchy of records: payment types, shapes, events, AST nodes, API results like `Success` / `Failure`.
- Nested record patterns when the rule depends on the *structure*, e.g. "an order paid by UPI with an amount over 10,000".

**Don't:**
- Replace ordinary polymorphism when the behaviour belongs to the class. If every shape knows its own area, `shape.area()` is still fine. Patterns shine when the operation lives *outside* the types (reports, rules, serializers) or the types are plain data.
- Add `default` to a switch over a sealed type "just in case". You lose the compile-time check.
- Write long chains of guards that are really business rules. Consider a rules table or a strategy instead.

## 7. Interview angle

1. **What is pattern matching for `instanceof`, and what's the scope of the pattern variable?** `obj instanceof String s` tests the type and binds `s` if it matches. `s` is in scope wherever the compiler can prove the match succeeded: after `&&`, inside the `if`, or after an `if (!(... instanceof ...)) return;`.
2. **What is a guarded pattern?** A case pattern with an extra condition: `case String s when s.isEmpty() ->`. The case matches only if the type matches *and* the guard is true.
3. **Why combine sealed types with pattern switches?** The compiler knows every permitted subtype, so it can check the switch covers them all with no `default`. Adding a new subtype breaks the compile wherever handling is missing.
4. **What is a record pattern?** A pattern that deconstructs a record into its components using the canonical constructor's shape: `case Point(int x, int y)`. Patterns can nest, e.g. `Line(Point(var x1, var y1), var end)`.
5. **What happens if you switch on `null`?** A traditional or pattern switch throws `NullPointerException`, unless the pattern switch has a `case null`. `null instanceof X` is just `false`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_InstanceofPatterns.java` | Describe mixed objects with `instanceof` patterns and early returns, and rewrite an `equals` method | Easy |
| 2 | `exercises/Exercise2_ExpressionEvaluator.java` | Evaluate, print and simplify arithmetic expressions with a sealed interface, record patterns and nested patterns | Medium |
| 3 | `exercises/Exercise3_PaymentRouterBug.java` | A payment router lets huge payments through, routes a new payment type silently, and crashes on `null`. Fix the switch | Hard |
