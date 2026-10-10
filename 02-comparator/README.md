# Comparator

## 1. What it is

`Comparator<T>` is an interface from `java.util` that holds a sorting rule **outside** the class being sorted:

```java
public interface Comparator<T> {
    int compare(T a, T b);
    // plus many helper methods: comparing, thenComparing, reversed, ...
}
```

`Comparable` gives a class *one* natural order. `Comparator` lets you define as many orders as you like (by price, by name, by date) and pick one each time you sort.

## 2. Why it exists

`Comparable` falls short in three cases:

1. **You need more than one order.** An `Employee` can only have one `compareTo`, but the HR screen sorts by name and the payroll screen sorts by salary.
2. **You can't edit the class.** `String` sorts alphabetically and case-sensitively. You can't change `String.compareTo` to ignore case.
3. **The class has no sensible natural order.** What's the "natural" order of an `Order`? By ID? By date? By total? There's no single answer.

Without `Comparator`, you'd write wrapper classes or your own sort loops. With it, you pass the rule straight to the sort:

```java
employees.sort(Comparator.comparing(Employee::getSalary));
```

## 3. Core concepts

**Same sign rule as `compareTo`.**

| `cmp.compare(a, b)` returns | Meaning |
|---|---|
| negative | `a` comes **before** `b` |
| zero | equal in this ordering |
| positive | `a` comes **after** `b` |

**Three ways to write one** (oldest to most modern):

```java
// 1. Anonymous class (pre-Java 8, you'll still see it in old code)
Comparator<Employee> bySalary = new Comparator<Employee>() {
    @Override
    public int compare(Employee a, Employee b) {
        return Integer.compare(a.getSalary(), b.getSalary());
    }
};

// 2. Lambda (Java 8+). Comparator has one abstract method, so it's a functional interface.
Comparator<Employee> bySalary = (a, b) -> Integer.compare(a.getSalary(), b.getSalary());

// 3. Factory method (Java 8+). Shortest and hardest to get wrong. Prefer this.
Comparator<Employee> bySalary = Comparator.comparingInt(Employee::getSalary);
```

**Building blocks**

| Method | What it does |
|---|---|
| `Comparator.comparing(keyFn)` | order by a key that is itself `Comparable` (String, LocalDate, ...) |
| `comparingInt / comparingLong / comparingDouble` | same, for primitives, no boxing |
| `.reversed()` | flip the whole comparator |
| `.thenComparing(keyFn)` | tie-breaker, used only when the previous rule says "equal" |
| `Comparator.naturalOrder()` / `reverseOrder()` | the type's `Comparable` order, forwards or backwards |
| `Comparator.nullsFirst(cmp)` / `nullsLast(cmp)` | handle `null` values safely |
| `comparing(keyFn, keyCmp)` | order by a key, using a custom rule for the key |

**Chaining**

```java
Comparator<Employee> cmp = Comparator
        .comparing(Employee::getDepartment)                 // first by department A-Z
        .thenComparing(Employee::getSalary, Comparator.reverseOrder()) // then highest salary first
        .thenComparing(Employee::getName);                  // then by name
```

**Where you pass a Comparator**

```java
list.sort(cmp);                       // sort a List in place
Collections.sort(list, cmp);          // same thing, older style
Arrays.sort(array, cmp);              // object arrays only (not int[])
new TreeSet<>(cmp);                   // sorted set using this rule
new TreeMap<>(cmp);                   // sorted keys using this rule
new PriorityQueue<>(cmp);             // heap ordered by this rule
Collections.max(list, cmp);           // largest by this rule
list.stream().sorted(cmp)             // sorted stream
```

**Case-insensitive strings**

```java
names.sort(String.CASE_INSENSITIVE_ORDER);
// or: Comparator.comparing(String::toLowerCase)
```

## 4. How it works under the hood

- `Comparator.comparing(Employee::getSalary)` just builds a lambda equivalent to `(a, b) -> a.getSalary().compareTo(b.getSalary())`. No magic.
- `thenComparing` builds a new comparator: run the first one; if it returns non-zero, use that; if zero, run the second. That's why order of chaining matters.
- `.reversed()` wraps the comparator and swaps the arguments: `(a, b) -> original.compare(b, a)`. **It reverses everything chained before it**, which is a frequent surprise (see gotchas).
- `List.sort` and `Collections.sort` use TimSort, which is **stable**: elements that compare as equal keep their original relative order. This means sorting by B and then by A gives "by A, ties by B", though `thenComparing` is clearer.
- `TreeSet` / `TreeMap` use the comparator **instead of `equals`** to detect duplicates. If `compare` returns 0, the second element is treated as a duplicate and dropped.

## 5. Common mistakes and gotchas

**Subtraction overflow**

```java
// WRONG: overflows when values are far apart (e.g. Integer.MIN_VALUE - 1)
Comparator<Account> cmp = (a, b) -> a.getBalance() - b.getBalance();

// RIGHT
Comparator<Account> cmp = Comparator.comparingInt(Account::getBalance);
```

**`.reversed()` at the end of a chain reverses everything**

```java
// WRONG if you wanted "department A-Z, salary high-to-low":
// this makes BOTH department Z-A and salary high-to-low.
Comparator.comparing(Employee::getDepartment)
          .thenComparing(Employee::getSalary)
          .reversed();

// RIGHT: reverse only the salary key
Comparator.comparing(Employee::getDepartment)
          .thenComparing(Employee::getSalary, Comparator.reverseOrder());
```

