# Comparable

## 1. What it is

`Comparable<T>` is an interface from `java.lang` with one method:

```java
public interface Comparable<T> {
    int compareTo(T other);
}
```

A class that implements it has a **natural ordering**: one built-in, default way to sort its objects. `String`, `Integer`, `LocalDate` and `BigDecimal` all implement it, which is why `Collections.sort(listOfStrings)` just works.

## 2. Why it exists

Sorting needs an answer to "is A before B?". For `int`, Java already knows. For your own `Employee` class, it doesn't, so this fails:

```java
List<Employee> staff = ...;
Collections.sort(staff);   // compile error: Employee is not Comparable
```

Without `Comparable`, you'd have to pass a comparison rule every time you sort, or write your own sort loop. `Comparable` lets the class say once "this is how I'm normally ordered", and every sorting API (`Collections.sort`, `List.sort(null)`, `Arrays.sort`, `TreeSet`, `TreeMap`, `PriorityQueue`, `Collections.max`) picks it up automatically.

## 3. Core concepts

**The return value is all about the sign.**

| `a.compareTo(b)` returns | Meaning |
|---|---|
| negative (any value < 0) | `a` comes **before** `b` |
| zero | `a` and `b` are **equal** in ordering |
| positive (any value > 0) | `a` comes **after** `b` |

Don't check `== -1` or `== 1`. Only the sign is guaranteed.

**Implementing it**

```java
class Student implements Comparable<Student> {
    String name;
    int rollNumber;

    @Override
    public int compareTo(Student other) {
        return Integer.compare(this.rollNumber, other.rollNumber); // ascending by roll number
    }
}
```

**Reuse the built-in compare helpers.** `Integer.compare`, `Long.compare`, `Double.compare`, and `compareTo` on `String`, `LocalDate` and `BigDecimal`.

**Descending order:** flip the arguments.

```java
return Integer.compare(other.rollNumber, this.rollNumber); // descending
```

**Multiple fields (tie-breakers):** compare the first field and only move on when it's a tie.

```java
public int compareTo(Employee other) {
    int byDept = this.department.compareTo(other.department);
    if (byDept != 0) {
        return byDept;
    }
    return Integer.compare(this.id, other.id);
}
```

**Sorted collections use it automatically.**

```java
TreeSet<Student> set = new TreeSet<>(students);         // kept in natural order
PriorityQueue<Task> queue = new PriorityQueue<>();       // poll() returns the "smallest"
Student top = Collections.min(students);
```

## 4. How it works under the hood

- Sort algorithms (TimSort for objects) only ever ask one question: `a.compareTo(b)`. Your method *is* the ordering. If it's inconsistent, the sort is wrong or throws `IllegalArgumentException: Comparison method violates its general contract!`.
- `TreeSet` and `TreeMap` are red-black trees. They use **`compareTo`, not `equals`**, to decide whether an element is already present. If `compareTo` returns 0, the tree treats the two objects as duplicates.
- `HashSet` and `HashMap` use `equals` and `hashCode`, not `compareTo`. So a class whose `compareTo` disagrees with `equals` behaves differently in a `HashSet` and in a `TreeSet`.

**The contract** (what your `compareTo` must satisfy):
1. **Antisymmetric:** `sign(a.compareTo(b)) == -sign(b.compareTo(a))`
2. **Transitive:** if `a > b` and `b > c`, then `a > c`
3. **Consistent:** if `a.compareTo(b) == 0`, then `a` and `b` compare the same against everything else
4. **Strongly recommended:** `a.compareTo(b) == 0` exactly when `a.equals(b)` (*consistent with equals*)

## 5. Common mistakes and gotchas

**1. Subtraction trick: integer overflow**

```java
// WRONG: overflows when values are far apart
return this.balance - other.balance;
// Integer.MIN_VALUE - 1 wraps to a huge positive number, so the order flips

// RIGHT
return Integer.compare(this.balance, other.balance);
```

**2. Casting a `long` or `double` difference to `int`**

