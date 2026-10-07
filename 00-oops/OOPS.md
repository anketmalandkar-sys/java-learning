# Object-Oriented Programming in Java

Read this file first, then run the matching demo for each section. The code comments go into more detail.

---

## 1. What is OOP?

**Procedural programming** organizes a program around *functions* that act on shared data.
**Object-oriented programming** organizes it around *objects*: small units that bundle **data** with the **code that works on that data**.

| Term | Meaning | Analogy |
|---|---|---|
| **Class** | Blueprint that defines fields (data) and methods (behavior) | Architect's drawing of a house |
| **Object** | A concrete instance created with `new`, living on the heap | An actual house built from the drawing |

Every object has three things:

- **State**: the current values of its fields (`speed = 50`)
- **Behavior**: the methods you can call on it (`accelerate()`)
- **Identity**: its own place in memory. Two objects with equal state are still two objects (`==` vs `equals`).

**Why OOP?** It lets you model real-world things, reuse code, contain the impact of a change, and let large teams work on separate parts that only meet through clear contracts.

Demo: [`basics/ClassesAndObjects.java`](basics/ClassesAndObjects.java)

### Supporting building blocks

- **Constructor**: runs on `new`. It has the class name and no return type. It can be overloaded, and `this(...)` chains to another constructor. If you write no constructor at all, Java adds a default no-arg one.
- **`this`**: a reference to the current object. Use it to tell fields from parameters (`this.name = name`) and for constructor chaining.
- **`static`**: belongs to the class, not to any object. There is one copy shared by all objects, and it is reachable without an object. A static method has no `this`.
- **References**: a variable of a class type holds an *arrow* to the object. `Car b2 = b;` does **not** copy the object.
- **`equals` / `hashCode` / `toString`**: inherited from `Object`. If you override `equals`, you **must** override `hashCode`, or `HashMap`/`HashSet` will misbehave.

---

## 2. The Four Pillars

```
                         OOP
        ┌────────────┬────────┴────┬──────────────┐
  Encapsulation  Inheritance  Polymorphism   Abstraction
  (hide data)    (reuse, IS-A) (many forms)  (hide complexity)
```

### 2.1 Encapsulation: protect the data

**Definition:** Bundle data and the methods that operate on it into one unit, and **restrict direct access** to the data. The object is then the only one able to change its own state, so it can keep its **invariants** (rules that must always hold, such as "balance ≥ 0").

**Analogy:** An ATM. You can't open the cash box. You can only use the allowed operations (withdraw, deposit), and each one is validated.

**How in Java:**
1. `private` fields
2. Public methods that expose **behavior** (`deposit`, `withdraw`) with validation
3. Getters for read access. Return copies or unmodifiable views of mutable internals.
4. Go further with **immutability**: `final` fields and no setters, or a `record`

| Modifier | Same class | Same package | Subclass (other pkg) | Everywhere |
|---|:-:|:-:|:-:|:-:|
| `private` | ✔ | ✘ | ✘ | ✘ |
| *(default)* | ✔ | ✔ | ✘ | ✘ |
| `protected` | ✔ | ✔ | ✔ | ✘ |
| `public` | ✔ | ✔ | ✔ | ✔ |

**Pitfalls**
- Adding a getter **and setter** for every field is not encapsulation. `setBalance()` exposes the field as much as making it public would.
- Returning an internal `List` directly lets callers modify your state.

Demo: [`encapsulation/`](encapsulation/)

### 2.2 Inheritance: IS-A and code reuse

**Definition:** A class (**subclass/child**) acquires the fields and methods of another (**superclass/parent**) using `extends`. The child can **add** members, **override** methods, and call the parent's version with `super`.

**Test:** "Is a Dog an Animal?" Yes, so inheritance fits. "Is a Car an Engine?" No, so use composition.

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

**The diamond problem:** if `B` and `C` both extend `A` and override `run()`, and `D extends B, C`, which `run()` does `D` get? Java avoids this by allowing only one parent class. Interfaces may cause a similar clash with `default` methods, and Java makes you resolve it explicitly (see the abstraction demo).

Demo: [`inheritance/InheritanceDemo.java`](inheritance/InheritanceDemo.java)

### 2.3 Polymorphism: one interface, many forms

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

### 2.4 Abstraction: hide complexity, show essentials

