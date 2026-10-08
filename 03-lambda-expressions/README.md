# Lambda Expressions

## 1. What it is

A lambda expression is a short way to write **a piece of behaviour you can pass around like a value** (Java 8+):

```java
(a, b) -> a + b
```

It is an implementation of an interface that has exactly one abstract method (a **functional interface**). The lambda's parameters and body become that one method. You already used them in the Comparator lesson: `(a, b) -> Integer.compare(a.getSalary(), b.getSalary())`.

## 2. Why it exists

Before Java 8, passing behaviour meant writing an anonymous class: five lines of ceremony for one line of logic.

```java
// Before Java 8
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Clicked");
    }
});

// With a lambda
button.addActionListener(e -> System.out.println("Clicked"));
```

Lambdas make it practical to write code that takes behaviour as a parameter: "filter this list **by some rule**", "run this **later**", "sort **by this key**". The Streams API, `Comparator.comparing`, `List.forEach`, `Map.computeIfAbsent` and `CompletableFuture` are all built on this.

## 3. Core concepts

**Syntax variations**

```java
() -> System.out.println("hi")          // no parameters
x -> x * 2                              // one parameter: parentheses optional
(x, y) -> x + y                         // several parameters
(int x, int y) -> x + y                 // explicit types (all or none)
(x, y) -> {                             // block body: needs braces and return
    int sum = x + y;
    return sum;
}
```

**Functional interface:** any interface with exactly one abstract method. `@FunctionalInterface` is optional, but makes the compiler check it.

```java
@FunctionalInterface
interface DiscountRule {
    double apply(double price);
}

DiscountRule tenPercentOff = price -> price * 0.9;
tenPercentOff.apply(200);   // 180.0
```

**The built-in ones (`java.util.function`).** Learn these six; most others are variations.

| Interface | Method | Shape | Example |
|---|---|---|---|
| `Predicate<T>` | `test(T)` | T → boolean | `s -> s.isEmpty()` |
| `Function<T, R>` | `apply(T)` | T → R | `s -> s.length()` |
| `Consumer<T>` | `accept(T)` | T → nothing | `s -> System.out.println(s)` |
| `Supplier<T>` | `get()` | nothing → T | `() -> new ArrayList<>()` |
| `UnaryOperator<T>` | `apply(T)` | T → T | `s -> s.toUpperCase()` |
| `BinaryOperator<T>` | `apply(T, T)` | (T, T) → T | `(a, b) -> a + b` |

Two-argument versions: `BiFunction<T, U, R>`, `BiPredicate<T, U>`, `BiConsumer<T, U>`.
Primitive versions avoid boxing: `IntPredicate`, `IntFunction<R>`, `ToIntFunction<T>`, `IntBinaryOperator`, ...

**Method references:** a shorter lambda when the body just calls one existing method.

| Kind | Method reference | Same as lambda |
|---|---|---|
| Static method | `Integer::parseInt` | `s -> Integer.parseInt(s)` |
| Method on the parameter | `String::toUpperCase` | `s -> s.toUpperCase()` |
| Method on a specific object | `System.out::println` | `x -> System.out.println(x)` |
| Constructor | `ArrayList::new` | `() -> new ArrayList<>()` |

**Composition:** the built-in interfaces have `default` methods to combine them.

```java
Predicate<String> notEmpty = s -> !s.isEmpty();
Predicate<String> shortWord = s -> s.length() < 5;
Predicate<String> both = notEmpty.and(shortWord);      // also: or(), negate()

Function<Integer, Integer> addTax = p -> p + p / 10;
Function<Integer, Integer> addShipping = p -> p + 50;
addTax.andThen(addShipping).apply(100);   // (100 + 10) + 50 = 160
addTax.compose(addShipping).apply(100);   // (100 + 50) + 15 = 165
```

**Capturing variables:** a lambda can read local variables from the surrounding method, but only if they are **effectively final** (never reassigned).

```java
int minAge = 18;                            // never reassigned: OK to capture
Predicate<Person> isAdult = p -> p.getAge() >= minAge;
```

## 4. How it works under the hood

- A lambda is **not** compiled to an anonymous inner class. The compiler turns the body into a private method and uses `invokedynamic` so the JVM creates the functional-interface object at runtime. You don't need the details; what matters is the behaviour below.
- **`this` inside a lambda means the enclosing object**, not the lambda. Inside an anonymous class, `this` means the anonymous class instance.
- **Captured variables are copied** into the lambda when it's created. That's why they must be effectively final: if Java let you change the local later, the copy and the original would silently disagree. Fields (`this.count`) are not copied, because the lambda captures `this`, so fields *can* be changed.
- The lambda's type comes from the **target type**: the variable or parameter it is assigned to. The same text `x -> x * 2` can be a `Function<Integer, Integer>`, a `UnaryOperator<Integer>` or an `IntUnaryOperator`, depending on where you put it.

