# Strings

## 1. Big picture

`String` is the most-used class in Java: names, ids, JSON, SQL, log lines, user input. It looks simple, but it has rules that catch people out. Strings are **immutable**. Identical literals share one object through the **string pool**. `==` and `equals` give different answers. Building strings in a loop can be slow.

This topic is split into lessons you take one at a time.

```
Strings
├── basics & the string pool   immutability, literals vs new String(), == vs equals, intern()
├── the String API             substring, indexOf, split, join, strip, format, text blocks
├── building strings           + vs StringBuilder vs StringBuffer, String.join, Collectors.joining
└── characters & encoding      char vs code point, emoji, UTF-8 bytes, compact strings
```

## 2. Roadmap

| # | Subtopic | What you'll learn | Status |
|---|----------|-------------------|--------|
| 01 | [String basics and the string pool](01-string-basics-and-pool/) | Immutability and why it matters, literals vs `new String()`, the string pool, `==` vs `equals`, compile-time constants, `intern()`, where the pool lives | 📖 |
| 02 | [The String API](02-string-api/) | Comparing (`equalsIgnoreCase`, `compareTo`), searching (`indexOf`, `contains`), slicing (`substring`, `split`), cleaning (`strip`, `isBlank`), `join`, `repeat`, `String.format` / `formatted`, text blocks (Java 15+) | 📖 |
| 03 | [Building strings efficiently](03-building-strings/) | Why `+=` in a loop is slow, `StringBuilder` (and the older `StringBuffer`), how the compiler handles `+` (Java 9+), `String.join` and `Collectors.joining` | 📖 |
| 04 | [Characters, Unicode and encoding](04-unicode-and-encoding/) | `char` vs code points, why `length()` lies for emoji, `chars()` / `codePoints()`, `getBytes(UTF_8)` and charsets, compact strings (Latin-1 vs UTF-16) | 📖 |

All lessons are written. Work through them in order, and mark each one ✅ once its exercises have been reviewed.

Extras from the dev.java "Numbers and Strings" tutorial were added to lessons 02, 03 and 04: each has a new section 9 in its README, an `Example3_*.java` and an `Exercise4_*.java`. See also [09-numbers-and-strings](../09-numbers-and-strings/README.md).

## 3. How the pieces relate

- **Comparing two strings?** Learn **01** first: `equals`, never `==`.
- **Cutting, searching or cleaning up text** (user input, CSV lines)? That's the API, **02**.
- **Building one big string from many pieces** (a report, a CSV file, a log line in a loop)? **03**: use a `StringBuilder`.
- **Names with accents, emoji, or bytes going to a file or network?** That's **04**.
