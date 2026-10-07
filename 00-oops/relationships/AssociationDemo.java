package oops.relationships;

import java.util.ArrayList;
import java.util.List;

/*
 * ============================================================================
 *  ASSOCIATION — the general "HAS-A / KNOWS-A / USES-A" relationship
 * ============================================================================
 *
 *  Association simply means: objects of one class hold a reference to
 *  (and work with) objects of another class. Neither side OWNS the other;
 *  both have their OWN independent lifecycle.
 *
 *         Teacher  ------------  Student          (UML: plain line)
 *
 *  Two dimensions to describe any association:
 *
 *   DIRECTION
 *     Uni-directional : only A knows about B          Doctor  ----> Patient
 *     Bi-directional  : A knows B AND B knows A        Teacher <---> Student
 *
 *   MULTIPLICITY (cardinality)
 *     One-to-One   : Person  - Passport
 *     One-to-Many  : Doctor  - many Patients
 *     Many-to-One  : many Employees - one Company
 *     Many-to-Many : many Teachers - many Students
 *
 *  Aggregation and Composition (see the other demos) are SPECIAL, stronger
 *  kinds of association that add the idea of "whole/part" and ownership.
 *  Plain association has no whole/part meaning — a teacher is not "part of"
 *  a student, they just interact.
 */

// --------------------------------------------------------------------------
// 1) Bi-directional, Many-to-Many: Teacher <-> Student
// --------------------------------------------------------------------------
class Teacher {
    private final String name;
    private final List<Student> students = new ArrayList<>();

    Teacher(String name) { this.name = name; }

    String getName() { return name; }

    /*
     * In a bi-directional link BOTH sides must be updated, otherwise the
     * object graph becomes inconsistent (teacher knows student, student
     * doesn't know teacher). Keep ONE method in charge of wiring both ends.
     */
    void addStudent(Student s) {
        if (!students.contains(s)) {
            students.add(s);
            s.addTeacher(this);       // keep the other side in sync
        }
    }

    List<Student> getStudents() { return List.copyOf(students); }
}

class Student {
    private final String name;
    private final List<Teacher> teachers = new ArrayList<>();

    Student(String name) { this.name = name; }

    String getName() { return name; }

    void addTeacher(Teacher t) {
        if (!teachers.contains(t)) {
            teachers.add(t);
            t.addStudent(this);       // contains() checks stop infinite recursion
        }
    }

    List<Teacher> getTeachers() { return List.copyOf(teachers); }
}

// --------------------------------------------------------------------------
// 2) Uni-directional, One-to-Many: Doctor -> Patient
//    The doctor keeps a list of patients; a Patient has no idea which
//    doctor(s) it is linked to.
// --------------------------------------------------------------------------
class Patient {
    private final String name;
    Patient(String name) { this.name = name; }
    String getName() { return name; }
}

class Doctor {
    private final String name;
    private final List<Patient> patients = new ArrayList<>();

    Doctor(String name) { this.name = name; }

    void register(Patient p) { patients.add(p); }

    void doRounds() {
        for (Patient p : patients) {
            System.out.println("  Dr. " + name + " checks on " + p.getName());
        }
    }

    /*
     * DEPENDENCY — the weakest link of all. Here the Patient is only a
     * method PARAMETER: used for the duration of the call, never stored in
     * a field (it works even for a patient who isn't registered).
     * ("uses-a" for a moment, rather than "has-a").
     */
    String prescribe(Patient p, String medicine) {
        return "Rx for " + p.getName() + ": " + medicine + " (signed Dr. " + name + ")";
    }
}

public class AssociationDemo {

    public static void main(String[] args) {
        System.out.println("=== Many-to-many, bi-directional ===");
        Teacher sharma = new Teacher("Mr. Sharma");
        Teacher iyer   = new Teacher("Ms. Iyer");

        Student ravi  = new Student("Ravi");
        Student priya = new Student("Priya");

        sharma.addStudent(ravi);
        sharma.addStudent(priya);
        priya.addTeacher(iyer);     // wiring from the other end works too

        for (Teacher t : List.of(sharma, iyer)) {
            System.out.println(t.getName() + " teaches " +
                    t.getStudents().stream().map(Student::getName).toList());
        }
        for (Student s : List.of(ravi, priya)) {
            System.out.println(s.getName() + " is taught by " +
                    s.getTeachers().stream().map(Teacher::getName).toList());
        }

        System.out.println("\n=== One-to-many, uni-directional ===");
        Doctor doc = new Doctor("Rao");
        Patient p1 = new Patient("Amit");
        Patient p2 = new Patient("Neha");
        doc.register(p1);
        doc.register(p2);
        doc.doRounds();
        System.out.println("  " + doc.prescribe(p1, "Paracetamol"));

        System.out.println("\n=== Independent lifecycles ===");
        // Drop the teacher. Students are created OUTSIDE and still exist —
        // association never controls the other object's life.
        sharma = null;
        System.out.println("Teacher reference dropped, Ravi still exists: " + ravi.getName());
    }
}
