# Throwing Exceptions

Source: https://dev.java/learn/language/annotations-exceptions/exceptions/throwing

← Back to [Annotations and Exceptions](../README.md)

## 1. What it is

The throwing side of exceptions:
- **`throw`** creates the problem report: `throw new IllegalArgumentException("amount must be positive")`.
- **`throws`** in a method signature declares which checked exceptions can come out of it: `void load() throws IOException`.
- **Chained exceptions** wrap a low-level cause inside a higher-level exception, so nothing is lost.
- **Custom exception classes** give your own failures a precise type that callers can catch.

## 2. Why it exists

A method that detects a problem it can't fix itself needs a way to say so that the caller can't ignore by accident, and that carries enough detail to act on: what failed, why, and where. `throw` delivers that report; `throws` makes the contract visible; chaining keeps the original cause; a good exception type lets callers react to exactly the failures they care about.

## 3. Core concepts

**`throws`: declare checked exceptions** after the parameter list, comma-separated. Unchecked exceptions may be listed too (documentation), but don't have to be.
```java
public Order load(String id) throws IOException, OrderNotFoundException { ... }
```
Letting the caller handle an exception is often the right choice, especially in reusable code: you can't know whether *this* caller wants to retry, use a default, or give up.

**`throw`: throw one `Throwable` object.** Anything that isn't a `Throwable` can't be thrown.
```java
if (amount <= 0) {
    throw new IllegalArgumentException("amount must be positive: " + amount);
}
```

**Reuse the standard exceptions** before inventing your own:

| Situation | Throw |
|---|---|
| An argument is invalid | `IllegalArgumentException` |
| An argument is `null` where it mustn't be | `NullPointerException`, e.g. `Objects.requireNonNull(name, "name")` |
| The object is in the wrong state for this call (closed, not started) | `IllegalStateException` |
| An index is out of range | `IndexOutOfBoundsException`, e.g. `Objects.checkIndex(i, size)` |
| The operation isn't supported (read-only view) | `UnsupportedOperationException` |

**The hierarchy, again**: `Throwable` has two direct subclasses. **`Error`** is for serious JVM problems (dynamic linking failures, out of memory): application code doesn't throw it. **`Exception`** is what programs throw and catch. **`RuntimeException`** is for API misuse; most code shouldn't subclass it just to avoid `throws`.

**Chained exceptions.** When you catch a low-level exception and throw a higher-level one, pass the original as the **cause**:
```java
try {
    return parse(Files.readString(path));
} catch (IOException e) {
    throw new ConfigException("can't read config " + path, e);   // e becomes the cause
}
```
`Throwable` has constructors taking a cause (`(String message, Throwable cause)`), `initCause(cause)` for exceptions that lack such a constructor, and `getCause()` to read it back. The stack trace prints the whole chain: `Caused by: java.io.FileNotFoundException: ...`.

**Stack traces.** `printStackTrace()` prints the trace; `getStackTrace()` returns it as an array of `StackTraceElement` (class, method, file, line) that you can log or format yourself.

**Logging instead of `System.err`.** Real programs send exception details to a logger (`java.util.logging`, or SLF4J/Log4j in most projects). Pass a `Supplier` so an expensive message is only built if that level is actually enabled:
```java
logger.log(Level.WARNING, e, () -> "order " + id + " failed after " + attempts + " attempts");
```

**Custom exception classes.** Write your own when:
- no standard exception fits, or callers need to tell **your** failures apart from others
- you throw **several related** exceptions: give them a **common superclass**, so callers can catch the whole family with one handler
- your package should be self-contained

```java
public class InventoryException extends Exception {                 // the family, checked
    public InventoryException(String message) { super(message); }
    public InventoryException(String message, Throwable cause) { super(message, cause); }
}
public class OutOfStockException extends InventoryException { ... } // specific members
public class UnknownSkuException extends InventoryException { ... }
```
Conventions: extend **`Exception`** (checked) for recoverable problems, `RuntimeException` only for misuse; never extend `Error`; name the class `...Exception`; offer a constructor that takes a cause.

## 4. How it works under the hood

- The stack trace is captured in the `Throwable` **constructor** (`fillInStackTrace()`), not at the `throw`. Creating an exception is relatively expensive because of that; don't use exceptions for normal control flow.
- `throw` evaluates its expression; if it's `null`, you get a `NullPointerException` instead.
- `throws` clauses are checked by the compiler only. An overriding method may declare **fewer or narrower** checked exceptions than the method it overrides, never broader ones (see `00-oops`).
- The cause chain is just a linked list of `Throwable`s via `getCause()`. Walking it to the end gives the **root cause**.

## 5. Common mistakes and gotchas

**Losing the cause**
```java
catch (IOException e) {
    throw new ConfigException("bad config: " + e.getMessage());     // WRONG: the original stack trace is gone
}
catch (IOException e) {
    throw new ConfigException("bad config", e);                     // RIGHT: chained
}
```

**Throwing `Exception` or `RuntimeException` itself**: callers can't catch anything specific. Use a precise type.

**Exceptions without a message**, or a vague one: include the value that was wrong (`"amount must be positive: -5"`).

**Using exceptions for normal flow** (e.g. throwing to break out of a loop): slow and confusing.

**Logging *and* rethrowing** the same exception at every layer: the same stack trace appears many times. Either handle (and log) it, or rethrow it, not both.

**A custom exception per tiny case** (`NegativeAmountException`, `ZeroAmountException`, ...): one `IllegalArgumentException` with a clear message is usually better.

## 6. When to use / when not to

- Throw as soon as you detect invalid input ("fail fast"), with a message that names the bad value.
- Declare `throws` for checked exceptions a caller can act on; don't catch them just to silence the compiler.
- Translate low-level exceptions into your layer's vocabulary (`IOException` → `ConfigException`), **always** chaining the cause.
- Create custom exceptions for meaningful domain failures, grouped under one superclass per area.

## 7. Interview angle

1. **`throw` vs `throws`?** `throw` is a statement that throws one exception object. `throws` is part of a method signature that declares which (checked) exceptions the method can throw.
2. **What is a chained exception?** An exception that records the exception that caused it, via the `(message, cause)` constructor or `initCause`; read with `getCause()`. It keeps low-level details when you rethrow at a higher level.
3. **When would you write a custom exception?** When callers need to distinguish your failures, when you have several related failures (give them a common superclass), or when no standard exception fits.
4. **Should a custom exception be checked or unchecked?** Checked if callers can reasonably recover; unchecked if it signals misuse of your API.
5. **Why shouldn't you subclass `Error`?** `Error` is reserved for serious JVM-level failures that applications shouldn't try to handle.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_FailFast.java` | Validate inputs with the right standard exceptions and clear messages | Easy |
| 2 | `exercises/Exercise2_ListExceptions.java` | Design a family of custom exceptions for a small list class, and catch them as a group | Medium |
| 3 | `exercises/Exercise3_ChainAndTrace.java` | Keep the cause when translating exceptions, find the root cause, read the stack trace, and log lazily | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
