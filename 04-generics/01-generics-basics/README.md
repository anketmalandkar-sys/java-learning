[← Generics roadmap](../README.md)

# Generics basics

## 1. What it is

A **type parameter** is a placeholder for a type, written in angle brackets: `class Box<T>`. When you use the class, you fill the placeholder in: `Box<String>`, `Box<Order>`. From then on the compiler treats every `T` in that box as `String` (or `Order`), so you get type checks and no casts.

Two words that get mixed up:
- **Type parameter**: the placeholder in the declaration. In `class Box<T>`, `T` is the type parameter.
- **Type argument**: the real type you supply when you use it. In `Box<String>`, `String` is the type argument, and `Box<String>` as a whole is a **parameterized type**.

Methods can have their own type parameters too: `static <T> T lastOf(List<T> items)`.

## 2. Why it exists

Before Java 5, collections held `Object`. Every read needed a cast, and nothing stopped you putting the wrong thing in:

```java
List names = new ArrayList();        // raw: holds Object
names.add("Ada");
names.add(42);                       // compiles fine
String first = (String) names.get(0);
String second = (String) names.get(1);   // ClassCastException, at runtime
```

With generics, the bug can't even compile:

```java
List<String> names = new ArrayList<>();
names.add("Ada");
names.add(42);                       // compile error: int is not a String
String first = names.get(0);         // no cast
```

Generics move the error from *runtime, in production* to *compile time, in your IDE*.

## 3. Core concepts

**Generic class.** Declare the parameter after the class name and use it like a type:

```java
class Box<T> {
    private T value;
    void set(T value) { this.value = value; }
    T get() { return value; }
}

Box<String> box = new Box<>();
box.set("hello");
String s = box.get();                // no cast
```

**Several parameters.** Separate them with commas. Conventions: `T` type, `E` element, `K`/`V` key/value, `R` result.

```java
class Pair<K, V> { ... }
Pair<String, Integer> stock = new Pair<>("Pens", 120);
```

**Generic method.** The `<T>` goes *before the return type*. It belongs to the method, not the class, so it also works in `static` methods:

```java
static <T> T lastOf(List<T> items) {
    return items.get(items.size() - 1);
}

String city = lastOf(List.of("Pune", "Goa"));   // T inferred as String
```

**Generic constructor.** A constructor can declare its own type parameter too, placed before the class name. It's rare, but you'll see it in library code. The class here isn't generic at all:

```java
class Label {
    private final String text;
    <T> Label(T value) { this.text = "[" + value + "]"; }
}

new Label(42);       // T inferred as Integer
new Label("Ada");    // T inferred as String
```

A class can also have its own `<T>` and a constructor with a different `<U>`: `class Box<T> { <U> Box(U seed) { ... } }`.

**Diamond `<>` and inference.** You write the type once; the compiler fills it in on the right-hand side and for method calls (Java 7+):

```java
Map<String, List<Integer>> scores = new HashMap<>();   // not new HashMap<String, List<Integer>>()
```

**Primitives aren't allowed.** Use the wrapper class. Autoboxing converts for you:

```java
List<int> a;            // compile error
List<Integer> b = new ArrayList<>();
b.add(5);               // int 5 is boxed to Integer
```

**Generic types and subtyping.** Normal subtyping still works as long as the type argument stays the same. `ArrayList` implements `List`, which extends `Collection`, so:

```java
ArrayList<String>  →  List<String>  →  Collection<String>    // each is a subtype of the next

List<String> names = new ArrayList<String>();       // fine
Collection<String> all = names;                     // fine
```

What breaks it is changing the **type argument**. `Integer` is a `Number`, but `List<Integer>` is not a `List<Number>` (see section 5). The rule: the class part (`ArrayList` → `List`) can vary, the part in `<...>` can't.

## 4. How it works under the hood

The type parameter exists **only for the compiler**. After checking your code, the compiler erases `T` to `Object` and inserts the casts you'd have written yourself. At runtime, a `Box<String>` and a `Box<Integer>` are the same class (`Box`).

Two things follow from that, enough for now (lesson 05 goes deeper):
- Generics cost nothing at runtime: there's no extra class per type.
- Anything that needs the type *at runtime* doesn't work: `new T()`, `T.class`, `instanceof List<String>`.

