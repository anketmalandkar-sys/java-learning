# Interfaces and Abstract Classes

Source: https://dev.java/learn/language/oop/inheritance/abstract-classes
Source: https://dev.java/learn/language/oop/interfaces/defining-interfaces
Source: https://dev.java/learn/language/oop/interfaces/examples
Source: https://dev.java/learn/language/oop/interfaces/interfaces-as-a-type

← Back to [Object-Oriented Programming](../README.md)

dev.java lists abstract classes under Inheritance, but they're best learned next to interfaces, so both are here. `Comparator`'s static and default helpers (`comparing`, `thenComparing`, `reversed`) are covered in [02-comparator](../../02-comparator/README.md).

The demos use the package `oops.abstraction`; see [How to run](#how-to-run).

## 1. Abstraction: hide complexity, show essentials

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

**An interface is a type.** A variable or parameter of an interface type accepts any implementing class (`Payable p = new Order(...)`), so code written against the interface works with every implementation.

## 2. Demos

| # | File | What it teaches |
|---|---|---|
| 1 | [abstraction/AbstractionDemo.java](abstraction/AbstractionDemo.java) | Abstract class with the template method pattern, interfaces, default-method diamond, sealed types |
| 2 | [abstraction/InterfaceAndAbstractRules.java](abstraction/InterfaceAndAbstractRules.java) | Private/static/default interface methods, constants, interfaces extending several, re-abstracting a default, nested types in interfaces, evolving a published interface, interface-typed parameters, abstract classes implementing an interface partially |

Demo 2 was added from dev.java's Interfaces and Abstract Classes pages, to cover what demo 1 didn't.

## 3. Interview quick-fire

1. **Can an abstract class have a constructor?** Yes. It runs when a subclass is constructed.
2. **Can an interface have a method body?** Yes, as `default`, `static`, or `private` methods (Java 8/9+).
3. **Abstract class or interface?** An abstract class when related classes share state and code; an interface for a capability unrelated classes can share, or when a class needs several types.

## 4. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | [exercises/Exercise1_EvolveTheInterface.java](exercises/Exercise1_EvolveTheInterface.java) | Evolve a published interface without breaking an existing implementation: default and private methods, a constant, a re-abstracted method, an abstract base class | Medium |

The exercise prints PASS/FAIL.

## How to run

**IntelliJ:** click the green ▶ next to `main`. This folder is the source root with package prefix `oops`.

**Command line**, from the repo root:

```bash
javac -d out $(find 00-oops/07-interfaces -name '*.java')
java -cp out oops.abstraction.AbstractionDemo
java -cp out oops.exercises.Exercise1_EvolveTheInterface
```

```powershell
javac -d out (Get-ChildItem -Recurse 00-oops/07-interfaces -Filter *.java).FullName
java -cp out oops.abstraction.InterfaceAndAbstractRules
```
