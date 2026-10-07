---
name: learn-java
description: Teach a Java topic the user names: create a topic folder in the workspace with notes, simple runnable sample code, and practice exercises, then review attempts.
---

# Learn Java

The user invokes this skill with a Java topic (e.g. `/learn-java streams`, `/learn-java HashMap internals`). Teach it in the **explain-then-drill** style: a clear, concise explanation with simple examples, then exercises for the user to solve.

If no topic is given, ask for one in a single short question and stop.

## 1. Set up the folder

- Root folder: if the current working directory is itself named `java-learning`, use it as the root. Otherwise use `java-learning/` inside the current working directory, and create it if it doesn't exist.
- Topic folder: `java-learning/NN-topic-slug/`, where `NN` is the next two-digit number based on the highest existing `NN-` prefix (e.g. after `00-oops` and `01-comparable` comes `02`), and `topic-slug` is lowercase-hyphenated (e.g. `03-streams-api`).
- If a folder for the same topic already exists, reuse it: don't overwrite the user's work. Add new material as new files, and tell the user what's new.

Layout:
```
java-learning/
  03-streams-api/
    03-streams-api.iml <- IntelliJ module (see "Register with IntelliJ" below)
    README.md          <- the lesson notes
    examples/
      Example1_Basics.java
      Example2_RealWorld.java
    exercises/
      Exercise1_*.java  <- starter code with TODOs
      Exercise2_*.java
      Exercise3_*.java
    solutions/          <- created ONLY on request (see step 5)
      Solution1_*.java  <- NOT Exercise1_*.java: same class name would clash in the module
```

### Register with IntelliJ

If the root has an `.idea/` folder, make the new topic runnable from the IDE. The user runs files with the green ▶ button, and without this IntelliJ treats the files as plain text.

- **One module per topic.** Topic files have no `package` line and reuse names like `Example1_Basics` across topics. If two topics shared one module, IntelliJ would fail with "duplicate class". Never add topic folders as source folders of the root `.idea/java-learning.iml`.
- Create `NN-topic-slug/NN-topic-slug.iml`:
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <module type="JAVA_MODULE" version="4">
    <component name="NewModuleRootManager" inherit-compiler-output="true">
      <exclude-output />
      <content url="file://$MODULE_DIR$">
        <sourceFolder url="file://$MODULE_DIR$/examples" isTestSource="false" />
        <sourceFolder url="file://$MODULE_DIR$/exercises" isTestSource="false" />
      </content>
      <orderEntry type="inheritedJdk" />
      <orderEntry type="sourceFolder" forTests="false" />
    </component>
  </module>
  ```
- Add the module to `.idea/modules.xml` inside `<modules>`, and leave the existing entries alone:
  `<module fileurl="file://$PROJECT_DIR$/NN-topic-slug/NN-topic-slug.iml" filepath="$PROJECT_DIR$/NN-topic-slug/NN-topic-slug.iml" />`
- When `solutions/` is created later, add a matching `<sourceFolder .../solutions>` line to that topic's `.iml`.
- If an entry in `modules.xml` points at an `.iml` that no longer exists, tell the user and offer to remove it. Don't silently delete it.
- If the root has no `.idea/`, skip this step.
- Tell the user that IntelliJ may need **File → Reload All from Disk** to pick up the new module.

## 2. Write the lesson (README.md)

Keep it tight and practical. Sections:
1. **What it is**: 2-4 sentences, plain language.
2. **Why it exists**: the problem it solves; what you'd write without it.
3. **Core concepts**: the handful of ideas that matter, each with a tiny inline snippet.
4. **How it works under the hood**: only as deep as needed to avoid common bugs. No JDK-source dumps.
5. **Common mistakes and gotchas**: concrete, with a wrong vs right snippet.
6. **When to use / when not to**.
7. **Interview angle**: 3-5 questions an interviewer would ask on this topic, with short answers.
8. **Exercises**: list each exercise with its goal and difficulty.

## 3. Write the sample code (examples/)

Rules:
- **Simple and readable beats clever.** Basic, easy-to-follow code, meaningful names, short methods. Avoid dense or JDK-internal-style examples.
- One idea per example. Example 1 shows the basics; Example 2 shows a realistic use (e.g. orders, employees, bank accounts, not `foo`/`bar`).
- Each file is self-contained with a `main` method, runnable with `java FileName.java` (no build tool, no external libraries unless the topic requires one).
- Comment the *why*, not the obvious *what*. Print output so running it shows what happened, and put the expected output in a comment at the bottom.
- Target Java 17+ features only when they're natural for the topic; mention the version when a feature needs it (e.g. records: Java 16+, virtual threads: Java 21).

## 4. Write the exercises (exercises/)

- 3 exercises, increasing difficulty: **Easy** (apply the basic idea), **Medium** (combine with something else), **Hard** (a realistic mini-problem or a bug to find and fix).
- Each file: a header comment with the task, input/expected output, and hints; starter code that compiles, with `// TODO` where the user writes code.
- Where possible, include a small check in `main` that prints PASS/FAIL so the user can self-verify.
- **Never put solutions in this folder or in the README.**

## 5. Solutions and review

- Do not write solutions up front.
- When the user says they've attempted an exercise, read their file, run it, and review it: correctness first, then readability, then idiomatic improvements. Be direct about mistakes and show the fix.
- Write `solutions/SolutionN_*.java` (e.g. `Solution1_BookSort.java`, class `Solution1_BookSort`) only when the user asks for the solution or has submitted an attempt. Then add `solutions` as a source folder in the topic's `.iml`.
- Keep helper classes **nested** inside the main class, as in the starters. If the user moves a class to the top level, check two things. A top-level class can't be `private`. And `java File.java` runs the *first* class in the file, so the class with `main` must come first.

## 6. Verify before handing over

- If `java`/`javac` is available, compile and run every example and solution, and compile every exercise starter. Fix anything that fails. Check printed output matches the documented expected output.
- Also compile the whole topic in one go, the way IntelliJ builds the module: `javac -d <scratchpad>/build examples/*.java exercises/*.java [solutions/*.java]`. This catches duplicate class names across files. Write the build output to the scratchpad, never into the topic folder.
- If Java isn't available, say so in one line.

## 7. Reply to the user

Keep the chat reply short:
- A 4-6 line summary of the key idea (the README holds the detail).
- The folder path created and the files in it.
- How to run: from a terminal in the topic folder, `java examples/Example1_Basics.java`; or in IntelliJ, the green ▶ next to `main` (mention Reload All from Disk if a module was just added).
- A nudge to start with Exercise 1 and say when it's ready for review.

Don't paste the full README or code into chat; it's in the files.
