# Numbers: Wrappers, Formatting and Math

Source: https://dev.java/learn/language/constructs/numbers-strings/numbers

← Back to [Numbers and Strings](../README.md)

## 1. What it is

Each numeric primitive has a **wrapper class** (`Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`), all subclasses of the abstract `java.lang.Number`. Wrappers add constants (`Integer.MAX_VALUE`), conversion methods (`Integer.parseInt("42")`, `Integer.toBinaryString(10)`) and let numbers be used as objects. Around them, Java gives you **formatted output** (`printf`, `String.format`, `DecimalFormat`) and the **`Math`** class for rounding, powers, roots, trigonometry and random numbers.

## 2. Why it exists

Real programs read numbers from text (forms, files, URLs), print them for humans (`1,234.50`, `007`, `+3.2%`), and calculate with them (round to cents, square roots, distances). Doing that by hand means string-chopping and loops. The wrapper and `Math` classes give tested, one-call answers.

## 3. Core concepts

**Use a wrapper when** a method needs an object (collections), you need the type's limits (`Long.MAX_VALUE`), or you're converting to/from text or another number base.

**Wrapper → primitive** (every `Number` has these):
```java
Integer n = 42;
n.intValue(); n.longValue(); n.doubleValue();   // 42, 42L, 42.0
```

**Text → number**
```java
int a = Integer.parseInt("42");           // primitive int
Integer b = Integer.valueOf("42");        // Integer object
int c = Integer.parseInt("ff", 16);       // 255: any radix
int d = Integer.valueOf("333", 8);        // 219: octal
Integer e = Integer.decode("0x1F");       // 31: reads 0x, #, 0 prefixes
double f = Double.parseDouble("3.75");
Integer.parseInt("4.5");                  // NumberFormatException
```

**Number → text**
```java
String.valueOf(42)            // "42"
Integer.toString(255, 16)     // "ff"
Integer.toBinaryString(10)    // "1010"
Integer.toHexString(255)      // "ff"
```

**Comparing**: `compareTo` gives negative/zero/positive. `equals` is true only for **the same type and value**.
```java
Integer.valueOf(5).compareTo(7)     // negative
Integer.compare(5, 7)               // negative, works on primitives
Integer.valueOf(5).equals(5L)       // false! 5L boxes to a Long
```

**`printf` / `String.format`**: `%[flags][width][.precision]conversion`.
```java
System.out.printf("%d items%n", 3);          // %d integer, %n newline (use %n, not \n)
System.out.printf("%.2f%n", 3.14159);        // 3.14
System.out.printf("%08.2f%n", 3.14159);      // 00003.14  zero-padded to width 8
System.out.printf("%+d%n", 5);               // +5        always show sign
System.out.printf("%,d%n", 1234567);         // 1,234,567 grouping
System.out.printf("[%-6s][%6s]%n", "ab", "cd"); // [ab    ][    cd] left/right justify
System.out.printf("%tY-%<tm-%<td%n", date);  // 2024-03-09 dates: %t + a letter
```
`printf` and `format` on `System.out` are the same method; `String.format(...)` returns the text instead of printing it. A `Locale` argument changes separators: `String.format(Locale.FRANCE, "%.2f", 3.5)` → `3,50`.

**`DecimalFormat`**: a reusable pattern. `0` = digit, always shown; `#` = digit, hidden if zero; `,` grouping; `.` decimal point.
```java
new DecimalFormat("###,###.###").format(123456.789)  // 123,456.789
new DecimalFormat("###.##").format(123456.789)       // 123456.79 (rounded)
new DecimalFormat("000000.000").format(123.78)       // 000123.780
new DecimalFormat("$###,###.##").format(12345.67)    // $12,345.67
```

**`Math`**: all static.
```java
Math.abs(-7)          // 7
Math.ceil(2.1)        // 3.0   (a double!)
Math.floor(2.9)       // 2.0
Math.rint(2.5)        // 2.0   nearest integer; ties go to the EVEN one
Math.round(2.5)       // 3     returns long (int for a float argument)
Math.max(3, 8)        // 8
Math.pow(2, 10)       // 1024.0
Math.sqrt(144)        // 12.0
Math.exp(1), Math.log(Math.E)   // e, 1.0
Math.sin(Math.toRadians(30))    // 0.5 (roughly): trig uses radians
Math.PI, Math.E
```