## 5. Common mistakes and gotchas

**Using raw types.** Leaving off `<...>` turns all checks off. The compiler only warns ("unchecked or unsafe operations").

```java
List orders = new ArrayList();             // wrong: raw
List<Order> orders = new ArrayList<>();    // right
```

By default `javac` only prints a one-line summary ("Note: ... uses unchecked or unsafe operations"). Compile with `javac -Xlint:unchecked` to see each line that caused it. If you've checked a line and it really is safe (usually code talking to an old raw-type API), you can silence it with `@SuppressWarnings("unchecked")`. Put it on the smallest scope you can, a single variable or method, never a whole class, so it can't hide new problems:

```java
@SuppressWarnings("unchecked")   // legacyApi returns a raw List, but it only ever holds Strings
List<String> names = (List<String>) legacyApi.getNames();
```

**Shadowing the class's `T` with a method's `<T>`.** Here the method declares a *new* `T` that hides the class's `T`, so `save` accepts anything:

```java
class Repository<T> {
    <T> void save(T item) { ... }   // wrong: this T is unrelated to Repository's T
    void save(T item) { ... }       // right: uses the class's T
}
```

Only put `<T>` on a method when the method needs its *own* type parameter.

**Expecting `List<Integer>` to be a `List<Number>`.** It isn't, even though `Integer` is a `Number`. Otherwise you could add a `Double` to a list of integers through the `List<Number>` reference. Wildcards (lesson 04) fix this.

```java
List<Number> nums = new ArrayList<Integer>();   // compile error
```

**Comparing boxed values with `==`.** `List<Integer>` holds `Integer` objects. Use `equals`:

```java
list.get(0) == list.get(1)          // wrong: compares references (breaks above 127)
list.get(0).equals(list.get(1))     // right
```

## 6. When to use / when not to

**Use generics when:**
- A class holds or passes through a value whose type doesn't matter to it: containers, caches, `Page<T>`, `Result<T>`, `Pair<A, B>`.
- A helper method works the same for any type: `lastOf`, `findFirst`, `repeat`.
- You'd otherwise write `Object` and cast.

**Don't when:**
- The code only ever deals with one type. `class OrderService<T>` with `T` always `Order` is just noise.
- You need to call specific methods on `T`. A plain `<T>` only offers `Object`'s methods; you need a bound (lesson 03) or a concrete type.

## 7. Interview angle

1. **Why were generics added?** Compile-time type safety and no casts. Errors that used to be runtime `ClassCastException`s become compile errors.
2. **Can you write `List<int>`?** No. Type arguments must be reference types; use `List<Integer>` and rely on autoboxing.
3. **What's the difference between a generic class and a generic method?** A generic class's parameter is fixed when you create the object (`new Box<String>()`). A generic method's parameter is chosen for each call, usually inferred from the arguments, and works in static methods.
4. **What is a raw type and why avoid it?** A generic type used without `<...>`, like `List`. It exists for pre-Java-5 compatibility and disables type checks, giving only an "unchecked" warning.
5. **Is a `List<String>` a different class from a `List<Integer>` at runtime?** No. Because of type erasure both are just `ArrayList` (or whatever) at runtime; `list1.getClass() == list2.getClass()` is `true`.
6. **What's the difference between a type parameter and a type argument?** The parameter is the placeholder in the declaration (`T` in `class Box<T>`). The argument is the real type you fill in when you use it (`String` in `Box<String>`).
7. **Is `ArrayList<String>` a subtype of `List<String>`? Is `List<Integer>` a subtype of `List<Number>`?** Yes to the first: the class part can change as long as the type argument stays the same. No to the second: a changed type argument breaks subtyping, otherwise you could add a `Double` to a list of `Integer`s.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_PairAndHelpers.java` | Finish a generic `Pair<A, B>` with a `swap()`, and three generic helper methods | Easy |
| 2 | `exercises/Exercise2_UndoHistory.java` | Build a generic `History<T>` with undo, plus a `map` that turns a `History<T>` into a `History<R>` | Medium |
| 3 | `exercises/Exercise3_ShipmentRepoBug.java` | A repository crashes with `ClassCastException`. Find the three generics mistakes and make the compiler catch the bad call | Hard |
