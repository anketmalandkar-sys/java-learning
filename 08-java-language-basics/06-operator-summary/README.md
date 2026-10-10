# Operator Summary and Precedence

Source: https://dev.java/learn/language/constructs/basics/all-operators

← Back to [Java Language Basics](../README.md)

## 1. What it is

A one-page **reference** of every Java operator, grouped by kind, plus the rules that decide **which operator runs first** in an expression: **precedence** (which operators bind tighter) and **associativity** (left-to-right or right-to-left for operators of equal precedence). Lesson 05 showed what each operator does; this lesson is about reading and writing whole expressions correctly.

## 2. Why it exists

`a + b / 2` and `(a + b) / 2` are different numbers, and `flags & MASK == MASK` doesn't even compile. When an expression mixes operators, you need either the precedence rules in your head or parentheses on the page. Knowing the rules lets you read other people's code; using parentheses keeps yours readable.

## 3. Core concepts

**All operators by group**

| Group | Operators |
|---|---|
| Simple assignment | `=` |
| Arithmetic | `+` (also String concatenation) `-` `*` `/` `%` |
| Unary | `+` `-` `++` `--` `!` |
| Equality / relational | `==` `!=` `>` `>=` `<` `<=` |
| Conditional | `&&` `||` `?:` |
| Type comparison | `instanceof` |
| Bitwise / shift | `~` `<<` `>>` `>>>` `&` `^` `|` |
| Compound assignment | `+=` `-=` `*=` `/=` `%=` `&=` `^=` `|=` `<<=` `>>=` `>>>=` |

**Precedence, highest first** (same row = same precedence)

| Level | Operators | Associativity |
|---|---|---|
| postfix | `x++` `x--` | left |
| unary | `++x` `--x` `+x` `-x` `~` `!` | right |
| multiplicative | `*` `/` `%` | left |
| additive | `+` `-` | left |
| shift | `<<` `>>` `>>>` | left |
| relational | `<` `>` `<=` `>=` `instanceof` | left |
| equality | `==` `!=` | left |
| bitwise AND | `&` | left |
| bitwise XOR | `^` | left |
| bitwise OR | `|` | left |
| logical AND | `&&` | left |
| logical OR | `||` | left |
| ternary | `? :` | right |
| assignment | `=` `+=` `-=` … | right |

Memory aid: **unary → math (`* /` then `+ -`) → shift → compare → bits → logic (`&&` then `||`) → ternary → assign**.

**Associativity in action**
```java
10 - 4 - 3       // left to right: (10 - 4) - 3 = 3
a = b = c = 0;   // right to left: a = (b = (c = 0))
```

## 4. How it works under the hood

- Precedence decides how the compiler **groups** an expression into a tree. Operands are still **evaluated left to right**: in `f() + g() * h()`, `f()` runs first even though the multiplication happens first.
- `&&`, `||` and `?:` are the exceptions that may **skip** evaluating an operand.
- The compiler adds no "smart" grouping: `"Sum: " + 1 + 2` is `("Sum: " + 1) + 2` = `"Sum: 12"`, because `+` is left-associative.

## 5. Common mistakes and gotchas

**Average without parentheses**
```java
int avg = a + b / 2;       // WRONG: a + (b / 2)
int avg = (a + b) / 2;     // RIGHT
```

**Bitwise `&` vs `==`**: `==` binds tighter.
```java
if (flags & MASK == MASK)     // WRONG: means flags & (MASK == MASK) → compile error
if ((flags & MASK) == MASK)   // RIGHT
```

**Mixing `&&` and `||`**: `&&` binds tighter.
```java
year % 4 == 0 && year % 100 != 0 || year % 400 == 0   // correct, but needs a second look
(year % 4 == 0 && year % 100 != 0) || year % 400 == 0 // RIGHT: same meaning, obvious
```

**Shift vs addition**: `+` binds tighter than `<<`.
```java
1 << n + 1      // means 1 << (n + 1), probably not what was meant
(1 << n) + 1    // RIGHT if you wanted "2^n plus one"
```

**String concatenation**
```java
"Total: " + price * qty + tax    // * first (good), but then + tax is concatenated as text
"Total: " + (price * qty + tax)  // RIGHT
```

## 6. When to use / when not to

- Rely on precedence for the obvious cases everybody knows: `*` before `+`, comparisons before `&&`.
- **Add parentheses** whenever you mix bitwise and comparison operators, mix `&&` with `||`, use shifts with arithmetic, or concatenate Strings with math. Parentheses cost nothing at runtime.
- If an expression needs a precedence table to read, split it into named local variables.

## 7. Interview angle

1. **Which runs first in `a + b * c`?** `b * c`: multiplicative operators have higher precedence than additive.
2. **What does `a = b = 5` do?** Assignment is right-associative: `b = 5` first, its value (5) is then assigned to `a`.
3. **Does precedence change the order operands are evaluated in?** No. Operands are evaluated left to right; precedence only decides grouping.
4. **What's `"1" + 2 + 3` vs `1 + 2 + "3"`?** `"123"` vs `"33"`: left-to-right, and `+` becomes concatenation once a String is involved.
5. **Why doesn't `x & 1 == 0` compile for an int `x`?** `==` binds tighter than `&`, so it's `x & (1 == 0)`, which is `int & boolean`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_EvaluateQuiz.java` | Predict the value of 8 expressions; the program checks you | Easy |
| 2 | `exercises/Exercise2_AddParentheses.java` | Fix four buggy formulas by adding parentheses only | Medium |
| 3 | `exercises/Exercise3_OneLiners.java` | Write leap-year, range and power-of-two checks as single expressions | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
