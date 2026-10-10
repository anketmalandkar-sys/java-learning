# Annotations and Exceptions

Source: https://dev.java/learn/language/annotations-exceptions

## 1. Big picture

Two features that sit *around* your normal code rather than inside it:

- **Annotations** (`@Override`, `@Deprecated`, your own `@Audited`) attach **metadata** to code. The compiler, tools and frameworks read it; the code itself doesn't change behaviour because of it unless something reads the annotation.
- **Exceptions** are Java's way to report that something went wrong, **separately from the normal flow**: a method throws an object describing the problem, and the runtime walks back up the call stack until some code catches it.

```
Throwable
├── Error                      serious JVM problems (OutOfMemoryError): don't catch
└── Exception                  checked: the compiler makes you catch or declare it (IOException)
    └── RuntimeException       unchecked: programming errors (NullPointerException, IllegalArgumentException)
```

## 2. Roadmap

| # | Subtopic | What you'll learn | Source | Status |
|---|----------|-------------------|--------|--------|
| 01 | [Annotations](01-annotations/) | Syntax, where annotations go, writing your own, `@Deprecated`/`@Override`/`@SuppressWarnings`/`@SafeVarargs`/`@FunctionalInterface`, meta-annotations (`@Retention`, `@Target`, `@Inherited`, `@Repeatable`), type annotations, reading them with reflection | [annotations](https://dev.java/learn/language/annotations-exceptions/annotations) | 📖 |
| 02 | [What is an exception](02-what-is-an-exception/) | The call stack and the handler search, catch-or-specify, checked vs unchecked vs errors, when to use which, the three advantages of exceptions | [what-is-an-exception](https://dev.java/learn/language/annotations-exceptions/exceptions/what-is-an-exception), [unchecked-exception-controversy](https://dev.java/learn/language/annotations-exceptions/exceptions/unchecked-exception-controversy) | 📖 |
| 03 | [Catching and handling](03-catching-handling/) | `try`/`catch` order, multi-catch, `finally`, try-with-resources, suppressed exceptions, `AutoCloseable` vs `Closeable` | [catching-handling](https://dev.java/learn/language/annotations-exceptions/exceptions/catching-handling) | 📖 |
| 04 | [Throwing exceptions](04-throwing-exceptions/) | `throw` vs `throws`, choosing an exception type, chained exceptions, stack traces, logging, custom exception classes | [throwing](https://dev.java/learn/language/annotations-exceptions/exceptions/throwing) | 📖 |

The "Unchecked Exceptions — The Controversy" page is folded into lesson 02, where the checked/unchecked choice is introduced. The [Exceptions index](https://dev.java/learn/language/annotations-exceptions/exceptions) only lists the pages above.

Mark each lesson ✅ once its exercises have been reviewed.

## 3. How the pieces relate

- **Telling the compiler or a tool something about your code?** An annotation (**01**).
- **Something can go wrong that the caller should handle?** Throw a checked exception and declare it (**02**, **04**).
- **The caller made a mistake (null, bad index, bad argument)?** Throw an unchecked exception (**02**, **04**).
- **Calling code that throws, or using a file/connection?** `try`/`catch`/`finally` and try-with-resources (**03**).

Related lessons elsewhere: `@Override` and overriding rules in [00-oops/05-inheritance](../00-oops/05-inheritance/README.md); `@SuppressWarnings` and `@SafeVarargs` in [04-generics/05-type-erasure](../04-generics/05-type-erasure/README.md); `@FunctionalInterface` in [03-lambda-expressions](../03-lambda-expressions/README.md); `AutoCloseable` replacing `finalize` in [00-oops/06-object-superclass](../00-oops/06-object-superclass/README.md).
