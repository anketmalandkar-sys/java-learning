[← Strings roadmap](../README.md)

# The String API

## 1. What it is

`String` has around 70 methods for comparing, searching, slicing, cleaning and formatting text. You'll use about 20 of them every week. This lesson covers those 20, grouped by job, along with the traps in each: `split` takes a **regex**, `substring`'s end index is **exclusive**, and `toLowerCase()` depends on the **locale**.

Because strings are immutable (lesson 01), every method that "changes" a string returns a new one.

## 2. Why it exists

Almost all real input is text: CSV lines, form fields, log lines, URLs, file names. Without these methods you'd loop over characters by hand to find a comma, cut out a field, or drop spaces. The API turns each of those jobs into one readable call:

```java
// By hand: find the first comma, copy the characters before it
int comma = -1;
for (int i = 0; i < line.length(); i++) {
    if (line.charAt(i) == ',') { comma = i; break; }
}
String id = line.substring(0, comma);

// With the API
String id = line.split(",")[0];
```

## 3. Core concepts

**Comparing.**

```java
"Pune".equals("pune")              // false: case-sensitive
"Pune".equalsIgnoreCase("pune")    // true
"apple".compareTo("banana")        // negative: "apple" sorts first
"Zebra".compareTo("apple")         // negative! Upper case sorts before lower case
"Zebra".compareToIgnoreCase("apple")   // positive
names.sort(String.CASE_INSENSITIVE_ORDER);   // a ready-made Comparator
```

**Searching.** Positions start at 0. `indexOf` returns `-1` when nothing is found:

```java
String path = "/home/ravi/report.final.pdf";
path.indexOf('/')               // 0
path.indexOf('/', 1)            // 5: search from index 1
path.lastIndexOf('.')           // 23: the extension starts after this
path.contains("ravi")           // true
path.startsWith("/home")        // true
path.endsWith(".pdf")           // true
path.charAt(1)                  // 'h'
```

**Slicing.** `substring(begin, end)` includes `begin` and **excludes** `end`:

```java
"report.pdf".substring(0, 6)    // "report"   (indexes 0..5)
"report.pdf".substring(7)       // "pdf"      (from 7 to the end)
```

`split` cuts a string into an array. Its argument is a **regular expression**:

```java
"a,b,c".split(",")              // [a, b, c]
"a, b ,c".split("\\s*,\\s*")    // [a, b, c]: also eats spaces around commas
"1.2.3".split("\\.")            // [1, 2, 3]: "." alone means "any character" in regex
"a,b,,".split(",")              // [a, b]: trailing empty fields are DROPPED
"a,b,,".split(",", -1)          // [a, b, , ]: a limit of -1 keeps them
```

**Cleaning.**

```java
"  hi  ".trim()                 // "hi": removes spaces and control chars (<= U+0020)
"  hi  ".strip()                // "hi": removes all Unicode whitespace (Java 11+). Prefer strip().
"  hi  ".stripLeading()         // "hi  "
"".isEmpty()                    // true
"   ".isEmpty()                 // false
"   ".isBlank()                 // true (Java 11+): empty or only whitespace
"a-b-c".replace("-", "+")       // "a+b+c": plain text, every occurrence
"a1b22c".replaceAll("[0-9]+", "#")   // "a#b#c": regex
"TITLE".toLowerCase(Locale.ROOT)     // "title": locale-independent (see section 5)
```

**Joining and repeating.**

```java
String.join(", ", List.of("Pune", "Goa"))   // "Pune, Goa"
"-".repeat(10)                               // "----------" (Java 11+)
String.valueOf(42)                           // "42": works for any type, and "null" for null
"line1\nline2".lines().count()              // 2 (Java 11+)
```

**Formatting.**

```java
String.format("%-8s|%6.2f|%03d", "Pens", 4.5, 7)   // "Pens    |  4.50|007"
"%s owes %,d".formatted("Ravi", 1250000)          // "Ravi owes 1,250,000" (Java 15+)
```

| Spec | Meaning |
|------|---------|
| `%s` | any value, via `toString()` |
| `%d` | integer (`%,d` adds thousands separators) |
| `%.2f` | decimal with 2 places |
| `%8s` / `%-8s` | pad to 8 characters, right / left aligned |
| `%05d` | pad with zeros to 5 digits |
| `%n` | the platform's line break |

**Text blocks (Java 15+).** Multi-line strings without `\n` and `+`. The common leading indentation is removed:

```java
String json = """
        {
          "id": "A101",
          "total": 2499.5
        }
        """;
```

## 4. How it works under the hood

