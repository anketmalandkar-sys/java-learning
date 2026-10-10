# Control Flow Statements

Source: https://dev.java/learn/language/constructs/basics/controlling-flow

← Back to [Java Language Basics](../README.md)

## 1. What it is

By default, statements run top to bottom, once each. **Control flow statements** change that: they **decide** (`if`, `if-else`), **repeat** (`while`, `do-while`, `for`, enhanced `for`), and **jump** (`break`, `continue`, `return`, and `yield` inside switch expressions).

## 2. Why it exists

Real programs make decisions (is the password right?) and repeat work (for every order in the cart...). Without control flow you could only write straight-line scripts. Choosing the *right* construct also documents intent: a `for` says "counted", an enhanced `for` says "every element", a `do-while` says "at least once".

## 3. Core concepts

**if / else if / else**: the first true condition wins; the rest aren't checked.
```java
if (score >= 90) {
    grade = 'A';
} else if (score >= 80) {
    grade = 'B';
} else {
    grade = 'C';
}
```

**while**: checks first, may run zero times.
```java
while (balance < target) {
    balance += balance * rate;
    years++;
}
```

**do-while**: runs the body first, checks after: at least once.
```java
do {
    input = askUser();
} while (!isValid(input));
```

**for**: init; condition; update. The loop variable exists only inside the loop.
```java
for (int i = 0; i < 5; i++) { ... }
for (int i = 10; i > 0; i -= 2) { ... }   // counting down by 2
for (;;) { ... }                          // infinite loop: all parts optional
```

**Enhanced for** (for-each): every element of an array or collection, no index.
```java
for (String name : names) { ... }
```

**break**: leave the innermost loop (or `switch`) now.
**continue**: skip the rest of this pass, go to the next one.
```java
for (int n : numbers) {
    if (n < 0) continue;       // skip negatives
    if (n == 0) break;         // stop at the first zero
    sum += n;
}
```

**Labeled break / continue**: target an *outer* loop.
```java
search:
for (int r = 0; r < grid.length; r++) {
    for (int c = 0; c < grid[r].length; c++) {
        if (grid[r][c] == target) {
            found = true;
            break search;          // leaves BOTH loops
        }
    }
}
```

**return** leaves the method (with a value if the method isn't `void`). **yield** gives a value from a `switch` expression (lesson 10).

## 4. How it works under the hood

- The `for` loop runs in this order: init once → check → body → update → check → body → ... The update runs **after** the body, including after a `continue`.
- In a `while` loop, `continue` jumps straight to the condition check. If your update (`i++`) is at the bottom of the body, `continue` skips it → infinite loop.
- A label names a statement; `break label` jumps to **just after** that statement, not back to the label.
- The enhanced `for` over an array is compiled to an index loop; over a collection, to an `Iterator`. That's why you can't remove from a list inside a for-each (`ConcurrentModificationException`).

## 5. Common mistakes and gotchas

**`continue` skipping the update in a while loop**
```java
int i = 0;
while (i < 10) {
    if (i % 2 == 0) continue;   // WRONG: i never changes → infinite loop
    i++;
}
for (int i = 0; i < 10; i++) {  // RIGHT: the update always runs
    if (i % 2 == 0) continue;
}
```

**Off-by-one bounds**
```java
for (int i = 1; i < 10; i++)    // runs 9 times, not 10
for (int i = 0; i < 10; i++)    // RIGHT: 10 times
```

**Wrong order in an else-if chain**
```java
if (score >= 50) grade = "Pass";
else if (score >= 90) grade = "Distinction";   // WRONG: never reached

if (score >= 90) grade = "Distinction";        // RIGHT: most specific first
else if (score >= 50) grade = "Pass";
```

**`break` in nested loops only leaves the inner loop**: use a label, or move the loops into a method and `return`.

**Using the loop variable after the loop**: `for (int i = ...) {}` then `i` → compile error. Declare it before the loop if you need it later.

## 6. When to use / when not to

| Need | Use |
|---|---|
| Every element, no index needed | enhanced `for` |
| Counted loop / need the index | `for` |
| Repeat until a condition changes, maybe zero times | `while` |
| Must run at least once (menus, input retries) | `do-while` |
| Stop early when found | `break` (or `return` from a helper method) |
| Skip some elements | `continue`, or an `if` around the body if that's clearer |
| Leave nested loops | labeled `break`, but a helper method with `return` is often cleaner |

## 7. Interview angle

1. **Difference between `while` and `do-while`?** `while` checks before the body (0+ runs); `do-while` checks after (1+ runs).
2. **What does `continue` do in a `for` loop vs a `while` loop?** In `for`, it jumps to the update then the check. In `while`, it jumps straight to the check, so an update at the end of the body is skipped.
3. **How do you exit two nested loops at once?** A labeled `break outer;`, or put the loops in a method and `return`.
4. **Can you modify a list while looping over it with for-each?** Not structurally (add/remove); it throws `ConcurrentModificationException`. Use an `Iterator` and `iterator.remove()`, or `removeIf`.
5. **Write an infinite loop with `for`.** `for (;;) { ... }`

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_GradesAndLoops.java` | if/else-if chain, a counting loop with `continue`, a countdown with `while` | Easy |
| 2 | `exercises/Exercise2_RetryAndSearch.java` | Retry with `do-while`, stop early with `break`, compound interest with `while` | Medium |
| 3 | `exercises/Exercise3_NestedLoops.java` | Labeled `break` and `continue` in 2D searches | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
