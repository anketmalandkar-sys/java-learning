# Records: Modeling Immutable Data

Source: https://dev.java/learn/language/oop/records

← Back to [Object Oriented Programming](../README.md) · A first look at records is in [00-oops/encapsulation/EncapsulationDemo.java](../../00-oops/encapsulation/EncapsulationDemo.java) (`Money`).

## 1. What it is

A **record** (Java 16+, previewed in 14) is a class for **immutable data**, declared in one line: `record Point(int x, int y) {}`. From the header, the compiler generates private final fields, a constructor that sets them, an accessor per field (`x()`, `y()`), and `equals`, `hashCode` and `toString` based on all the fields.

## 2. Why it exists

A correct immutable class written by hand is about 40 lines: `final` class, `private final` fields, a constructor, getters, and an `equals`/`hashCode`/`toString` that must be kept in sync with the fields. Forget to update `equals` after adding a field and `HashSet` breaks. A record states the intent ("this is just data") and the compiler writes and maintains the boilerplate.

## 3. Core concepts

**Components, fields, accessors**
```java
record Point(int x, int y) {}

Point p = new Point(3, 4);
p.x();                         // 3: the accessor is x(), not getX()
p.equals(new Point(3, 4));     // true: compares all components
p.toString();                  // "Point[x=3, y=4]"
```

**Compact canonical constructor**: validate or normalise; the fields are assigned automatically **after** your code runs. No parameter list, and no `this.x = ...`.
```java
record Range(int start, int end) {
    Range {
        if (start > end) throw new IllegalArgumentException(start + " > " + end);
    }
}

record Email(String value) {
    Email {
        value = value.strip().toLowerCase();   // reassign the PARAMETER; the field gets the new value
    }
}
```

**Explicit canonical constructor**: the full signature; you must assign every field yourself. Use it rarely; compact is clearer.
```java
record Point(int x, int y) {
    Point(int x, int y) { this.x = x; this.y = y; }
}
```

**Other constructors** must delegate to the canonical one with `this(...)`.
```java
record Range(int start, int end) {
    Range(int single) { this(single, single); }
}
```

Up to Java 24, `this(...)` must be the **first statement**. Since **Java 25** (flexible constructor bodies, JEP 513), statements may come **before** it, as long as they don't use the object being built: no reading or calling its fields or methods, and no `this`. That lets you validate or prepare arguments first:
```java
record Range(int start, int end) {
    Range(String text) {                     // Java 25+
        String[] parts = text.split("-");    // runs before this(...): only touches the parameter
        this(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}
```
On Java 21 (what this repo uses), move that logic into a `static` helper or factory method instead.

**Methods, static members, interfaces**
```java
record Money(long paise, String currency) implements Comparable<Money> {
    static final Money ZERO_INR = new Money(0, "INR");     // static fields are fine
    static Money rupees(long r) { return new Money(r * 100, "INR"); }   // static factory
    Money plus(Money other) { return new Money(paise + other.paise, currency); }
    public int compareTo(Money o) { return Long.compare(paise, o.paise); }
}
```

**Custom accessor**: allowed, e.g. to return a defensive copy (it must be `public` and keep the same return type).

**Local records**: declared inside a method, perfect for an intermediate result in a calculation or stream.
```java
record CityCount(String state, long cities) {}
```

## 4. How it works under the hood

- A record compiles to a `final class` that `extends java.lang.Record`. So a record **can't extend** another class (it can implement any number of interfaces), and **can't be extended**.
- Restrictions: **no extra instance fields** (only the components), no instance field initializers, no instance initializer blocks. Static fields and static initializers are fine.
- Records are **shallowly immutable**: the fields are final, but a `List` component can still be modified unless you copy it. Use `List.copyOf` in the compact constructor (note: it rejects `null` elements) and the list is unmodifiable from then on.
- Serialization: records are `Serializable` only if they implement it. Deserialization always goes **through the canonical constructor**, so your validation also protects data read back from a file. Custom `writeObject`/`readObject` are ignored for records, and `Externalizable` isn't supported. `writeReplace()` and `readResolve()` **do** still work, so a record can serve as a **serialization proxy**: a class's `writeReplace()` returns a small record holding just its essential data, and that record's `readResolve()` rebuilds the real object on the way back. Because the record is always recreated through its canonical constructor, its validation runs on everything deserialized.
```java
class Account implements Serializable {
    private final String id;
    private final long balancePaise;
    ...
    private Object writeReplace() { return new AccountProxy(id, balancePaise); }   // serialize the proxy instead

    private record AccountProxy(String id, long balancePaise) implements Serializable {
        AccountProxy {
            if (balancePaise < 0) throw new IllegalArgumentException("corrupt data");   // checked on every read
        }
        private Object readResolve() { return new Account(id, balancePaise); }        // turn it back into an Account
    }
}
```
- Nested records are implicitly `static` (they never capture an outer instance).

## 5. Common mistakes and gotchas

**Assigning fields in a compact constructor**
```java
record Range(int start, int end) {
    Range { this.start = Math.min(start, end); }   // WRONG: compile error
    Range { int s = Math.min(start, end); end = Math.max(start, end); start = s; }   // RIGHT: reassign parameters
}
```

**Leaking a mutable component**
```java
record Team(String name, List<String> members) {}
var list = new ArrayList<>(List.of("asha"));
var team = new Team("A", list);
list.add("mallory");                // WRONG: team.members() changed too

record Team(String name, List<String> members) {
    Team { members = List.copyOf(members); }   // RIGHT: an unmodifiable private copy
}
```

**Expecting getters**: `point.getX()` doesn't exist; it's `point.x()`. Some older libraries that look for `getX()` won't see record fields.

**Trying to add state**
```java
record Counter(int start) { int current; }    // WRONG: no extra instance fields
```

**Using records for entities with identity and changing state** (a bank account, a JPA entity): records are values, compared by content. Two accounts with the same balance are not the same account.

## 6. When to use / when not to

- **Use a record** for data carriers: DTOs, API responses, map keys, value objects (`Money`, `Point`, `Range`), multiple return values, and intermediate results in streams (local records).
- **Use a class** when the object has identity, changes over time, needs inheritance from another class, or must hide its internal representation behind a different public API.
- Validate in the **compact constructor**, so an invalid record can never exist.

## 7. Interview angle

1. **What does the compiler generate for a record?** Private final fields, a canonical constructor, one accessor per component, and `equals`, `hashCode`, `toString` based on all components.
2. **Can a record extend a class? Have extra fields?** No to both. It already extends `java.lang.Record`, and only the header components can be instance fields. It can implement interfaces and have static fields and methods.
3. **Compact vs canonical constructor?** A compact constructor has no parameter list; you validate or reassign the parameters and the compiler assigns the fields afterwards. An explicit canonical constructor lists all parameters and must assign every field itself.
4. **Is a record deeply immutable?** No, only shallowly: a mutable component (like an `ArrayList`) can still change unless you copy it defensively.
5. **How does deserialization work for records?** It calls the canonical constructor, so validation runs for deserialized data too.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_TemperatureBooking.java` | Records with accessors, a compact constructor that validates, and an extra constructor | Easy |
| 2 | `exercises/Exercise2_MoneyValue.java` | A `Money` value record with normalisation, a static factory, methods and `Comparable` | Medium |
| 3 | `exercises/Exercise3_TeamRoster.java` | Fix a record that leaks its mutable list, and use a local record to summarise data | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
