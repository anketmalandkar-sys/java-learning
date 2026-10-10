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

## 9. Extra: method references in depth

Source: https://dev.java/learn/language/fp/lambdas/method-references

Added from the dev.java "Writing Lambda Expressions as Method References" page, which goes further than the table in section 3.

**The four kinds, with their usual names**

| Kind | Syntax | Equivalent lambda | Where the object comes from |
|---|---|---|---|
| Static | `Type::staticMethod` | `(a, b) -> Type.staticMethod(a, b)` | no object |
| **Bound** | `expr::instanceMethod` | `(a) -> expr.instanceMethod(a)` | fixed **inside the reference** |
| **Unbound** | `Type::instanceMethod` | `(obj, a) -> obj.instanceMethod(a)` | the lambda's **first argument** |
| Constructor | `Type::new` | `(a) -> new Type(a)` | a new object |

**Bound vs unbound** is the part that trips people up. `System.out::println` and `"Hello"::concat` are bound: the target object is already chosen. `String::length` and `String::concat` are unbound: they *look* like static calls, but the object to call them on arrives as the first argument.

**Every kind can take several arguments.** The functional interface decides how many:
```java
IntBinaryOperator max = Math::max;                       // static, 2 args:  (a, b) -> Math.max(a, b)
BinaryOperator<String> join = String::concat;            // unbound, 2 args: (s, t) -> s.concat(t)
Comparator<String> ignoreCase = String::compareToIgnoreCase;   // unbound:   (s, t) -> s.compareToIgnoreCase(t)
Function<String, String> greet = "Hello, "::concat;      // bound, 1 arg:   name -> "Hello, ".concat(name)
ToIntFunction<String> length = String::length;           // unbound, 1 arg: s -> s.length()
```
For an unbound reference, the receiver is always the **first** parameter of the functional interface. That's why `String::compareToIgnoreCase` (one parameter) fits `Comparator<String>.compare(a, b)` (two).

**Constructor references follow the target type.** `ArrayList::new` is not one constructor; the compiler picks the one that fits:
```java
Supplier<List<String>> empty = ArrayList::new;                         // new ArrayList<>()
Function<Integer, List<String>> sized = ArrayList::new;                 // new ArrayList<>(capacity)
Function<Collection<String>, List<String>> copied = ArrayList::new;     // new ArrayList<>(collection)
```
So a constructor reference needs a target type that says which one you mean. You can write a type argument (`ArrayList<String>::new`), but there's **no diamond**: `ArrayList<>::new` doesn't compile. Usually you just write `ArrayList::new` and let inference fill it in.

**Gotchas**
- `Integer::toString` is ambiguous for `Function<Integer, String>`: it matches both the static `Integer.toString(int)` and the unbound instance `toString()`. Use a lambda (`i -> i.toString()`) or `String::valueOf`.
- A bound reference evaluates its target **once, when the reference is created**: `user.getName()::length` captures the name at that moment, not each time it's called.

**Extra exercise**

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 4 | `exercises/Exercise4_MethodReferences.java` | Replace lambdas with the right kind of method reference: static, bound, unbound with extra arguments, and target-dependent constructor references | Medium |

Example: `examples/Example3_MethodReferenceKinds.java`.

## 10. Extra: the functional interface family and combinators

Source: https://dev.java/learn/language/fp/lambdas/first-lambdas
Source: https://dev.java/learn/language/fp/lambdas/functional-interfaces
Source: https://dev.java/learn/language/fp/lambdas/combining-chaining-composing

Added from three dev.java lambda pages, covering what sections 3-5 didn't.

