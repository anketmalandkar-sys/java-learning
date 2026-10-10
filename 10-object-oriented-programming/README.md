# Object Oriented Programming (dev.java)

Source: https://dev.java/learn/language/oop

## 1. Big picture

This section follows the dev.java "Object Oriented Programming" tutorial. Most of its core ideas (classes, constructors, inheritance, polymorphism, abstract classes, interfaces) were already in [00-oops](../00-oops/OOPS.md). So this folder holds only the topics that were **new**, and the missing details of the overlapping pages were added to `00-oops` as extra demos and exercises.

```
Object Oriented Programming
├── Classes and Objects ─┬─ basics, methods, constructors, objects  → 00-oops (+ gap demos)
│                        ├─ nested, local, anonymous classes        → 01 here
│                        └─ enums                                   → 02 here
├── Records                                                         → 03 here
├── Inheritance ─────────┬─ inheritance, overriding, polymorphism   → 00-oops (+ gap demo)
│                        ├─ Object as a superclass                  → 04 here
│                        └─ abstract classes                        → 00-oops (+ gap demo)
├── Interfaces                                                      → 00-oops (+ gap demo), 02-comparator
└── Packages                                                        → 05 here
```

## 2. Roadmap

New lessons in this folder:

| # | Subtopic | What you'll learn | Source | Status |
|---|----------|-------------------|--------|--------|
| 01 | [Nested classes](01-nested-classes/) | Static nested vs inner classes, local and anonymous classes, shadowing, and when to use each vs a lambda | [nested-classes](https://dev.java/learn/language/oop/classes-objects/nested-classes), [design-best-practices](https://dev.java/learn/language/oop/classes-objects/design-best-practices) | 📖 |
| 02 | [Enums](02-enums/) | Constants, fields and constructors, `values`/`valueOf`/`ordinal`, switch, constant-specific methods, `EnumMap`/`EnumSet` | [enums](https://dev.java/learn/language/oop/classes-objects/enums) | 📖 |
| 03 | [Records](03-records/) | Components and accessors, compact vs canonical constructors, restrictions, defensive copies, local records | [records](https://dev.java/learn/language/oop/records) | 📖 |
| 04 | [Object as a superclass](04-object-superclass/) | `toString`, the `equals`/`hashCode` contract, `getClass`, `clone` and its traps, why not `finalize` | [objects](https://dev.java/learn/language/oop/inheritance/objects) | 📖 |
| 05 | [Packages](05-packages/) | Package statements and naming, imports, wildcards, ambiguity, static imports, folders and the classpath | [packages](https://dev.java/learn/language/oop/packages) | 📖 |

Pages already taught in `00-oops`, with the missing parts added there:

| Page | Taught in | Gap filled with | Source |
|---|---|---|---|
| Providing Constructors, Calling Methods and Constructors, Creating and Using Objects | [basics/ClassesAndObjects.java](../00-oops/basics/ClassesAndObjects.java) | [basics/ParametersAndObjects.java](../00-oops/basics/ParametersAndObjects.java) + exercise 1 | [defining-constructors](https://dev.java/learn/language/oop/classes-objects/defining-constructors), [calling-methods-constructors](https://dev.java/learn/language/oop/classes-objects/calling-methods-constructors), [creating-objects](https://dev.java/learn/language/oop/classes-objects/creating-objects) |
| More on Classes | `basics/`, `encapsulation/` | [basics/InitializationOrder.java](../00-oops/basics/InitializationOrder.java) + exercise 2 | [more-on-classes](https://dev.java/learn/language/oop/classes-objects/more-on-classes) |
| Inheritance, Overriding and Hiding, Polymorphism | `inheritance/`, `polymorphism/` | [inheritance/OverridingRules.java](../00-oops/inheritance/OverridingRules.java) + exercise 3 | [what-is-inheritance](https://dev.java/learn/language/oop/inheritance/what-is-inheritance), [overriding](https://dev.java/learn/language/oop/inheritance/overriding), [polymorphism](https://dev.java/learn/language/oop/inheritance/polymorphism) |
| Abstract Classes, Defining Interfaces, Implementing an Interface | `abstraction/` (+ [02-comparator](../02-comparator/README.md) for `Comparator`) | [abstraction/InterfaceAndAbstractRules.java](../00-oops/abstraction/InterfaceAndAbstractRules.java) + exercise 4 | [abstract-classes](https://dev.java/learn/language/oop/inheritance/abstract-classes), [defining-interfaces](https://dev.java/learn/language/oop/interfaces/defining-interfaces), [examples](https://dev.java/learn/language/oop/interfaces/examples) |

Pages fully covered already, nothing added:

| Page | Taught in | Source |
|---|---|---|
| Objects, Classes, Interfaces, Packages, and Inheritance | [00-oops/OOPS.md](../00-oops/OOPS.md) §1-2 | [classes](https://dev.java/learn/language/oop/classes) |
| Creating Classes, Defining Methods | `00-oops/basics/` | [creating-classes](https://dev.java/learn/language/oop/classes-objects/creating-classes), [defining-methods](https://dev.java/learn/language/oop/classes-objects/defining-methods) |
| Using an Interface as a Type | `00-oops/polymorphism/`, `00-oops/abstraction/` | [interfaces-as-a-type](https://dev.java/learn/language/oop/interfaces/interfaces-as-a-type) |

Index pages (lists of lessons, nothing to teach): [classes-objects](https://dev.java/learn/language/oop/classes-objects), [inheritance](https://dev.java/learn/language/oop/inheritance), [interfaces](https://dev.java/learn/language/oop/interfaces).

Mark each lesson ✅ once its exercises have been reviewed.

## 3. How the pieces relate

- **A helper type used by only one class?** A nested class (**01**): `static` unless it needs the outer object.
- **A fixed set of values (days, statuses, plans)?** An enum (**02**).
- **A plain immutable data carrier (a point, a DTO, a map key)?** A record (**03**). It gets `equals`/`hashCode`/`toString` for free; a normal class needs them written by hand (**04**).
- **Growing past a handful of files?** Packages (**05**) group them and control what's visible.
