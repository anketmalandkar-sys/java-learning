package oops.relationships;

import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================================
 *  AGGREGATION — a WEAK "whole/part" association  ("has-a", shared parts)
 * ============================================================================
 *
 *         Department <>------- Professor        (UML: HOLLOW diamond on the whole)
 *
 *  - It IS an association (one object holds references to others) ...
 *  - ... PLUS a whole/part meaning: a Department is "made up of" Professors.
 *  - BUT the parts have an INDEPENDENT LIFECYCLE:
 *       * parts are created OUTSIDE the whole and passed in
 *       * a part can belong to several wholes at the same time
 *       * destroying the whole does NOT destroy the parts
 *
 *  Java signal: the part arrives as a constructor/method PARAMETER
 *  (someone else did the `new`), and the whole just keeps the reference.
 *
 *  Other examples: Team <>- Player, Playlist <>- Song, Library <>- Book,
 *  Cart <>- Product.
 */

class Professor {
    private final String name;
    private final String subject;

    Professor(String name, String subject) {
        this.name = name;
        this.subject = subject;
    }

    String getName() { return name; }

    @Override
    public String toString() { return name + " (" + subject + ")"; }
}

class Department {
    private final String name;
    // The department REFERENCES professors; it does not create them.
    private final List<Professor> professors = new ArrayList<>();

    Department(String name) { this.name = name; }

    // Parts are handed in from outside -> aggregation.
    void addProfessor(Professor p) { professors.add(p); }

    void removeProfessor(Professor p) { professors.remove(p); }

    // Closing a department just forgets the professors; it does not
    // "destroy" them — they still exist for whoever else references them.
    void close() {
        System.out.println("  Closing " + name + ", releasing " + professors);
        professors.clear();
    }

    @Override
    public String toString() { return name + " -> " + professors; }
}

public class AggregationDemo {

    public static void main(String[] args) {
        // Parts are created FIRST and INDEPENDENTLY of any department.
        Professor alan  = new Professor("Dr. Alan", "Algorithms");
        Professor grace = new Professor("Dr. Grace", "Compilers");
        Professor ada   = new Professor("Dr. Ada", "Mathematics");

        Department cs   = new Department("Computer Science");
        Department math = new Department("Mathematics");

        cs.addProfessor(alan);
        cs.addProfessor(grace);
        cs.addProfessor(ada);
        math.addProfessor(ada);           // SHARED part: Ada is in two departments

        System.out.println("=== Before ===");
        System.out.println(cs);
        System.out.println(math);

        System.out.println("\n=== The whole goes away ===");
        cs.close();
        cs = null;                         // department object is now garbage

        System.out.println("\n=== After ===");
        // Professors are alive and well — the whole did not own them.
        System.out.println("Alan still exists : " + alan);
        System.out.println("Grace still exists: " + grace);
        System.out.println(math);          // Ada is still in Mathematics

        // They can even join a new whole.
        Department ai = new Department("AI Lab");
        ai.addProfessor(alan);
        ai.addProfessor(grace);
        System.out.println(ai);
    }
}