```java
// WRONG: 0.4 - 0.1 = 0.3, cast to int = 0, so they look "equal"
return (int) (this.price - other.price);

// RIGHT
return Double.compare(this.price, other.price);
```

**3. Inconsistent with equals: `TreeSet` silently drops elements**

```java
// Employee compares by salary only
public int compareTo(Employee o) { return Integer.compare(salary, o.salary); }

Set<Employee> set = new TreeSet<>();
set.add(new Employee("Asha", 50_000));
set.add(new Employee("Ravi", 50_000));   // compareTo == 0, treated as a duplicate and NOT added
System.out.println(set.size());           // 1

// RIGHT: add a tie-breaker on a unique field
int bySalary = Integer.compare(salary, o.salary);
return bySalary != 0 ? bySalary : name.compareTo(o.name);
```

**4. Raw type `Comparable`**

```java
// WRONG: needs a cast and fails at runtime with the wrong type
class Book implements Comparable {
    public int compareTo(Object o) { Book b = (Book) o; ... }
}

// RIGHT
class Book implements Comparable<Book> {
    public int compareTo(Book other) { ... }
}
```

**5. Nulls:** `compareTo(null)` should throw `NullPointerException`. If a field can be null, handle it explicitly. Calling `this.name.compareTo(...)` on a null name crashes the sort.

**6. Mutating a field used in `compareTo` while the object is inside a `TreeSet` or `TreeMap`.** The tree doesn't re-sort, so lookups break. Make the compared fields `final`, or remove the object, change it, and re-add it.

## 6. When to use / when not to

**Use `Comparable` when:**
- The class has **one obvious, natural order**: numbers, dates, versions, money, IDs, ranks.
- You want it to work out of the box with `TreeSet`, `TreeMap`, `PriorityQueue` and `sort()`.

**Don't use it (use a `Comparator` instead) when:**
- There's no single obvious order. Should a `Product` sort by price, name or rating? It depends on the screen.
- You need several different orders for the same class.
- You can't modify the class (third-party code).

`Comparable` is the class's *default* order, built in. `Comparator` is an *external* rule you pass in, and you can have as many as you like.

## 7. Interview angle

**Q: Comparable vs Comparator?**
`Comparable` is implemented by the class itself (`compareTo(T)`, in `java.lang`) and defines one natural order. `Comparator` is a separate object (`compare(T, T)`, in `java.util`) and defines any number of external orders. Use `Comparable` for the default order and `Comparator` for alternatives or classes you don't own.

**Q: Why is `return a - b;` a bad `compareTo`?**
Integer overflow. With large or opposite-sign values the subtraction wraps around and returns the wrong sign. Use `Integer.compare(a, b)`.

**Q: What does "consistent with equals" mean and why does it matter?**
`compareTo` returns 0 exactly when `equals` returns true. If they disagree, `TreeSet` and `TreeMap` (which use `compareTo`) and `HashSet` and `HashMap` (which use `equals`) disagree about duplicates. Classic example: `new BigDecimal("1.0")` and `new BigDecimal("1.00")` are not `equals`, but `compareTo` returns 0, so a `HashSet` holds 2 of them and a `TreeSet` holds 1.

**Q: What happens if you put a non-Comparable object in a `TreeSet` without a Comparator?**
It compiles, but `add()` throws `ClassCastException` at runtime, because the tree casts the element to `Comparable`.

**Q: What happens if `compareTo` breaks the contract?**
Sorting can produce a wrong order, or `TimSort` can detect it and throw `IllegalArgumentException: Comparison method violates its general contract!`.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|---|---|---|
| 1 | `exercises/Exercise1_BookSort.java` | Give `Book` a natural order by title and sort a list. | Easy |
| 2 | `exercises/Exercise2_ProductRanking.java` | Multi-field order (price ascending, then name), used with `TreeSet`, `Collections.min` and `Collections.max`. | Medium |
| 3 | `exercises/Exercise3_LeaderboardBug.java` | A leaderboard loses players and misorders scores. Find and fix the bugs in `compareTo`. | Hard |

Run any file directly: `java exercises/Exercise1_BookSort.java`. Each one prints PASS or FAIL.
