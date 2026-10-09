# Generics

## 1. Big picture

Generics let you write a class or method once and use it safely with many types: `List<String>`, `Map<Integer, Order>`, `Optional<Employee>`. The type goes in angle brackets, so the compiler knows what's inside and catches mistakes **at compile time** instead of with a `ClassCastException` at runtime.

The topic is bigger than it first looks. Writing `List<String>` is easy. Bounds, wildcards (`? extends`, `? super`) and type erasure are where the real questions (and bugs) are, so this topic is split into lessons you take one at a time.

```
Generics
├── type parameters        class Box<T>, <T> T first(List<T>)
├── type inference         how the compiler works out T: diamond, target types, type witness
├── bounds                 <T extends Comparable<T>>
├── wildcards              List<?>, List<? extends Number>, List<? super Integer>
└── type erasure           what the JVM actually sees at runtime
```

## 2. Roadmap

| # | Subtopic | What you'll learn | Status |
|---|----------|-------------------|--------|
| 01 | [Generics basics](01-generics-basics/) | Why generics exist, type parameter vs type argument, generic classes, methods and constructors, the diamond `<>`, raw types and unchecked warnings, subtyping with generic types, no primitives | ✅ |
| 02 | [Type inference and target types](02-type-inference/) | Explicit type witness `Collections.<String>emptyList()`, inference from the assignment target, method arguments as target types (Java 8+), inference picking the most specific common type, all the places a lambda gets its target type (return, `?:`, cast, array initializer), overload resolution between `Runnable` and `Callable` | ✅ |
| 03 | [Bounded type parameters](03-bounded-types/) | `<T extends Number>`, `<T extends Comparable<T>>`, multiple bounds `<T extends A & B>` (class bound first), calling the bound's methods on `T` | 📖 |
| 04 | [Wildcards and PECS](04-wildcards/) | `?`, `? extends T`, `? super T`, why `List<Integer>` isn't a `List<Number>`, `List<?>` as their common parent, `List<?>` vs `List<Object>`, "Producer Extends, Consumer Super", why `List<? extends T>` is only mostly read-only, no wildcards in return types, wildcard capture (`CAP#1` errors) and the helper-method fix | 📖 |
| 05 | [Type erasure and restrictions](05-type-erasure/) | Erasure to the first bound (or `Object`), bridge methods, reifiable vs non-reifiable types, heap pollution and generic varargs, `@SafeVarargs`. Restrictions: no `new T()`, no `instanceof List<String>`, no `static T` fields, no generic arrays (`new List<String>[10]`), no generic `Throwable` subclasses or `catch (T e)`, no overloads that erase to the same signature | 📖 |
| 06 | [Generics in practice](06-generics-in-practice/) | Generic interfaces and the three ways to implement one, designing a `Repository<T, ID>` and a sealed `Result<T>` API, recursive bounds like `Enum<E extends Enum<E>>` and self-typed builders | 📖 |

All lessons are written. Work through them in order, and mark each one ✅ once its exercises have been reviewed.

## 3. How the pieces relate

- **Writing your own container or helper?** Start with **01**: a type parameter `<T>` is all you need.
- **The compiler can't work out `T`**, e.g. `Collections.emptyList()` gives a `List<Object>` where you wanted `List<String>`? That's **type inference** (**02**).
- **The helper needs to call methods on `T`** (compare it, add it up)? That needs a **bound** (**03**): `<T extends Comparable<T>>`.
- **A method should accept "a list of any kind of number"?** That needs a **wildcard** (**04**): `List<? extends Number>`.
- **Confused by a compiler error like "cannot create a generic array" or an "unchecked" warning?** That's **erasure** (**05**).
- **Designing an API others will use** (a repository, a result type, a fluent builder)? Put it all together in **06**.
