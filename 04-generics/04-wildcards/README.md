[← Generics roadmap](../README.md)

# Wildcards and PECS

## 1. What it is

A **wildcard** `?` stands for "some type, I don't know or care which". It's used in parameter, field and variable types, never in a class or method declaration:

| Wildcard | Reads as | Accepts |
|----------|----------|---------|
| `List<?>` | a list of some unknown type | any list |
| `List<? extends Number>` | a list of some type that is `Number` or a subtype (**upper bound**) | `List<Number>`, `List<Integer>`, `List<Double>` |
| `List<? super Integer>` | a list of some type that is `Integer` or a supertype (**lower bound**) | `List<Integer>`, `List<Number>`, `List<Object>` |

Wildcards make methods accept a whole family of generic types, which is what plain generic types refuse to do.

## 2. Why it exists

Generic types are **invariant**. `Integer` is a `Number`, but `List<Integer>` is **not** a `List<Number>`:

```java
static double sum(List<Number> values) { ... }

List<Integer> scores = List.of(90, 75);
sum(scores);                           // compile error
```

There's a good reason. If it were allowed, this would compile:

```java
List<Integer> scores = new ArrayList<>();
List<Number> numbers = scores;         // pretend this compiled
numbers.add(3.14);                     // a Double, inside a List<Integer>
Integer first = scores.get(0);         // ClassCastException
```

Wildcards restore the flexibility safely. `List<? extends Number>` accepts a `List<Integer>`, and the compiler stops you from adding to it, so the problem above can't happen.

## 3. Core concepts

**Upper bound `? extends T`: you can read as `T`, you can't add.**

```java
static double sum(List<? extends Number> values) {
    double total = 0;
    for (Number n : values) {      // every element is at least a Number
        total += n.doubleValue();
    }
    return total;
}

sum(List.of(1, 2, 3));        // List<Integer>: fine
sum(List.of(1.5, 2.5));       // List<Double>:  fine

values.add(5);                // compile error: it might be a List<Double>
```

**Lower bound `? super T`: you can add `T`s, reads come back as `Object`.**

```java
static void addScores(List<? super Integer> target) {
    target.add(90);           // fine: an Integer fits in List<Integer>, List<Number> or List<Object>
    target.add(75);
    Object first = target.get(0);    // reads are only Object: it might be a List<Object>
}

addScores(new ArrayList<Integer>());
addScores(new ArrayList<Number>());
addScores(new ArrayList<Object>());
```

**Unbounded `?`: you only need `Object` methods, or methods that don't care about the type.**

```java
static void printAll(List<?> items) {
    for (Object item : items) {
        System.out.println(item);
    }
    System.out.println(items.size() + " items");
}
```

**`List<?>` is not `List<Object>`.**

| | `List<Object>` | `List<?>` |
|-|----------------|-----------|
| Can you pass a `List<String>`? | No (invariance) | Yes |
| Can you add a `String`? | Yes | No, only `null` |
| What do reads return? | `Object` | `Object` |

`List<Object>` means "a list that holds anything". `List<?>` means "a list of one specific type I don't know".

**Wildcards create a subtype hierarchy.** `List<Integer>` and `List<Number>` are unrelated, but both fit under wildcard types:

```
                     List<?>
                   /         \
 List<? extends Number>     List<? super Integer>
          |                 /                  \
 List<? extends Integer>  List<? super Number>  ...
          |                 |
     List<Integer>       List<Number>
```

So `List<Integer>` is a `List<? extends Integer>`, which is a `List<? extends Number>`, which is a `List<?>`.

**PECS: Producer Extends, Consumer Super.** For each parameter, ask what the method does with it:
- It **produces** values for you to read: use `? extends T`.
- It **consumes** values you put in: use `? super T`.
- Both: no wildcard, plain `List<T>`.
- Neither, only `Object` methods / `size()` / `clear()`: use `?`.

```java
static <T> void copy(List<? extends T> source, List<? super T> target) {
    for (T item : source) {     // source produces Ts
        target.add(item);       // target consumes Ts
    }
}

List<Integer> ids = List.of(1, 2);
List<Number> numbers = new ArrayList<>();
copy(ids, numbers);             // T = Integer: an Integer source into a Number target
```

The JDK uses this everywhere: `Collections.copy(List<? super T> dest, List<? extends T> src)`, `addAll(Collection<? extends E>)`, `forEach(Consumer<? super T>)`, `removeIf(Predicate<? super E>)`, `sort(Comparator<? super E>)`.

**Wildcard capture and helper methods.** Every `?` is a specific type, just unnamed. The compiler gives it a temporary name (`CAP#1` in error messages). That's why this fails:

```java
static void swapFirstAndLast(List<?> list) {
    Object first = list.get(0);
    list.set(0, list.get(list.size() - 1));   // compile error: Object is not CAP#1
}
```

The fix is a private generic helper. Calling it **captures** the `?` as a named `T`:

```java
static void swapFirstAndLast(List<?> list) {
    swapFirstAndLastHelper(list);              // ? is captured as T
}

private static <T> void swapFirstAndLastHelper(List<T> list) {
    T first = list.get(0);
    list.set(0, list.get(list.size() - 1));
    list.set(list.size() - 1, first);
}
```

