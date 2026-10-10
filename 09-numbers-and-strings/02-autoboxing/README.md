# Autoboxing and Unboxing

Source: https://dev.java/learn/language/constructs/numbers-strings/autoboxing

← Back to [Numbers and Strings](../README.md)

## 1. What it is

**Autoboxing** is the compiler automatically wrapping a primitive in its wrapper object (`int` → `Integer`). **Unboxing** is the reverse (`Integer` → `int`). It lets you write `list.add(5)` instead of `list.add(Integer.valueOf(5))`, and `int total = list.get(0) + 1` instead of `list.get(0).intValue() + 1`.

| Primitive | Wrapper |
|---|---|
| `boolean` | `Boolean` |
| `byte` | `Byte` |
| `char` | `Character` |
| `short` | `Short` |
| `int` | `Integer` |
| `long` | `Long` |
| `float` | `Float` |
| `double` | `Double` |

## 2. Why it exists

Collections and generics only work with objects: there's no `List<int>`. Before Java 5, every add and get needed a manual conversion, which buried the logic in noise. Autoboxing hides that conversion, but it doesn't remove it. Knowing where it happens explains several classic bugs.

## 3. Core concepts

**Autoboxing happens when a primitive is**
- assigned to a wrapper variable: `Integer count = 5;`
- passed where a wrapper (or `Object`) is expected: `list.add(5);`, `map.put("a", 1);`

**Unboxing happens when a wrapper is**
- assigned to a primitive variable: `int n = list.get(0);`
- passed where a primitive is expected: `Math.abs(boxedValue)`
- used with arithmetic or comparison operators: `boxed + 1`, `boxed % 2`, `boxed > 3`, `boxed++`

```java
List<Integer> scores = new ArrayList<>();
scores.add(90);                 // boxing: Integer.valueOf(90)
int first = scores.get(0);      // unboxing: scores.get(0).intValue()

Integer total = 0;
for (int s : scores) {
    total += s;                 // unbox total, add, box the result again
}
```

## 4. How it works under the hood

- Boxing compiles to `Integer.valueOf(x)`; unboxing compiles to `x.intValue()` (and `doubleValue()`, `booleanValue()` ... for the others).
- **The Integer cache**: `Integer.valueOf` returns **the same object** for values from **-128 to 127**. Outside that range, each boxing usually creates a new object. `Short`, `Byte`, `Long` and `Character` (0..127) cache too; `Float` and `Double` don't.
- **`==` on two wrappers compares references**, not values. Because of the cache, it "works" for small numbers and fails for bigger ones.
- **`==` between a wrapper and a primitive** unboxes the wrapper and compares values.
- **Unboxing `null` throws `NullPointerException`**: there's no `int` value for "nothing".
- Every boxing may allocate an object. In a hot loop with millions of iterations, `Integer`/`Long` accumulators are much slower than `int`/`long`.

## 5. Common mistakes and gotchas

**Comparing wrappers with `==`**
```java
Integer a = 127, b = 127;
a == b                    // true (cached object)
Integer c = 128, d = 128;
c == d                    // WRONG: false (two different objects)
c.equals(d)               // RIGHT: true
```

**Unboxing null**
```java
Map<String, Integer> stock = new HashMap<>();
int qty = stock.get("pen");                  // WRONG: NullPointerException, key missing
int qty = stock.getOrDefault("pen", 0);      // RIGHT
```

**`List<Integer>.remove(int)` vs `remove(Object)`**
```java
List<Integer> ids = new ArrayList<>(List.of(10, 20, 30));
ids.remove(10);                      // WRONG: removes the element at INDEX 10 → exception
ids.remove(Integer.valueOf(10));     // RIGHT: removes the value 10
```

**Boxed accumulator in a loop**
```java
Long sum = 0L;                        // WRONG: boxes a new Long every iteration
for (long i = 0; i < 1_000_000; i++) sum += i;
long sum = 0L;                        // RIGHT: primitive
```

**Ternary with mixed types unboxes**
```java
Integer cached = null;
Integer result = useCache ? cached : 0;   // WRONG: NPE when useCache is true. The 0 makes
                                          // the whole ternary int, so cached is unboxed
Integer result = useCache ? cached : Integer.valueOf(0);   // RIGHT: both sides Integer
```

**`equals` across types**: `Long.valueOf(5).equals(5)` is `false` (the `5` boxes to an `Integer`).

## 6. When to use / when not to

- **Use wrappers** in collections, generics, and fields where `null` legitimately means "not set" (an optional age from a form).
- **Use primitives** for local variables, counters, loop indexes, arithmetic and performance-sensitive code.
- Compare wrapper **values** with `equals` (or unbox first). Never `==`.
- Before unboxing something that could be `null` (map lookups, database fields), check for `null` or use a default.

## 7. Interview angle

1. **What is autoboxing?** The compiler automatically converting a primitive to its wrapper (via `valueOf`) where an object is needed, and back (via `xxxValue()`) where a primitive is needed.
2. **Why is `Integer.valueOf(127) == Integer.valueOf(127)` true but `128 == 128` (as Integers) false?** `Integer.valueOf` caches -128..127; outside that, separate objects are created and `==` compares references.
3. **What happens when you unbox `null`?** `NullPointerException`.
4. **What does `list.remove(1)` do on a `List<Integer>`?** It calls `remove(int index)`: removes the element at index 1, not the value 1.
5. **Is autoboxing free?** No. It can allocate objects and adds a method call; it matters in tight loops and large data sets.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_PredictBoxing.java` | Predict `==` and `equals` results for boxed values | Easy |
| 2 | `exercises/Exercise2_InventoryNulls.java` | Fix null-unboxing crashes in an inventory map | Medium |
| 3 | `exercises/Exercise3_BoxingBugs.java` | Fix a `remove` overload bug, a `==` bug and a ternary NPE | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
