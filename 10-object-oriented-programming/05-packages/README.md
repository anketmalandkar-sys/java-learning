# Packages

Source: https://dev.java/learn/language/oop/packages

← Back to [Object Oriented Programming](../README.md)

## 1. What it is

A **package** is a named group of related types (classes, interfaces, enums, records, annotations), like a folder. `java.util`, `java.time` and `java.io` are packages. A package gives its types a **namespace** (`java.util.List` and `java.awt.List` can coexist) and a **visibility boundary** (package-private members are visible only inside it).

## 2. Why it exists

- **Find things**: related types live together (`com.shop.billing.*`).
- **Avoid name clashes**: your `Order` won't collide with a library's `Order`, because their full names differ.
- **Control access**: a type or member without `public` is visible only in its own package, so you can hide helpers from the rest of the program.

## 3. Core concepts

**Declaring a package**: the first statement in the file (only comments may come before it). One per file.
```java
package com.example.shop.billing;

public class Invoice { ... }
```

**One public top-level type per file**, and the file must be named after it (`Invoice.java`). Other top-level types in the same file must be package-private (allowed, but keep them small and closely related).

**Naming conventions**
- All lowercase: `com.example.shop`.
- Start with your **reversed internet domain**: `example.com` → `com.example`, then project/module names: `com.example.shop.billing`.
- `java.` and `javax.` are reserved for the platform.
- If the domain isn't a valid identifier, add an underscore:

| Domain | Package prefix | Why |
|---|---|---|
| `hyphenated-name.example.org` | `org.example.hyphenated_name` | `-` isn't allowed |
| `example.int` | `int_.example` | `int` is a keyword |
| `123name.example.com` | `com.example._123name` | can't start with a digit |

**Using a type from another package**: three ways.
```java
java.time.LocalDate d = java.time.LocalDate.now();   // 1. fully qualified name: fine for a one-off

import java.time.LocalDate;                          // 2. single-type import: the usual choice
import java.time.*;                                  // 3. wildcard: every type in java.time (NOT its subpackages)
```
`java.lang` (`String`, `Math`, `Integer` ...) and **your own package** are imported automatically.

**Packages aren't nested.** `java.util.function` is a separate package from `java.util`. `import java.util.*;` does **not** import `java.util.function.Function`.

**Importing nested classes**: a less common form imports the public nested types of a class.
```java
import java.util.Map.Entry;     // one nested type: write Entry instead of Map.Entry
import java.util.Map.*;         // ALL public nested types of Map (Entry) ... but NOT Map itself
Map<String, Integer> m;         // compile error with only the second import: Map isn't imported
```
The wildcard after a class name works like a package wildcard, one level deep: it brings in the types declared *inside* that class, never the class itself. Import `java.util.Map` separately (or use `java.util.*`).

**Name ambiguity**: if two imported packages have a type with the same simple name, use the fully qualified name.
```java
import java.util.*;
import java.sql.*;
Date d;                       // compile error: java.util.Date or java.sql.Date?
java.sql.Date d;              // fine
```
A **single-type** import beats a wildcard: with `import java.util.Date;` and `import java.sql.*;`, `Date` means `java.util.Date`.

**Static import**: use static members without the class name.
```java
import static java.lang.Math.PI;
import static java.lang.Math.*;          // all static members of Math
double area = PI * r * r;
double big = max(a, b);
```
Use it sparingly (constants, test assertions like `assertEquals`): a reader can't tell where `max` comes from.

**Folders mirror package names**
```
src/
  com/example/shop/billing/Invoice.java     → package com.example.shop.billing;
  com/example/shop/app/ShopApp.java         → package com.example.shop.app;
```
```
javac -d out $(find src -name "*.java")       # compile into out/, which mirrors the packages
java -cp out com.example.shop.app.ShopApp     # run by FULLY QUALIFIED class name
```
`-cp` (the **classpath**) tells `java`/`javac` where to look for `.class` files. You can also set the `CLASSPATH` environment variable, but `-cp` is clearer.

