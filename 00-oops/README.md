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

## How to run

Requires **JDK 21+** (uses records, sealed types, and pattern-matching `switch`).

**IntelliJ:** open any file and click the green ▶ next to `main`. The repo root is the source root, so the packages are `oops.*`.

**Command line**, run from the repo root (`java-learning/`):

```powershell
# compile everything into ./out
javac -d out (Get-ChildItem -Recurse oops -Filter *.java).FullName

# run a demo
java -cp out oops.relationships.CompositionDemo
```

```bash
# Git Bash / Linux / macOS
javac -d out $(find oops -name '*.java')
java -cp out oops.polymorphism.PolymorphismDemo
```
