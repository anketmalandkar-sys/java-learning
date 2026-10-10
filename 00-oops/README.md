# OOP in Java: Learning Module

Start with **[OOPS.md](OOPS.md)** for the theory. Then run each demo in order. Every file has its own `main` and detailed comments.

| # | File | What it teaches |
|---|---|---|
| 1 | [basics/ClassesAndObjects.java](basics/ClassesAndObjects.java) | Class vs object, constructors and `this(...)` chaining, overloading, `static` vs instance, references, `==` vs `equals` |
| 2 | [encapsulation/BankAccount.java](encapsulation/BankAccount.java) | Private state, validated behavior, invariants, defensive getters |
| 3 | [encapsulation/EncapsulationDemo.java](encapsulation/EncapsulationDemo.java) | Access-modifier table, immutable `record` value object |
| 4 | [inheritance/InheritanceDemo.java](inheritance/InheritanceDemo.java) | `extends`, `super`, constructor order, multilevel and hierarchical inheritance, `protected`, `final` |
| 5 | [polymorphism/PolymorphismDemo.java](polymorphism/PolymorphismDemo.java) | Overloading vs overriding, dynamic dispatch, up/down-casting, `instanceof` patterns, static-method hiding |
| 6 | [abstraction/AbstractionDemo.java](abstraction/AbstractionDemo.java) | Abstract class with the template method pattern, interfaces, default-method diamond, sealed types |
| 7 | [relationships/AssociationDemo.java](relationships/AssociationDemo.java) | Uni/bi-directional, one-to-many, many-to-many, dependency |
| 8 | [relationships/AggregationDemo.java](relationships/AggregationDemo.java) | Weak whole/part: parts are passed in, shared, and outlive the whole |
| 9 | [relationships/CompositionDemo.java](relationships/CompositionDemo.java) | Strong whole/part: parts are created inside and die with the whole |
| 10 | [relationships/CompositionOverInheritance.java](relationships/CompositionOverInheritance.java) | Why `Stack extends ArrayList` is bad, and delegation as the fix |

### Added from dev.java "Object Oriented Programming"

These fill the gaps the demos above didn't cover. New topics (nested classes, enums, records, `Object`'s methods, packages) are full lessons in [10-object-oriented-programming](../10-object-oriented-programming/README.md).

| # | File | What it teaches | Exercise |
|---|---|---|---|
| 11 | [basics/ParametersAndObjects.java](basics/ParametersAndObjects.java) | Varargs, pass-by-value for primitives vs references, parameter shadowing, private constructors + static factories, when objects become unreachable | [Exercise1_ParametersAndObjects](exercises/Exercise1_ParametersAndObjects.java) |
| 12 | [basics/InitializationOrder.java](basics/InitializationOrder.java) | Static and instance initializer blocks, full parent/child initialization order, compile-time constant inlining, static calls through an instance | [Exercise2_PredictInitOrder](exercises/Exercise2_PredictInitOrder.java) |
| 13 | [inheritance/OverridingRules.java](inheritance/OverridingRules.java) | Widening access, instance/static mismatch, overloading in a subclass, field hiding, overridable calls in constructors, default-method resolution, static interface methods | [Exercise3_OverridingTraps](exercises/Exercise3_OverridingTraps.java) |
| 14 | [abstraction/InterfaceAndAbstractRules.java](abstraction/InterfaceAndAbstractRules.java) | Private/static/default interface methods, constants, interfaces extending several, evolving a published interface, interface-typed parameters, abstract classes implementing an interface partially | [Exercise4_EvolveTheInterface](exercises/Exercise4_EvolveTheInterface.java) |

Sources for these additions:

Source: https://dev.java/learn/language/oop/classes-objects/defining-constructors
Source: https://dev.java/learn/language/oop/classes-objects/calling-methods-constructors
Source: https://dev.java/learn/language/oop/classes-objects/creating-objects
Source: https://dev.java/learn/language/oop/classes-objects/more-on-classes
Source: https://dev.java/learn/language/oop/inheritance/what-is-inheritance
Source: https://dev.java/learn/language/oop/inheritance/overriding
Source: https://dev.java/learn/language/oop/inheritance/polymorphism
Source: https://dev.java/learn/language/oop/inheritance/abstract-classes
Source: https://dev.java/learn/language/oop/interfaces/defining-interfaces
Source: https://dev.java/learn/language/oop/interfaces/examples

The exercises print PASS/FAIL. Run them like the demos, e.g. `java -cp out oops.exercises.Exercise1_ParametersAndObjects`.

## How to run

Requires **JDK 21+** (uses records, sealed types, and pattern-matching `switch`).

**IntelliJ:** open any file and click the green ▶ next to `main`. The repo root is the source root, so the packages are `oops.*`.

**Command line**, run from the repo root (`java-learning/`):

```powershell
# compile everything into ./out
javac -d out (Get-ChildItem -Recurse 00-oops -Filter *.java).FullName

# run a demo
java -cp out oops.relationships.CompositionDemo
```

```bash
# Git Bash / Linux / macOS
javac -d out $(find 00-oops -name '*.java')
java -cp out oops.polymorphism.PolymorphismDemo
```