**Lambda type inference breaks with `.reversed()`**

```java
// WRONG: compile error. Java can't infer the type of e, so it treats it as Object.
Comparator<Employee> cmp = Comparator.comparing(e -> e.getName()).reversed();

// RIGHT: use a method reference, or give the lambda a type
Comparator<Employee> cmp = Comparator.comparing(Employee::getName).reversed();
Comparator<Employee> cmp = Comparator.comparing((Employee e) -> e.getName()).reversed();
```

**Nulls crash the sort**

```java
// WRONG: NullPointerException if any email is null
users.sort(Comparator.comparing(User::getEmail));

// RIGHT: decide where nulls go
users.sort(Comparator.comparing(User::getEmail, Comparator.nullsLast(Comparator.naturalOrder())));
```

**TreeSet silently drops "duplicates"**

```java
// Two different employees with the same salary: only ONE ends up in the set.
Set<Employee> set = new TreeSet<>(Comparator.comparingInt(Employee::getSalary));

// RIGHT: add a tie-breaker that makes distinct objects compare as different
new TreeSet<>(Comparator.comparingInt(Employee::getSalary).thenComparing(Employee::getId));
```

**Comparing `Integer` objects with `==` inside a comparator**

```java
// WRONG: == on Integer compares references, unreliable above 127
(a, b) -> a.getAge() == b.getAge() ? 0 : ...
// RIGHT
Comparator.comparing(Person::getAge)
```

## 6. When to use / when not to

**Use `Comparator` when:**
- you need several different orders for the same class
- you're sorting a class you don't own (`String`, a library type)
- the order is specific to one screen or report, not a property of the type
- you need a sort order for a `TreeMap`, `TreeSet` or `PriorityQueue` that differs from the natural one

**Use `Comparable` instead when:**
- the class has one obvious natural order (`Money` by amount, `Version` by number) that almost every caller wants

Very often you do both: `Comparable` for the default, `Comparator`s for the special cases.

## 7. Interview angle

**Q: Comparable vs Comparator?**
`Comparable` is implemented by the class itself (`compareTo(T other)`, `java.lang`) and defines one natural order. `Comparator` is a separate object (`compare(T a, T b)`, `java.util`) and you can have as many as you want, including for classes you can't modify.

**Q: Is Comparator a functional interface? It has more than one method.**
Yes. It has exactly one *abstract* method, `compare`. The rest (`reversed`, `thenComparing`, ...) are `default` or `static` methods. (`equals` is also declared, but methods from `Object` don't count.)

**Q: Why is `(a, b) -> a.x - b.x` a bad comparator?**
Integer overflow. If `a.x` is large positive and `b.x` large negative, the subtraction wraps around and the sign flips. Use `Integer.compare` or `comparingInt`.

**Q: What does "consistent with equals" mean, and why does it matter?**
`compare(a, b) == 0` exactly when `a.equals(b)`. If not, sorted collections like `TreeSet` disagree with `HashSet` about what a duplicate is, and may drop elements you expected to keep.

**Q: How do you sort by multiple fields, one descending?**
`Comparator.comparing(A::getX).thenComparing(A::getY, Comparator.reverseOrder())`. Don't put `.reversed()` at the end unless you want the whole chain reversed.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|---|---|---|
| 1 | `exercises/Exercise1_MovieSort.java` | Sort movies three different ways using `Comparator` factory methods | Easy |
| 2 | `exercises/Exercise2_EmployeeDirectory.java` | Multi-field sort with mixed ascending/descending, plus null handling | Medium |
| 3 | `exercises/Exercise3_TaskQueueBug.java` | Find and fix the bugs in a task scheduler built on `PriorityQueue` and `TreeSet` | Hard |

## 9. Extra: the contract's symmetry rule, and the two `thenComparing`s

Source: https://dev.java/learn/language/fp/lambdas/writing-comparators

Added from the dev.java "Writing and Combining Comparators" page.

**Antisymmetry.** Besides the sign rule in section 3, a comparator must agree with itself when the arguments are swapped: `compare(a, b)` and `compare(b, a)` must have **opposite signs** (or both be 0). Break it and sorting can give different results depending on the input order, and `TimSort` may even throw `IllegalArgumentException: Comparison method violates its general contract!`.
```java
// WRONG: never returns a negative number, so compare(a, b) and compare(b, a) can both be 1
Comparator<Task> urgentFirst = (a, b) -> a.isUrgent() ? -1 : 1;
// RIGHT: both directions are consistent, and two urgent tasks compare as 0
Comparator<Task> urgentFirst = Comparator.comparing(Task::isUrgent).reversed();   // true before false
```

**Two kinds of `thenComparing`.** Pass a key extractor, or a whole comparator, which is handy when you already have one, e.g. a reusable `byName`:
```java
Comparator<Employee> byName = Comparator.comparing(Employee::getName);

Comparator.comparing(Employee::getDepartment).thenComparing(Employee::getSalary)   // a key extractor
Comparator.comparing(Employee::getDepartment).thenComparing(byName)                // a whole comparator
Comparator.comparing(Employee::getDepartment).thenComparingInt(Employee::getAge)   // primitive key, no boxing
```

Example: `examples/Example3_ComparatorContract.java`.