The public method keeps the simple `List<?>` signature, and the helper does the typed work. The convention is to name it `originalNameHelper`.

## 4. How it works under the hood

Wildcards are purely a compile-time check. At runtime, `List<? extends Number>`, `List<?>` and `List<Integer>` are all just `List` (lesson 05).

The compiler's reasoning for each `?`:
- `? extends Number`: "it's some specific subtype of `Number`". Reading gives at least a `Number`. Writing is refused, because it can't prove your value matches the unknown subtype. (`null` is the one exception, as it fits every type.)
- `? super Integer`: "it's `Integer` or some parent of it". Writing an `Integer` is always safe. Reading only guarantees `Object`.

## 5. Common mistakes and gotchas

**Using `List<Number>` (or `List<Object>`) when you mean "any list of numbers".**

```java
static double sum(List<Number> values)              // wrong: rejects List<Integer>
static double sum(List<? extends Number> values)    // right
```

**Expecting to add to a `? extends` list.**

```java
static void addDefault(List<? extends Number> values) {
    values.add(0);            // wrong: doesn't compile. It could be a List<Double>.
}
static void addDefault(List<? super Integer> values) {
    values.add(0);            // right
}
```

**Thinking `? extends` makes a list read-only.** It stops `add(...)` of real values, but some changes still go through:

```java
List<? extends Number> values = new ArrayList<>(List.of(1, 2, 3));
values.add(null);             // allowed: null fits every type
values.clear();               // allowed
values.remove(0);             // allowed
values.removeIf(n -> n.intValue() > 1);   // allowed
```

If you need a read-only list, use `List.copyOf(...)` or `Collections.unmodifiableList(...)`.

**Wildcards in return types.** They push the problem onto every caller:

```java
static List<? extends Number> loadTotals()   // wrong: callers can't add, and need casts to get a real type
static List<Double> loadTotals()             // right: return the concrete type
```

Wildcards belong in **parameters**, where they make a method accept more. In return types they only take capabilities away from the caller.

**Using `?` when the method needs to tie two types together.**

```java
static void copy(List<?> source, List<?> target)               // wrong: can't add, types unrelated
static <T> void copy(List<? extends T> source, List<? super T> target)   // right
```

**Some capture errors can't be fixed with a helper.** If two different `?`s really could be different types, a helper can't make them match, and that's the compiler protecting you:

```java
static void swapFirst(List<? extends Number> a, List<? extends Number> b) {
    Number temp = a.get(0);
    a.set(0, b.get(0));       // error, and no helper fixes it: a could be List<Integer>, b List<Double>
}
```

## 6. When to use / when not to

**Use wildcards when:**
- A method parameter only reads from a generic type: `? extends T`.
- A method parameter only writes into it: `? super T`.
- A parameter only uses `size()`, `clear()`, `Object` methods, or prints things: `?`.
- You accept functional interfaces: `Predicate<? super T>`, `Function<? super T, ? extends R>`, `Comparator<? super T>`. This lets callers pass a more general function, e.g. a `Predicate<Object>` where a `Predicate<String>` is expected.

**Don't when:**
- The method both reads and writes the same type: use plain `List<T>`.
- It's a return type, or a field you'll add to later: use a concrete type.
- You need to name the type to use it twice (e.g. return the same type you received). Use a type parameter `<T>`.

## 7. Interview angle

1. **Why isn't `List<Integer>` a subtype of `List<Number>`?** Generics are invariant. If it were a subtype, you could add a `Double` through the `List<Number>` reference and corrupt the `List<Integer>`. Arrays *are* covariant, and that's why they need a runtime `ArrayStoreException` check.
2. **What is PECS?** Producer Extends, Consumer Super. Use `? extends T` for a parameter you read `T`s from, and `? super T` for one you write `T`s into. `Collections.copy(List<? super T> dest, List<? extends T> src)` is the classic example.
3. **What's the difference between `List<?>` and `List<Object>`?** `List<Object>` accepts only an actual `List<Object>`, and you can add anything to it. `List<?>` accepts any list, but you can't add anything except `null`.
4. **What can you do with a `List<? extends Number>`?** Read elements as `Number`, add `null`, remove and clear. You can't add a non-null element, because the compiler doesn't know the real element type.
5. **What is wildcard capture?** The compiler treats each `?` as a specific unnamed type (`CAP#1`). To work with it, for example to `set` an element you just read, pass the list to a private generic helper `<T> void helper(List<T>)`. The helper names the captured type.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_WildcardSignatures.java` | Every parameter starts as `List<?>`. Choose `? extends`, `? super` or `?` for each method, then implement it | Easy |
| 2 | `exercises/Exercise2_TaskQueue.java` | Give a generic `TaskQueue<T>` PECS-style `addAll`, `drainTo`, `removeIf` and `forEach`, and write a wildcard capture helper | Medium |
| 3 | `exercises/Exercise3_ReportUtilsBug.java` | Report helpers use raw casts and wildcard return types. Customer names end up in a list of sales figures. Fix the signatures so the compiler catches it | Hard |