- **`split`, `replaceAll` and `matches` compile a regex on every call.** For a single plain character like `","`, `split` takes a fast path that skips the regex engine. For anything else in a hot loop, compile the pattern once: `private static final Pattern COMMA_SPACES = Pattern.compile("\\s*,\\s*");`, then `COMMA_SPACES.split(line)`.
- **`replace` vs `replaceAll`.** `replace(CharSequence, CharSequence)` replaces plain text, every occurrence, no regex. `replaceAll` and `replaceFirst` use regex, and their replacement text treats `$` and `\` specially.
- **`substring` copies.** Since Java 7u6 a substring gets its own copy of the characters. Before that it shared the parent's array, which could keep a huge string alive. Today it's safe, but cutting many substrings out of a big string costs memory.
- **Case conversion depends on the locale.** `toLowerCase()` with no argument uses the JVM's default locale. In Turkish, upper-case `I` becomes a dotless `ı`, so `"TITLE".toLowerCase()` is not `"title"` on a Turkish server.

## 5. Common mistakes and gotchas

**Splitting on a regex special character.** The characters `. | $ ^ * + ? ( ) [ ] { } \` all mean something in regex:

```java
"1.2.3".split(".")        // wrong: [] (every character is a separator, all fields empty, all dropped)
"1.2.3".split("\\.")      // right
"a|b".split("|")          // wrong: [a, |, b]
"a|b".split("\\|")        // right   (or Pattern.quote("|"))
```

**`replaceAll` when you meant plain text.**

```java
"v1.2".replaceAll(".", "-")   // wrong: "----"
"v1.2".replace(".", "-")      // right: "v1-2"
```

**Off-by-one with `substring`.**

```java
String ext = "report.pdf".substring(7, 9);    // wrong: "pd"
String ext = "report.pdf".substring(7, 10);   // right: end is exclusive (or just substring(7))
```

**Losing empty trailing fields.**

```java
"A101,Ravi,,".split(",").length        // wrong: 2, so fields[3] throws
"A101,Ravi,,".split(",", -1).length    // right: 4
```

**Case conversion without a locale** for identifiers, keys, file names and protocols:

```java
key.toLowerCase()                 // wrong: depends on where the server runs
key.toLowerCase(Locale.ROOT)      // right
```

Use the default locale only for text you show to a human in their own language.

**Using `isEmpty` to reject blank input.**

```java
if (name.isEmpty())   // wrong: "   " gets through
if (name.isBlank())   // right
```

## 6. When to use / when not to

- **Use `strip()`, `isBlank()`, `repeat()` and `lines()`** (Java 11+) instead of older workarounds.
- **Use `String.format` / `formatted`** for aligned tables and fixed decimals. For simple joins, `+` reads better.
- **Use text blocks** for embedded JSON, SQL and HTML.
- **Don't parse real CSV with `split(",")`.** Quoted fields like `"Kumar, Ravi"` break it. Use a CSV library for real files.
- **Don't use regex methods for plain text.** `replace` beats `replaceAll(Pattern.quote(...))`.
- **Don't call `split` / `replaceAll` with a complex regex in a hot loop.** Precompile a `Pattern`.

## 7. Interview angle

1. **What's the difference between `trim()` and `strip()`?** `trim()` removes characters up to U+0020 (space and control characters). `strip()` (Java 11) removes all Unicode whitespace, such as the non-breaking or ideographic space. Prefer `strip()`.
2. **Why does `"1.2.3".split(".")` return an empty array?** `split` takes a regex, and `.` matches any character. Every character becomes a separator, all the fields are empty, and trailing empty strings are dropped. Use `split("\\.")`.
3. **What's the difference between `replace` and `replaceAll`?** Both replace every occurrence. `replace` works on plain text. `replaceAll` treats its first argument as a regex and its replacement's `$` and `\` as special.
4. **What's the difference between `isEmpty()` and `isBlank()`?** `isEmpty()` is true only for `""`. `isBlank()` (Java 11) is also true for whitespace-only strings like `"   "`.
5. **Why pass `Locale.ROOT` to `toLowerCase`?** The no-argument version uses the default locale. Under Turkish, `"I".toLowerCase()` is the dotless `"ı"`, which breaks keys, enum names and protocol strings.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_TextHelpers.java` | Initials, card masking, palindromes and word counting with `split`, `substring`, `indexOf` and `repeat` | Easy |
| 2 | `exercises/Exercise2_OrderLineParser.java` | Parse messy pipe-separated order lines (spaces, blank lines, empty fields) and print an aligned table with `format` | Medium |
| 3 | `exercises/Exercise3_SlugBug.java` | A release-notes tool builds broken URL slugs on the production server. Find the regex and locale bugs | Hard |
