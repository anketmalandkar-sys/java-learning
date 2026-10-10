[← Strings roadmap](../README.md)

# Characters, Unicode and encoding

## 1. What it is

Text is more than letters A to Z. Names have accents (José), currencies have symbols (₹), and chat apps have emoji (😀). **Unicode** gives every character a number called a **code point**, written `U+20B9` (₹) or `U+1F600` (😀). There are over a million possible code points.

Java's `char` is only 16 bits, so it holds 65,536 values. A Java `String` is a sequence of **UTF-16 code units** (`char`s):
- Most characters (including ₹ and é) fit in one `char`.
- Code points above `U+FFFF`, like most emoji, take **two** `char`s, called a **surrogate pair**.

**Encoding** is how characters become bytes for files, databases and networks. **UTF-8** is the standard. It uses 1 to 4 bytes per character.

## 2. Why it exists

If you assume "1 char = 1 character = 1 byte", real data breaks your code:

```java
String name = "Ravi😀";
name.length()                    // 6, not 5: the emoji is two chars
name.substring(0, 5)             // "Ravi" + half an emoji: a broken character
name.getBytes(UTF_8).length      // 8 bytes: what actually goes into a VARCHAR(5) column
```

Typical bugs: username length limits that reject emoji names, truncation that leaves `?` at the end, "José" not matching "José" (two different byte sequences), and `JosÃ©` (mojibake) after a file is read with the wrong charset.

## 3. Core concepts

**Three ways to count.**

| Text | `length()` (chars) | code points | what a user sees | UTF-8 bytes |
|------|-------------------|-------------|------------------|-------------|
| `"A"` | 1 | 1 | 1 | 1 |
| `"é"` (U+00E9) | 1 | 1 | 1 | 2 |
| `"₹"` (U+20B9) | 1 | 1 | 1 | 3 |
| `"😀"` (U+1F600) | 2 | 1 | 1 | 4 |
| `"🇮🇳"` (flag: 2 regional indicators) | 4 | 2 | 1 | 8 |
| `"é"` as `e` + U+0301 (combining accent) | 2 | 2 | 1 | 3 |

```java
s.length()                              // UTF-16 chars
s.codePointCount(0, s.length())         // code points
s.codePoints().count()                  // same, as a stream
Pattern.compile("\\X").matcher(s).results().count()   // grapheme clusters: what a user sees (Java 9+)
s.getBytes(StandardCharsets.UTF_8).length            // bytes in UTF-8
```

**Working with code points instead of chars.**

```java
int cp = s.codePointAt(0);              // the whole code point, even for an emoji
Character.isLetter(cp);                 // use the int versions of Character methods
Character.toString(cp);                 // code point -> String (Java 11+)
Character.getName(0x1F600);             // "GRINNING FACE"
s.codePoints().forEach(...);            // iterate by code point, not by char
s.offsetByCodePoints(0, 3);             // the char index of the 4th code point: for safe substring
```

**Surrogates.** A `char` from an emoji on its own is meaningless. `Character.isHighSurrogate(c)` and `isLowSurrogate(c)` tell you that you're in the middle of a pair.

**Normalization.** "é" can be stored two ways: one code point (U+00E9, "NFC") or `e` + a combining accent (U+0065 U+0301, "NFD"). They look identical but aren't `equals`. Normalize before comparing or storing:

```java
Normalizer.normalize(input, Normalizer.Form.NFC)
```

macOS file names and some keyboards produce NFD. Most other sources use NFC.

**Encoding and decoding: always name the charset.**

```java
byte[] bytes = text.getBytes(StandardCharsets.UTF_8);       // String -> bytes
String back  = new String(bytes, StandardCharsets.UTF_8);   // bytes -> String
Files.readString(path, StandardCharsets.UTF_8);
```

Decoding with the wrong charset doesn't throw. It silently produces garbage ("mojibake"). The UTF-8 bytes of `é` (`C3 A9`) read as ISO-8859-1 become `Ã©`.

## 4. How it works under the hood

- **Compact strings (Java 9+).** Internally a `String` is a `byte[]` plus a flag. If every character fits in Latin-1 (U+0000 to U+00FF), it stores 1 byte per char. Otherwise it uses UTF-16, at 2 bytes per char. Most English text uses half the memory it did before Java 9. One emoji switches the whole string to 2 bytes per char. This is invisible to your code: `length()` and `charAt()` behave the same.
- **The default charset is UTF-8 since Java 18** (JEP 400). Before that it depended on the operating system, e.g. windows-1252 on Windows. That's why code which "worked on my machine" broke on servers. Passing `StandardCharsets.UTF_8` explicitly makes code behave the same everywhere.
- **The console is separate.** Printing an emoji to a Windows terminal may show `?` even when the `String` is correct, because the terminal's encoding is a separate setting. IntelliJ's console uses UTF-8. The examples print `U+XXXX` codes so the output is reliable everywhere.
- **`StringBuilder.reverse()` is surrogate-aware.** It keeps surrogate pairs in order, so reversing `"a😀b"` gives `"b😀a"`.

## 5. Common mistakes and gotchas

**Using `length()` as "number of characters".**

```java
if (username.length() > 10)                                  // wrong: an emoji counts as 2
if (username.codePointCount(0, username.length()) > 10)      // right: counts code points
```

**Cutting a string at a char index.**

```java
s.substring(0, 5)                                // wrong: can split an emoji in half
s.substring(0, s.offsetByCodePoints(0, 5))       // right: first 5 code points
```

