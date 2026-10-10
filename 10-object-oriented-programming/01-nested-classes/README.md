# Nested, Local and Anonymous Classes

Source: https://dev.java/learn/language/oop/classes-objects/nested-classes
Source: https://dev.java/learn/language/oop/classes-objects/design-best-practices

← Back to [Object Oriented Programming](../README.md)

## 1. What it is

A **nested class** is a class declared inside another class. There are four kinds:

| Kind | Declared | Has an outer object? | Typical use |
|---|---|---|---|
| **Static nested class** | as a `static` member of a class | No | A helper type that belongs with its outer class (`Map.Entry`, a `Builder`) |
| **Inner class** | as a non-static member | Yes, always tied to one outer instance | Something that needs the outer object's state (an iterator over its data) |
| **Local class** | inside a method or block | Yes, if declared in an instance method | A named helper used only in one method |
| **Anonymous class** | inside an expression, no name | Same as local | A one-off implementation of an interface or subclass |

## 2. Why it exists

- **Grouping**: a class used by only one other class can live inside it, instead of cluttering the package.
- **Encapsulation**: a nested class can read the outer class's `private` members, so the outer class doesn't have to open them up to the world. And the nested class itself can be `private`.
- **Readability**: the helper sits next to the only code that uses it.

## 3. Core concepts

**Static nested class**: like a top-level class that happens to live inside another. No access to the outer *instance*.
```java
public class Order {
    private final List<Line> lines = new ArrayList<>();

    static class Line {                      // Order.Line
        final String sku; final int qty;
        Line(String sku, int qty) { this.sku = sku; this.qty = qty; }
    }
}
Order.Line line = new Order.Line("PEN", 2);  // no Order object needed
```

**Inner class**: every instance belongs to one outer instance and can use its fields directly, even `private` ones.
```java
public class Playlist {
    private final String[] songs = {"Intro", "Rain"};

    class Cursor {                            // inner: not static
        private int index = 0;
        boolean hasNext() { return index < songs.length; }   // reads the OUTER field
        String next() { return songs[index++]; }
    }
}
Playlist p = new Playlist();
Playlist.Cursor c = p.new Cursor();           // needs an outer object
```

**Local class**: a named class inside a method. It can use the method's local variables **only if they're final or effectively final** (never reassigned).
```java
List<String> validate(List<String> emails, String domain) {
    class Rule {                                // local class
        boolean ok(String e) { return e.endsWith("@" + domain); }   // captures domain
    }
    Rule rule = new Rule();
    ...
}
```

**Anonymous class**: declares and creates a one-off class in a single expression. Note the `;` at the end: it's part of a statement.
```java
Comparator<String> byLength = new Comparator<>() {
    @Override
    public int compare(String a, String b) { return Integer.compare(a.length(), b.length()); }
};
```

**Shadowing**: an inner declaration hides an outer one with the same name. Reach the outer ones with `this.x` (inner class field) and `Outer.this.x` (outer class field).
```java
class Outer {
    int x = 1;
    class Inner {
        int x = 2;
        void show(int x) {             // x = 3
            System.out.println(x);           // 3  the parameter
            System.out.println(this.x);      // 2  Inner's field
            System.out.println(Outer.this.x);// 1  Outer's field
        }
    }
}
```

**Access modifiers**: a top-level class can only be `public` or package-private. A **nested** class can be `public`, `protected`, package-private or `private`.

## 4. How it works under the hood

- The compiler turns each nested class into its own `.class` file: `Order$Line.class`, `Playlist$Cursor.class`, `Outer$1.class` for the first anonymous class.
- An **inner** class gets a hidden field (`this$0`) pointing to its outer object. That's why it needs `outer.new Inner()` and why it can read outer fields. It also means **an inner object keeps its outer object alive** (memory leaks if you hand inner objects out and keep them). Since JDK 18, javac leaves the field out when the inner class never uses the outer object, but that's an optimisation, not a design tool: make the class `static` to say what you mean.
- A **static nested** class has no such field: it's cheaper and can't leak the outer object.
- Local and anonymous classes **copy** the captured local variables. That's why those variables must be effectively final: the copy could never see a later change.
- Local classes can't declare most `static` members (only constants), and you can't declare an `interface` inside a method body (interfaces are implicitly static).
- Anonymous classes can't have constructors (they have no name). Use an instance initializer block `{ ... }` if you need setup code.
- Serializing inner, local or anonymous classes is strongly discouraged: the synthetic fields the compiler generates aren't portable.

## 5. Common mistakes and gotchas

**Inner class when static would do**
```java
class Node { ... }          // WRONG (inside LinkedList): every Node holds a hidden LinkedList reference
static class Node { ... }   // RIGHT: Node doesn't need the list object
```

**Creating an inner class from a static method**
```java
public static void main(String[] args) {
    Cursor c = new Cursor();           // WRONG: compile error, no enclosing Playlist
    Cursor c = new Playlist().new Cursor();   // RIGHT
}
```

**Capturing a variable that changes**
```java
int count = 0;
Runnable r = new Runnable() {
    public void run() { System.out.println(count); }   // compile error...
};
count++;                                               // ...because count is reassigned
```

**`this` inside an anonymous class** means the anonymous object, not the enclosing one. Use `Outer.this` for the outer object. (In a **lambda**, `this` means the enclosing object.)

## 6. When to use / when not to

From the dev.java decision guide:

| Use a... | When you need... |
|---|---|
| **Lambda** | a single piece of behaviour for a functional interface (one abstract method), with no fields, no extra methods, no constructor, no named type |
| **Anonymous class** | a one-off implementation that needs **fields or extra methods**, or implements an interface with **more than one** abstract method |
| **Local class** | **several instances**, a **constructor**, or a **named type** whose extra methods you call later, all inside one method |
| **Static nested class** | a helper type used more widely than one method, that doesn't need the outer object |
| **Inner class** | a helper that needs the outer instance's (non-public) fields and methods |

Default to **static nested**; make it inner only when it truly needs the outer object.

## 7. Interview angle

1. **Static nested vs inner class?** A static nested class has no link to an outer instance; an inner class does, and can use the outer object's fields directly.
2. **Why must captured local variables be effectively final?** Local and anonymous classes (and lambdas) get a copy of the value. If the variable could change, the copy would be silently out of date, so Java forbids it.
3. **Can an anonymous class have a constructor?** No, it has no name. Use an instance initializer block, or pass arguments to the superclass constructor.
4. **How do you reach the outer class's field when an inner field has the same name?** `Outer.this.field`.
5. **Anonymous class vs lambda?** A lambda only implements a functional interface and has no state of its own; `this` refers to the enclosing object. An anonymous class can have fields and several methods, and its `this` is itself.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_InventoryReport.java` | A static nested record-like class and an inner iterator over the outer object's data | Easy |
| 2 | `exercises/Exercise2_PickTheKind.java` | Implement the same validator as a local class, an anonymous class and a lambda | Medium |
| 3 | `exercises/Exercise3_ShadowAndLeak.java` | Fix a shadowing bug and turn a needless inner class into a static nested one | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
