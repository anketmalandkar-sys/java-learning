# Arrays

Source: https://dev.java/learn/language/constructs/basics/arrays

← Back to [Java Language Basics](../README.md)

## 1. What it is

An **array** is an object that holds a **fixed number** of values of **one type**, each reached by a zero-based index. Its length is set when it's created and never changes. `java.util.Arrays` adds helpers for printing, sorting, searching, filling, copying and comparing arrays.

## 2. Why it exists

Without arrays, ten test scores would need ten variables (`score1` … `score10`) and no way to loop over them. An array gives you one name, an index, and a loop. It's also the building block under `ArrayList`, `String` and many other classes.

## 3. Core concepts

**Declare, create, use**
```java
int[] scores;              // declares a variable; no array exists yet
scores = new int[5];       // creates 5 ints, all 0
scores[0] = 90;            // index 0 is the first element
scores[4] = 75;            // index length-1 is the last
System.out.println(scores.length);   // 5: a field, no parentheses
```

**Create and fill in one step**
```java
String[] days = {"Mon", "Tue", "Wed"};       // length comes from the values
double[] prices = new double[] {9.5, 12.0};  // needed when not in a declaration
```

**Loop over it**
```java
for (int i = 0; i < scores.length; i++) { ... }   // when you need the index
for (int score : scores) { ... }                  // when you only need the value
```

**Multi-dimensional = arrays of arrays.** Rows can have different lengths ("jagged").
```java
int[][] grid = new int[3][4];            // 3 rows of 4
String[][] seats = {
    {"A1", "A2", "A3"},
    {"B1", "B2"}                          // shorter row: perfectly legal
};
System.out.println(seats[1].length);     // 2
```

**Copying**
```java
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[3];
System.arraycopy(src, 1, dest, 0, 3);           // dest must exist: [2, 3, 4]
int[] part = Arrays.copyOfRange(src, 1, 4);     // creates it for you: [2, 3, 4], end exclusive
int[] bigger = Arrays.copyOf(src, 7);           // [1, 2, 3, 4, 5, 0, 0]
```

**`java.util.Arrays` helpers**
```java
Arrays.toString(src)          // "[1, 2, 3, 4, 5]"
Arrays.deepToString(grid)     // for 2D arrays
Arrays.sort(src);             // ascending, in place
Arrays.binarySearch(src, 4)   // index of 4; the array MUST be sorted first
Arrays.fill(dest, -1);        // every slot = -1
Arrays.equals(a, b)           // same length and same elements?
Arrays.stream(src).sum()      // into a stream (Java 8+)
```

## 4. How it works under the hood

- An array is an **object** on the heap; the variable holds a **reference** to it. `int[] b = a;` copies the reference, so `a` and `b` are the same array.
- New arrays are filled with default values: `0`, `false`, `'\u0000'`, or `null` for object arrays.
- Every access is **bounds-checked**: `scores[5]` on a length-5 array throws `ArrayIndexOutOfBoundsException`.
- A 2D array is an array of references to row arrays. The rows aren't laid out as one block (unlike C), which is why they can differ in length.

## 5. Common mistakes and gotchas

**Off-by-one in the loop**
```java
for (int i = 0; i <= scores.length; i++)   // WRONG: index length throws
for (int i = 0; i < scores.length; i++)    // RIGHT
```

**Printing an array directly**
```java
System.out.println(scores);                     // WRONG: prints something like [I@1b6d3586
System.out.println(Arrays.toString(scores));    // RIGHT: [90, 0, 0, 0, 75]
```

**Comparing with `==` or `equals`**
```java
a == b; a.equals(b);       // WRONG: both check "same object"
Arrays.equals(a, b);       // RIGHT: compares contents
```

**"Copying" by assignment**
```java
int[] backup = scores;                          // WRONG: same array, changes show up in both
int[] backup = Arrays.copyOf(scores, scores.length);   // RIGHT: independent copy
```

**Assuming rows have equal length**
```java
for (int c = 0; c < grid[0].length; c++)   // WRONG for jagged arrays
for (int c = 0; c < grid[r].length; c++)   // RIGHT: each row's own length
```

**Brackets after the name**: `int scores[];` compiles, but write `int[] scores;`. The brackets are part of the type.

## 6. When to use / when not to

- **Use an array** when the size is known and fixed (12 months, a 9×9 board), for primitive data where speed and memory matter, or when an API needs one (`main(String[] args)`).
- **Use an `ArrayList`** when you need to add or remove items, or don't know the size up front.
- Use `Arrays.copyOfRange` when you just want a piece; use `System.arraycopy` to copy into an array you already have.

## 7. Interview angle

1. **Can you change an array's length?** No. Create a new array (e.g. `Arrays.copyOf(arr, newLength)`) and use that.
2. **`length` vs `length()` vs `size()`?** Arrays: `length` field. `String`: `length()` method. Collections: `size()` method.
3. **What's a jagged array?** A 2D array whose rows have different lengths. Possible because a 2D array is an array of row references.
4. **How do you compare two arrays' contents?** `Arrays.equals` (or `Arrays.deepEquals` for nested arrays). `==` and `.equals` compare references.
5. **What's the precondition for `Arrays.binarySearch`?** The array must be sorted; otherwise the result is undefined.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_TemperatureStats.java` | Loop over an array: average, max, and days above average | Easy |
| 2 | `exercises/Exercise2_SeatMap.java` | Work with a jagged 2D array of cinema seats | Medium |
| 3 | `exercises/Exercise3_PlaylistBugs.java` | Fix three array bugs: an aliasing "copy", `==` comparison, and an off-by-one rotation | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
