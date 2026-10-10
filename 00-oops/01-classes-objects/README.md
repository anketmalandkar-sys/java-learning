# Classes and Objects

Source: https://dev.java/learn/language/oop/classes
Source: https://dev.java/learn/language/oop/classes-objects/creating-classes
Source: https://dev.java/learn/language/oop/classes-objects/defining-methods
Source: https://dev.java/learn/language/oop/classes-objects/defining-constructors
Source: https://dev.java/learn/language/oop/classes-objects/calling-methods-constructors
Source: https://dev.java/learn/language/oop/classes-objects/creating-objects
Source: https://dev.java/learn/language/oop/classes-objects/more-on-classes

← Back to [Object-Oriented Programming](../README.md)

Read the notes below, then run each demo. Every file has its own `main` and detailed comments. The demos use packages (`oops.basics`, `oops.encapsulation`); see [How to run](#how-to-run).

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
- **`equals` / `hashCode` / `toString`**: inherited from `Object`. If you override `equals`, you **must** override `hashCode`, or `HashMap`/`HashSet` will misbehave (more in [06-object-superclass](../06-object-superclass/README.md)).

## 2. Encapsulation: protect the data

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

## 3. Demos

| # | File | What it teaches |
|---|---|---|
| 1 | [basics/ClassesAndObjects.java](basics/ClassesAndObjects.java) | Class vs object, constructors and `this(...)` chaining, overloading, `static` vs instance, references, `==` vs `equals` |
| 2 | [encapsulation/BankAccount.java](encapsulation/BankAccount.java) | Private state, validated behavior, invariants, defensive getters |
| 3 | [encapsulation/EncapsulationDemo.java](encapsulation/EncapsulationDemo.java) | Access-modifier table, immutable `record` value object |
| 4 | [basics/ParametersAndObjects.java](basics/ParametersAndObjects.java) | Varargs, pass-by-value for primitives vs references, parameter shadowing, private constructors + static factories, when objects become unreachable |
| 5 | [basics/InitializationOrder.java](basics/InitializationOrder.java) | Static and instance initializer blocks, full parent/child initialization order, compile-time constant inlining, static calls through an instance |

Demos 4 and 5 were added from dev.java's "Classes and Objects" pages, to cover what the first three didn't.

## 4. Interview quick-fire

1. **`==` vs `equals()`?** `==` compares identity (same object). `equals()` compares logical state, as the class defines it.
2. **Is Java pass-by-value or pass-by-reference?** Always pass-by-value. For objects, the value passed is a copy of the reference, so changes to the object are visible but reassigning the parameter isn't.
3. **When does a static initializer block run?** Once, when the class is initialized, before any instance is created.

## 5. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | [exercises/Exercise1_ParametersAndObjects.java](exercises/Exercise1_ParametersAndObjects.java) | Varargs, pass-by-value, shadowing, private constructors with cached instances | Easy |
| 2 | [exercises/Exercise2_PredictInitOrder.java](exercises/Exercise2_PredictInitOrder.java) | Predict the initialization order and constant inlining | Medium |

The exercises print PASS/FAIL.

## How to run

Requires **JDK 21+**.

**IntelliJ:** open any file and click the green ▶ next to `main`. This folder is the source root with package prefix `oops`.

**Command line**, from the repo root (`java-learning/`):

```bash
# Git Bash / Linux / macOS
javac -d out $(find 00-oops/01-classes-objects -name '*.java')
java -cp out oops.basics.ClassesAndObjects
java -cp out oops.exercises.Exercise1_ParametersAndObjects
```

```powershell
javac -d out (Get-ChildItem -Recurse 00-oops/01-classes-objects -Filter *.java).FullName
java -cp out oops.basics.ClassesAndObjects
```
