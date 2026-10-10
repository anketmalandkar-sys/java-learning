---
name: learn-java-from-url
description: "Build Java lessons from an online tutorial: crawl a URL for its Java lesson pages (same path, main content only, index pages expanded one level), audit every page concept by concept against the lessons already in the workspace, confirm the plan with the user, then create full lessons for new topics and fill only the gaps in existing ones. No page is taught twice and nothing is silently skipped."
---

# Learn Java from URL

The user invokes this skill with a tutorial URL (e.g. `/learn-java-from-url https://dev.java/learn/lambdas/`). Find the Java lesson pages under that URL, work out what the workspace already teaches, confirm a plan with the user, then:
- run the `learn-java` skill on each page whose topic is **new**, and
- add only the **missing concepts** to existing lessons for pages that **overlap**.

Two promises: no topic is taught twice, and **no concept from a confirmed page is skipped without the user seeing it** in the plan.

Fetch pages with the `WebFetch` tool. If its schema isn't loaded, load it first with ToolSearch (`select:WebFetch`).

## 0. Get the URL

- If no argument is given, or it isn't an `http://` or `https://` URL, ask for one in a single short question and stop.

## 1. Fetch the start page

- Fetch the URL with WebFetch.
- If it fails, tell the user what went wrong and ask for another URL. Don't guess one. Failures include a DNS error, a 4xx/5xx status, a timeout, a redirect to a different site or a login page, and a non-HTML response.

## 2. Collect the lesson pages

Ask WebFetch for every link in the page's **main content** (the lesson body or its table of contents), as `link text | absolute URL`, in page order. Tell it to ignore the header, top nav, breadcrumbs, sidebar chrome, footer, cookie banners, social links, login/sign-up, search, language switchers, and previous/next lesson buttons.

Filter the list:
- **Same path.** Keep a link only if it has the same scheme and host as the start URL, and its path starts with the start URL's directory (e.g. `/learn/lambdas/` for `https://dev.java/learn/lambdas/`).
- **Drop non-pages.** Drop fragment-only links (`#...`), `mailto:` and `javascript:` links, file links (`.pdf .zip .png .jpg .gif .svg .css .js`), and the start URL itself.
- **Java only.** Keep a link only if its text or path is clearly a Java topic: a language feature, an API, the JVM, or Java tooling. Drop generic pages such as about, contact, community, news, blog, download, events, privacy, and terms.

**Expand index pages.** Fetch every kept page and ask WebFetch whether it is an **index** (it mainly lists links to sub-lessons and teaches little itself) or a **lesson**. For each index page, collect its sub-lesson links with the same filters and add them in its place, keeping course order. Expand **one extra level** by default; go deeper only if the user asks. An index page is not a lesson; it's recorded in the parent README for reference.

**Record cross-links.** Lesson pages often link to related topics outside the start path (e.g. Exceptions, Annotations, Modules). Don't follow them, but collect them, with their titles, for the plan in step 4, marking which ones the workspace already covers. The user may want to run those separately.

Keep a count of dropped links and the reason.

## 3. Normalize, de-duplicate, and audit overlap

**Normalize** each URL: lowercase the scheme and host, resolve `../`, remove the `#fragment`, remove tracking query params (`utm_*`, `ref`), and remove a trailing `/` or `index.html`. Use the normalized form everywhere from here on. Keep the first occurrence of each normalized URL, so page order stays course order.

**Already done:** search the workspace's Markdown files (`README.md`, notes files) for the normalized URL. It can appear as a `Source: <url>` line (written by learn-java and by gap fills) or as a link in a parent README's roadmap or coverage table (`(<url>)`). Match the exact URL: it must be followed by `)`, whitespace, `|` or the end of the line, so `.../oop/classes` doesn't match `.../oop/classes-objects`. A match means the page was handled before: mark it **already done**, say where it was recorded, and skip it. This applies to the start URL too: if the start page itself is already done and has no sub-lessons left to handle, tell the user and stop.

**Overlap audit**, for every remaining lesson page:
1. Fetch the page and list **every concept it teaches**, one line each: rules, API methods, keywords, gotchas. This list is the page's checklist.
2. Find the existing lessons that might cover the topic. Look past folder names: check top-level folders, their subtopic folders, and modules with a different layout (e.g. a folder of demo files and notes with no exercises). Search READMEs, notes files and code for the topic's key terms to find candidates.
3. **Read the candidate lessons**: their notes and the relevant code. Mark a concept **covered** only when you found it actually explained or demonstrated. A keyword hit is a lead, not proof: a word can appear in an unrelated comment, or a concept can be mentioned in a table without being taught.
4. Classify the page:
   - **covered**: every checklist concept is taught somewhere. Name where.
   - **gap**: the topic exists, but some concepts are missing. List the missing concepts exactly.
   - **new**: the topic isn't taught anywhere (or only in passing).
