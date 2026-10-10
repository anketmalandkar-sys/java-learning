# Switch Statements

Source: https://dev.java/learn/language/constructs/basics/switch-statement

← Back to [Java Language Basics](../README.md)

## 1. What it is

A **switch statement** picks a path based on the value of one **selector** expression. The body has `case` labels (and an optional `default`). Java jumps to the matching label and runs from there **until a `break`** or the end of the switch. This lesson covers the classic colon form (`case X:`); lesson 10 covers the modern arrow form and switch *expressions*.

## 2. Why it exists

When you compare one value against many constants, a long `if (x == 1) ... else if (x == 2) ...` chain repeats the variable on every line. A switch states "branch on this value" once, groups values that share behaviour, and can be compiled into a fast jump table.

## 3. Core concepts

**Basic form**
```java
switch (month) {
    case 1:
        name = "January";
        break;
    case 2:
        name = "February";
        break;
    default:
        name = "Unknown";
        break;
}
```

**Allowed selector types**: `int`, `short`, `byte`, `char`, their wrappers (`Integer` ...), enums, and `String` (Java 7+).
**Not allowed**: `boolean`, `long`, `float`, `double`. (Java 21 pattern matching widens this for objects, but not for these primitives.)

**Several labels, one action**
```java
switch (month) {
    case 4: case 6: case 9: case 11:
        days = 30;
        break;
    case 2:
        days = isLeap ? 29 : 28;
        break;
    default:
        days = 31;
}
```

**Fall-through**: without `break`, execution continues into the next case's code, whatever its label.
```java
switch (level) {
    case 3: unlock("boss");    // no break: falls through
    case 2: unlock("bonus");
    case 1: unlock("basic");
}
// level 3 unlocks boss, bonus, basic
```

**Switch on String**: compared with `equals`, so case matters.
```java
switch (command.toLowerCase()) {
    case "start": ... break;
    case "stop":  ... break;
    default: System.out.println("Unknown command");
}
```

**`default`** runs when no case matches. It can go anywhere, but put it last.

## 4. How it works under the hood

- Case labels must be **compile-time constants** (literals, `final` constants, enum names), and must be **unique**.
- `case` labels are just **entry points**; `break` is what leaves. That's why forgetting it causes fall-through.
- The whole switch block is **one scope**: a variable declared under `case 1:` is visible (but maybe unassigned) under `case 2:`. Wrap a case's code in `{ }` if you need a local.
- A `String` switch compares `hashCode()` first, then `equals()`. A **`null` selector** throws `NullPointerException` before any case is tried (in this classic form).

## 5. Common mistakes and gotchas

**Forgotten `break`**
```java
switch (status) {
    case "PAID":    ship();       // WRONG: falls into REFUND
    case "REFUND":  refund(); break;
}
switch (status) {
    case "PAID":    ship();   break;   // RIGHT
    case "REFUND":  refund(); break;
}
```

**Null selector**
```java
switch (command) { ... }                 // WRONG: NPE if command is null
if (command == null) return "none";      // RIGHT: guard first
switch (command) { ... }
```

**Case-sensitive Strings**
```java
switch (input) { case "yes": ... }               // "YES" doesn't match
switch (input.toLowerCase()) { case "yes": ... } // RIGHT
```

**Ranges don't work**: `case score >= 90:` doesn't compile. Use `if`/`else if` for ranges.

**Missing `default`**: an unexpected value silently does nothing. Add a `default` that handles or reports it.

## 6. When to use / when not to

- **Use switch** to branch on one `int`/`char`/`String`/enum value against several constants.
- **Use if/else** for ranges (`score >= 90`), booleans, `long`/`double` values, or conditions over several variables.
- **Prefer the arrow form or a switch expression** (lesson 10) in new code (Java 14+): no fall-through by accident. Use classic fall-through only when it's deliberate, and comment it (`// fall through`).

## 7. Interview angle

1. **What types can a switch selector be?** `byte`, `short`, `char`, `int`, their wrappers, enums, `String`. Not `long`, `float`, `double`, `boolean`.
2. **What is fall-through?** Without `break`, execution continues into the next case's statements regardless of its label.
3. **What happens if the selector is `null`?** A classic switch throws `NullPointerException`. (Java 21 allows an explicit `case null` in pattern switches.)
4. **Can case labels be variables?** No, they must be compile-time constants (and unique).
5. **How is a String switch implemented?** By `hashCode()` to pick a candidate, then `equals()` to confirm.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_DaysInMonth.java` | Grouped case labels and `default` | Easy |
| 2 | `exercises/Exercise2_RolePermissions.java` | Deliberate fall-through on a String switch, with a null guard | Medium |
| 3 | `exercises/Exercise3_VendingCommands.java` | Fix a command switch with missing breaks, no default, and a null crash | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
