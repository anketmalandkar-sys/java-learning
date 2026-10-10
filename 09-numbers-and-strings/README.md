# Numbers and Strings

Source: https://dev.java/learn/language/constructs/numbers-strings

## 1. Big picture

Primitives (`int`, `double`, `char` ...) are fast, but they aren't objects. Java pairs each one with a **wrapper class** (`Integer`, `Double`, `Character` ...) that adds constants, conversion methods and the ability to live in collections. The compiler converts between the two automatically (**autoboxing**). On top of that sit `Math` for calculations, `printf`/`DecimalFormat` for output, and `String`/`StringBuilder` for text.

This section follows the dev.java "Numbers and Strings" tutorial. Strings were already covered in [05-strings](../05-strings/README.md), so those pages were compared with it and only the missing parts were added there.

```
java.lang.Object
├── Number (abstract)
│   ├── Byte  Short  Integer  Long  Float  Double     <- wrappers for numeric primitives
│   └── BigInteger  BigDecimal  AtomicInteger ...     <- not wrappers, but Numbers too
├── Character  Boolean                                <- wrappers for char, boolean
├── String  StringBuilder
└── Math                                              <- static helpers only
```

## 2. Roadmap

| # | Subtopic | What you'll learn | Source | Status |
|---|----------|-------------------|--------|--------|
| 01 | [Numbers](01-numbers/) | Wrapper classes, converting to/from strings and other bases, `printf` format specifiers, `DecimalFormat`, `Math`, random numbers | [numbers](https://dev.java/learn/language/constructs/numbers-strings/numbers) | 📖 |
| 02 | [Autoboxing and unboxing](02-autoboxing/) | When the compiler boxes/unboxes, the Integer cache and `==`, null unboxing, `List.remove` overloads, boxing cost | [autoboxing](https://dev.java/learn/language/constructs/numbers-strings/autoboxing) | 📖 |
| — | Characters | `Character` helpers and escape sequences, added to [05-strings/04-unicode-and-encoding](../05-strings/04-unicode-and-encoding/README.md#9-extra-character-helpers-and-escape-sequences) | [characters](https://dev.java/learn/language/constructs/numbers-strings/characters) | 📖 |
| — | Strings | String ↔ number conversion, `regionMatches`, `matches`, text-block escapes, added to [05-strings/02-string-api](../05-strings/02-string-api/README.md#9-extra-conversions-region-matching-and-text-block-escapes) | [strings](https://dev.java/learn/language/constructs/numbers-strings/strings) | 📖 |
| — | String builders | Length vs capacity, `setLength`, `ensureCapacity`, the full edit API, added to [05-strings/03-building-strings](../05-strings/03-building-strings/README.md#9-extra-length-capacity-and-editing-in-place) | [string-builders](https://dev.java/learn/language/constructs/numbers-strings/string-builders) | 📖 |

Mark each one ✅ once its exercises have been reviewed.

## 3. How the pieces relate

- **Need a number in a collection, or `null` as "no value"?** A wrapper (`Integer`), via autoboxing (**02**). Otherwise stay with the primitive.
- **Turning text into a number or back?** `Integer.parseInt` / `String.valueOf` (**01**, and the extra in `05-strings/02`).
- **Printing numbers nicely?** `printf`/`String.format` for one-offs, `DecimalFormat` for a reusable pattern (**01**).
- **Checking what kind of character something is?** `Character.isDigit` and friends (extra in `05-strings/04`).
- **Building or editing text in a loop?** `StringBuilder` (`05-strings/03`).
