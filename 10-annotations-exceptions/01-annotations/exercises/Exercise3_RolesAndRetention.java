import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Exercise 3 (Hard): fix a permission system built on annotations.
 *
 * TASK
 *   A service marks its methods with the roles allowed to call them, and canCall(user, method)
 *   checks them. The tests show three problems:
 *
 *   1. Every check says "allowed": canCall can't see any @RequiresRole at runtime. Fix the
 *      annotation so reflection can read it.
 *
 *   2. Some methods need TWO possible roles (admin OR auditor). Writing @RequiresRole twice
 *      doesn't compile yet. Make @RequiresRole repeatable: add the container annotation
 *      @RequiresRoles, then uncomment the second @RequiresRole on exportLedger() and
 *      deleteAccount(). canCall must accept a user who has ANY of the listed roles.
 *
 *   3. @RequiresRole should only be allowed on methods and classes (TYPE). Restrict it.
 *      A role on the CLASS applies to every method that has no role of its own.
 *
 *   Methods with no role on the method AND none on the class are open to everyone.
 *
 * EXPECTED OUTPUT
 *   open method:      PASS
 *   single role:      PASS
 *   either role:      PASS
 *   class default:    PASS
 *   target:           PASS
 *   ALL PASS
 *
 * HINTS
 *   - getAnnotationsByType(RequiresRole.class) returns all of them, repeated or not.
 *   - The container needs the same retention (and a @Target that includes the targets you use).
 *
 * Run: java exercises/Exercise3_RolesAndRetention.java
 */
public class Exercise3_RolesAndRetention {

    // TODO: fix retention, make repeatable, restrict targets
    @interface RequiresRole {
        String value();
    }

    // TODO: the container annotation @RequiresRoles

    record User(String name, Set<String> roles) { }

    static boolean canCall(User user, Method method) {
        RequiresRole[] roles = method.getAnnotationsByType(RequiresRole.class);
        // TODO: fall back to the class's roles when the method has none
        if (roles.length == 0) {
            return true;
        }
        for (RequiresRole role : roles) {
            if (!user.roles().contains(role.value())) {   // TODO: this demands ALL roles, not ANY
                return false;
            }
        }
        return true;
    }

    static class LedgerService {
        public void ping() { }

        @RequiresRole("admin")
        public void closeYear() { }

        @RequiresRole("admin")
        // @RequiresRole("auditor")      // TODO: uncomment once the annotation is repeatable
        public void exportLedger() { }
    }

    @RequiresRole("admin")
    static class AccountService {
        public void deleteAccount() { }           // inherits the class's "admin" requirement

        @RequiresRole("support")
        public void resetPassword() { }           // its own role replaces the class default
    }

    public static void main(String[] args) throws Exception {
        User admin = new User("asha", Set.of("admin"));
        User auditor = new User("ravi", Set.of("auditor"));
        User support = new User("zoya", Set.of("support"));
        User guest = new User("kiran", Set.of());

        Method ping = LedgerService.class.getMethod("ping");
        Method closeYear = LedgerService.class.getMethod("closeYear");
        Method exportLedger = LedgerService.class.getMethod("exportLedger");
        Method deleteAccount = AccountService.class.getMethod("deleteAccount");
        Method resetPassword = AccountService.class.getMethod("resetPassword");

        boolean allPass = true;
        allPass &= check("open method:", canCall(guest, ping) && canCall(admin, ping));
        allPass &= check("single role:", canCall(admin, closeYear) && !canCall(auditor, closeYear)
                && !canCall(guest, closeYear));
        allPass &= check("either role:", canCall(admin, exportLedger) && canCall(auditor, exportLedger)
                && !canCall(support, exportLedger));
        allPass &= check("class default:", canCall(admin, deleteAccount) && !canCall(support, deleteAccount)
                && canCall(support, resetPassword) && !canCall(admin, resetPassword));

        Target target = RequiresRole.class.getAnnotation(Target.class);
        Set<ElementType> allowed = target == null ? Set.of() : new TreeSet<>(Arrays.asList(target.value()));
        allPass &= check("target:", allowed.equals(Set.of(ElementType.METHOD, ElementType.TYPE)));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-17s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
