package oops.relationships;

import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================================
 *  "FAVOR COMPOSITION OVER INHERITANCE"  (Effective Java, Item 18)
 * ============================================================================
 *
 *  Inheritance (IS-A) gives the child EVERYTHING the parent exposes, forever.
 *  If that's more than you want, you can't take it back — and changes in the
 *  parent can silently break the child (the "fragile base class" problem).
 *
 *  Composition (HAS-A) lets you keep a parent-like object as a private part
 *  and expose ONLY the operations you choose (delegation).
 *
 *  Classic example: a Stack built by extending ArrayList.
 */

// ---------------------------------------------------------------------------
// BAD: Stack IS-A ArrayList?  Not really — and it shows.
// ---------------------------------------------------------------------------
@SuppressWarnings("serial")   // ArrayList is Serializable; irrelevant here
class InheritedStack<T> extends ArrayList<T> {
    void push(T item) { add(item); }
    T pop()           { return remove(size() - 1); }
    T peek()          { return get(size() - 1); }
}

// ---------------------------------------------------------------------------
// GOOD: Stack HAS-A List. Only stack operations are visible.
// ---------------------------------------------------------------------------
class ComposedStack<T> {
    private final List<T> items = new ArrayList<>();   // composition: created & owned here

    void push(T item) { items.add(item); }             // delegation
    T pop()           { return items.remove(items.size() - 1); }
    T peek()          { return items.get(items.size() - 1); }
    boolean isEmpty() { return items.isEmpty(); }
    int size()        { return items.size(); }

    @Override
    public String toString() { return "Stack" + items; }
}

public class CompositionOverInheritance {

    public static void main(String[] args) {
        System.out.println("=== Inheritance leaks the parent's whole API ===");
        InheritedStack<String> bad = new InheritedStack<>();
        bad.push("a");
        bad.push("b");
        bad.push("c");
        // These compile because InheritedStack IS-A ArrayList —
        // and they completely break LIFO behaviour:
        bad.add(0, "sneaky");    // insert at the bottom
        bad.remove("b");         // pull from the middle
        System.out.println("Broken stack: " + bad);   // [sneaky, a, c]

        System.out.println("\n=== Composition exposes only what we choose ===");
        ComposedStack<String> good = new ComposedStack<>();
        good.push("a");
        good.push("b");
        good.push("c");
        // good.add(0, "sneaky");   // COMPILE ERROR: no such method — exactly what we want
        System.out.println(good + ", pop -> " + good.pop() + ", now " + good);

        /*
         * When to use which:
         *   Inheritance  -> a TRUE is-a relationship, and the subclass should
         *                   be usable everywhere the parent is (Liskov
         *                   Substitution Principle). e.g. Dog extends Animal.
         *   Composition  -> you want to REUSE behaviour, not BE the thing.
         *                   Also lets you swap parts at runtime and keeps
         *                   coupling low.
         */
    }
}
