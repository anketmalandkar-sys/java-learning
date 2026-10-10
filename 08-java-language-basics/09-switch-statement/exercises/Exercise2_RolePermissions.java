import java.util.ArrayList;
import java.util.List;

/**
 * Exercise 2 (Medium): deliberate fall-through.
 *
 * TASK
 *   In a document app, each role has its own permissions PLUS everything the roles below it
 *   have:
 *     owner  -> "transfer", then everything admin has
 *     admin  -> "delete",   then everything editor has
 *     editor -> "edit",     then everything viewer has
 *     viewer -> "view"
 *
 *   Write permissionsFor(role) with ONE classic switch that uses fall-through (no break between
 *   the role cases), so each permission string is written only once in your code.
 *     permissionsFor("admin") -> [delete, edit, view]
 *
 *   Also:
 *     - Match roles case-insensitively ("ADMIN" works too).
 *     - Ignore surrounding spaces (" editor ").
 *     - null, or an unknown role, gives an empty list. Don't let null crash the switch.
 *
 * EXPECTED OUTPUT
 *   owner:   PASS
 *   admin:   PASS
 *   viewer:  PASS
 *   case:    PASS
 *   unknown: PASS
 *   null:    PASS
 *   ALL PASS
 *
 * HINTS
 *   - Order the cases from the most powerful role down.
 *   - Mark each intended fall-through with a "// fall through" comment for the next reader.
 *
 * Run: java exercises/Exercise2_RolePermissions.java
 */
public class Exercise2_RolePermissions {

    static List<String> permissionsFor(String role) {
        List<String> permissions = new ArrayList<>();
        // TODO
        return permissions;
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("owner:", permissionsFor("owner").equals(List.of("transfer", "delete", "edit", "view")));
        allPass &= check("admin:", permissionsFor("admin").equals(List.of("delete", "edit", "view")));
        allPass &= check("viewer:", permissionsFor("viewer").equals(List.of("view")));
        allPass &= check("case:", permissionsFor("ADMIN").equals(List.of("delete", "edit", "view"))
                && permissionsFor(" editor ").equals(List.of("edit", "view")));
        allPass &= check("unknown:", permissionsFor("guest").isEmpty());
        boolean nullOk;
        try {
            nullOk = permissionsFor(null).isEmpty();
        } catch (NullPointerException e) {
            nullOk = false;
        }
        allPass &= check("null:", nullOk);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-8s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
