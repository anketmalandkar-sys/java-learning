import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Example 1: declaring annotations, retention, repeating annotations, and reading them back.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Audited {
        String by();
        int level() default 1;
    }

    // Default retention (CLASS): in the .class file, but NOT visible to reflection.
    @Target(ElementType.METHOD)
    @interface Internal { }

    @Repeatable(Schedules.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Schedule {
        String day();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Schedules {
        Schedule[] value();
    }

    static class Jobs {
        @Audited(by = "asha", level = 2)
        @Internal
        public void transfer() { }

        @Schedule(day = "Mon")
        @Schedule(day = "Thu")
        public void backup() { }

        @Audited(by = "ravi")          // level uses its default
        @Deprecated(since = "2.0", forRemoval = true)
        public void legacyExport() { }
    }

    public static void main(String[] args) throws Exception {
        Method transfer = Jobs.class.getMethod("transfer");
        Audited audited = transfer.getAnnotation(Audited.class);
        System.out.println("transfer @Audited: by=" + audited.by() + ", level=" + audited.level());
        System.out.println("transfer @Internal visible at runtime? " + transfer.isAnnotationPresent(Internal.class)
                + " (default CLASS retention)");

        Method backup = Jobs.class.getMethod("backup");
        System.out.println("backup getAnnotation(Schedule): " + backup.getAnnotation(Schedule.class)
                + " (repeated ones live in the container)");
        String days = Arrays.toString(Arrays.stream(backup.getAnnotationsByType(Schedule.class))
                .map(Schedule::day).toArray());
        System.out.println("backup getAnnotationsByType(Schedule): " + days);

        Method legacy = Jobs.class.getMethod("legacyExport");
        Deprecated dep = legacy.getAnnotation(Deprecated.class);
        System.out.println("legacyExport: audited by " + legacy.getAnnotation(Audited.class).by()
                + " at default level " + legacy.getAnnotation(Audited.class).level()
                + ", deprecated since " + dep.since() + ", forRemoval=" + dep.forRemoval());

        // @Override has SOURCE retention: the compiler checks it, then it's gone.
        Method mainMethod = Example1_Basics.class.getDeclaredMethod("main", String[].class);
        System.out.println("annotation types are interfaces? " + Audited.class.isInterface()
                + ", isAnnotation? " + Audited.class.isAnnotation()
                + ", main has annotations: " + mainMethod.getAnnotations().length);
    }
}

/* Expected output:
transfer @Audited: by=asha, level=2
transfer @Internal visible at runtime? false (default CLASS retention)
backup getAnnotation(Schedule): null (repeated ones live in the container)
backup getAnnotationsByType(Schedule): [Mon, Thu]
legacyExport: audited by ravi at default level 1, deprecated since 2.0, forRemoval=true
annotation types are interfaces? true, isAnnotation? true, main has annotations: 0
*/
