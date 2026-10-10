# Expressions, Statements and Blocks

Source: https://dev.java/learn/language/constructs/basics/expressions-statements-blocks

← Back to [Java Language Basics](../README.md)

## 1. What it is

Java code is built from three layers. An **expression** computes a single value (`price * qty`). A **statement** is a complete instruction ending in `;` (`total = price * qty;`). A **block** is a group of statements in braces `{ ... }` that can go anywhere a single statement can, and that limits where its variables are visible.

## 2. Why it exists

These are the grammar of the language. Knowing which is which explains a lot of compiler errors ("not a statement", "cannot find symbol"), why `if (x) a(); b();` doesn't do what it looks like, and why a variable declared inside a loop isn't there after it.

## 3. Core concepts

**Expressions evaluate to a value, and every value has a type**
```java
qty * 2                 // int
price > 100             // boolean
"Hi " + name            // String
count = 0               // an assignment is an expression too: its value is 0 (int)
list.size()             // a method call: its return value
```

**Compound expressions** combine smaller ones. Parentheses make the order explicit:
```java
int x = 10 + 20 / 5;     // 14: / first
int y = (10 + 20) / 5;   // 6
```

**Statements**: a complete unit of execution.
- **Expression statements**: an expression made into a statement with `;`. Only some expressions are allowed: assignment, `++`/`--`, method calls, and `new`.
  ```java
  total += 5;          // assignment
  count++;             // increment
  System.out.println(); // method call
  new Thread();        // object creation
  ```
- **Declaration statements**: `int count = 0;`
- **Control flow statements**: `if`, `for`, `while`, `switch`, `return`, ... (next lessons).

**Blocks** group statements and create a **scope**:
```java
if (isMember) {                 // the block is the if's single "statement"
    double discount = 0.1;      // only exists inside these braces
    price -= price * discount;
}
// discount is not visible here
```

## 4. How it works under the hood

- An **assignment returns the assigned value**. That's why `a = b = 0;` works and why `while ((line = reader.readLine()) != null)` is a common idiom.
- A **local variable's scope** runs from its declaration to the end of the enclosing block. A variable declared inside a loop body is a fresh variable on every pass.
- Java does **not** allow a local in an inner block to reuse the name of a local in an outer block (unlike C). That's a compile error, not shadowing.
- **Floating-point expressions round** at each step. `0.1 + 0.2 == 0.3` is `false`, because neither side is exactly 0.3 and they round differently.

## 5. Common mistakes and gotchas

**Missing braces: only the first statement belongs to the `if`**
```java
if (isAdmin)
    grantAccess();
    logAccess();        // WRONG: indented, but runs for EVERYONE

if (isAdmin) {          // RIGHT: always use braces
    grantAccess();
    logAccess();
}
```

**A stray semicolon is an empty statement**
```java
if (balance < 0);       // WRONG: the ; is the whole if-body
{ sendWarning(); }      // this block always runs

if (balance < 0) { sendWarning(); }   // RIGHT
```

**Expression that isn't a statement**
```java
price * 2;              // WRONG: compile error "not a statement"
price = price * 2;      // RIGHT
```

**Variable declared in the wrong block**
```java
for (int day = 0; day < 7; day++) {
    int total = 0;      // WRONG: reset to 0 on every pass
    total += sales[day];
}

int total = 0;          // RIGHT: declared outside, so it accumulates
for (int day = 0; day < 7; day++) { total += sales[day]; }
```

**Comparing doubles with `==`**
```java
if (0.1 + 0.2 == 0.3)                      // WRONG: false
if (Math.abs((0.1 + 0.2) - 0.3) < 1e-9)    // RIGHT: compare with a tolerance
```

## 6. When to use / when not to

- **Always use braces** for `if`/`for`/`while` bodies, even for one line.
- Declare a variable in the **smallest block** that needs it; it can't be misused elsewhere.
- Use **parentheses** in compound expressions whenever a reader might hesitate.
- Using an assignment's value (`while ((x = next()) != null)`) is fine for that one read-loop idiom. Avoid it elsewhere; `if (a = b)`-style code is hard to read.

## 7. Interview angle

1. **Difference between an expression and a statement?** An expression produces a value; a statement is a complete instruction that does something. Adding `;` to some expressions (assignment, `++`, method call, `new`) makes them statements.
2. **What value does `x = 5` have?** `5`, with the type of `x`. That's why chained assignment works.
3. **What is a block, and what's its effect on variables?** `{ ... }` groups statements into one; locals declared inside are only visible until the closing brace.
4. **Why is `0.1 + 0.2 == 0.3` false?** Binary floating point can't represent these decimals exactly; the rounded sum differs from the rounded `0.3`. Compare with a tolerance or use `BigDecimal`.
5. **Can an inner block declare a local with the same name as an outer local?** No, it's a compile error in Java.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_MissingBraces.java` | Fix bugs caused by missing braces and a stray semicolon | Easy |
| 2 | `exercises/Exercise2_FloatCompare.java` | Compare doubles safely with a tolerance, and sum money exactly | Medium |
| 3 | `exercises/Exercise3_TokenReader.java` | Fix a block-scope bug and use an assignment as an expression in a read loop | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
