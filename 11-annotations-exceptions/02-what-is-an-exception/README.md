# What Is an Exception?

Source: https://dev.java/learn/language/annotations-exceptions/exceptions/what-is-an-exception
Source: https://dev.java/learn/language/annotations-exceptions/exceptions/unchecked-exception-controversy

← Back to [Annotations and Exceptions](../README.md)

## 1. What it is

An **exception** is an event that disrupts the normal flow of a program. When a method hits a problem, it creates an **exception object** describing what went wrong (its type, a message, where it happened) and hands it to the runtime: it **throws** the exception. The runtime then looks for code that can **handle** it, called an **exception handler** (a `catch` block).

## 2. Why it exists

Without exceptions, every method signals failure with special return values, and every caller must check them:

```java
int result = openFile();
if (result == -1) { /* not found */ }
else if (result == -2) { /* no permission */ }
else {
    int size = readSize();
    if (size < 0) { /* read error... and is the file still open? */ }
}
```

Exceptions give three advantages (from dev.java's "Unchecked Exceptions — The Controversy"):

1. **Error handling is separated from the main logic.** The normal path reads top to bottom; the problems are handled in their own `catch` blocks.
2. **Errors travel up the call stack** to the method that cares. Methods in between don't have to pass error codes along.
3. **Error types form a hierarchy.** `catch (FileNotFoundException e)` handles one specific problem; `catch (IOException e)` handles every I/O problem, including `FileNotFoundException` and `EOFException`.

Exceptions don't remove the work of detecting and handling errors. They organise it.

## 3. Core concepts

**The call stack and the handler search.** When `main` calls `processOrder`, which calls `loadStock`, which throws, the runtime searches **backwards** through the call stack for the first method with a matching `catch`:

```
main()              ← 3. found a catch for IOException: handled here
  processOrder()    ← 2. no handler: keep going up
    loadStock()     ← 1. throws FileNotFoundException
```

"Matching" means the handler's type is the thrown type **or one of its superclasses**. If no method handles it, the thread ends; for the main thread, the program stops and prints the stack trace.

**The Catch or Specify Requirement.** For **checked** exceptions, the compiler insists that each method either:
- **catches** it, inside a `try`/`catch`, or
- **specifies** it, by declaring `throws` in its signature, so its callers have to deal with it.

```java
void load(String path) throws IOException {          // specify: callers must handle IOException
    FileReader in = new FileReader(path);             // may throw FileNotFoundException (an IOException)
}
void loadOrDefault(String path) {
    try {
        load(path);
    } catch (IOException e) {                         // catch: the requirement stops here
        System.out.println("using defaults: " + e.getMessage());
    }
}
```
Code that does neither doesn't compile.

**Three kinds of exceptional conditions**

| Kind | Class | Example | Catch or specify? | Typical response |
|---|---|---|---|---|
| **Checked exception** | `Exception` (not `RuntimeException`) | `FileNotFoundException`, `IOException`, `SQLException` | **Required** | Recover: ask for another file, retry, use a default |
| **Error** | `Error` and subclasses | `OutOfMemoryError`, `StackOverflowError`, `IOError` | Not required | Usually nothing you can do: log and exit |
| **Runtime exception** | `RuntimeException` and subclasses | `NullPointerException`, `IllegalArgumentException`, `ArrayIndexOutOfBoundsException` | Not required | **Fix the bug** that caused it |

Errors and runtime exceptions together are called **unchecked exceptions**.

```
Throwable
├── Error                  unchecked
└── Exception              checked
    ├── IOException        checked
    │   └── FileNotFoundException
    └── RuntimeException   unchecked
        ├── NullPointerException
        └── IllegalArgumentException
            └── NumberFormatException
```

**Checked or unchecked? (the controversy).** A checked exception is **part of the method's API**, as much as its parameters and return type: it tells callers "this can go wrong, and you can do something about it". The rule from dev.java:

> If a client can reasonably be expected to **recover** from an exception, make it a **checked** exception. If a client **cannot do anything** to recover, make it an **unchecked** exception.

- Missing file, network down, insufficient funds: the caller can retry, ask the user, or pick a default → **checked**.
- `null` passed where it isn't allowed, index out of range, an invalid argument: that's a **programming error** in the caller → **unchecked** (`NullPointerException`, `IllegalArgumentException`).

Don't make everything unchecked just to avoid writing `throws`: you lose the compiler's help in making callers handle real failures.

## 4. How it works under the hood

- An exception is a normal object; `throw` hands it to the JVM, which **unwinds** the stack: each method without a matching handler is exited immediately (its `finally` blocks run, see lesson 03), until a handler is found.
- The exception object records the **stack trace** (the chain of method calls) when it's **created**. That's what you see printed: `at Shop.loadStock(Shop.java:42)`.
- Checked vs unchecked is **purely a compile-time rule**. At runtime the JVM treats all `Throwable`s the same.
- Catching a superclass catches all its subclasses: `catch (Exception e)` also catches every `RuntimeException`. That's why it's so broad.

## 5. Common mistakes and gotchas

**Swallowing an exception**
```java
try { load(path); } catch (IOException e) { }               // WRONG: the failure vanishes silently
try { load(path); } catch (IOException e) {                  // RIGHT: handle it, or at least report it
    log.warning("could not load " + path + ": " + e.getMessage());
}
```

**Catching `Exception` (or `Throwable`) everywhere**
```java
catch (Exception e)          // WRONG for normal code: also catches bugs (NPE) you should fix
catch (IOException e)        // RIGHT: catch what you can actually handle
```
Catching `Exception` is acceptable at the very top of a program, to print a message and exit cleanly.

**Catching a runtime exception instead of fixing the bug**
```java
try { name.length(); } catch (NullPointerException e) { ... }   // WRONG
if (name != null) { name.length(); }                              // RIGHT: don't let the bug happen
```

**Making everything a `RuntimeException`** to "get rid of the `throws` noise": callers no longer learn, from the compiler, that a recoverable failure can happen.

**`throws Exception` on every method**: it says nothing useful and forces every caller to catch everything.

## 6. When to use / when not to

- **Throw a checked exception** for expected, recoverable problems outside the program's control: I/O, network, a business rule the caller can react to (insufficient funds, item out of stock).
- **Throw an unchecked exception** for misuse of your API: invalid arguments, wrong state, `null` where not allowed.
- **Let `Error`s propagate.** You almost never catch them.
- **Handle an exception where you can do something useful** about it. If you can't, declare it with `throws` and let it travel up.

## 7. Interview angle

1. **Checked vs unchecked exceptions?** Checked (subclasses of `Exception` but not `RuntimeException`) must be caught or declared; the compiler enforces it. Unchecked (`RuntimeException`, `Error` and subclasses) don't need to be.
2. **What is the Catch or Specify Requirement?** Code that can throw a checked exception must either catch it in a `try`/`catch` or declare it with `throws`.
3. **What happens if no handler is found?** The thread terminates; for the main thread the program ends and the stack trace is printed.
4. **When should an exception be checked?** When the caller can reasonably recover from it. If it signals a programming error, make it unchecked.
5. **Error vs Exception?** `Error` signals serious problems outside the application's control (out of memory, stack overflow) that it shouldn't try to catch. `Exception` covers conditions a program may want to handle.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_ClassifyExceptions.java` | Classify exception types as checked, runtime or error by walking the class hierarchy | Easy |
| 2 | `exercises/Exercise2_HandleAtTheRightLevel.java` | An order pipeline swallows errors too early. Let them propagate with `throws` and handle them in the right place | Medium |
| 3 | `exercises/Exercise3_ErrorCodesToExceptions.java` | Replace error codes with a checked exception for a recoverable problem and an unchecked one for misuse | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
