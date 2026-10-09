[← Generics roadmap](../README.md)

# Generics in practice

## 1. What it is

The earlier lessons were about *using* generics. This one is about **designing** generic APIs yourself:
- **Generic interfaces**, and the three ways a class can implement one.
- Two patterns you'll see in almost every real codebase: a **`Repository<T, ID>`** for storing entities, and a **`Result<T>`** for returning "a value or an error".
- **Recursive bounds** like `Enum<E extends Enum<E>>` and `Builder<B extends Builder<B>>`, where a type refers to itself in its own bound.

## 2. Why it exists

Without generic interfaces, every entity gets its own copy of the same code:

```java
class CustomerRepository { void save(Customer c); Optional<Customer> findById(String id); ... }
class OrderRepository    { void save(Order o);    Optional<Order> findById(Long id); ... }
class ProductRepository  { ... }   // the same methods again
```

With one generic interface, the contract is written once, and each use fills in the types:

```java
interface Repository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
}

Repository<Customer, String> customers;
Repository<Order, Long> orders;
```

Spring Data's `CrudRepository<T, ID>`, `Comparable<T>`, `Function<T, R>` and `Optional<T>` are all this idea.

## 3. Core concepts

**Implementing a generic interface: three choices.** Given `interface Pair<K, V> { K key(); V value(); }`:

```java
// 1. Stay generic: the caller still chooses the types.
class OrderedPair<K, V> implements Pair<K, V> { ... }
Pair<String, Integer> p = new OrderedPair<>("Pens", 120);

// 2. Fix all the types: the class is no longer generic.
class StockLine implements Pair<String, Integer> {
    public String key() { ... }
    public Integer value() { ... }
}

// 3. Fix some, keep others.
class Labelled<V> implements Pair<String, V> { ... }
```

**A generic interface with default methods.** Defaults can use the type parameters too, so implementers get extra methods for free:

```java
interface Repository<T, ID> {
    void save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();

    default boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}
```

**Bounding the entity type.** If the repository needs each entity's id, bound `T` with an interface that provides it:

```java
interface Entity<ID> {
    ID id();
}

class InMemoryRepository<T extends Entity<ID>, ID> implements Repository<T, ID> {
    private final Map<ID, T> store = new LinkedHashMap<>();
    public void save(T entity) { store.put(entity.id(), entity); }   // id() comes from the bound
    ...
}
```

**`Result<T>`: a value or an error.** It's like `Optional<T>`, but a failure carries a reason. With `sealed` (Java 17) and records:

```java
sealed interface Result<T> permits Success, Failure {
    static <T> Result<T> success(T value)       { return new Success<>(value); }
    static <T> Result<T> failure(String error)  { return new Failure<>(error); }

    <R> Result<R> map(Function<? super T, ? extends R> mapper);
}

record Success<T>(T value) implements Result<T> { ... }
record Failure<T>(String error) implements Result<T> { ... }
```

Note the PECS in `map`: the function **consumes** a `T` (`? super T`) and **produces** an `R` (`? extends R`). That lets callers pass a more general function.

**Generic methods inside generic types.** `map` has its own `<R>` on top of the interface's `T`. The class's parameter is fixed when the object is created. The method's parameter is chosen for each call.

**Recursive bounds.** A type parameter can appear in its own bound. You've already seen one:

```java
<T extends Comparable<T>>     // "T can be compared with other Ts"
```

The JDK's `Enum` is declared as:

```java
abstract class Enum<E extends Enum<E>> implements Comparable<E>
```

So every enum `Size` is really `Size extends Enum<Size>`, which makes `compareTo` accept only a `Size`. `Size.SMALL.compareTo(Colour.RED)` doesn't compile. `EnumSet<E extends Enum<E>>` and `EnumMap<K extends Enum<K>, V>` use the same bound to accept only enums.

**Self-typed builders.** The same trick lets a base builder's methods return the *subclass* type, so chaining keeps working:

```java
abstract static class Builder<B extends Builder<B>> {
    String name;
    B name(String name) { this.name = name; return self(); }   // returns the subclass type
    abstract B self();
}

static class PizzaBuilder extends Builder<PizzaBuilder> {
    boolean extraCheese;
    PizzaBuilder extraCheese() { extraCheese = true; return this; }
    PizzaBuilder self() { return this; }
}

new PizzaBuilder().name("Margherita").extraCheese();   // name() returned a PizzaBuilder, not a Builder
```

