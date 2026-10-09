[← Generics roadmap](../README.md)

# Type inference and target types

## 1. What it is

**Type inference** is the compiler working out type arguments you didn't write. In `List<String> names = new ArrayList<>()` or `lastOf(List.of("Pune", "Goa"))`, nobody wrote `String` in the angle brackets. The compiler figured it out.

It uses two sources of information:
- **The arguments** you pass to a generic method or constructor.
- **The target type**: the type the result is expected to have, based on where the expression appears. That can be the variable it's assigned to, the parameter it's passed to, or the return type of the method it's returned from.

Lambdas rely on the target type even more. A lambda has no type of its own. It takes the type of whatever slot it's put in.

## 2. Why it exists

Without inference you'd repeat every type, every time (this is what Java 6 code looked like):

```java
Map<String, List<Order>> byCustomer = new HashMap<String, List<Order>>();
List<String> none = Collections.<String>emptyList();
```

With inference:

```java
Map<String, List<Order>> byCustomer = new HashMap<>();
List<String> none = Collections.emptyList();
```

Same types, same safety, less noise. Inference never weakens type checking. It only saves you typing what the compiler can already work out.

## 3. Core concepts

**Inference from arguments.** The compiler picks the most specific type that fits *all* the arguments:

```java
static <T> T pick(T a, T b) { return b; }

String s = pick("a", "b");                          // T = String
Serializable x = pick("d", new ArrayList<String>()); // T = Serializable: the closest type both share
```

**Inference from the target type.** `Collections.emptyList()` takes no arguments, so the compiler looks at where the result goes:

```java
List<String> names = Collections.emptyList();   // target is List<String>, so T = String
```

**Method arguments are target types too (Java 8+).** Passing a generic call straight into a method works:

```java
static void printAll(List<String> names) { ... }

printAll(Collections.emptyList());   // T = String, inferred from printAll's parameter
```

In Java 7 this was a compile error: `T` defaulted to `Object`, giving a `List<Object>`.

**Explicit type witness.** When inference can't work it out, write the type argument yourself, between the dot and the method name:

```java
List<String> names = Collections.<String>emptyList();
int size = Collections.<String>emptyList().size();
```

A witness needs something before the dot: the class name for a static method (`Util.<String>build()`) or `this` for an instance method (`this.<String>build()`). Plain `<String>build()` doesn't compile.

**Diamond and generic constructors.** The diamond infers the class's type arguments. If the constructor declares its own type parameter, that is inferred too:

```java
class Box<T> {
    <U> Box(U seed) { ... }
}

Box<Integer> box = new Box<>("seed");   // T = Integer (from the target), U = String (from the argument)
```

**Lambdas get their type from the target.** The same text can become different functional interfaces:

```java
Function<Integer, Integer> f = x -> x * 2;
UnaryOperator<Integer>     g = x -> x * 2;
```

A lambda can only appear where there is a target type:

| Context | Example |
|---------|---------|
| Variable declaration / assignment | `Runnable r = () -> log("hi");` |
| Return statement | `return order -> order.total() > 100;` |
| Method or constructor argument | `orders.removeIf(o -> o.isCancelled());` |
| Conditional `?:` | `Comparator<Order> c = byDate ? (a, b) -> ... : (a, b) -> ...;` |
| Cast | `Object task = (Runnable) () -> log("hi");` |
| Array initializer | `Runnable[] steps = { () -> load(), () -> save() };` |
| Lambda body | `Supplier<Runnable> s = () -> () -> log("hi");` |

**Overloads: `Runnable` vs `Callable`.** When a method is overloaded, the lambda's shape decides which version is called:

```java
void submit(Runnable task)        // run(): returns nothing
<T> void submit(Callable<T> task) // call(): returns a T

submit(() -> "done");             // returns a value  -> Callable<String>
submit(() -> { log("done"); });   // returns nothing  -> Runnable
submit(() -> counter.incrementAndGet()); // fits both; Callable wins because it returns a value
```

## 4. How it works under the hood

Inference is a compile-time puzzle. For each call, the compiler collects constraints from the arguments and from the target type, then picks the most specific type that satisfies all of them. After that, erasure removes the types as usual (lesson 05).

