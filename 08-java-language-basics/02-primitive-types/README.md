# Primitive Types and Literals

Source: https://dev.java/learn/language/constructs/basics/primitive-types

← Back to [Java Language Basics](../README.md)

## 1. What it is

Java is **statically typed**: every variable has a type, fixed when you declare it. The simplest types are the eight **primitives**: `byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`. They hold a plain value (not an object), and you create them by writing a **literal** such as `42`, `3.5`, `'A'` or `true`.

## 2. Why it exists

The type tells the compiler how much memory a value needs and which operations make sense. `int` can be added; `boolean` can't. Catching `int age = "ten";` at compile time is much cheaper than finding it in production. Primitives are also fast: no object, no `new`, no garbage collection.

## 3. Core concepts

**The eight primitives**

| Type | Size | Range / values | Typical use |
|---|---|---|---|
| `byte` | 8 bit | -128 … 127 | raw bytes, big arrays |
| `short` | 16 bit | -32,768 … 32,767 | rarely used |
| `int` | 32 bit | about ±2.1 billion | **default for whole numbers** |
| `long` | 64 bit | about ±9.2 × 10¹⁸ | timestamps, file sizes, big counts |
| `float` | 32 bit | ~7 significant digits | big arrays of decimals, graphics |
| `double` | 64 bit | ~15-16 significant digits | **default for decimals** |
| `char` | 16 bit | one UTF-16 unit, `'\u0000'` … `'\uffff'` | a single character |
| `boolean` | — | `true` / `false` | flags, conditions |

**Integer literals**: `int` unless you add `L`.
```java
int  decimal = 26;
int  hex     = 0x1A;          // 26
int  binary  = 0b11010;       // 26 (Java 7+)
long big     = 8_000_000_000L; // too big for int: needs L. Use capital L, not l
```

**Floating-point literals**: `double` unless you add `f`.
```java
double price  = 19.99;
float  ratio  = 0.75f;        // without f: compile error (double → float loses precision)
double avogadro = 6.022e23;   // scientific notation
```

**char and String literals**: single quotes for `char`, double quotes for `String`.
```java
char grade = 'A';
char omega = '\u03A9';        // Ω, written as a Unicode escape
String path = "C:\\temp\\new"; // escapes: \n \t \" \' \\
```

**Underscores** make long numbers readable (Java 7+): `1_000_000`, `0xFF_FF`. Only *between* digits.

**`null`** can go in any reference variable (`String s = null;`), never in a primitive.

**Default values**: fields only. Number types → `0`, `boolean` → `false`, `char` → `'\u0000'`, objects → `null`. Local variables get no default.

## 4. How it works under the hood

- **Overflow wraps around silently.** `int` arithmetic that goes past `Integer.MAX_VALUE` jumps to a large negative number; no exception.
- **The type of an expression comes from its operands, not its target.** In `long ms = days * 24 * 60 * 60 * 1000;` every operand is `int`, so the multiplication is done in `int` and overflows *before* the result is widened to `long`.
- **`float` and `double` are binary fractions.** Most decimal values (`0.1`, `0.2`) can't be stored exactly, so `0.1 + 0.2` is `0.30000000000000004`.
- **`char` is a number.** `'A' + 1` is `66`; `(char) ('A' + 1)` is `'B'`.
- **Unsigned values (Java 8+)**: there's no `unsigned int` type, but `Integer`/`Long` have helpers like `Integer.toUnsignedString`, `Integer.compareUnsigned` and `Long.divideUnsigned` when you need to treat bits as unsigned.

## 5. Common mistakes and gotchas

**Overflow in int math stored into a long**
```java
long ms = 30 * 24 * 60 * 60 * 1000;    // WRONG: -1702967296 (int overflow)
long ms = 30L * 24 * 60 * 60 * 1000;   // RIGHT: 2592000000
```

**Money in double**
```java
double change = 1.00 - 0.90;           // WRONG: 0.09999999999999998
long changeCents = 100 - 90;           // RIGHT: count in cents (or use BigDecimal)
```

**Missing suffixes**
```java
float f = 2.5;      // WRONG: compile error, 2.5 is a double
float f = 2.5f;     // RIGHT
long  n = 3_000_000_000;   // WRONG: compile error, int literal too large
long  n = 3_000_000_000L;  // RIGHT
```

**Lowercase l**: `10l` looks like `101`. Always write `10L`.

**Bad underscore placement**: `_100`, `100_`, `3._14`, `3_.14`, `10_L` are all compile errors.

**Quotes**: `char c = "A";` doesn't compile. `char` uses `'A'`.

## 6. When to use / when not to

- Whole numbers → `int`. If it might pass ~2 billion (ids, timestamps in ms, bytes on disk) → `long`.
- Decimals for measurements and science → `double`. Use `float` only when memory matters (huge arrays).
- **Exact decimals (money)** → never `double`; count in the smallest unit (`long cents`) or use `BigDecimal`.
- `byte`/`short` → only for raw binary data or huge arrays; for normal math they get promoted to `int` anyway.
- Don't rely on default values; initialize explicitly so the intent is clear.

## 7. Interview angle

1. **Name the 8 primitive types and their sizes.** byte 8, short 16, int 32, long 64, float 32, double 64, char 16, boolean (JVM-dependent; logically 1 bit).
2. **What does `Integer.MAX_VALUE + 1` give?** `Integer.MIN_VALUE` (-2147483648). Integer overflow wraps silently.
3. **Why is `0.1 + 0.2 != 0.3` in Java?** Doubles are binary fractions; 0.1 and 0.2 can't be represented exactly. Use `BigDecimal` or integer cents for exact values.
4. **Is `String` a primitive?** No, it's a class. It just has special support: literals in double quotes and the `+` operator.
5. **Default value of a local `int`?** None. It must be assigned before use, or the code won't compile. Fields default to `0`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_PickTheType.java` | Write the right literals: long, hex, binary, float, char, underscores | Easy |
| 2 | `exercises/Exercise2_OverflowAndChars.java` | Fix an int-overflow bug and do arithmetic with `char` | Medium |
| 3 | `exercises/Exercise3_ChangeMachine.java` | A vending machine gives wrong change because of `double`. Rewrite it with integer cents | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
