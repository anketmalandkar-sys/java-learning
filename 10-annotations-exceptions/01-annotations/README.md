# Annotations

Source: https://dev.java/learn/language/annotations-exceptions/annotations

← Back to [Annotations and Exceptions](../README.md)

## 1. What it is

An **annotation** is metadata attached to a piece of code: `@Override`, `@Deprecated(since = "2.0")`, `@Test`. It doesn't change what the code does by itself. Something has to **read** it: the compiler (to check `@Override`), a build tool (to generate code), or your program at runtime (through reflection, like JUnit finding `@Test` methods or Spring finding `@Autowired` fields).

## 2. Why it exists

Before annotations, this kind of information lived in comments (which no tool can trust), naming conventions (`testSomething()`), or separate XML files that drifted out of sync with the code. Annotations put the metadata **next to the code it describes**, in a form the compiler checks and tools can read.

Three uses:
1. **Information for the compiler**: catch mistakes (`@Override`), silence warnings (`@SuppressWarnings`).
2. **Compile-time and build-time processing**: tools generate code, docs or config from annotations (Lombok, MapStruct, Javadoc).
3. **Runtime processing**: frameworks inspect annotations while the program runs (JUnit, Spring, Jackson).

## 3. Core concepts

**Syntax**
```java
@Override                                   // no elements: no parentheses
@SuppressWarnings("unchecked")              // one element named value: the name can be dropped
@SuppressWarnings(value = "unchecked")      // the same, written out
@Deprecated(since = "2.0", forRemoval = true)   // named elements
@SuppressWarnings({"unchecked", "rawtypes"})    // an array value
```
Several annotations can sit on one declaration, conventionally one per line.

**Where annotations go.** On declarations: classes, interfaces, methods, constructors, fields, parameters, local variables, packages, modules, record components, type parameters. Since Java 8 also on **type uses** (type annotations, below).

**Declaring your own** with `@interface`. Elements look like methods and can have defaults:
```java
@Retention(RetentionPolicy.RUNTIME)      // keep it until runtime, so reflection can see it
@Target(ElementType.METHOD)              // only allowed on methods
@interface Audited {
    String by();                         // required: no default
    int level() default 1;               // optional
    String[] tags() default {};          // arrays are allowed
}

@Audited(by = "asha", level = 2)
void transfer() { ... }
```
Element types are limited to primitives, `String`, `Class`, enums, other annotations, and arrays of those. Values must be **compile-time constants**.

**Predefined annotations (`java.lang`)**

| Annotation | What it does |
|---|---|
| `@Override` | Compile error if the method doesn't actually override or implement anything |
| `@Deprecated` | Marks something obsolete; using it gives a warning. `since = "..."`, and `forRemoval = true` (Java 9+) for a stronger "will be deleted" warning. Pair it with the Javadoc tag `@deprecated` (lowercase d) explaining what to use instead |
| `@SuppressWarnings` | Silences warning categories: `"unchecked"`, `"deprecation"`, `"removal"`, `"rawtypes"`, `"preview"`... |
| `@SafeVarargs` | Promises a generic varargs method doesn't misuse its array; silences heap-pollution warnings (see `04-generics/05`) |
| `@FunctionalInterface` | Compile error if the interface doesn't have exactly one abstract method |

**Meta-annotations (`java.lang.annotation`)**: annotations on annotation types.

| Meta-annotation | Meaning |
|---|---|
| `@Retention(SOURCE)` | Discarded by the compiler (`@Override`, `@SuppressWarnings`) |
| `@Retention(CLASS)` | Kept in the `.class` file, invisible at runtime. **The default** |
| `@Retention(RUNTIME)` | Readable through reflection at runtime |
| `@Target(...)` | Where it may be used: `TYPE`, `METHOD`, `FIELD`, `PARAMETER`, `CONSTRUCTOR`, `LOCAL_VARIABLE`, `ANNOTATION_TYPE`, `PACKAGE`, `MODULE`, `RECORD_COMPONENT`, `TYPE_PARAMETER`, `TYPE_USE` |
| `@Documented` | Uses of the annotation appear in the generated Javadoc |
| `@Inherited` | A class annotation is inherited by subclasses (class annotations only) |
| `@Repeatable(Container.class)` | The annotation may appear more than once on the same element |

**Type annotations (Java 8+).** With `@Target(ElementType.TYPE_USE)`, an annotation can go anywhere a type is used:
```java
@NonNull String name;
List<@NonNull String> names;
String s = (@NonNull String) obj;
class Cache implements @Readonly Map<String, String> { ... }
void open() throws @Critical IOException { ... }
```
Java itself doesn't check them. **Pluggable type checkers** such as the Checker Framework run alongside `javac` and report, for example, a possible `null` stored in a `@NonNull` variable.