**Definition:** Expose **what** an object does, and hide **how** it does it.
**Analogy:** You drive using a steering wheel and pedals without knowing how fuel injection works.

**Encapsulation vs abstraction** (a classic interview question):
- *Encapsulation* hides **data** and is implemented with access modifiers.
- *Abstraction* hides **implementation complexity** and is implemented with abstract classes and interfaces.
- They work together: abstraction defines the clean outside view, and encapsulation protects the inside.

| | Abstract class | Interface |
|---|---|---|
| Keyword | `abstract class` / `extends` | `interface` / `implements` |
| Instantiable? | No | No |
| Methods | Abstract **and** concrete | Abstract, `default`, `static`, `private` (Java 9+) |
| Fields | Any kind (state) | Only `public static final` constants |
| Constructors | Yes | No |
| Multiple? | Extend only **one** | Implement **many** |
| Relationship | "is-a" family sharing code/state | "can-do" capability |
| Use when | Related classes share common code | Unrelated classes share a contract |

**Sealed types** (Java 17+): `sealed interface PaymentMethod permits Card, Upi, Cash` limits which classes may implement it, so a `switch` over it can be exhaustive.

Demo: [`abstraction/AbstractionDemo.java`](abstraction/AbstractionDemo.java) (abstract class with the *template method* pattern, interface diamond resolution, sealed types)

---

## 3. Relationships Between Classes

Classes relate to each other in two basic ways:

| Relationship | Meaning | Java |
|---|---|---|
| **IS-A** | Inheritance / generalization | `extends`, `implements` |
| **HAS-A** | One object refers to another | A field holding a reference |

The HAS-A family is **Association**, and it has two stronger special cases: **Aggregation** and **Composition**.
(**Dependency** is even weaker than association: an object only *uses* another briefly, as a method parameter or local variable, without storing it.)

### 3.1 Association: "knows-a / uses-a"

Any relationship where objects of one class hold references to and work with objects of another. **Neither owns the other. Both have independent lifecycles.**

- **Direction:** uni-directional (`Doctor → Patient`) or bi-directional (`Teacher ↔ Student`). In a bi-directional link you must keep **both sides in sync**.
- **Multiplicity:** one-to-one (Person–Passport), one-to-many (Doctor–Patients), many-to-one (Employees–Company), many-to-many (Teachers–Students)
- There is **no whole/part meaning**. A teacher isn't "part of" a student.

Demo: [`relationships/AssociationDemo.java`](relationships/AssociationDemo.java)

### 3.2 Aggregation: weak "whole/part" (has-a, shared)

An association where one side is the **whole** and the other is a **part**, but **the part can exist independently** of the whole.

- Parts are **created outside** and **passed in** (constructor/setter parameter).
- A part may belong to **several wholes** at once.
- Destroying the whole does **not** destroy the parts.

Examples: Department ◇— Professor, Team ◇— Player, Playlist ◇— Song, Library ◇— Book.

```java
class Department {
    private final List<Professor> professors = new ArrayList<>();
    void addProfessor(Professor p) { professors.add(p); }   // passed in → aggregation
}
```

Demo: [`relationships/AggregationDemo.java`](relationships/AggregationDemo.java)

### 3.3 Composition: strong "whole/part" (owns-a, part-of)

An aggregation with **exclusive ownership** and a **shared lifecycle**.

- The whole **creates** its parts (`new` happens inside the whole).
- A part belongs to **exactly one** whole and is never shared or handed out.
- When the whole dies, its parts die with it. In Java this means they become unreachable together and are garbage-collected.

Examples: House ◆— Room, Car ◆— Engine, Human ◆— Heart, Order ◆— OrderLine, Book ◆— Chapter.

```java
class Car {
    private final Engine engine;
    Car(int hp) { this.engine = new Engine(hp); }   // created inside → composition
}
```

Demo: [`relationships/CompositionDemo.java`](relationships/CompositionDemo.java)

### 3.4 How they relate

They are **nested**. Every composition is an aggregation, and every aggregation is an association:

```
┌──────────────────────────────────────────────────────────────┐
│ ASSOCIATION   "A knows/uses B"   independent lifecycles      │
│   ┌──────────────────────────────────────────────────────┐   │
│   │ AGGREGATION   + whole/part,   parts can live alone   │   │
│   │   ┌──────────────────────────────────────────────┐   │   │
│   │   │ COMPOSITION  + exclusive ownership,          │   │   │
│   │   │               parts live & die with whole    │   │   │
│   │   └──────────────────────────────────────────────┘   │   │
│   └──────────────────────────────────────────────────────┘   │
└──────────────────────────────────────────────────────────────┘
        weaker coupling  ─────────────────▶  stronger coupling
```

