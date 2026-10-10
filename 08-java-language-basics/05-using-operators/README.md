# Using Operators

Source: https://dev.java/learn/language/constructs/basics/using-operators

← Back to [Java Language Basics](../README.md)

## 1. What it is

**Operators** are the symbols that compute a new value from one, two or three **operands**: `+` adds, `==` compares, `&&` combines conditions, `?:` picks one of two values. Java groups them into assignment, arithmetic, unary, equality/relational, conditional, type comparison (`instanceof`), and bitwise/shift operators.

## 2. Why it exists

Without operators you couldn't express even `total = price * quantity`. Knowing the exact behaviour of each one matters: integer division, `x++` vs `++x`, and short-circuiting `&&` cause real bugs when you guess.

## 3. Core concepts

**Assignment**: `=` stores the right side in the variable on the left. Compound forms do an operation and assign: `+=`, `-=`, `*=`, `/=`, `%=`.
```java
int stock = 10;
stock -= 3;          // stock = stock - 3 → 7
```

**Arithmetic**: `+ - * / %`. Between two ints, `/` drops the fraction and `%` gives the remainder.
```java
7 / 2      // 3
7 % 2      // 1   (odd!)
7 / 2.0    // 3.5 (one double operand → double math)
"Total: " + 5 + 5   // "Total: 55": + concatenates once a String is involved
```

**Unary**: `+x`, `-x`, `++x`, `x++`, `--x`, `x--`, `!flag`.
```java
int x = 5;
int a = x++;   // a = 5, then x becomes 6   (postfix: use, then increment)
int b = ++x;   // x becomes 7, then b = 7   (prefix: increment, then use)
```

**Equality and relational**: `== != < <= > >=`, always produce a `boolean`.

**Conditional**: `&&` (AND), `||` (OR). They **short-circuit**: the right side runs only if it can still change the answer.
```java
if (user != null && user.isActive()) { ... }   // safe: isActive() never runs on null
```

**Ternary** `condition ? valueIfTrue : valueIfFalse`, a compact if/else that produces a value.
```java
String label = stock > 0 ? "In stock" : "Sold out";
```

**`instanceof`** tests an object's type. `null instanceof Anything` is `false`.
```java
if (shape instanceof Circle c) { ... c.radius() ... }   // with pattern binding, Java 16+
```

**Bitwise and shift**: work on the individual bits of integers.
```java
0b1100 & 0b1010   // 0b1000  AND
0b1100 | 0b1010   // 0b1110  OR
0b1100 ^ 0b1010   // 0b0110  XOR
~0                // -1      flip every bit
1 << 3            // 8       shift left (×2 per step)
-16 >> 2          // -4      shift right, keeps the sign
-16 >>> 28        // 15      shift right, fills with 0
```

## 4. How it works under the hood

- **Numeric promotion**: before arithmetic, `byte`, `short` and `char` are promoted to `int`. If either operand is `long`, `float` or `double`, the other is widened to match. That's why `'A' + 1` is the `int` 66.
- **Compound assignment casts silently**: `byte b = 10; b += 5;` compiles (it means `b = (byte)(b + 5)`), but `b = b + 5;` doesn't.
- **Integer division truncates toward zero**: `-7 / 2` is `-3`, and `-7 % 2` is `-1`. The remainder takes the sign of the left operand.
- **`&` and `|` on booleans** also work, but they **don't** short-circuit; both sides always run.
- `==` on objects compares **references**, not contents (that's what `equals` is for).

## 5. Common mistakes and gotchas

**`=` instead of `==`**
```java
if (isAdmin = true) { ... }   // WRONG: assigns, always true
if (isAdmin) { ... }          // RIGHT
```

**Integer division**
```java
double avg = sum / count;            // WRONG if both are int: fraction lost before widening
double avg = (double) sum / count;   // RIGHT
```

**Checking "odd" with `== 1`**
```java
n % 2 == 1      // WRONG for negatives: -3 % 2 is -1
n % 2 != 0      // RIGHT
```

**Postfix in an expression**
```java
int count = 0;
count = count++;   // WRONG: count stays 0 (old value is assigned back)
count++;           // RIGHT
```

**`&` instead of `&&`**
```java
if (name != null & name.length() > 0)    // WRONG: NullPointerException when name is null
if (name != null && name.length() > 0)   // RIGHT
```

**`==` on Strings**: `a == b` compares references. Use `a.equals(b)`.

## 6. When to use / when not to

- Use **compound assignment** (`+=`) for updates; it's shorter and states intent.
- Use `++`/`--` **on their own line**. Inside bigger expressions they're hard to read.
- Use the **ternary** for a short choice between two values. Nested ternaries or ones with side effects → write an `if`.
- Always use `&&`/`||` for conditions; use `&`/`|`/`^` for bit manipulation.
- Bitwise operators: flags, masks, hashing, low-level protocols. Not for everyday arithmetic tricks (`x << 1` instead of `x * 2` just hurts readability).

## 7. Interview angle

1. **Difference between `x++` and `++x`?** Both add 1. `x++` evaluates to the old value, `++x` to the new one.
2. **Difference between `&&` and `&`?** `&&` short-circuits: the right side is skipped when the left is false. `&` always evaluates both (and is also bitwise AND on integers).
3. **What does `-7 % 3` give?** `-1`. The result takes the sign of the left operand.
4. **`>>` vs `>>>`?** `>>` keeps the sign bit (arithmetic shift); `>>>` fills with zeros (logical shift). They differ only for negative numbers.
5. **Why does `byte b = 1; b += 1;` compile but `b = b + 1;` doesn't?** Compound assignment includes an implicit cast back to the variable's type; `b + 1` is an `int`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_DigitsAndTime.java` | Use `/` and `%` for even/odd, digit sums and time formatting | Easy |
| 2 | `exercises/Exercise2_SafeChecks.java` | Use short-circuit `&&`/`||`, the ternary and `instanceof` safely | Medium |
| 3 | `exercises/Exercise3_PermissionFlags.java` | Store file permissions as bit flags with `&`, `|`, `~`, `<<` | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