## 5. Common mistakes and gotchas

**Modifying a captured local**

```java
// WRONG: compile error, "variable used in lambda should be final or effectively final"
int total = 0;
orders.forEach(o -> total += o.getAmount());

// RIGHT: let a stream do the sum (or use a plain for loop)
int total = orders.stream().mapToInt(Order::getAmount).sum();
```

Don't "fix" it with `int[] total = {0}`. It compiles, but it hides side effects and breaks with parallel streams.

**`andThen` vs `compose` order**

```java
// f.andThen(g) = g(f(x))   -> f runs FIRST
// f.compose(g) = f(g(x))   -> g runs FIRST
Function<Double, Double> discount = p -> p * 0.9;
Function<Double, Double> tax = p -> p * 1.18;
discount.andThen(tax);   // discount first, then tax on the reduced price
```

**Predicate chaining has no precedence, it runs left to right**

```java
// a.or(b).and(c) means (a OR b) AND c, not a OR (b AND c)
isVip.or(isEmployee).and(hasCoupon);      // (VIP or employee) and coupon
isVip.or(isEmployee.and(hasCoupon));      // VIP, or (employee with coupon)
```

**Checked exceptions don't fit the built-in interfaces**

```java
// WRONG: compile error, Function.apply doesn't declare IOException
Function<Path, String> read = path -> Files.readString(path);

// RIGHT: handle it inside the lambda (or define your own functional interface that throws)
Function<Path, String> read = path -> {
    try {
        return Files.readString(path);
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
};
```

**Comparing boxed values with `==`**

```java
// WRONG: Integer objects; == compares references, false for values above 127
BiPredicate<Integer, Integer> same = (a, b) -> a == b;
// RIGHT
BiPredicate<Integer, Integer> same = (a, b) -> a.equals(b);   // or Objects.equals(a, b)
```

**Forgetting `return` in a block body**

```java
Function<Integer, Integer> square = x -> { x * x; };          // compile error
Function<Integer, Integer> square = x -> { return x * x; };   // OK
Function<Integer, Integer> square = x -> x * x;               // better
```

## 6. When to use / when not to

**Use a lambda when:**
- the behaviour is short (1-3 lines) and used in one place
- an API asks for a functional interface (`sort`, `removeIf`, `forEach`, `computeIfAbsent`, streams, `Thread`, `ExecutorService`)
- you want to pass "a rule" or "a step" into a method instead of hard-coding it

**Use a method reference when** the lambda only calls one existing method (`String::length` beats `s -> s.length()`).

**Use a named method or class instead when:**
- the logic is more than a few lines: give it a name and reference it (`this::isEligible`)
- you need state, several methods, or the same logic in many places
- the lambda would need to throw checked exceptions everywhere

## 7. Interview angle

**Q: What is a functional interface? Can it have more than one method?**
An interface with exactly one abstract method. It can have any number of `default` and `static` methods, and can redeclare `Object` methods like `equals`. `Comparator` and `Runnable` are examples.

**Q: Why must captured local variables be effectively final?**
The lambda gets a copy of the value. If the local could change afterwards, the lambda and the method would see different values, which is especially dangerous when the lambda runs on another thread. Making it final keeps them in sync.

**Q: Difference between a lambda and an anonymous class?**
A lambda only works for functional interfaces, has no state of its own, and `this` refers to the enclosing instance. An anonymous class can implement interfaces with many methods or extend classes, can have fields, and its `this` is itself. Lambdas also avoid generating a separate `.class` file per use.

**Q: What are the four kinds of method reference?**
Static (`Integer::parseInt`), instance method of an arbitrary object of a type (`String::length`), instance method of a particular object (`System.out::println`), and constructor (`ArrayList::new`).

**Q: Difference between `Function.andThen` and `Function.compose`?**
`f.andThen(g)` runs `f` first, then `g` on its result. `f.compose(g)` runs `g` first, then `f`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|---|---|---|
| 1 | `exercises/Exercise1_BasicLambdas.java` | Write lambdas and method references for the six core functional interfaces | Easy |
| 2 | `exercises/Exercise2_ProductFilter.java` | Write a reusable filter/map helper and combine predicates and functions to build a shop's pricing rules | Medium |
| 3 | `exercises/Exercise3_FraudAlertBug.java` | Find and fix three bugs in a bank's fraud-alert rules built from lambdas | Hard |