This lesson now covers the whole dev.java "Lambda Expressions" series ([index](https://dev.java/learn/language/fp/lambdas)): pages 1, 2 and 4 here, method references in section 9, and the comparator page in [02-comparator §9](../02-comparator/README.md#9-extra-the-contracts-symmetry-rule-and-the-two-thencomparings).

**What a lambda can and can't implement.** A lambda implements the **one abstract method** of its interface, nothing else. It can't override a `default` method: calling `pred.negate()` on a lambda `Predicate` always runs `Predicate`'s own `negate()` code. If you need to override a default method, write a class.

**Serializable lambdas.** A lambda is serializable only if its target type is. A plain `Runnable` lambda isn't, but an intersection cast makes it so:
```java
Runnable r = (Runnable & Serializable) () -> System.out.println("hi");   // r instanceof Serializable is true
```
You'll rarely need this; it matters when a lambda is stored in a field of a `Serializable` class.

**The four families and their primitive versions.** `java.util.function` has 40+ interfaces, but they're all variations of four shapes. The primitive versions avoid boxing (see `09-numbers-and-strings/02-autoboxing`).

| Family | Object version | `int` versions (also `long`, `double`) | Two-argument version |
|---|---|---|---|
| **Supplier**: nothing → value | `Supplier<T>.get()` | `IntSupplier.getAsInt()`, plus `BooleanSupplier.getAsBoolean()` | none |
| **Consumer**: value → nothing | `Consumer<T>.accept(t)` | `IntConsumer.accept(int)` | `BiConsumer<T, U>`, `ObjIntConsumer<T>.accept(t, int)` |
| **Predicate**: value → boolean | `Predicate<T>.test(t)` | `IntPredicate.test(int)` | `BiPredicate<T, U>` (no primitive version) |
| **Function**: value → value | `Function<T, R>.apply(t)` | see the grid below | `BiFunction<T, U, R>`, `ToIntBiFunction<T, U>` |

**The function grid.** The name tells you which side is primitive:

| Interface | Takes | Returns | Method |
|---|---|---|---|
| `Function<T, R>` | `T` | `R` | `apply` |
| `IntFunction<R>` | `int` | `R` | `apply` |
| `ToIntFunction<T>` | `T` | `int` | `applyAsInt` |
| `IntUnaryOperator` | `int` | `int` | `applyAsInt` |
| `IntToLongFunction` | `int` | `long` | `applyAsLong` |
| `UnaryOperator<T>` | `T` | `T` | `apply` (it extends `Function<T, T>`) |
| `BinaryOperator<T>` / `IntBinaryOperator` | `T, T` / `int, int` | `T` / `int` | `apply` / `applyAsInt` |

Naming rule: the method is `get`/`apply`/`accept`/`test`, plus `As<Type>` when it **returns** a primitive (`getAsInt`, `applyAsDouble`).

**Collection methods that take lambdas, and their traps**
```java
list.forEach(System.out::println);          // Consumer: internal iteration (on every Iterable)
list.removeIf(s -> s.isBlank());            // Predicate: removes matches; MUTATES the list
list.replaceAll(String::trim);              // UnaryOperator: replaces each element in place
```
- `replaceAll` takes a `UnaryOperator<T>`, not a `Function<T, R>`, because a `List<String>` must stay a `List<String>`: the element type can't change.
- `List.of(...)` is immutable: **both** methods throw `UnsupportedOperationException`, even when nothing would change.
- `Arrays.asList(...)` is fixed-size: `replaceAll` **works** (it only sets elements), but `removeIf` throws as soon as an element matches.

**More combinators.** The combining methods have to be `default` (or `static`) methods, because a functional interface may have only one abstract method:
```java
Predicate<String> notBlank = Predicate.not(String::isBlank);   // static factory (Java 11): reads better than s -> !s.isBlank()
Predicate<String> isDuke = Predicate.isEqual("Duke");          // static factory: null-safe equals test
Consumer<String> logThenPrint = log.andThen(System.out::println);   // both run, in order, on the same input
Function<String, String> same = Function.identity();          // returns its input unchanged (e.g. as a "no-op" step or a map key)
```
`f.andThen(g)` and `g.compose(f)` build the **same** function. Only the reading order differs. The output type of the first must fit the input type of the second.

**Extra exercise**

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 5 | `exercises/Exercise5_CombineAndSpecialize.java` | Pick the right primitive interfaces, build rules with `Predicate.not`, `isEqual`, `Consumer.andThen` and `Function.identity`, and avoid the `removeIf` traps | Medium |

Example: `examples/Example4_FunctionalInterfaceFamily.java`.