**Random numbers**
```java
double r = Math.random();                 // 0.0 <= r < 1.0
int die = (int) (Math.random() * 6) + 1;  // 1..6
Random rng = new Random(42);              // seeded: same sequence every run
int roll = rng.nextInt(6) + 1;            // 1..6
```

## 4. How it works under the hood

- `parseInt` returns a primitive; `valueOf` returns an `Integer` and, for -128..127, a **cached** object (more in lesson 02).
- `ceil`, `floor` and `rint` return **`double`** so they can represent huge values and `NaN`; cast or use `Math.round` if you need an integer type.
- `Math.round(x)` is `floor(x + 0.5)`: `round(-2.5)` is `-2`, not `-3`.
- `printf` uses `java.util.Formatter` and the **default locale** unless you pass one. On a machine set to German, `%.2f` prints `3,14`.
- `Math.random()` uses one shared `Random`. For many numbers, repeatability (tests), or ranges, create your own `Random` (or `ThreadLocalRandom` in multi-threaded code).

## 5. Common mistakes and gotchas

**Parsing without handling bad input**
```java
int qty = Integer.parseInt(userInput);        // WRONG: crashes on "abc" or ""
try {                                         // RIGHT
    qty = Integer.parseInt(userInput.strip());
} catch (NumberFormatException e) {
    qty = 0;
}
```

**Expecting `ceil`/`floor` to return an int**
```java
int pages = Math.ceil(items / 10.0);          // WRONG: compile error (double → int)
int pages = (int) Math.ceil(items / 10.0);    // RIGHT
```

**Integer division inside `ceil`**
```java
Math.ceil(7 / 2)       // WRONG: 3.0, because 7 / 2 is already 3
Math.ceil(7 / 2.0)     // RIGHT: 4.0
```

**`equals` across wrapper types**
```java
Long id = 5L;
id.equals(5)           // WRONG: false, 5 boxes to Integer
id.equals(5L)          // RIGHT: true
```

**`\n` in `printf`**: prints a Unix newline everywhere. `%n` gives the platform's newline.

**`%d` with a double** → `IllegalFormatConversionException` at runtime. Use `%f` (or `%.0f`).

## 6. When to use / when not to

- `parseInt`/`parseDouble` when you want a primitive; `valueOf` when you need the object.
- `String.format`/`printf` for one-off output; `DecimalFormat` when the same pattern is reused (reports, invoices).
- `Math.round` for nearest integer; `ceil` for "how many boxes/pages do I need"; `floor` for "how many full ones".
- `new Random(seed)` in tests and simulations so runs are repeatable; `Math.random()` only for a quick one-off.
- Money: **not** `double` + `DecimalFormat` for calculations. Use `BigDecimal` or integer cents, and format at the very end.

## 7. Interview angle

1. **`Integer.parseInt` vs `Integer.valueOf`?** `parseInt` returns an `int`; `valueOf` returns an `Integer` (cached for -128..127).
2. **What does `Math.round(-2.5)` return?** `-2`. `round` is `floor(x + 0.5)`, so halves round toward positive infinity.
3. **Why does `Math.ceil` return a double?** So it can represent results outside the `long` range and special values like `NaN` and infinity.
4. **Why prefer `%n` over `\n` in format strings?** `%n` outputs the platform-specific line separator.
5. **How do you get a random int from 1 to 6?** `new Random().nextInt(6) + 1` (or `ThreadLocalRandom.current().nextInt(1, 7)`).

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_ParseAndConvert.java` | Parse numbers safely and convert between bases | Easy |
| 2 | `exercises/Exercise2_FormatReport.java` | Format a sales report with `String.format` and `DecimalFormat` | Medium |
| 3 | `exercises/Exercise3_MathToolkit.java` | Pagination, rounding to a step, distance and a seeded dice game with `Math` and `Random` | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
