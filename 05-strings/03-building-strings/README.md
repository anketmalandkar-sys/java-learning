[← Strings roadmap](../README.md)

# Building strings efficiently

## 1. What it is

Strings are immutable, so "adding" to a string always builds a **new** one. That's fine once. In a loop, every step copies everything built so far. **`StringBuilder`** is the fix: a *mutable* buffer of characters that you append to in place, and turn into a `String` once at the end.

For the common case of joining items with a separator, Java also has `String.join`, `StringJoiner` and `Collectors.joining`.

## 2. Why it exists

```java
String report = "";
for (Order order : orders) {          // 20,000 orders
    report += order.toLine() + "\n";  // copies the WHOLE report so far, every time
}
```

Each `+=` creates a new string and copies all the old characters into it. With `n` lines that's about `n²/2` character copies: for 20,000 lines of 30 characters, roughly **6 billion**. It can take seconds, and it creates 20,000 throwaway strings for the garbage collector.

```java
StringBuilder report = new StringBuilder();
for (Order order : orders) {
    report.append(order.toLine()).append('\n');   // writes into the same buffer
}
String result = report.toString();                // one final copy
```

That's about `n` character copies in total, so it finishes in milliseconds.

## 3. Core concepts

**`StringBuilder` basics.** Every method returns the builder itself, so calls chain:

```java
StringBuilder sb = new StringBuilder("Order");
sb.append(' ').append(101).append(": ").append(2499.5);   // "Order 101: 2499.5"
sb.insert(0, "[").append("]");                            // "[Order 101: 2499.5]"
sb.reverse();                                             // reverses in place
sb.setLength(0);                                          // empty it, to reuse
sb.length();                                              // characters so far
sb.charAt(0); sb.setCharAt(0, 'X'); sb.deleteCharAt(0);
sb.delete(2, 5); sb.replace(0, 3, "abc"); sb.indexOf("abc");
String done = sb.toString();
```

**`StringBuffer`** is the old (Java 1.0) version. Every method is `synchronized`, which makes it slower and is almost never needed. A string being built usually belongs to one thread. Use `StringBuilder`.

**`+` in a single expression is fine.** The compiler optimises it (Java 9+ uses `invokedynamic` and `StringConcatFactory`):

```java
String line = id + "," + name + "," + total;   // fine: one expression, one result
```

The cost only appears when a **loop** keeps adding to the same string.

**Joining with a separator.**

```java
String.join(", ", List.of("Pune", "Goa", "Delhi"))   // "Pune, Goa, Delhi"

StringJoiner joiner = new StringJoiner(", ", "[", "]");
joiner.add("Pune").add("Goa");
joiner.toString()                                     // "[Pune, Goa]"
joiner.setEmptyValue("none");                         // what to return if nothing was added

orders.stream()
      .map(Order::id)
      .collect(Collectors.joining(", ", "Orders: ", "."))   // "Orders: A1, A2, A3."
```

No more "remove the last comma" code: the separator only goes *between* items.

**`repeat`.** `"-".repeat(40)` (Java 11+) instead of a loop of appends.

## 4. How it works under the hood

- **A `StringBuilder` holds a growable array.** It starts with room for 16 characters (or the initial string's length + 16). When it fills up, it allocates a bigger array (about double) and copies. That's rare, so appends are cheap on average. If you know the final size, `new StringBuilder(expectedLength)` skips even that.
- **`toString()` makes a copy**, so later changes to the builder don't affect the returned `String`, which stays immutable.
- **Compile-time `+`:** `"a" + "b"` is joined by the compiler into one literal (lesson 01). With variables, Java 9+ compiles `a + b + c` into one call that works out the final size and copies each piece once.
- **In a loop**, the compiler can't see that you're building one big string over many iterations, so each `+=` is a separate full copy.

## 5. Common mistakes and gotchas

**`+=` in a loop.**

```java
String csv = "";
for (String name : names) csv += name + ",";       // wrong: n² copying
String csv = String.join(",", names);               // right
```

**`+` inside `append`.** This builds a temporary string first, which defeats the point in hot code:

```java
sb.append(name + ": " + total);                      // wasteful in a loop
sb.append(name).append(": ").append(total);          // right
```

**`new StringBuilder('[')`.** There's no `char` constructor. The `char` is widened to an `int` and used as the **capacity** (91):

```java
new StringBuilder('[').append("a").toString()    // wrong: "a", the '[' is gone
new StringBuilder("[").append("a").toString()    // right: "[a"
```

**Comparing builders with `equals`.** `StringBuilder` doesn't override `equals`, so it compares object identity. And a `String` is never `equals` to a `StringBuilder`:

```java
sb1.equals(sb2)                 // wrong: false even with the same text
"abc".equals(sb)                // wrong: false, different types
sb1.compareTo(sb2) == 0         // right (Java 11+)
"abc".contentEquals(sb)         // right
sb.toString().equals("abc")     // right
```

**Keeping a builder after `toString()` and expecting the string to change.** The returned `String` is a snapshot.

**The trailing separator.**

```java
for (String s : items) sb.append(s).append(", ");   // "a, b, c, ": then code to chop it off
String.join(", ", items)                            // "a, b, c"
```

## 6. When to use / when not to

| Situation | Use |
|-----------|-----|
| A few pieces in one expression | `+` |
| Building a string in a loop | `StringBuilder` |
| Items joined with a separator | `String.join`, or `Collectors.joining` from a stream |
| Separator plus a prefix/suffix, or an "empty" text | `StringJoiner` / `Collectors.joining(sep, prefix, suffix)` |
| Fixed-width columns and decimals | `String.format` / `formatted`, appended into a `StringBuilder` |
| Many threads appending to one buffer | rarely needed. Usually build per thread and combine. (That's `StringBuffer`'s only niche.) |

## 7. Interview angle

1. **Why is `+=` in a loop slow?** Strings are immutable, so each `+=` creates a new string and copies everything built so far. That's quadratic work. A `StringBuilder` appends into one growable buffer, which is linear.
2. **`StringBuilder` vs `StringBuffer`?** Same API. `StringBuffer` is synchronized (thread-safe, slower) and dates from Java 1.0. `StringBuilder` (Java 5) isn't synchronized and is the default choice.
3. **Is `String s = a + b + c;` slow?** No. In a single expression the compiler optimises it into one efficient concatenation (via `StringBuilder` before Java 9, `StringConcatFactory` since). The problem is only repeated `+=` in a loop.
4. **Does `StringBuilder` override `equals`?** No, so it compares references. Use `sb1.compareTo(sb2) == 0` (Java 11+), `String.contentEquals(sb)`, or compare the `toString()`s.
5. **What does `new StringBuilder('a')` do?** It creates an empty builder with capacity 97. The `char` is widened to `int` and picks the `int capacity` constructor.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_BuilderBasics.java` | Reverse words, build a CSV row, draw a progress bar and compress repeated letters with `StringBuilder` | Easy |
| 2 | `exercises/Exercise2_ReceiptPrinter.java` | Print an aligned shop receipt with `StringBuilder` + `format`, and a one-line summary with `Collectors.joining` | Medium |
| 3 | `exercises/Exercise3_SlowReportBug.java` | A nightly report takes seconds, loses its opening bracket, and never sees it's unchanged. Fix the three builder bugs | Hard |
