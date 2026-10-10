# Java Language Basics

Source: https://dev.java/learn/language/constructs/basics

## 1. Big picture

These are the building blocks every Java program is made of: **variables** that hold values, **types** that say what kind of value, **arrays** for many values, **operators** that compute new values, and **statements** that decide what runs and how often. Everything else in Java (classes, collections, streams) is built on top of these.

This topic is split into lessons you take one at a time, following the dev.java "Language Basics" tutorial.

```
Java Language Basics
├── data         variables & naming, primitive types, arrays, var
├── computation  operators, operator summary, expressions/statements/blocks
└── control      if/loops/break/continue, switch statements, switch expressions
```

## 2. Roadmap

| # | Subtopic | What you'll learn | Source | Status |
|---|----------|-------------------|--------|--------|
| 01 | [Variables](01-variables/) | Kinds of variables (instance, static, local, parameters), naming rules and conventions | [variables](https://dev.java/learn/language/constructs/basics/variables) | 📖 |
| 02 | [Primitive types](02-primitive-types/) | The 8 primitive types, default values, literals, underscores in numbers | [primitive-types](https://dev.java/learn/language/constructs/basics/primitive-types) | 📖 |
| 03 | [Arrays](03-arrays/) | Declaring, creating, initializing, copying arrays, multi-dimensional arrays, `Arrays` utility | [arrays](https://dev.java/learn/language/constructs/basics/arrays) | 📖 |
| 04 | [Using `var`](04-using-var/) | Local variable type inference (Java 10+), where it works and where it doesn't | [using-var](https://dev.java/learn/language/constructs/basics/using-var) | 📖 |
| 05 | [Using operators](05-using-operators/) | Assignment, arithmetic, unary, equality/relational, conditional, `instanceof`, bitwise | [using-operators](https://dev.java/learn/language/constructs/basics/using-operators) | 📖 |
| 06 | [Operator summary](06-operator-summary/) | Precedence and associativity, reading tricky expressions correctly | [all-operators](https://dev.java/learn/language/constructs/basics/all-operators) | 📖 |
| 07 | [Expressions, statements and blocks](07-expressions-statements-blocks/) | What an expression is, statement kinds, blocks and scope | [expressions-statements-blocks](https://dev.java/learn/language/constructs/basics/expressions-statements-blocks) | 📖 |
| 08 | [Control flow](08-control-flow/) | `if`/`else`, `while`, `do-while`, `for`, enhanced `for`, `break`, `continue`, `return`, labels | [controlling-flow](https://dev.java/learn/language/constructs/basics/controlling-flow) | 📖 |
| 09 | [Switch statements](09-switch-statement/) | Classic `switch`, fall-through, `break`, allowed selector types | [switch-statement](https://dev.java/learn/language/constructs/basics/switch-statement) | 📖 |
| 10 | [Switch expressions](10-switch-expression/) | Arrow labels, `yield`, exhaustiveness (Java 14+) | [switch-expression](https://dev.java/learn/language/constructs/basics/switch-expression) | 📖 |

All lessons are written. Work through them in order, and mark each one ✅ once its exercises have been reviewed.

## 3. How the pieces relate

- **Storing a value?** **01** (variables) + **02** (which primitive type). Many values of one type? **03** arrays.
- **Tired of writing long type names on the left?** **04** `var`, but only for local variables.
- **Computing something?** **05** operators; if an expression surprises you, check precedence in **06**.
- **Deciding what runs?** `if`/loops in **08**. Choosing between many fixed values? Prefer a switch **expression** (**10**) over a classic switch **statement** (**09**).
