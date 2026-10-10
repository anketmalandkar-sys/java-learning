# Refactoring to functional style

Based on the dev.java series by Venkat Subramaniam: [Refactoring from the Imperative to the Functional Style](https://dev.java/learn/language/fp/refactoring-to-functional-style/).

## 1. What it is

**Imperative** code says *what* to do and *how*: a counter, a condition, an increment, a temporary list.
**Functional** (declarative) code says only *what*: "the numbers 1 to 10, keep the even ones, square them".
The library handles the *how*. This lesson is a set of mechanical recipes for turning common loops into stream pipelines:

| Imperative | Functional equivalent | Since |
|------------|----------------------|-------|
| `for (int i = 0; i < n; i++)` | `IntStream.range(0, n)` / `rangeClosed(1, n)` | 8 |
| `for (int i = 0; i <= 15; i += 3)` | `IntStream.iterate(0, i -> i <= 15, i -> i + 3)` | 9 |
| a loop that ends with `break` | `iterate(seed, next)` + `takeWhile(...)` | 9 |
| `for (x : list) { if (...) ... }` | `list.stream().filter(...)` | 8 |
| `for (x : list) { y = transform(x); ... }` | `list.stream().map(...)` | 8 |
| `while ((line = reader.readLine()) != null)` | `Files.lines(path)` | 8 |

## 2. Why it exists

A loop mixes several jobs together: where the data comes from, which items to keep, how to change them, and what to collect.

```java
List<String> result = new ArrayList<>();          // 1. a mutable holder
for (String name : names) {                       // 2. how to step through
    if (name.length() == 4) {                     // 3. which to keep
        result.add(name.toUpperCase());           // 4. how to change + where to put it
    }
}
```

A pipeline separates those jobs into one named step each, with no mutable variables:

```java
List<String> result = names.stream()
        .filter(name -> name.length() == 4)       // which to keep
        .map(String::toUpperCase)                 // how to change
        .toList();                                // what to collect
```

It reads top to bottom like the requirement. Each step can be changed alone, and nothing can be left half-updated.

## 3. Core concepts

**Simple loops → `range` / `rangeClosed`.** The essence of a counting loop is its range. `range(a, b)` excludes `b` (like `i < b`); `rangeClosed(a, b)` includes it (like `i <= b`).
```java
for (int i = 0; i < 5; i++) { System.out.println(i); }
IntStream.range(0, 5).forEach(System.out::println);       // 0 1 2 3 4

int sum = IntStream.rangeClosed(1, 10).sum();             // 55: no "total +=" needed
```

**Loops with steps → 3-argument `iterate`.** The three parts of the `for` header become three arguments: the `;` becomes `,`.
```java
for (int i = 0; i <= 15; i += 3) { ... }
IntStream.iterate(0, i -> i <= 15, i -> i + 3)            // seed, hasNext, next -> 0 3 6 9 12 15
```
Don't use `rangeClosed(0, 15).filter(i -> i % 3 == 0)` for this: it works here, but it visits every number and breaks for steps that aren't simple multiples (e.g. `i *= 2`).

**Loops with `break` → 2-argument `iterate` + `takeWhile`.** `iterate(seed, next)` is infinite; `takeWhile` is the functional `break`: it stops at the first element that fails.
```java
for (int p = 1; ; p *= 2) { if (p > 1000) break; ... }
IntStream.iterate(1, p -> p * 2).takeWhile(p -> p <= 1000)   // 1 2 4 ... 512
```

**`foreach` + `if` → `filter`.** A plain `for (x : list)` is just `list.forEach(...)`. Once there's an `if`, switch to a stream: `filter` exists on `Stream`, not on `Collection`.
```java
names.forEach(System.out::println);                       // no if: forEach on the collection
names.stream().filter(n -> n.length() == 4).forEach(System.out::println);
```

**Transformation → `map`.** Any "compute a new value from each element" step is a `map`. Filter first, then map: that way you transform only what you keep.
```java
names.stream().filter(n -> n.length() == 4).map(String::toUpperCase).toList();
```

**Data sources → streams.** Look for a method that gives you a stream instead of a reader loop: `Files.lines(path)`, `String.lines()`, `map.entrySet().stream()`, `Pattern.splitAsStream`...
```java
try (Stream<String> lines = Files.lines(path)) {          // try-with-resources: closes the file
    long count = lines.filter(line -> line.contains("public")).count();
}
```

**Results without mutation.** Replace "a variable updated in the loop" with a terminal operation: `count()`, `sum()`, `max()`, `toList()`, `collect(Collectors.groupingBy(...))`, `findFirst()`, `anyMatch()`.

**Refactor in small steps** (dev.java's advice). First split a busy loop body into one-job lines, then move to `stream()`, then turn each line into `filter` / `map` / a terminal operation. Run the tests after each step.

## 4. How it works under the hood

- **Lazy.** `filter` and `map` are *intermediate* operations: they only describe the work. Nothing runs until a *terminal* operation (`forEach`, `count`, `toList`, `sum`...) pulls elements through.
- **One element at a time.** A pipeline doesn't build a full list after each step. Each element goes through `filter → map → ...` before the next one starts. That's why `takeWhile` and `findFirst` can stop an infinite `iterate`.
- **Order of steps matters.** `limit(3).filter(...)` keeps the first 3 elements and *then* filters them; `filter(...).limit(3)` gives the first 3 that pass.
- **Single use.** A stream is consumed by its terminal operation. Using it again throws `IllegalStateException: stream has already been operated upon or closed`. Keep the *source* (a list, a path) and make a new stream each time.
- **Primitive streams.** `IntStream`, `LongStream`, `DoubleStream` hold raw numbers (no boxing) and add `sum()`, `average()`, `max()`. Use `boxed()` or `mapToObj` to get back to `Stream<Integer>`/objects, and `mapToInt` to go the other way.
- **Lambdas capture values, not variables.** A lambda can read a local variable only if it's *effectively final*. That's why `total += x` inside `forEach` doesn't compile, and it's a hint to use `sum()` instead.
- **`Files.lines` is lazy and holds the file open** until the stream is closed. Unlike a list, the stream has a resource behind it.

## 5. Common mistakes and gotchas

**Mutating outside state from `forEach`** (works, but it's imperative code in disguise and breaks with `parallel()`):
```java
List<String> upper = new ArrayList<>();
names.stream().filter(n -> n.length() == 4).forEach(n -> upper.add(n.toUpperCase()));   // wrong
List<String> upper = names.stream().filter(n -> n.length() == 4).map(String::toUpperCase).toList();   // right
```

**Reusing a stream:**
```java
Stream<String> lines = Files.lines(path);
long total = lines.count();
long errors = lines.filter(l -> l.contains("ERROR")).count();   // wrong: IllegalStateException

List<String> all;
try (Stream<String> lines = Files.lines(path)) { all = lines.toList(); }   // right: read once,
long total = all.size();                                                   // stream the list as often as you like
long errors = all.stream().filter(l -> l.contains("ERROR")).count();
```

**`filter` where you meant `takeWhile`.** `filter` checks every element; `takeWhile` stops at the first failure.
```java
lines.stream().filter(l -> !l.equals("SHUTDOWN"))      // wrong: also keeps lines after SHUTDOWN
lines.stream().takeWhile(l -> !l.equals("SHUTDOWN"))   // right: everything before the first SHUTDOWN
IntStream.iterate(1, p -> p * 2).filter(p -> p < 1000).forEach(...)   // never ends: filter can't stop an infinite stream
```

**`limit` in the wrong place:**
```java
orders.stream().limit(3).filter(o -> o.amount() > 1000)   // wrong: big orders among the first 3
orders.stream().filter(o -> o.amount() > 1000).limit(3)   // right: the first 3 big orders
```

**Not closing `Files.lines`.** The dev.java page skips this. Without try-with-resources, the file handle leaks until garbage collection (on Windows the file stays locked).

**`toList()` vs `collect(Collectors.toList())`.** `Stream.toList()` (Java 16) returns an *unmodifiable* list; `Collectors.toList()` makes no promise, and today it returns an `ArrayList`. Use `toList()` unless you need to modify the result.

**Off-by-one.** `range(1, 10)` stops at 9. If the loop said `i <= n`, you want `rangeClosed`.

## 6. When to use / when not to

Use it when:
- a loop filters, transforms, counts, sums, groups, or searches a collection: the pipeline is shorter and states the intent
- you want no mutable accumulators (fewer places for bugs)
- the data comes from a source that already offers a stream (files, maps, strings)

Keep the loop when:
- the body has several `break`/`continue`s, or updates several things at once
- the algorithm works with indexes or neighbours (two pointers, `a[i]` vs `a[i - 1]`, in-place swaps)
- the body throws checked exceptions (lambdas in `map`/`filter` can't throw them without wrapping)
- a profiler shows the loop is hot and the stream costs too much (rare; measure first)

The goal is readability. A pipeline that needs a paragraph of comments is worse than the loop it replaced.

## 7. Interview angle

1. **Imperative vs declarative?** Imperative describes the steps and manages state (indexes, accumulators). Declarative states the result you want and lets a library do the iteration (internal iteration). Streams are declarative.
2. **Intermediate vs terminal operations?** Intermediate (`filter`, `map`, `takeWhile`, `limit`, `sorted`) return a new stream and are lazy. Terminal (`forEach`, `count`, `toList`, `collect`, `sum`, `findFirst`) run the pipeline and end the stream.
3. **`filter` vs `takeWhile`?** `filter` checks every element and keeps all matches. `takeWhile` keeps elements until the first one that fails, then stops, so it works on infinite streams and is the equivalent of `break`.
4. **Why can't you do `count++` inside a lambda?** Lambdas can only capture effectively final locals. That stops data races in parallel streams and pushes you to use `count()`/`sum()`/`reduce`.
5. **What's wrong with `Files.lines(path).count()`?** The stream is never closed, so the file handle leaks. Use try-with-resources.

## 8. Exercises

| # | File | Difficulty | Goal |
|---|------|-----------|------|
| 1 | `exercises/Exercise1_LoopsToRanges.java` | Easy | Rewrite three counting loops with `rangeClosed`, 3-argument `iterate`, and `iterate` + `takeWhile` |
| 2 | `exercises/Exercise2_EmployeeReports.java` | Medium | Rewrite three employee reports (foreach + if + transformation) with `filter`, `map`, `mapToInt`, `groupingBy` |
| 3 | `exercises/Exercise3_LogReportBug.java` | Hard | A "refactored" log report has three bugs: a reused stream, `limit` in the wrong place, and `filter` instead of `takeWhile` |
