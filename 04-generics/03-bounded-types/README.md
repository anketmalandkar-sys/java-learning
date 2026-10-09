[← Generics roadmap](../README.md)

# Bounded type parameters

## 1. What it is

A **bound** limits which types can be used for a type parameter. `<T extends Number>` means "`T` can be `Number` or any subtype of it": `Integer`, `Double`, `Long`, and so on.

In return, the compiler lets you call the bound's methods on `T`. A plain `<T>` only knows `Object`'s methods. A `<T extends Number>` also knows `doubleValue()` and `intValue()`.

`extends` is used for both classes and interfaces here: `<T extends Comparable<T>>` means "`T` implements `Comparable<T>`".

## 2. Why it exists

With a plain `<T>`, you can store and pass values around, but you can't *do* anything type-specific with them:

```java
static <T> T max(List<T> items) {
    T best = items.get(0);
    for (T item : items) {
        if (item > best) best = item;              // compile error: > only works on primitives
        if (item.compareTo(best) > 0) best = item; // compile error: Object has no compareTo
    }
    return best;
}
```

Dropping generics and using the concrete type works, but you lose the caller's type:

```java
static Comparable max(List<Comparable> items)   // raw types, casts at every call site
static Number largest(List<Number> nums)        // returns a Number, not the Integer you passed in
```

A bound gives you both: you can call the methods you need, and the caller gets their exact type back.

```java
static <T extends Comparable<T>> T max(List<T> items) { ... }

Integer top = max(List.of(3, 9, 4));             // returns Integer, no cast
String last = max(List.of("pear", "apple"));     // returns String
```

## 3. Core concepts

**A class bound.** `T` can only be `Number` or a subtype, so `Number`'s methods are available:

```java
static <T extends Number> double sum(List<T> values) {
    double total = 0;
    for (T value : values) {
        total += value.doubleValue();   // allowed: every T is a Number
    }
    return total;
}

sum(List.of(1, 2, 3));       // T = Integer
sum(List.of(2.5, 1.5));      // T = Double
sum(List.of("a", "b"));      // compile error: String is not a Number
```

**An interface bound: `Comparable<T>`.** The most common bound in real code. It means "`T` can be compared with other `T`s":

```java
static <T extends Comparable<T>> T max(List<T> items) {
    T best = items.get(0);
    for (T item : items) {
        if (item.compareTo(best) > 0) {
            best = item;
        }
    }
    return best;
}
```

**Bounds on a generic class.** The same syntax goes after the class name, and the bound holds for the whole class:

```java
class Range<T extends Comparable<T>> {
    private final T low, high;
    boolean contains(T value) {
        return value.compareTo(low) >= 0 && value.compareTo(high) <= 0;
    }
}

Range<Integer> ages = new Range<>(18, 65);
Range<Object> bad;           // compile error: Object is not Comparable
```

**Multiple bounds.** Join them with `&`. `T` must satisfy all of them, and you can call methods from each:

```java
static <T extends Number & Comparable<T>> T maxAbove(List<T> values, double floor) {
    T best = null;
    for (T v : values) {
        if (v.doubleValue() > floor                          // from Number
                && (best == null || v.compareTo(best) > 0)) { // from Comparable
            best = v;
        }
    }
    return best;
}
```

The rules: at most **one class**, and it must come **first**. Any number of interfaces can follow.

```java
<T extends Number & Comparable<T>>      // right
<T extends Comparable<T> & Number>      // compile error: the class must be first
<T extends Number & Integer>            // compile error: two classes
```

## 4. How it works under the hood

At runtime, the type parameter is replaced by its **first bound** (lesson 05 covers erasure in full):

```java
<T>                                  // erased to Object
<T extends Number>                   // erased to Number
<T extends Comparable<T>>            // erased to Comparable
<T extends Number & Comparable<T>>   // erased to Number; a cast to Comparable is added where needed
```

That's why calling `value.doubleValue()` on a `<T extends Number>` needs no cast at runtime. The erased code really does call `Number.doubleValue()`.

## 5. Common mistakes and gotchas

