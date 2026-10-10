# Relationships Between Classes, and Good Design

← Back to [Object-Oriented Programming](../README.md)

This part isn't in the dev.java tutorial. It covers how classes relate to each other (association, aggregation, composition) and the design principles built on the earlier parts. The demos use the package `oops.relationships`; see [How to run](#how-to-run).

## 1. Relationships between classes

Classes relate to each other in two basic ways:

| Relationship | Meaning | Java |
|---|---|---|
| **IS-A** | Inheritance / generalization | `extends`, `implements` |
| **HAS-A** | One object refers to another | A field holding a reference |

The HAS-A family is **Association**, and it has two stronger special cases: **Aggregation** and **Composition**.
(**Dependency** is even weaker than association: an object only *uses* another briefly, as a method parameter or local variable, without storing it.)

### 1.1 Association: "knows-a / uses-a"

Any relationship where objects of one class hold references to and work with objects of another. **Neither owns the other. Both have independent lifecycles.**

- **Direction:** uni-directional (`Doctor → Patient`) or bi-directional (`Teacher ↔ Student`). In a bi-directional link you must keep **both sides in sync**.
- **Multiplicity:** one-to-one (Person–Passport), one-to-many (Doctor–Patients), many-to-one (Employees–Company), many-to-many (Teachers–Students)
- There is **no whole/part meaning**. A teacher isn't "part of" a student.

Demo: [`relationships/AssociationDemo.java`](relationships/AssociationDemo.java)

### 1.2 Aggregation: weak "whole/part" (has-a, shared)

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

### 1.3 Composition: strong "whole/part" (owns-a, part-of)

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

### 1.4 How they relate

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

### 1.5 Favor composition over inheritance

Inheritance gives the child the parent's **entire** public API, permanently, and ties the child to the parent's implementation details (the *fragile base class* problem). Composition lets you **reuse** behavior by holding the object privately and delegating to it, exposing only what you choose.

- `class Stack extends ArrayList` leaks `add(0, x)` and `remove(i)`, which breaks LIFO.
- `class Stack { private final List items; ... }` exposes only `push`, `pop`, and `peek`.

Use inheritance only for a true IS-A where the child can stand in anywhere the parent is expected (Liskov Substitution Principle).

Demo: [`relationships/CompositionOverInheritance.java`](relationships/CompositionOverInheritance.java)

## 2. Good design: coupling, cohesion and SOLID

- **Cohesion** (aim high): a class does one focused job.
- **Coupling** (aim low): classes depend on each other as little as possible, preferably on interfaces.

| SOLID principle | One-liner | Pillar it builds on |
|---|---|---|
| **S**ingle Responsibility | A class should have one reason to change | Encapsulation / cohesion |
| **O**pen/Closed | Open for extension, closed for modification | Polymorphism |
| **L**iskov Substitution | Subtypes must be usable wherever the parent is | Inheritance |
| **I**nterface Segregation | Many small interfaces beat one fat one | Abstraction |
| **D**ependency Inversion | Depend on abstractions, not concrete classes | Abstraction + composition |

## 3. Demos

| # | File | What it teaches |
|---|---|---|
| 1 | [relationships/AssociationDemo.java](relationships/AssociationDemo.java) | Uni/bi-directional, one-to-many, many-to-many, dependency |
| 2 | [relationships/AggregationDemo.java](relationships/AggregationDemo.java) | Weak whole/part: parts are passed in, shared, and outlive the whole |
| 3 | [relationships/CompositionDemo.java](relationships/CompositionDemo.java) | Strong whole/part: parts are created inside and die with the whole |
| 4 | [relationships/CompositionOverInheritance.java](relationships/CompositionOverInheritance.java) | Why `Stack extends ArrayList` is bad, and delegation as the fix |

This part has no exercises yet.

## 4. Interview quick-fire

1. **Aggregation vs composition?** It's about lifecycle and ownership. Aggregated parts are passed in and survive the whole. Composed parts are created by the whole and die with it.
2. **Is every aggregation an association?** Yes. Composition ⊂ Aggregation ⊂ Association.

## How to run

**IntelliJ:** click the green ▶ next to `main`. This folder is the source root with package prefix `oops`.

**Command line**, from the repo root:

```bash
javac -d out $(find 00-oops/09-relationships-and-design -name '*.java')
java -cp out oops.relationships.CompositionDemo
```

```powershell
javac -d out (Get-ChildItem -Recurse 00-oops/09-relationships-and-design -Filter *.java).FullName
java -cp out oops.relationships.AssociationDemo
```