## 4. How it works under the hood

Everything here is still erased at runtime (lesson 05). `Repository<Customer, String>` is just `Repository`, and `InMemoryRepository`'s map is a plain `Map`. All the safety comes from compile-time checks, so it only holds if you avoid raw types and unchecked casts in the implementation.

A class that implements a generic interface with **fixed** types (`StockLine implements Pair<String, Integer>`) gets **bridge methods**. `Integer value()` also gets a hidden `Object value()` that calls it. It's the same mechanism as in lesson 05, and it's why calls through the `Pair` interface reach your typed method.

## 5. Common mistakes and gotchas

**Implementing the raw interface.**

```java
class Price implements Comparable {                  // wrong: compareTo(Object), casts, no type checking
    public int compareTo(Object o) { ... }
}
class Price implements Comparable<Price> {           // right
    public int compareTo(Price other) { ... }
}
```

**A builder without the recursive bound loses its type when you chain.**

```java
abstract class Builder { Builder name(String n) { ...; return this; } }
new PizzaBuilder().name("Margherita").extraCheese();   // wrong: name() returns Builder, which has no extraCheese()
```

Use `Builder<B extends Builder<B>>` with `self()`, as in section 3.

**Returning `null` from a `Result<T>` or `Optional<T>` method.** It defeats the purpose: callers check `isSuccess()`, not `null`. Return `Result.failure(...)` or `Optional.empty()`.

**Too many type parameters.** `Service<T, ID, DTO, MAPPER, VALIDATOR>` is hard to read and harder to use. If a type parameter is always the same concrete type, replace it with that type.

**Exposing a mutable internal collection.**

```java
public List<T> findAll() { return items; }                   // wrong: callers can modify your store
public List<T> findAll() { return List.copyOf(items); }      // right
```

## 6. When to use / when not to

**Make an interface generic when:**
- The same operations apply to many types: storage (`Repository<T, ID>`), conversion (`Converter<S, T>`), validation (`Validator<T>`), events (`EventHandler<E>`).
- You return "a T, plus something": `Result<T>`, `Page<T>`, `Optional<T>`, `Response<T>`.

**Use a recursive bound when:**
- A type must be compared or combined only with its own kind (`Comparable<T>`, `Enum<E>`).
- Methods inherited from a base class must return the subclass type (fluent builders).

**Don't when:**
- There's only ever one implementation with one type. A generic interface is extra ceremony then.
- You'd need `instanceof` checks on `T` inside the generic code. That usually means the abstraction is wrong. Use an interface bound or separate classes.

## 7. Interview angle

1. **How can a class implement a generic interface?** It can stay generic (`OrderedPair<K, V> implements Pair<K, V>`), fix all the type arguments (`StockLine implements Pair<String, Integer>`), or fix some (`Labelled<V> implements Pair<String, V>`).
2. **What does `Enum<E extends Enum<E>>` mean, and why is it declared that way?** Each enum `X` extends `Enum<X>`. Methods like `compareTo(E)` and `getDeclaringClass()` then work with the exact enum type, so you can only compare an enum with constants of the same enum.
3. **How would you design a generic repository?** `interface Repository<T, ID>` with `save(T)`, `Optional<T> findById(ID)` and `List<T> findAll()`. Implementations bound `T` to something that exposes its id, e.g. `T extends Entity<ID>`.
4. **Why `Function<? super T, ? extends R>` instead of `Function<T, R>` in a `map` method?** PECS: the function consumes `T`s and produces `R`s, so it should accept a function over a supertype of `T`, or one returning a subtype of `R`. This is how the JDK's `Optional.map` and `Stream.map` are declared.
5. **What problem does a self-typed builder `Builder<B extends Builder<B>>` solve?** Inherited setter methods return `B`, the concrete builder subclass, instead of the base type. A chain can then mix base-class and subclass methods.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_Converters.java` | Implement a generic `Converter<S, T>` interface three ways, plus an `andThen` default method that chains converters | Easy |
| 2 | `exercises/Exercise2_InMemoryRepository.java` | Build `InMemoryRepository<T extends Entity<ID>, ID>` and use one class to store customers (String ids) and orders (Long ids) | Medium |
| 3 | `exercises/Exercise3_CheckoutResult.java` | Build a sealed `Result<T>` with `map`, `flatMap` and `getOrElse`, then use it to write a checkout pipeline with no exceptions and no nulls | Hard |
