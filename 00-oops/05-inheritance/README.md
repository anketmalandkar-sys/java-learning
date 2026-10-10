# Inheritance and Polymorphism

Source: https://dev.java/learn/language/oop/inheritance/what-is-inheritance
Source: https://dev.java/learn/language/oop/inheritance/overriding
Source: https://dev.java/learn/language/oop/inheritance/polymorphism

← Back to [Object-Oriented Programming](../README.md) · Next in dev.java's Inheritance section: [06 Object as a superclass](../06-object-superclass/README.md) and abstract classes, which are taught with interfaces in [07](../07-interfaces/README.md).

Read the notes below, then run each demo. The demos use packages (`oops.inheritance`, `oops.polymorphism`); see [How to run](#how-to-run).

## 1. Inheritance: IS-A and code reuse

**Definition:** A class (**subclass/child**) acquires the fields and methods of another (**superclass/parent**) using `extends`. The child can **add** members, **override** methods, and call the parent's version with `super`.

**Test:** "Is a Dog an Animal?" Yes, so inheritance fits. "Is a Car an Engine?" No, so use composition (see [09](../09-relationships-and-design/README.md)).

| Type | Example | Java support |
|---|---|---|
| Single | `Dog extends Animal` | ✔ |
| Multilevel | `Puppy extends Dog extends Animal` | ✔ |
| Hierarchical | `Dog`, `Cat` both extend `Animal` | ✔ |
| Multiple | `C extends A, B` | ✘ for classes (diamond problem); ✔ via interfaces |
| Hybrid | mix of the above | only via interfaces |

**Key rules**
- Constructors are **not** inherited. The parent constructor always runs **first**, through an explicit or implicit `super(...)`.
- `private` members exist in the child object but are not accessible from the child's code.
- A `final` method can't be overridden. A `final` class can't be extended (e.g. `String`).
- Every class ultimately extends `java.lang.Object`.

**The diamond problem:** if `B` and `C` both extend `A` and override `run()`, and `D extends B, C`, which `run()` does `D` get? Java avoids this by allowing only one parent class. Interfaces may cause a similar clash with `default` methods, and Java makes you resolve it explicitly (see the abstraction demo in [07](../07-interfaces/README.md)).

Demo: [`inheritance/InheritanceDemo.java`](inheritance/InheritanceDemo.java)

## 2. Polymorphism: one interface, many forms

| | Compile-time (static) | Runtime (dynamic) |
|---|---|---|
| Mechanism | **Overloading** | **Overriding** |
| Where | Same class | Parent ↔ child |
| Signature | Same name, **different parameters** | Same name **and** parameters |
| Decided by | Compiler, from argument types | JVM, from the **actual object** type |
| Also called | Early binding | Late binding / dynamic dispatch |

```java
Shape s = new Circle(1);   // reference type Shape, object type Circle
s.area();                  // runs Circle.area(), decided at RUNTIME
```

**Overriding rules**
- Same name and parameter list. Return type is the same or a subtype (*covariant return*).
- Access can't be narrowed (`public` can't become `protected`).
- Can't throw new or broader **checked** exceptions.
- `static`, `private`, and `final` methods are **not** overridden. A static method with the same signature *hides* the parent's, and which one runs depends on the reference type.
- Return type alone is **not** enough for overloading.

**Casting**
- **Upcasting** (`Shape s = circle;`) is implicit and always safe.
- **Downcasting** (`(Rectangle) s`) is explicit and may throw `ClassCastException`. Check with `instanceof` first: `if (s instanceof Rectangle r) { ... }`.

**Why it matters:** code written against `Shape` works for every current and future shape without changes (the Open/Closed Principle).

Demo: [`polymorphism/PolymorphismDemo.java`](polymorphism/PolymorphismDemo.java)

## 3. Demos

| # | File | What it teaches |
|---|---|---|
| 1 | [inheritance/InheritanceDemo.java](inheritance/InheritanceDemo.java) | `extends`, `super`, constructor order, multilevel and hierarchical inheritance, `protected`, `final` |
| 2 | [polymorphism/PolymorphismDemo.java](polymorphism/PolymorphismDemo.java) | Overloading vs overriding, dynamic dispatch, up/down-casting, `instanceof` patterns, static-method hiding |
| 3 | [inheritance/OverridingRules.java](inheritance/OverridingRules.java) | Widening access, instance/static mismatch, overloading in a subclass, field hiding, overridable calls in constructors, default-method resolution, static interface methods, inherited nested classes, inherited methods implementing interfaces |

Demo 3 was added from dev.java's Inheritance pages, to cover what the first two didn't.

## 4. Interview quick-fire

1. **Can we override a static method?** No. It is *hidden*, and which one runs depends on the reference type.
2. **Can we override a private method?** No. It isn't visible to the subclass, so a method with the same name is a brand-new method.
3. **Why doesn't Java support multiple inheritance of classes?** To avoid the diamond problem. Use interfaces instead.
4. **Overloading vs overriding?** Overloading has the same name with different parameters and is resolved at compile time. Overriding has the same signature in a subclass and is resolved at runtime.
5. **What is dynamic dispatch?** The JVM picks the overridden method from the object's actual runtime type, not the reference type.

## 5. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | [exercises/Exercise1_OverridingTraps.java](exercises/Exercise1_OverridingTraps.java) | An overridable call in a constructor, field hiding, and default-method resolution | Hard |

The exercise prints PASS/FAIL.

## How to run

**IntelliJ:** click the green ▶ next to `main`. This folder is the source root with package prefix `oops`.

**Command line**, from the repo root:

```bash
javac -d out $(find 00-oops/05-inheritance -name '*.java')
java -cp out oops.polymorphism.PolymorphismDemo
java -cp out oops.exercises.Exercise1_OverridingTraps
```

```powershell
javac -d out (Get-ChildItem -Recurse 00-oops/05-inheritance -Filter *.java).FullName
java -cp out oops.inheritance.InheritanceDemo
```