**Unnamed package**: a file without a `package` line (like the other lessons' examples) is in the unnamed package. Fine for small experiments; real projects always use packages, and named packages **can't import** classes from the unnamed one.

## 4. How it works under the hood

- A class's real name is its **fully qualified name**: `com.example.shop.billing.Invoice`. Imports are only a compile-time shortcut; the `.class` file always uses full names, so imports cost nothing at runtime and wildcard imports don't make programs slower.
- The compiler and JVM find `com.example.shop.billing.Invoice` by looking for `com/example/shop/billing/Invoice.class` under each classpath entry (a folder or a JAR).
- Package-private access is checked by package **name**: two classes in `com.example.shop` see each other's package-private members even if they come from different JARs (modules, a later topic, tighten this).

## 5. Common mistakes and gotchas

**`package` line doesn't match the folder**
```
src/com/example/Invoice.java containing "package billing;"   // WRONG: javac/IDE can't find it reliably
src/billing/Invoice.java containing "package billing;"       // RIGHT
```

**Running a packaged class by file path**
```
java -cp out com/example/shop/app/ShopApp       // WRONG
java -cp out com.example.shop.app.ShopApp       // RIGHT: fully qualified name with dots
```

**Expecting wildcards to include subpackages**: `import java.util.*;` then `Function<...>` → compile error; add `import java.util.function.Function;`.

**Expecting `Outer.*` to import `Outer`**: `import graphics.Rectangle.*;` gives you Rectangle's nested classes only; using `Rectangle` itself is still a compile error until you add `import graphics.Rectangle;`.

**Selective wildcards**: `import java.util.A*;` isn't valid Java. A wildcard is always the whole package.

**Ambiguous simple names** with two wildcards (`java.util.*` + `java.sql.*` → `Date`): use a single-type import or the full name.

**Overusing static imports**: `import static ...Utils.*;` everywhere makes `format(...)` impossible to trace.

## 6. When to use / when not to

- Put every real project's code in packages named after a domain you control.
- Group by **feature** (`shop.billing`, `shop.catalog`) rather than by kind (`shop.controllers`, `shop.models`) once a project grows: package-private can then hide a feature's internals.
- Prefer **single-type imports** (your IDE manages them); wildcards are acceptable but can cause ambiguity later.
- Static imports: constants and well-known helpers only.
- Make types and members **package-private by default**; add `public` only for what other packages need.

## 7. Interview angle

1. **What does a package provide?** A namespace (avoids name clashes), organisation, and an access boundary for package-private members.
2. **Does `import java.util.*` import `java.util.function`?** No. Packages aren't hierarchical; each subpackage needs its own import.
3. **Do imports affect runtime performance?** No. They're resolved at compile time; bytecode uses fully qualified names.
4. **What's a static import?** `import static pkg.Class.member;` lets you use static fields/methods without the class name.
5. **Two imported packages both have `Date`; what happens?** Using the simple name is a compile error (ambiguous) unless one is a single-type import. Use the fully qualified name.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_PackageNames.java` | Turn domain names into valid package names following the conventions | Easy |
| 2 | `exercises/com/acme/app/Exercise2_Payroll.java` (+ `com/acme/payroll/`) | Fix visibility across packages: hide internals, keep the app working | Medium |
| 3 | `exercises/Exercise3_ImportResolver.java` | Implement Java's rules for resolving a simple name from a list of imports | Hard |

Examples: `examples/Example1_Basics.java` runs with `java examples/Example1_Basics.java`. Example 2 is a small app split across packages in `examples/com/example/shop/` (`catalog`, `billing`, `app`). Run it with:
```
javac -d out/ex $(find examples/com -name "*.java")
java -cp out/ex com.example.shop.app.ShopApp
```

Run exercises 1 and 3 with `java exercises/ExerciseN_*.java` from this folder. Exercise 2 spans packages, so compile it first:
```
javac -d out/ex2 $(find exercises/com -name "*.java")
java -cp out/ex2 com.acme.app.Exercise2_Payroll
```
In IntelliJ, just press ▶ next to its `main`. (`out/` is already in `.gitignore`.)
