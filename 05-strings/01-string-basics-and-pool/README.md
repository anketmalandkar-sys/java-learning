[← Strings roadmap](../README.md)

# String basics and the string pool

## 1. What it is

A `String` is an **immutable** sequence of characters. Once created, its content never changes. Methods like `toUpperCase()` or `trim()` return a *new* string and leave the original alone.

Because strings can't change, the JVM can safely share them. It keeps a **string pool** (also called the string constant pool or intern pool): a table of unique strings. Every string **literal** in your code, like `"Pune"`, is stored there once, and every use of that literal points to the same object.

## 2. Why it exists

A typical program creates huge numbers of identical strings: `"OK"`, `"IN"`, `"application/json"`, column names, status codes. Without a pool, every occurrence would be a separate object in memory.

Sharing is only safe because strings are immutable. If one part of the program could change a shared `"OK"` into `"KO"`, every other part using it would break. Immutability gives you more than the pool:
- **Safe hash keys.** A string's hash code can't change, so it can't get "lost" inside a `HashMap`. The hash code is even cached after the first call.
- **Thread safety.** Strings can be shared between threads with no locking.
- **Security.** A file path, URL or class name can't be changed after it has been checked.

## 3. Core concepts

**Literal vs `new String()`.**

```java
String a = "Pune";               // from the pool
String b = "Pune";               // the same pooled object as a
String c = new String("Pune");   // always a brand-new object on the heap

a == b        // true:  same object
a == c        // false: different objects
a.equals(c)   // true:  same characters
```

```
            Heap
 a ──┐     ┌────────── string pool ──────────┐
     ├───► │  "Pune"                          │
 b ──┘     └──────────────────────────────────┘
 c ──────► "Pune"   (a separate copy outside the pool)
```

**`==` vs `equals`.**
- `==` compares **references**: are these the same object?
- `equals` compares **content**: do they hold the same characters?

For strings you almost always mean `equals`.

**Compile-time constants are pooled too.** When every part of an expression is known at compile time, the compiler joins the pieces itself and the result goes in the pool:

```java
String city = "Pune";
"Pu" + "ne" == city               // true:  joined by the compiler into the literal "Pune"

final String prefix = "Pu";       // final + literal = a compile-time constant
prefix + "ne" == city             // true

String part = "Pu";               // not final: only known at runtime
part + "ne" == city               // false: built at runtime, a new object
```

**Strings built at runtime are never pooled automatically.** Anything from `new String`, `substring`, `split`, `+` with a variable, `StringBuilder`, a file, a database or user input is a new object, even if the text matches a literal.

**`intern()`** returns the pool's copy of a string, adding it to the pool first if needed:

```java
String fromInput = new String("Pune");
fromInput == "Pune"              // false
fromInput.intern() == "Pune"     // true: intern() returned the pooled object
```

**Immutability in practice.** Methods return new strings, so you must use the result:

```java
String code = "  abc12 ";
code.trim();                     // returns "abc12" and throws it away; code is unchanged
code = code.trim().toUpperCase();   // "ABC12": reassign to keep it
```

## 4. How it works under the hood

- **Where the pool lives.** Since Java 7 the pool is in the normal **heap** (before that it was in a fixed-size area called PermGen). So pooled strings that nothing refers to any more can be garbage-collected.
- **What the pool is.** It's a hash table inside the JVM (the "StringTable"). Its number of buckets can be tuned with `-XX:StringTableSize`, but you'll rarely need to.
- **When literals enter the pool.** A literal is added the first time the code that uses it runs, and the same object is reused afterwards.
- **What a String holds.** Since Java 9 ("compact strings"), a `String` stores a `byte[]` plus a flag saying whether the text is Latin-1 (1 byte per character) or UTF-16 (2 bytes per character). Before Java 9 it was always a `char[]`. Lesson 04 goes deeper.
- **`new String("Pune")` can create two objects:** the pooled literal `"Pune"` (if it isn't there yet) and the new copy. That's a classic interview question.

## 5. Common mistakes and gotchas

**Comparing with `==`.** It "works" in small tests with literals, then fails with real input:

```java
String input = scanner.nextLine();   // "yes", typed by the user: built at runtime
if (input == "yes") { ... }          // wrong: false, a different object
if (input.equals("yes")) { ... }     // right
```

**`equals` on a variable that might be `null`.**

```java
if (input.equals("yes"))             // wrong: NullPointerException when input is null
if ("yes".equals(input))             // right: a literal is never null
if (Objects.equals(input, other))    // right: when both could be null
```

**Forgetting to reassign.** Strings never change in place:

```java
name.toUpperCase();                  // wrong: the result is thrown away
name = name.toUpperCase();           // right
```

**Interning everything "to save memory".** `intern()` costs a hash table lookup each time, and filling the pool with millions of one-off values (ids, timestamps) wastes CPU and memory. Use it only for a small set of values that repeat a lot, and only after measuring.

**Expecting `+` with a variable to be pooled.**

```java
String status = "OK";
String full = "Status: " + status;   // built at runtime: not in the pool
full == "Status: OK"                 // false
```

## 6. When to use / when not to

**Use:**
- `equals` (or `equalsIgnoreCase`) to compare strings, every time.
- `"literal".equals(variable)` or `Objects.equals(a, b)` when a value might be `null`.
- Literals and constants (`static final String`) freely. They're pooled for you at no cost.

**Avoid:**
- `==` on strings, except when you deliberately want to know whether two references are the same object (almost never in application code).
- `new String("...")`. It just makes an extra copy. Write the literal.
- `intern()` in application code, unless profiling shows many duplicate strings from a small set of values.

## 7. Interview angle

1. **What's the difference between `==` and `equals` for strings?** `==` checks whether two references point to the same object. `equals` checks whether the characters are the same. Use `equals` for comparison.
2. **How many objects does `String s = new String("hello");` create?** Up to two: the `"hello"` literal in the string pool (if it isn't already there) and a new `String` on the heap that `s` refers to.
3. **Why are strings immutable in Java?** So they can be shared safely (the string pool), used as reliable hash keys (cached hash codes), shared between threads without locks, and trusted after security checks (file paths, URLs, class names).
4. **Where is the string pool, and can pooled strings be garbage-collected?** In the heap, since Java 7 (before that, PermGen). Yes, pooled strings that are no longer referenced can be collected.
5. **Is `"a" + "b" == "ab"` true? What about `x + "b" == "ab"` where `x = "a"`?** The first is true: both parts are literals, so the compiler joins them into the pooled `"ab"`. The second is false, unless `x` is `final`: `x + "b"` is built at runtime as a new object.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_PredictThePool.java` | Predict `true`/`false` for 10 `==` and `equals` comparisons, then run to see which you got right | Easy |
| 2 | `exercises/Exercise2_CommandMatcher.java` | Write null-safe, input-proof comparison helpers for commands typed by users | Medium |
| 3 | `exercises/Exercise3_CouponBug.java` | A coupon service passes its tests but rejects every real customer. Find the three string bugs | Hard |