Two rules explain most surprises:
- **Inference only looks at the call and its target.** It never reads later lines. `var list = new ArrayList<>();` has no target type, so it becomes `ArrayList<Object>`, even if you only ever add strings afterwards.
- **A method call that is followed by `.something()` has no target type.** In `Comparator.comparing(p -> p.name()).reversed()`, `comparing(...)` is the *receiver* of `.reversed()`, not the value assigned to the variable. So nothing tells the compiler what `p` is, and it falls back to `Object`.

## 5. Common mistakes and gotchas

**Chaining after a generic call loses the target type.**

```java
Comparator<Employee> c = Comparator.comparing(e -> e.name()).reversed();   // wrong: e is Object, no name()

Comparator<Employee> c = Comparator.comparing((Employee e) -> e.name()).reversed();   // right: typed lambda
Comparator<Employee> c = Comparator.comparing(Employee::name).reversed();             // right: method ref names the class
Comparator<Employee> c = Comparator.<Employee, String>comparing(e -> e.name()).reversed();   // right: witness
```

**`var` with the diamond.** There's no target type to infer from:

```java
var tags = new ArrayList<>();          // wrong: ArrayList<Object>
var tags = new ArrayList<String>();    // right
List<String> tags = new ArrayList<>(); // right
```

**A block lambda that forgets `return` picks a different overload.**

```java
executor.submit(() -> { computeTotal(); });         // wrong: no value returned, so it's a Runnable; result lost
executor.submit(() -> computeTotal());              // right: Callable, the result is kept
executor.submit(() -> { return computeTotal(); });  // right
```

**A type witness without a qualifier.**

```java
List<String> a = <String>emptyList();              // wrong: doesn't compile
List<String> a = Collections.<String>emptyList();  // right
```

**A lambda with no target.**

```java
Object task = () -> log("hi");             // wrong: Object isn't a functional interface
Object task = (Runnable) () -> log("hi");  // right: the cast gives it a target
```

## 6. When to use / when not to

**Let inference do the work** almost always. Write `new HashMap<>()`, call generic methods without witnesses, and let lambdas take their types from context.

**Step in when the compiler gets it wrong or can't decide:**
- Prefer the gentlest fix: a method reference (`Employee::name`) or an explicitly typed lambda (`(Employee e) -> ...`).
- Next, split a chain into a local variable with a declared type.
- Use a type witness (`Comparator.<Employee, String>comparing(...)`) when the other fixes don't read well.

**Don't** add witnesses "just to be explicit" everywhere. They're noise when inference already works.

## 7. Interview angle

1. **What is a target type?** The type the compiler expects an expression to have, based on where it appears: the assigned variable, a method parameter, a return type, a cast. Generic methods and lambdas use it for inference.
2. **What is an explicit type witness and when do you need one?** Writing the type argument at the call site: `Collections.<String>emptyList()`. You need it when the compiler has no target type to infer from, e.g. in the middle of a method chain.
3. **Why does `Comparator.comparing(e -> e.getName()).reversed()` fail to compile?** `comparing(...)` is the receiver of `.reversed()`, so it has no target type, and `e` is inferred as `Object`. Fix it with `Employee::getName`, `(Employee e) -> ...` or a witness.
4. **What changed about inference in Java 8?** Method arguments became target types. `printAll(Collections.emptyList())` compiles in Java 8 but not in Java 7. Inference also became powerful enough to support lambdas and streams.
5. **`executor.submit(() -> doWork())`: `Runnable` or `Callable`?** If `doWork()` returns a value, `Callable`, because both fit and `Callable` is more specific. If it's `void`, only `Runnable` fits.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_InventoryFactories.java` | Write generic factory methods whose types come entirely from inference: from arguments, from the target, and through a type witness | Easy |
| 2 | `exercises/Exercise2_EmployeeSorting.java` | Build chained comparators (`reversed()`, `thenComparing`) and get around the "lambda parameter is Object" inference trap | Medium |
| 3 | `exercises/Exercise3_TaskRunnerBug.java` | A task runner silently drops results. Find why the wrong overload (`Runnable` vs `Callable`) is chosen and fix the call sites | Hard |