**Repeating annotations (Java 8+)** take two declarations: the repeatable annotation, and a container whose `value()` is an array of it.
```java
@Repeatable(Schedules.class)
@Retention(RetentionPolicy.RUNTIME)
@interface Schedule { String day(); }

@Retention(RetentionPolicy.RUNTIME)
@interface Schedules { Schedule[] value(); }

@Schedule(day = "Mon")
@Schedule(day = "Thu")
void backup() { ... }
```
Repeating an annotation without `@Repeatable` is a compile error.

**Reading annotations at runtime** (reflection, `AnnotatedElement`):
```java
Method m = Jobs.class.getDeclaredMethod("backup");
Audited a = m.getAnnotation(Audited.class);              // null if absent (or not RUNTIME)
Schedule[] all = m.getAnnotationsByType(Schedule.class);  // handles repeated ones: both schedules
boolean marked = m.isAnnotationPresent(Audited.class);
```

## 4. How it works under the hood

- An annotation type is compiled to a special **interface** extending `java.lang.annotation.Annotation`. At runtime, `getAnnotation` returns a proxy object implementing it, and calling `a.by()` returns the stored value.
- **Retention decides who can see it.** `SOURCE` annotations never reach the `.class` file. `CLASS` annotations are in the file (tools like bytecode analysers can read them) but reflection can't. Only `RUNTIME` annotations are visible to `getAnnotation`.
- Repeated annotations are stored **wrapped in the container**: two `@Schedule`s become one `@Schedules({...})`. So `getAnnotation(Schedule.class)` returns `null` for a repeated element; use `getAnnotationsByType`.
- Annotations are inert. Without code that reads them (the compiler, a processor, a framework), they do nothing.

## 5. Common mistakes and gotchas

**Forgetting `RUNTIME` retention**
```java
@interface Audited { }                     // WRONG for reflection: default CLASS retention
m.getAnnotation(Audited.class)             // always null

@Retention(RetentionPolicy.RUNTIME)        // RIGHT
@interface Audited { }
```

**`getAnnotation` on a repeated annotation**: returns `null` (the container holds them). Use `getAnnotationsByType`.

**Non-constant element values**
```java
@Audited(by = currentUser())               // WRONG: compile error, must be a constant
@Audited(by = "asha")                      // RIGHT
```

**`@Deprecated` without guidance.** Always say `since` and, in the Javadoc `@deprecated` tag, what to use instead.

**Over-broad `@SuppressWarnings`** on a whole class hides future problems. Put it on the smallest declaration.

**Expecting `@Inherited` to work on methods or interfaces.** It only affects annotations on **classes**, inherited by **subclasses**.

## 6. When to use / when not to

- Use the predefined ones always: `@Override` on every override, `@FunctionalInterface` on your functional interfaces, `@Deprecated(since=..)` when retiring an API.
- Write your own annotation when a tool or framework (or your own reflection code) will **read** it: marking tests, validation rules (`@NotBlank`), permissions (`@RequiresRole("admin")`), auditing.
- Don't use annotations to carry logic that a plain method call or interface would express more clearly. "Magic" behaviour driven by annotations is hard to trace.
- Restrict custom annotations with `@Target`, and choose `@Retention` deliberately.

## 7. Interview angle

1. **What are the three retention policies?** `SOURCE` (dropped by the compiler), `CLASS` (in the class file, not visible at runtime; the default), `RUNTIME` (readable via reflection).
2. **What does `@Target` do?** Restricts which program elements an annotation can be applied to, e.g. `METHOD`, `FIELD`, `TYPE_USE`.
3. **How do repeating annotations work?** Mark the annotation `@Repeatable(Container.class)` and declare a container annotation with a `value()` array. The compiler wraps repeated uses into the container; read them with `getAnnotationsByType`.
4. **What's the difference between `@Deprecated` and `@deprecated`?** `@Deprecated` is the annotation the compiler acts on (warnings); `@deprecated` is the Javadoc tag that documents why and what to use instead. Use both.
5. **Do annotations change program behaviour?** Not on their own. They're metadata; behaviour changes only if the compiler, an annotation processor, or runtime code reads them.

## 8. Exercises

| # | File | Goal | Difficulty |
|---|------|------|------------|
| 1 | `exercises/Exercise1_DeclareAndRead.java` | Declare a runtime annotation with elements and defaults, and read it with reflection | Easy |
| 2 | `exercises/Exercise2_MiniTestRunner.java` | Build a tiny JUnit-style runner that finds and runs `@Check` methods, honours `@Skip`, and reports results | Medium |
| 3 | `exercises/Exercise3_RolesAndRetention.java` | Fix a permission system whose annotations are invisible at runtime, make a role annotation repeatable, and restrict where it can be used | Hard |

Run each with `java exercises/ExerciseN_*.java` from this folder; each prints PASS/FAIL.