**Assuming chars = bytes.**

```java
if (name.length() <= 50) db.save(name);          // wrong: a VARCHAR(50) in bytes can still overflow
if (name.getBytes(UTF_8).length <= 50) ...       // right
```

**Relying on the default charset.**

```java
new String(bytes)                                // wrong: depends on the JVM's default
new String(bytes, StandardCharsets.UTF_8)        // right
text.getBytes()                                  // wrong
text.getBytes(StandardCharsets.UTF_8)            // right
```

**Comparing user text without normalizing.**

```java
nfcName.equals(nfdName)                          // wrong: false, though they look identical
normalize(nfcName, NFC).equals(normalize(nfdName, NFC))   // right
```

**Looping over `char`s for character checks.**

```java
for (char c : s.toCharArray()) Character.isLetter(c)   // wrong for emoji and rare scripts
s.codePoints().allMatch(Character::isLetter)           // right
```

## 6. When to use / when not to

- **`length()` and `charAt()` are fine** for ASCII-only data: ids, codes, hex, protocol keywords.
- **Use code points** for user-visible text limits, truncation and per-character checks.
- **Use grapheme clusters (`\X`)** when you must match what a person sees (a flag or a family emoji counts as 1), e.g. a "characters left" counter. For full accuracy, use a library like ICU4J.
- **Use UTF-8 bytes** when the limit is storage or network size.
- **Always pass a charset** at every boundary: files, sockets, HTTP bodies, `getBytes`.
- **Normalize to NFC** when storing or comparing names, search terms and usernames.

## 7. Interview angle

1. **What does `String.length()` return?** The number of UTF-16 code units (`char`s), not characters. An emoji outside the Basic Multilingual Plane takes two `char`s (a surrogate pair), so `"😀".length()` is 2.
2. **What is a surrogate pair?** Two `char`s (a high surrogate U+D800–U+DBFF, then a low surrogate U+DC00–U+DFFF) that together encode one code point above U+FFFF.
3. **How many bytes is a character in UTF-8?** 1 to 4: ASCII is 1, é is 2, ₹ is 3, emoji are 4. UTF-16 uses 2 or 4.
4. **What are compact strings?** A Java 9 optimisation. A `String` stores Latin-1 text with 1 byte per char instead of 2, and switches to UTF-16 only if any character needs it. It saves memory and is invisible to code.
5. **Why can two strings that look identical not be `equals`?** They may use different Unicode forms, e.g. precomposed `é` (NFC) vs `e` + a combining accent (NFD). Normalize both with `java.text.Normalizer` first.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_CountingCharacters.java` | Count code points and UTF-8 bytes, detect ASCII, and describe a string as `U+XXXX` codes | Easy |
| 2 | `exercises/Exercise2_SafeTruncate.java` | Truncate text to N code points or N bytes without ever splitting a character, and get an emoji-safe initial | Medium |
| 3 | `exercises/Exercise3_UsernameBug.java` | A sign-up service rejects valid emoji names, shows `JosÃ©`, and says "José" isn't "José". Fix the three Unicode bugs | Hard |

## 9. Extra: Character helpers and escape sequences

Source: https://dev.java/learn/language/constructs/numbers-strings/characters

Added from the dev.java "Characters" page, which covers two things this lesson didn't.

**`Character` is the wrapper for `char`.** It's immutable, and autoboxing converts between the two (`Character c = 'a';`). Its static helpers classify and convert single characters:

```java
Character.isLetter('é')        // true: works for every alphabet, not just a-z
Character.isDigit('7')         // true (also true for other scripts' digits, e.g. '٧')
Character.isLetterOrDigit('_') // false
Character.isWhitespace('\t')   // true: space, tab, newline, ...
Character.isUpperCase('Q')     // true
Character.isLowerCase('q')     // true
Character.toUpperCase('q')     // 'Q'
Character.toLowerCase('Q')     // 'q'
Character.toString('x')        // "x"
Character.getNumericValue('7') // 7: the digit's value, not its code (which is 55)
```

Each helper has a `char` version and an `int` (code point) version. Only the `int` versions handle supplementary characters (emoji and other code points above `U+FFFF`, see section 3), so prefer `Character.isLetter(text.codePointAt(i))` over `isLetter(text.charAt(i))` for text that might contain them.

**Gotcha: `'7' - '0'` vs `(int) '7'`.** `(int) '7'` is the character code 55. To get the digit value, use `Character.getNumericValue('7')` or `'7' - '0'` (only safe for ASCII digits).

**Escape sequences.** A backslash gives the next character a special meaning in `char` and `String` literals:

| Escape | Meaning |
|---|---|
| `\t` | tab |
| `\n` | newline |
| `\r` | carriage return (Windows line endings are `\r\n`) |
| `\b` | backspace |
| `\f` | form feed |
| `\s` | a space (Java 15+); mainly used to keep trailing spaces in text blocks |
| `\'` | single quote, needed in a `char` literal: `'\''` |
| `\"` | double quote, needed inside a `String`: `"say \"hi\""` |
| `\\` | a backslash itself: `"C:\\temp"` |
| `\` at the end of a line | inside a text block only: joins the next line, no newline inserted |

**Extra exercise**

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 4 | `exercises/Exercise4_PasswordRules.java` | Check password rules with `Character` helpers, count character kinds, and build strings that need escapes | Medium |

Example: `examples/Example3_CharacterHelpers.java`.