**UML notation** (the diamond goes on the **whole** side):

```
Teacher ────────── Student        Association  (plain line, arrow if uni-directional)
Department ◇────── Professor      Aggregation  (hollow diamond)
House ◆─────────── Room           Composition  (filled diamond)
Dog ──────────────▷ Animal        Inheritance  (hollow triangle, IS-A)
Doctor - - - - - -> Prescription  Dependency   (dashed arrow)
```

| Aspect | Association | Aggregation | Composition |
|---|---|---|---|
| Meaning | knows / uses | has-a (whole/part) | owns / part-of |
| Whole/part? | No | Yes | Yes |
| Ownership | None | Weak | Strong, exclusive |
| Lifecycle | Independent | Independent: part survives whole | Dependent: part dies with whole |
| Part shareable? | n/a | Yes | No |
| Who creates the part? | Anyone | **Outside**, passed in | **The whole itself** (`new` inside) |
| Coupling | Lowest | Medium | Highest |
| Example | Teacher – Student | Department – Professor | House – Room |

**Quick test to decide:**
1. Is there a whole/part meaning? No → **association**.
2. Can the part exist, or be shared, without this whole? Yes → **aggregation**.
3. Otherwise → **composition**.

> The same real-world pair can be modelled differently depending on the domain. In a car-rental app an `Engine` may be a swappable part (aggregation). In a simple game it's just internal to the `Car` (composition). Choose based on **who controls the part's lifecycle in your code**.

### 3.5 Favor composition over inheritance

Inheritance gives the child the parent's **entire** public API, permanently, and ties the child to the parent's implementation details (the *fragile base class* problem). Composition lets you **reuse** behavior by holding the object privately and delegating to it, exposing only what you choose.

- `class Stack extends ArrayList` leaks `add(0, x)` and `remove(i)`, which breaks LIFO.
- `class Stack { private final List items; ... }` exposes only `push`, `pop`, and `peek`.

Use inheritance only for a true IS-A where the child can stand in anywhere the parent is expected (Liskov Substitution Principle).

Demo: [`relationships/CompositionOverInheritance.java`](relationships/CompositionOverInheritance.java)

---

## 4. Good Design: Coupling, Cohesion & SOLID

- **Cohesion** (aim high): a class does one focused job.
- **Coupling** (aim low): classes depend on each other as little as possible, preferably on interfaces.

| SOLID principle | One-liner | Pillar it builds on |
|---|---|---|
| **S**ingle Responsibility | A class should have one reason to change | Encapsulation / cohesion |
| **O**pen/Closed | Open for extension, closed for modification | Polymorphism |
| **L**iskov Substitution | Subtypes must be usable wherever the parent is | Inheritance |
| **I**nterface Segregation | Many small interfaces beat one fat one | Abstraction |
| **D**ependency Inversion | Depend on abstractions, not concrete classes | Abstraction + composition |

---

## 5. Interview Quick-Fire

1. **Can we override a static method?** No. It is *hidden*, and which one runs depends on the reference type.
2. **Can we override a private method?** No. It isn't visible to the subclass, so a method with the same name is a brand-new method.
3. **Why doesn't Java support multiple inheritance of classes?** To avoid the diamond problem. Use interfaces instead.
4. **Can an abstract class have a constructor?** Yes. It runs when a subclass is constructed.
5. **Can an interface have a method body?** Yes, as `default`, `static`, or `private` methods (Java 8/9+).
6. **Overloading vs overriding?** Overloading has the same name with different parameters and is resolved at compile time. Overriding has the same signature in a subclass and is resolved at runtime.
7. **Aggregation vs composition?** It's about lifecycle and ownership. Aggregated parts are passed in and survive the whole. Composed parts are created by the whole and die with it.
8. **Is every aggregation an association?** Yes. Composition ⊂ Aggregation ⊂ Association.
9. **`==` vs `equals()`?** `==` compares identity (same object). `equals()` compares logical state, as the class defines it.
10. **What is dynamic dispatch?** The JVM picks the overridden method from the object's actual runtime type, not the reference type.
