# Catching and Handling Exceptions

Source: https://dev.java/learn/language/annotations-exceptions/exceptions/catching-handling

← Back to [Annotations and Exceptions](../README.md) · A first look at try-with-resources is in [00-oops/06-object-superclass](../../00-oops/06-object-superclass/README.md).

## 1. What it is

The handler side of exceptions. Three blocks work together:

- **`try`**: the code that might throw.
- **`catch (SomeException e)`**: runs if that type (or a subclass) is thrown in the `try`. There can be several.
- **`finally`**: runs **whenever the `try` block exits**, whether it finished normally, threw, or returned.

And **try-with-resources** (Java 7+), which opens resources (files, connections, streams) and closes them automatically.

## 2. Why it exists

Without structured handling, cleanup is the first thing to break. If a method opens a file, reads it, then throws halfway, who closes the file? Before Java 7 the answer was a `finally` block with its own nested `try`/`catch` around `close()`: ten lines of boilerplate per resource, often written wrong. Try-with-resources makes "always close it" a one-liner, and keeps the **original** error when closing fails too.

## 3. Core concepts

**`try` / `catch`**: the first matching `catch` wins; a `catch` block must directly follow the `try` (or the previous `catch`).
```java
try {
    int qty = Integer.parseInt(text);
    items.get(qty);
} catch (NumberFormatException e) {            // more specific first
    System.out.println("not a number: " + text);
} catch (IndexOutOfBoundsException e) {
    System.out.println("no item " + text);
}
```

**Order: subclass before superclass.** A `catch` for a superclass placed first would make the subclass `catch` unreachable, so it's a **compile error**:
```java
catch (IOException e) { ... }
catch (FileNotFoundException e) { ... }        // compile error: already caught by IOException
```

**Multi-catch (Java 7+)**: one handler for several **unrelated** types.
```java
catch (NumberFormatException | ArithmeticException e) {
    System.out.println("bad input: " + e.getMessage());
}
```
The types must be **disjoint** (neither a subclass of the other), and `e` is implicitly **final**.

**`finally`**: always runs when the `try` exits, even on `return`, `break` or `continue`. The place for cleanup that must happen.
```java
lock.lock();
try {
    return balance;              // finally still runs before the method actually returns
} finally {
    lock.unlock();
}
```
It doesn't run if the JVM itself stops first (e.g. `System.exit()` in the `try`).

**Try-with-resources**: resources declared in the parentheses are closed automatically when the block ends, **in reverse order of creation**:
```java
try (var in = Files.newBufferedReader(source);
     var out = Files.newBufferedWriter(target)) {
    out.write(in.readLine());
}                                 // out.close() first, then in.close(), even if write throws
```
- Any `AutoCloseable` can be a resource. `Closeable` (from `java.io`) extends it.
- Resources are implicitly `final`. Since Java 9 an existing **effectively final** variable can be used directly: `try (reader) { ... }`.
- A `catch` or `finally` on a try-with-resources runs **after** the resources are closed.

**Suppressed exceptions.** If the `try` body throws **and** `close()` throws too, the body's exception is the one that propagates. The close failure is attached to it as **suppressed**:
```java
catch (IOException e) {
    System.out.println(e.getMessage());                 // the original problem
    for (Throwable s : e.getSuppressed()) { ... }       // the close() failures
}
```

`AutoCloseable.close()` is declared `throws Exception`; `Closeable.close()` is declared `throws IOException`. Your own `close()` can declare **less** (or nothing).

## 4. How it works under the hood

- The compiler turns try-with-resources into roughly: create the resource; `try` the body; on an exception, try `close()` and `addSuppressed` any failure; otherwise call `close()` normally. That's the code you'd have written by hand before Java 7, done correctly every time.
- A `finally` block is copied onto every exit path of the `try` (normal end, each `return`, each `catch`).
- Multi-catch compiles to one handler; that's why the variable must be final (its static type is the common supertype).
- **Java 22+**: an exception variable you don't use can be written `_` (`catch (NumberFormatException _)`). On Java 21, name it `ignored`.

## 5. Common mistakes and gotchas

**Closing in the `try` instead of `finally`/try-with-resources**
```java
Reader in = new FileReader(path);
process(in);
in.close();                       // WRONG: never runs if process() throws
try (Reader in = new FileReader(path)) { process(in); }   // RIGHT
```

**`finally` replacing the real exception.** If `finally` throws, the original exception is **lost**:
```java
try { write(); }                  // throws "disk full"
finally { file.close(); }         // also throws: "disk full" is gone, you only see the close error
```
Try-with-resources fixes this: the close error becomes a suppressed exception of "disk full".

**`return` in `finally`**: it discards any exception the `try` threw and replaces the `try`'s return value. Never return from `finally`.

**Catching too broadly** (`catch (Exception e)`), or an **empty catch**: covered in lesson 02, still the most common real-world bug.

**Catch order mistakes** with subclass/superclass: compile error, but easy to "fix" wrongly by deleting the specific handler.

## 6. When to use / when not to

- **Try-with-resources** for anything that must be closed: files, sockets, JDBC `Connection`/`Statement`/`ResultSet`, streams from `Files.lines`, your own `AutoCloseable` classes.
- **`finally`** for cleanup that isn't a resource: unlocking a lock, restoring a flag, stopping a timer.
- **Several `catch` blocks** when each exception needs different handling; **multi-catch** when they need the same.
- **Don't catch** what you can't handle. Let it propagate (lesson 02).

## 7. Interview angle

1. **When does `finally` not run?** If the JVM exits during the `try`/`catch` (e.g. `System.exit`, a crash, the thread being killed).
2. **What is a suppressed exception?** In try-with-resources, when both the body and `close()` throw, the body's exception propagates and the close exception is attached to it; read them with `getSuppressed()`.
3. **In what order are resources closed?** The reverse of the order they were declared.
4. **What does multi-catch require?** The types must not be subclasses of each other, and the catch parameter is implicitly final.
5. **What happens if `finally` contains `return`?** It overrides the `try`'s return value and silently discards any exception thrown in the `try`. Avoid it.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_SafeCalculator.java` | Use separate catches, multi-catch and `finally` to parse and divide user input | Easy |
| 2 | `exercises/Exercise2_LeakyReports.java` | Fix resource leaks with try-with-resources, and get the close order right | Medium |
| 3 | `exercises/Exercise3_LostExceptions.java` | Two methods lose the real error (a throwing `finally`, a `return` in `finally`). Fix them and expose the suppressed close failure | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
