/**
 * Example 1: the string pool, == vs equals, and immutability.
 *   1. Literals share one pooled object; new String() makes a copy.
 *   2. Compile-time constants are pooled; strings built at runtime are not.
 *   3. intern() returns the pooled copy.
 *   4. Immutability: methods return new strings.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    public static void main(String[] args) {
        System.out.println("--- 1. Literals vs new String() ---");
        String a = "Pune";
        String b = "Pune";
        String c = new String("Pune");
        System.out.println("a == b      : " + (a == b) + "   (both point to the pooled \"Pune\")");
        System.out.println("a == c      : " + (a == c) + "  (c is a separate copy)");
        System.out.println("a.equals(c) : " + a.equals(c) + "   (same characters)");

        System.out.println();
        System.out.println("--- 2. Compile time vs runtime ---");
        String joinedByCompiler = "Pu" + "ne";      // both literals: the compiler writes "Pune"
        final String finalPrefix = "Pu";
        String joinedFinal = finalPrefix + "ne";    // final + literal: still a compile-time constant
        String prefix = "Pu";
        String joinedAtRuntime = prefix + "ne";     // prefix is a variable: built while running
        System.out.println("\"Pu\" + \"ne\" == a   : " + (joinedByCompiler == a));
        System.out.println("finalPrefix + \"ne\" : " + (joinedFinal == a));
        System.out.println("prefix + \"ne\" == a : " + (joinedAtRuntime == a));
        System.out.println("...but equals      : " + joinedAtRuntime.equals(a));

        System.out.println();
        System.out.println("--- 3. intern() ---");
        String interned = joinedAtRuntime.intern();   // look up "Pune" in the pool: found, return it
        System.out.println("interned == a      : " + (interned == a));
        System.out.println("joinedAtRuntime == a still: " + (joinedAtRuntime == a) + " (intern returns a reference; it doesn't change the original)");

        System.out.println();
        System.out.println("--- 4. Immutability ---");
        String code = "  abc12 ";
        code.trim();                                 // the result is thrown away
        System.out.println("after code.trim()         : [" + code + "]");
        code = code.trim().toUpperCase();            // reassign to keep it
        System.out.println("after code = trim().upper : [" + code + "]");

        String original = "order";
        String plural = original.concat("s");        // a new string; original is untouched
        System.out.println("original = " + original + ", plural = " + plural);
    }
}

/* Expected output:
--- 1. Literals vs new String() ---
a == b      : true   (both point to the pooled "Pune")
a == c      : false  (c is a separate copy)
a.equals(c) : true   (same characters)

--- 2. Compile time vs runtime ---
"Pu" + "ne" == a   : true
finalPrefix + "ne" : true
prefix + "ne" == a : false
...but equals      : true

--- 3. intern() ---
interned == a      : true
joinedAtRuntime == a still: false (intern returns a reference; it doesn't change the original)

--- 4. Immutability ---
after code.trim()         : [  abc12 ]
after code = trim().upper : [ABC12]
original = order, plural = orders
*/
