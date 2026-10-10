---
name: learn-java
description: "Teach a Java topic the user names, or one taken from a tutorial page URL: create a topic folder in the workspace with notes, simple runnable sample code, and practice exercises, then review attempts. Vast topics (e.g. Collections, Concurrency) are split into a roadmap of subtopics taught one at a time."
---

# Learn Java

The user invokes this skill with a Java topic (e.g. `/learn-java streams`, `/learn-java HashMap internals`). Teach it in the **explain-then-drill** style: a clear, concise explanation with simple examples, then exercises for the user to solve.

If no topic is given, ask for one in a single short question and stop.

## URL as source

If the argument starts with `http://` or `https://`, the page is the lesson's source (e.g. `/learn-java https://dev.java/learn/lambdas/first-lambdas/`). The `learn-java-from-url` skill calls this mode for each page it finds.

- Fetch the page with the `WebFetch` tool (load it with ToolSearch `select:WebFetch` if needed). Ask for the page title, the main concepts, the code samples, and the caveats. If the page can't be fetched, tell the user and ask for another URL or a topic name, then stop.
- Take the topic name and slug from the page title, without the site name (e.g. "Writing Your First Lambdas – Dev.java" → `writing-your-first-lambdas`).
- Base the README, examples, and exercises on the page's concepts, in this skill's usual simple style. Don't copy the page's text or code: explain in your own words and write fresh examples.
- In the README, add `Source: <url>` as the first line under the title. Use the URL normalized as `learn-java-from-url` does it: lowercase host, no `#fragment`, no `utm_*`/`ref` params, no trailing `/` or `index.html`. `learn-java-from-url` uses this line to avoid making the same lesson twice.
- Skip step 0's scoping: one URL is one focused lesson.
- Optional args `--parent <NN-parent-slug> --sub <MM>`: create the lesson as subtopic `MM-slug/` inside that existing parent folder. Follow the "Vast topics" rules for layout, `.iml` naming (`NN-parent_MM-sub.iml`), and the roadmap update. Without these args, use normal top-level numbering.
- When called from `learn-java-from-url`, keep the step 7 reply to 1-2 lines (folder created, files). That skill gives the full summary.

Every other rule below still applies: reuse instead of overwriting, IntelliJ registration, verification, and no up-front solutions.

## 0. Scope the topic

Before creating anything, decide whether the topic is **focused** or **vast**:
- **Focused**: one lesson covers it well (e.g. Comparator, lambdas, records, `equals`/`hashCode`). Follow steps 1-7 as written.
- **Vast**: it splits naturally into parts that each deserve their own lesson (e.g. Collections, Concurrency, Streams API, Exceptions, Generics, I/O & NIO, JVM memory). Rule of thumb: if a good lesson would need more than ~5 core concepts, or the parts can be learned independently, it's vast.

For a vast topic:
- Split it into 3-8 ordered subtopics, basics first. Example, Collections: `01-collections-overview` (hierarchy, `Iterable`/`Collection`, the `Collections` utility class), `02-list`, `03-set`, `04-queue-deque`, `05-map`, `06-iteration-and-fail-fast`.
- Teach **one subtopic per invocation**, as a full lesson (README, examples, exercises), using the "Vast topics" rules below.
- Which subtopic to teach:
  - `/learn-java collections` and no parent folder exists yet: create the parent (overview + roadmap) and teach subtopic 01.
  - `/learn-java collections` and the parent exists: teach the first roadmap subtopic that has no folder yet. If all are done, say so and offer extras.
  - `/learn-java collections map`, or a subtopic named on its own (e.g. `/learn-java HashMap`) that appears in an existing parent's roadmap: teach that subtopic inside the existing parent. Don't create a new top-level folder for it.

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

### Vast topics

The parent folder holds only the overview. Each subtopic is a full topic folder nested inside it:
```
java-learning/
  04-collections/
    README.md                      <- overview + roadmap (see "Parent README"); no code here
    01-collections-overview/
      04-collections_01-collections-overview.iml
      README.md
      examples/
      exercises/
      solutions/                   <- on request, as usual
    02-list/
      04-collections_02-list.iml
      ...
```
- The parent gets the next top-level `NN` as usual. Subtopic folders are numbered `MM-` from `01` inside the parent, in roadmap order.
- A subtopic folder follows every rule for a topic folder: the same README sections, 2 examples, 3 exercises, and solutions only on request.
- Create a subtopic folder only when it's taught. The roadmap lists the rest.
- Reuse: never overwrite an existing subtopic. Update the parent README's roadmap in place instead of rewriting it.

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
- **Vast topics:** one module per *subtopic*. The parent folder gets no `.iml`. Name the file `NN-parent_MM-sub.iml` (e.g. `04-collections_02-list.iml`). IntelliJ module names must be unique across the project, and a bare `01-basics.iml` could clash between two vast topics. The `.iml` content is the same as above. The `modules.xml` entry uses the nested path:
  `<module fileurl="file://$PROJECT_DIR$/04-collections/02-list/04-collections_02-list.iml" filepath="$PROJECT_DIR$/04-collections/02-list/04-collections_02-list.iml" />`
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

For a vast topic, each subtopic's README uses these sections. Add a one-line link back to the parent README at the top.

### Parent README (vast topics only)

Write it once, when the parent is created, and keep it short:
1. **Big picture**: what the topic covers in 3-5 sentences, plus an ASCII diagram if it helps (e.g. the `Collection` interface tree).
2. **Roadmap**: a table `# | Subtopic | What you'll learn | Status`. Status is ⬜ not started, 📖 in progress, or ✅ done. Link each taught subtopic to its folder.
3. **How the pieces relate**: a short "which to pick when" guide (e.g. List vs Set vs Map vs Queue).

Keep the roadmap current: mark a subtopic 📖 when it's taught, and ✅ once its exercises have been reviewed.

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
- Also compile the whole topic in one go, the way IntelliJ builds the module: `javac -d <scratchpad>/build examples/*.java exercises/*.java [solutions/*.java]`. This catches duplicate class names across files. Write the build output to the scratchpad, never into the topic folder. For a vast topic, do this in each subtopic folder taught in this run (each one is its own module).
- If Java isn't available, say so in one line.

## 7. Reply to the user

Keep the chat reply short:
- A 4-6 line summary of the key idea (the README holds the detail).
- The folder path created and the files in it.
- How to run: from a terminal in the topic folder, `java examples/Example1_Basics.java`; or in IntelliJ, the green ▶ next to `main` (mention Reload All from Disk if a module was just added).
- A nudge to start with Exercise 1 and say when it's ready for review.
- For a vast topic, also: the roadmap as a short list of subtopic names with the current one marked, and the exact command for the next one (e.g. `/learn-java collections set`).

Don't paste the full README or code into chat; it's in the files.