**Using operators on `T`.** `<`, `>`, `+` only work on primitives. A bound doesn't change that. Call the bound's methods instead:

```java
if (a > b)                        // wrong, even with <T extends Number>
if (a.compareTo(b) > 0)           // right, with <T extends Comparable<T>>
if (a.doubleValue() > b.doubleValue())   // right, with <T extends Number>
```

**A raw `Comparable` bound.** It compiles with only a warning, and lets in types that can't be compared with each other:

```java
<T extends Comparable>            // wrong: raw. max(List.of("a", 1)) compiles, then throws ClassCastException
<T extends Comparable<T>>         // right
```

**Subclasses of a `Comparable` class.** Say `Employee implements Comparable<Employee>` and `Manager extends Employee`. `Manager` is a `Comparable<Employee>`, not a `Comparable<Manager>`, so `<T extends Comparable<T>>` rejects a `List<Manager>`:

```java
static <T extends Comparable<T>> T max(List<T> items)          // max(managers): compile error
static <T extends Comparable<? super T>> T max(List<T> items)  // max(managers): works
```

`? super T` reads as "`T` can be compared with `T` or with one of its parents". It's a wildcard (lesson 04). The JDK's `Collections.max` and `sort` use this form. The same happens with `LocalDate`, which implements `Comparable<ChronoLocalDate>`, not `Comparable<LocalDate>`.

**Thinking `extends` means "class only".** In a bound, `extends` covers interfaces too. There's no `implements` keyword here.

```java
<T implements Comparable<T>>      // wrong: doesn't compile
<T extends Comparable<T>>         // right
```

**Trying `super` on a type parameter.** `super` only works on wildcards, not on a named parameter:

```java
<T super Integer>                 // wrong: doesn't compile
List<? super Integer>             // right: wildcard (lesson 04)
```

## 6. When to use / when not to

**Use a bound when:**
- Your generic code needs to call methods on `T`: compare it (`Comparable`), do maths with it (`Number`), or call your own interface (`<T extends Identifiable>`, then `item.getId()`).
- You want to return the caller's exact type, not the bound type. `<T extends Number> T largest(...)` returns an `Integer` for a list of `Integer`s.

**Don't when:**
- You only read values as the bound type and return nothing typed. `double sum(List<? extends Number>)` is simpler than `<T extends Number> double sum(List<T>)`. Lesson 04 covers when a wildcard is enough.
- The bound would be `Object`. `<T extends Object>` is the same as `<T>`.

## 7. Interview angle

1. **What does `<T extends Comparable<T>>` mean?** `T` must be a type whose objects can be compared to other `T`s. Inside the method you can call `compareTo` on any `T`.
2. **What are multiple bounds, and what's the rule about ordering?** `<T extends A & B & C>`: `T` must satisfy all of them. At most one may be a class, and it must come first. The first bound is also what `T` erases to.
3. **Why doesn't `<T extends Comparable<T>> T max(List<T>)` accept a `List<Manager>` when `Manager extends Employee implements Comparable<Employee>`?** `Manager` is a `Comparable<Employee>`, not a `Comparable<Manager>`. Use `<T extends Comparable<? super T>>`, as `Collections.max` does.
4. **What does a bounded type parameter erase to?** Its first bound. `<T extends Number>` becomes `Number`, and an unbounded `<T>` becomes `Object`.
5. **When would you use `<T extends Number> T` instead of just `Number`?** When the caller should get back the same type they passed in. `largest(List<Integer>)` then returns an `Integer`, not a `Number` the caller has to cast.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_NumberStats.java` | Write `sum`, `average`, `max` and `clamp` with `Number` and `Comparable` bounds | Easy |
| 2 | `exercises/Exercise2_RangeClass.java` | Build a bounded generic class `Range<T extends Comparable<T>>`, and a method with multiple bounds | Medium |
| 3 | `exercises/Exercise3_LeaderboardBug.java` | A leaderboard helper with a raw `Comparable` bound crashes at runtime and rejects `Manager`s. Fix the bound so the compiler catches the bad call | Hard |
