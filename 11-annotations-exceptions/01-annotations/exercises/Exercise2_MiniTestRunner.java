import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Exercise 2 (Medium): a tiny JUnit-style test runner.
 *
 * TASK
 *   Write run(Class<?> testClass), which returns a Report:
 *     - Create ONE instance of testClass with its no-arg constructor.
 *     - Find every method annotated @Check, in ALPHABETICAL order of method name.
 *     - If the method also has @Skip, don't run it: add its name to `skipped`.
 *     - Otherwise invoke it. If it returns normally, add the name to `passed`.
 *       If it throws, add "<name>: <message of the exception the test threw>" to `failed`.
 *     - Methods without @Check are never run.
 *
 *   Careful: Method.invoke wraps whatever the test threw in an InvocationTargetException.
 *   The real exception is getCause().
 *
 * EXPECTED OUTPUT
 *   passed:  PASS
 *   failed:  PASS
 *   skipped: PASS
 *   helper:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - Arrays.stream(cls.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Check.class))
 *   - Sort with Comparator.comparing(Method::getName): getDeclaredMethods has no fixed order.
 *   - testClass.getDeclaredConstructor().newInstance()
 *
 * Run: java exercises/Exercise2_MiniTestRunner.java
 */
public class Exercise2_MiniTestRunner {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Check { }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Skip {
        String reason() default "";
    }

    record Report(List<String> passed, List<String> failed, List<String> skipped) { }

    static Report run(Class<?> testClass) throws ReflectiveOperationException {
        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        List<String> skipped = new ArrayList<>();
        // TODO
        return new Report(passed, failed, skipped);
    }

    // ---- The class under test. Don't change it. ----
    public static class CartTests {
        static int helperCalls = 0;

        @Check
        public void addsItems() {
            int total = 2 + 3;
            if (total != 5) {
                throw new AssertionError("expected 5");
            }
        }

        @Check
        public void appliesDiscount() {
            throw new AssertionError("expected 90 but was 100");
        }

        @Check
        @Skip(reason = "payment gateway down")
        public void chargesCard() {
            throw new IllegalStateException("should never run");
        }

        @Check
        public void emptyCartIsFree() { }

        public void helper() {
            helperCalls++;
        }
    }

    public static void main(String[] args) throws Exception {
        Report r = run(CartTests.class);
        boolean allPass = true;
        allPass &= check("passed:", r.passed().equals(List.of("addsItems", "emptyCartIsFree")));
        allPass &= check("failed:", r.failed().equals(List.of("appliesDiscount: expected 90 but was 100")));
        allPass &= check("skipped:", r.skipped().equals(List.of("chargesCard")));
        allPass &= check("helper:", CartTests.helperCalls == 0);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-8s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
