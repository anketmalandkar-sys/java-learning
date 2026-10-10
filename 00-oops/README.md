# Object-Oriented Programming

Source: https://dev.java/learn/language/oop

## 1. Big picture

Object-oriented programming organizes a program around **objects**: units that bundle data with the code that works on it. Java's design rests on four pillars:

```
                         OOP
        ┌────────────┬────────┴────┬──────────────┐
  Encapsulation  Inheritance  Polymorphism   Abstraction
  (hide data)    (reuse, IS-A) (many forms)  (hide complexity)
     → 01           → 05          → 05           → 07
```

The parts below follow the dev.java "Object Oriented Programming" tutorial in its order (Classes and Objects → Records → Inheritance → Interfaces → Packages), plus a final part on relationships and design that dev.java doesn't cover.

```
Object Oriented Programming
├── Classes and Objects ─┬─ classes, methods, constructors, objects   → 01
│                        ├─ nested, local, anonymous classes         → 02
│                        └─ enums                                    → 03
├── Records                                                          → 04
├── Inheritance ─────────┬─ inheritance, overriding, polymorphism    → 05
│                        ├─ Object as a superclass                   → 06
│                        └─ abstract classes                         → 07 (with interfaces)
├── Interfaces                                                       → 07  (+ 02-comparator for Comparator)
├── Packages                                                         → 08
└── (extra) relationships, composition, SOLID                        → 09
```

## 2. Roadmap

| # | Part | What you'll learn | dev.java pages | Status |
|---|------|-------------------|----------------|--------|
| 01 | [Classes and objects](01-classes-objects/) | Class vs object, constructors, `this`, `static`, encapsulation and access modifiers, varargs, pass-by-value, initialization order | [overview](https://dev.java/learn/language/oop/classes), [creating-classes](https://dev.java/learn/language/oop/classes-objects/creating-classes), [defining-methods](https://dev.java/learn/language/oop/classes-objects/defining-methods), [defining-constructors](https://dev.java/learn/language/oop/classes-objects/defining-constructors), [calling-methods-constructors](https://dev.java/learn/language/oop/classes-objects/calling-methods-constructors), [creating-objects](https://dev.java/learn/language/oop/classes-objects/creating-objects), [more-on-classes](https://dev.java/learn/language/oop/classes-objects/more-on-classes) | 📖 |
| 02 | [Nested classes](02-nested-classes/) | Static nested vs inner classes, local and anonymous classes, shadowing, and when to use each vs a lambda | [nested-classes](https://dev.java/learn/language/oop/classes-objects/nested-classes), [design-best-practices](https://dev.java/learn/language/oop/classes-objects/design-best-practices) | 📖 |
| 03 | [Enums](03-enums/) | Constants, fields and constructors, `values`/`valueOf`/`ordinal`, switch, constant-specific methods, `EnumMap`/`EnumSet` | [enums](https://dev.java/learn/language/oop/classes-objects/enums) | 📖 |
| 04 | [Records](04-records/) | Components and accessors, compact vs canonical constructors, restrictions, defensive copies, local records | [records](https://dev.java/learn/language/oop/records) | 📖 |
| 05 | [Inheritance and polymorphism](05-inheritance/) | `extends`, `super`, constructor order, overriding vs overloading, dynamic dispatch, casting, hiding, overriding rules | [what-is-inheritance](https://dev.java/learn/language/oop/inheritance/what-is-inheritance), [overriding](https://dev.java/learn/language/oop/inheritance/overriding), [polymorphism](https://dev.java/learn/language/oop/inheritance/polymorphism) | 📖 |
| 06 | [Object as a superclass](06-object-superclass/) | `toString`, the `equals`/`hashCode` contract, `getClass`, `clone` and its traps, why not `finalize` | [objects](https://dev.java/learn/language/oop/inheritance/objects) | 📖 |
| 07 | [Interfaces and abstract classes](07-interfaces/) | Abstract class vs interface, default/static/private methods, the default diamond, sealed types, evolving an interface, interface as a type | [abstract-classes](https://dev.java/learn/language/oop/inheritance/abstract-classes), [defining-interfaces](https://dev.java/learn/language/oop/interfaces/defining-interfaces), [examples](https://dev.java/learn/language/oop/interfaces/examples), [interfaces-as-a-type](https://dev.java/learn/language/oop/interfaces/interfaces-as-a-type) | 📖 |
| 08 | [Packages](08-packages/) | Package statements and naming, imports, wildcards, ambiguity, static imports, folders and the classpath | [packages](https://dev.java/learn/language/oop/packages) | 📖 |
| 09 | [Relationships and design](09-relationships-and-design/) | Association, aggregation, composition, composition over inheritance, coupling, cohesion, SOLID | (not in dev.java) | 📖 |

Index pages on dev.java (lists of lessons, nothing to teach): [classes-objects](https://dev.java/learn/language/oop/classes-objects), [inheritance](https://dev.java/learn/language/oop/inheritance), [interfaces](https://dev.java/learn/language/oop/interfaces).

Mark each part ✅ once its exercises have been reviewed.

## 3. How the pieces relate

- **New to OOP?** Start at **01**: classes, objects, and encapsulation.
- **A helper type used by only one class?** A nested class (**02**): `static` unless it needs the outer object.
- **A fixed set of values (days, statuses, plans)?** An enum (**03**).
- **A plain immutable data carrier (a point, a DTO, a map key)?** A record (**04**). It gets `equals`/`hashCode`/`toString` for free; a normal class needs them written by hand (**06**).
- **Reusing code from a parent class, or writing code that works for many subtypes?** Inheritance and polymorphism (**05**). Unrelated classes sharing a capability? An interface (**07**).
- **Growing past a handful of files?** Packages (**08**) group them and control what's visible.
- **Should this be inheritance or a field?** Relationships and composition (**09**).

## 4. Two kinds of parts

- **01, 05, 07, 09** are demo modules in packages (`oops.basics`, `oops.inheritance`, `oops.abstraction`, `oops.relationships`, and `oops.exercises`). In IntelliJ press ▶ next to any `main`. From the repo root, compile one part at a time, e.g.:
  ```bash
  javac -d out $(find 00-oops/05-inheritance -name '*.java')
  java -cp out oops.polymorphism.PolymorphismDemo
  ```
- **02, 03, 04, 06, 08** are lessons like the rest of the repo: from the lesson folder, `java examples/Example1_Basics.java` or `java exercises/Exercise1_*.java`. **08** also has packaged examples; see its README.
