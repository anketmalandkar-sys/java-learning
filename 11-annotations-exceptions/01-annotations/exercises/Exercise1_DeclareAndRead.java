import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Exercise 1 (Easy): declare an annotation and read it back.
 *
 * TASK
 *   1. Complete the annotation @Endpoint so that:
 *        - it can be read at runtime
 *        - it can only be placed on methods
 *        - it has a required String element path()
 *        - it has a String element method() that defaults to "GET"
 *        - it has an int element timeoutSeconds() that defaults to 30
 *
 *   2. Annotate the three methods of OrderApi as described in their comments.
 *
 *   3. Write describe(Method m): if m has @Endpoint, return "<method> <path> (<timeout>s)",
 *      e.g. "POST /orders (10s)". Otherwise return "not an endpoint".
 *
 * EXPECTED OUTPUT
 *   list:    PASS
 *   create:  PASS
 *   helper:  PASS
 *   target:  PASS
 *   ALL PASS
 *
 * HINTS
 *   - @Retention(RetentionPolicy.RUNTIME) and @Target(ElementType.METHOD) go on the annotation.
 *   - m.getAnnotation(Endpoint.class) returns null when the annotation isn't there.
 *
 * Run: java exercises/Exercise1_DeclareAndRead.java
 */
public class Exercise1_DeclareAndRead {

    // TODO: add the meta-annotations and the three elements
    @interface Endpoint {
    }

    static class OrderApi {
        // TODO: @Endpoint with path "/orders" (default method and timeout)
        public void listOrders() { }

        // TODO: @Endpoint with path "/orders", method "POST", timeout 10
        public void createOrder() { }

        // not an endpoint: no annotation
        public void validate() { }
    }

    static String describe(Method m) {
        return ""; // TODO
    }

    public static void main(String[] args) throws Exception {
        boolean allPass = true;
        allPass &= check("list:", "GET /orders (30s)".equals(describe(OrderApi.class.getMethod("listOrders"))));
        allPass &= check("create:", "POST /orders (10s)".equals(describe(OrderApi.class.getMethod("createOrder"))));
        allPass &= check("helper:", "not an endpoint".equals(describe(OrderApi.class.getMethod("validate"))));
        Target target = Endpoint.class.getAnnotation(Target.class);
        allPass &= check("target:", target != null && target.value().length == 1
                && target.value()[0] == ElementType.METHOD);
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-8s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
