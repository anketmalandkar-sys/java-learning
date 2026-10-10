# Switch Expressions

Source: https://dev.java/learn/language/constructs/basics/switch-expression

← Back to [Java Language Basics](../README.md)

## 1. What it is

Since **Java 14**, `switch` can be an **expression** that produces a value, and cases can use **arrow labels** (`case X ->`) that never fall through. Several constants can share one case (`case SAT, SUN ->`), a case can run a block and hand back its value with **`yield`**, and the compiler checks that a switch expression covers **every possible value** (exhaustiveness).

## 2. Why it exists

The classic switch statement (lesson 09) has three problems:
1. **Fall-through by default**: forget one `break` and the next case runs too.
2. **One scope for the whole body**: a variable declared in one case leaks into the others.
3. **It's a statement**: to get a value out, you declare a variable, assign it in every case, and hope you didn't miss one.

```java
// Before: 9 lines, 3 breaks to forget
String type;
switch (day) {
    case SATURDAY: case SUNDAY: type = "Weekend"; break;
    default: type = "Weekday"; break;
}
// After: one expression, no breaks
String type = switch (day) {
    case SATURDAY, SUNDAY -> "Weekend";
    default -> "Weekday";
};
```

## 3. Core concepts

**Arrow labels**: only the code to the right of the matching arrow runs. It can be an expression, a block, or a `throw`.
```java
switch (command) {
    case "start" -> engine.start();
    case "stop"  -> engine.stop();
    default      -> System.out.println("Unknown: " + command);
}
```

**Several constants per case**
```java
case 4, 6, 9, 11 -> 30;
```

**Switch as an expression**: assign it, return it, pass it. Note the `;` after the closing brace.
```java
int days = switch (month) {
    case 4, 6, 9, 11 -> 30;
    case 2 -> leap ? 29 : 28;
    default -> 31;
};
```

**`yield`**: when a case needs a block, `yield` gives the block's value. (`return` would leave the whole method, so it isn't allowed there.)
```java
double fee = switch (zone) {
    case LOCAL -> 40;
    case NATIONAL -> {
        double base = 60;
        yield weightKg > 5 ? base * 2 : base;
    }
    case INTERNATIONAL -> throw new IllegalArgumentException("Not shipped abroad");
};
```

**Exhaustiveness**: a switch expression must handle every value. For an `int` or `String` that means a `default`. For an **enum**, listing every constant is enough, and then the compiler tells you when someone adds a new constant.

**Colon form inside an expression**: `case X:` is still allowed in a switch expression; then fall-through applies again and values are produced with `yield`. Don't mix `:` and `->` in one switch.

## 4. How it works under the hood

- Each arrow case is its **own scope**: variables in one case's block don't exist in others.
- The **type** of a switch expression comes from its case results (like the ternary): all `int` → `int`; `int` and `double` → `double`; mixed objects → their common supertype.
- For an exhaustive **enum** switch without `default`, the compiler adds a hidden default that throws (`MatchException` in Java 21, `IncompatibleClassChangeError` before) if a new constant shows up at runtime from a recompiled enum.
- A **`null` selector** still throws `NullPointerException`, unless you write `case null ->` (standard since **Java 21**, as part of pattern matching for switch).

## 5. Common mistakes and gotchas

**Forgetting the `;` after an expression switch**
```java
String s = switch (x) { default -> "a"; }    // WRONG: missing ;
String s = switch (x) { default -> "a"; };   // RIGHT
```

**`return` instead of `yield` inside a case block**
```java
int n = switch (k) {
    case 1 -> { int t = 5; return t; }   // WRONG: compile error
    case 1 -> { int t = 5; yield t; }    // RIGHT
    default -> 0;
};
```

**Not exhaustive**
```java
String size = switch (code) {   // code is a char
    case 'S' -> "Small";
    case 'L' -> "Large";
};                              // WRONG: compile error, other chars aren't covered
// RIGHT: add  default -> "Unknown";
```

**Adding a `default` to an enum switch that already covers everything**: it compiles, but it switches off the compiler's warning when a new constant is added. Leave it out for enums you control.

**Mixing `:` and `->`** in one switch: compile error. Pick one style.

## 6. When to use / when not to

- **Use a switch expression** whenever a switch exists to compute a value. It's shorter and the compiler checks coverage.
- **Use arrow-form switch statements** for side effects (calling different methods per case): no fall-through to worry about.
- **Use enum switches without `default`** to get compile errors when the enum grows.
- **Use if/else** for ranges and conditions on several variables (or, in Java 21+, switch with `when` guards; see pattern matching).
- Use the classic colon form only for deliberate fall-through, which is rare.

## 7. Interview angle

1. **Difference between a switch statement and a switch expression?** An expression produces a value and must be exhaustive; a statement just runs code and may match nothing.
2. **What does `yield` do?** It gives the value of a block inside a switch expression and leaves the switch (not the method).
3. **Does the arrow form fall through?** No. Only the matching case's right-hand side runs.
4. **When can you leave out `default` in a switch expression?** When the cases already cover every value: for example, all constants of an enum (or all permitted subtypes of a sealed type).
5. **Which Java version made switch expressions standard?** Java 14 (previewed in 12 and 13).

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_ToExpressions.java` | Rewrite classic switch statements as switch expressions | Easy |
| 2 | `exercises/Exercise2_ShippingYield.java` | Use `yield`, `throw` and an exhaustive enum switch without `default` | Medium |
| 3 | `exercises/Exercise3_RpnCalculator.java` | Build a stack calculator driven by a switch expression | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
