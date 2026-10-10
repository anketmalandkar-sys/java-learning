# Object as a Superclass

Source: https://dev.java/learn/language/oop/inheritance/objects

← Back to [Object Oriented Programming](../README.md) · `==` vs `equals` and the "override both" rule first appear in [01-classes-objects/basics/ClassesAndObjects.java](../01-classes-objects/basics/ClassesAndObjects.java) (`Point`).

## 1. What it is

Every class extends `java.lang.Object`, directly or through its superclasses, so every object has `Object`'s methods:

| Method | Default behaviour | Override? |
|---|---|---|
| `toString()` | `ClassName@1b6d3586` (class name + hash in hex) | Almost always, for logs and debugging |
| `equals(Object)` | `this == other`: same object only | When two objects with the same content should count as equal |
| `hashCode()` | an identity-based number | **Always together with `equals`** |
| `getClass()` | the runtime `Class` object | Can't: it's `final` |
| `clone()` | shallow field-by-field copy, only if the class implements `Cloneable` | Rarely; prefer copy constructors |
| `finalize()` | nothing | **Never**: deprecated for removal |
| `wait` / `notify` / `notifyAll` | thread coordination | Can't: `final` (see concurrency) |

## 2. Why it exists

Collections, logging, and frameworks need some behaviour from *every* object: "print yourself", "are you equal to this?", "what's your hash bucket?". Putting those in `Object` gives them all a default. The defaults are based on **identity**, which is wrong for value-like classes (`Isbn`, `Money`, `Point`). If you don't override them, `list.contains`, `HashSet` and `HashMap` silently fail to find your objects.

## 3. Core concepts

**`toString`**
```java
@Override
public String toString() {
    return "Book[isbn=" + isbn + ", title=" + title + "]";
}
```

**`equals`**: the standard recipe.
```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;                       // same object
    if (!(o instanceof Book other)) return false;     // null or a different type
    return isbn.equals(other.isbn);                   // compare the fields that define identity
}
```
The contract: **reflexive** (`a.equals(a)`), **symmetric** (`a.equals(b)` ⇔ `b.equals(a)`), **transitive**, **consistent** (same answer while the fields don't change), and `a.equals(null)` is `false`.

**`hashCode`**: equal objects **must** have equal hash codes. Use the same fields as `equals`.
```java
@Override
public int hashCode() {
    return Objects.hash(isbn);            // or isbn.hashCode() for a single field
}
```

**`getClass`**: the `Class` object, with metadata about the type.
```java
Class<?> c = book.getClass();
c.getSimpleName();      // "Book"
c.getSuperclass();      // class java.lang.Object
c.getInterfaces();      // the interfaces it implements
c.isEnum(); c.isInterface(); c.isRecord();
c.isAnnotation();       // true for annotation types like Override
c.getFields();          // PUBLIC fields, including inherited ones
c.getMethods();         // PUBLIC methods, including inherited ones (equals, hashCode, ... from Object)
```
`getDeclaredFields()`/`getDeclaredMethods()` are the counterparts that return **all** members declared in that class itself (any access level, nothing inherited). This metadata is the entry point to the Reflection API.

**`clone`**: works only if the class implements the `Cloneable` marker interface, otherwise it throws `CloneNotSupportedException`. The default copy is **shallow**: fields that reference other objects are shared.
```java
class Playlist implements Cloneable {
    private List<String> songs = new ArrayList<>();

    @Override
    public Playlist clone() {
        try {
            Playlist copy = (Playlist) super.clone();   // shallow copy
            copy.songs = new ArrayList<>(songs);         // deep-copy the mutable part
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);                 // can't happen: we implement Cloneable
        }
    }
}
```

**Instead of `finalize`**: implement `AutoCloseable` and use try-with-resources.
```java
try (var conn = new Connection("db")) {   // close() runs automatically, even on an exception
    conn.query("...");
}
```

## 4. How it works under the hood

- `HashMap`/`HashSet` first use `hashCode()` to pick a bucket, then `equals()` inside that bucket. If two equal objects have different hash codes, they land in different buckets and the set "can't find" its own element.
- Unequal objects *may* share a hash code (a collision): that's allowed, just slower.
- Fields used in `hashCode` should not change while the object is in a hash-based collection; otherwise it's stuck in the wrong bucket.
- `getClass()` returns the **runtime** class: for `Object o = "hi"`, `o.getClass()` is `String`.
- `finalize()` ran at an unpredictable time (or never), slowed down garbage collection, and could resurrect objects. It's deprecated since Java 9 and **deprecated for removal** since Java 18.

## 5. Common mistakes and gotchas

**Overriding `equals` but not `hashCode`**
```java
Set<Book> set = new HashSet<>();
set.add(new Book("978-1"));
set.contains(new Book("978-1"));    // WRONG: false (different hash codes, different buckets)
                                    // RIGHT: override hashCode with the same fields as equals
```

**Overloading instead of overriding `equals`**
```java
public boolean equals(Book other) { ... }     // WRONG: a NEW method; collections call equals(Object)
@Override public boolean equals(Object o) {...} // RIGHT: @Override catches the mistake
```

**Mutable fields in `hashCode`**: changing a field after inserting into a `HashSet` makes the element unfindable.

**Shallow `clone`**: the copy shares mutable fields (lists, arrays, dates) with the original; changing one changes both. Prefer a **copy constructor** or a static `copyOf` method over `clone`.

**Relying on `finalize`** to close files or connections: they may stay open indefinitely. Use `AutoCloseable` + try-with-resources.

## 6. When to use / when not to

- **Override `toString`** in nearly every class you write; it's free debugging.
- **Override `equals` + `hashCode`** for value-like classes compared by content (ids, money, coordinates). Don't for entities that are identified by *who* they are rather than *what* they contain, unless you compare by a stable id.
- **Consider a record** instead: it generates `equals`, `hashCode` and `toString` correctly (lesson 03).
- **Avoid `clone`**; write a copy constructor (`new Playlist(other)`).
- **Never use `finalize`**; implement `AutoCloseable`.

## 7. Interview angle

1. **What's the `equals`/`hashCode` contract?** If `a.equals(b)`, then `a.hashCode() == b.hashCode()`. The reverse isn't required. `equals` must be reflexive, symmetric, transitive, consistent, and false for `null`.
2. **What happens if you override `equals` but not `hashCode`?** Hash-based collections break: equal objects get different hash codes, so `contains`, `get` and duplicate detection fail.
3. **Shallow vs deep copy in `clone`?** `Object.clone` copies field values, so referenced objects are shared (shallow). A deep copy also copies those referenced objects.
4. **Why is `finalize` deprecated, and what replaces it?** Unpredictable timing, performance cost, and resurrection bugs. Use `AutoCloseable`/try-with-resources (or `Cleaner` for rare native-resource cases).
5. **Can you override `getClass()`?** No, it's `final`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_BookIdentity.java` | Implement `toString`, `equals` and `hashCode` so books work in a `HashSet` | Easy |
| 2 | `exercises/Exercise2_EqualsContract.java` | Find which `equals` implementations break the contract, and fix them | Medium |
| 3 | `exercises/Exercise3_CopyAndClose.java` | Fix a shallow `clone`, write a copy constructor, and replace a `finalize`-style cleanup with `AutoCloseable` | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
