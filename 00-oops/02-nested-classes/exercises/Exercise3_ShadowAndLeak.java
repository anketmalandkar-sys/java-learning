import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 3 (Hard): a shadowing bug and a needless inner class.
 *
 * TASK
 *   A ticket system has two problems.
 *
 *   1. Ticket.Note is an INNER class, so every note keeps its whole Ticket (and its big
 *      attachment) alive, even after the ticket is gone. Notes are archived for years.
 *      Note only needs the text and the author. Make Note a STATIC nested class, and check
 *      that addNote still compiles (it creates notes from inside a Ticket method).
 *
 *   2. Ticket.Escalation.apply(int priority) should set the TICKET's priority to the
 *      escalation's own level plus the given priority, capped at 5. Right now it changes
 *      the wrong variables because three variables are named "priority". Fix it using
 *      this. and Ticket.this. (don't rename anything).
 *
 * EXPECTED OUTPUT
 *   note is static: PASS
 *   notes work:     PASS
 *   escalation:     PASS
 *   capped:         PASS
 *   ALL PASS
 *
 * HINTS
 *   - The check uses reflection to see whether Note is declared static. (Since JDK 18 javac may
 *     drop the hidden this$0 field from an inner class that never uses it, but only "static"
 *     guarantees there's no link to the Ticket.)
 *   - Inside Escalation: "priority" is the parameter, this.priority is Escalation's field,
 *     Ticket.this.priority is the ticket's field.
 *
 * Run: java exercises/Exercise3_ShadowAndLeak.java
 */
public class Exercise3_ShadowAndLeak {

    static class Ticket {
        final String title;
        final byte[] attachment = new byte[1_000_000];   // something heavy
        int priority = 1;
        final List<Note> notes = new ArrayList<>();

        Ticket(String title) {
            this.title = title;
        }

        class Note {
            final String author;
            final String text;

            Note(String author, String text) {
                this.author = author;
                this.text = text;
            }
        }

        void addNote(String author, String text) {
            notes.add(new Note(author, text));
        }

        class Escalation {
            final int priority;    // the escalation's own level

            Escalation(int priority) {
                this.priority = priority;
            }

            void apply(int priority) {
                priority = Math.min(5, priority + priority);   // TODO: shadowing bug
            }
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        Class<?> noteClass = Ticket.Note.class;
        boolean isStatic = Modifier.isStatic(noteClass.getModifiers());
        boolean hasOuterField = false;
        for (Field f : noteClass.getDeclaredFields()) {
            if (f.getType() == Ticket.class) {
                hasOuterField = true;
            }
        }
        allPass &= check("note is static:", isStatic && !hasOuterField);

        Ticket ticket = new Ticket("Login broken");
        ticket.addNote("asha", "Seen on Android");
        ticket.addNote("ravi", "Fixed in 2.3");
        allPass &= check("notes work:", ticket.notes.size() == 2 && "ravi".equals(ticket.notes.get(1).author));

        Ticket.Escalation e = ticket.new Escalation(2);
        e.apply(1);
        allPass &= check("escalation:", ticket.priority == 3 && e.priority == 2);

        ticket.new Escalation(4).apply(3);
        allPass &= check("capped:", ticket.priority == 5);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-15s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