5. Before showing the plan, re-check every **covered** and **gap** row once more against the checklist. Anything you can't point to in the existing files is a gap.

## 4. Confirm the plan with the user

Show one numbered table: `# | Page | Status | Already taught in | Missing concepts → plan`. Then:
- the index pages that were expanded, and how many lesson pages came out of them,
- the cross-links not followed (title, and whether the workspace covers them),
- the number of links dropped and why,
- a one-line summary: N full lessons, M gap fills (and where), K pages fully covered.

If there are more than 15 full lessons, point out that each one becomes a full lesson. Pages that only make sense together (e.g. a code-free "when to use which" guide after the lesson it compares) may be folded into one lesson; say so in the plan.

Then use AskUserQuestion:
- **Create the plan**: full lessons for **new**, gap fills for **gap**, nothing for **covered**.
- **Let me change some**: the user drops or changes rows. Show the final plan once more, then go on.
- **Cancel**: stop.

If the user asks to double-check anything, redo step 3's audit for those rows by reading the files again before asking a second time.

## 5. Decide the folder layout

Use learn-java's rules for numbering and for vast topics (parent folder + numbered subtopics):
- **1 full lesson:** a normal top-level topic, `NN-slug/`. learn-java handles it.
- **2+ full lessons:** one **parent** folder named after the start page (e.g. `08-lambdas/`). Each new lesson becomes subtopic `MM-slug/` inside it, in course order.
  - Create the parent before calling learn-java. Its README follows learn-java's "Parent README" rules. Add `Source: <start-url>` as the first line under the title, and a `Source` column in the roadmap table with each page's URL.
  - The roadmap lists **every** confirmed page: new lessons link to their subtopic folder, gap fills link to the section added in the existing lesson, and covered pages link to where they're already taught. Each row carries its page's full normalized URL as a link target (`[name](<url>)`), so a re-run's "already done" check finds it.
  - Don't add an `.iml` to the parent.
  - If a parent with `Source: <start-url>` already exists, reuse it. Add new rows to its roadmap and continue its `MM` numbering.

## 6. Build it

For each confirmed page, in course order:
1. **Check it's available.** Fetch it with WebFetch. If the result is unclear, check the HTTP status with `Invoke-WebRequest -Method Head` (PowerShell) or `curl -sI` (Bash).
2. **Not available** (4xx/5xx, timeout, empty or near-empty body, or a redirect to a login page or another site): use AskUserQuestion with the options **Provide a replacement URL** (the user types it via Other), **Skip this page**, and **Stop here**. Run a replacement URL through steps 3 and 4 for that page, then check it again with this step.
3. **New:** invoke the `learn-java` skill with the Skill tool:
   - Single page: args `<url>`.
   - Parent layout: args `<url> --parent <NN-parent-slug> --sub <MM>`.
4. **Gap:** add the missing concepts to the existing lesson. Never rewrite or delete the user's files.
   - Follow that lesson's own layout and conventions: package names, file naming, folder structure.
   - For a learn-java style lesson: append a README section `## 9. Extra: <what>` (or the next free number) whose first line is `Source: <url>`, plus `examples/Example3_*.java` and `exercises/Exercise4_*.java` (the next free numbers).
   - For a lesson with a different layout (e.g. demo files and a notes file), add new demo files next to the related ones, add exercises in an `exercises/` folder inside it, and add a short "Added from <site>" table to its README listing each new file and the `Source:` URL of each page it covers.
   - Every concept on the page's missing list must end up in a new file. Check them off.
   - Verify the same way learn-java does: run the examples, compile the exercises, compile the whole module.
5. **Covered:** nothing to build; it's recorded in the parent README (step 5).
6. Record the outcome: created (with folder), gap filled (with files), covered, skipped, replaced, or failed. A failed page never stops the rest.

After all pages, make sure the parent README's roadmap lists every confirmed page with its status and link.

## 7. Reply to the user

Keep it short:
- A table of every page → folder created, gap filled (which files), already covered (where), or skipped (with the reason).
- The parent folder path, if one was created.
- Cross-links not followed, as candidates for a later run.
- If modules were added, a reminder that IntelliJ may need **File → Reload All from Disk**.
- Where to start: `<first folder>/exercises/Exercise1_*.java`, and to say when it's ready for review.
