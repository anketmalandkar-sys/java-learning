import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Solution to Exercise 3 (Hard): a checkout pipeline with Result<T>: no exceptions, no nulls.
 *
 * TASK
 *   Part A. Finish the sealed Result<T> (Java 17+). It's either a Success holding a value,
 *   or a Failure holding an error message. Implement in BOTH records:
 *
 *     map(mapper)       Success: apply mapper to the value and wrap the answer in a Success.
 *                       Failure: return a Failure with the same error (mapper is never called).
 *     flatMap(mapper)   like map, but mapper itself returns a Result. Don't wrap it again.
 *                       This is how you chain steps that can each fail.
 *     getOrElse(x)      Success: the value. Failure: x.
 *     isSuccess()       true for a Success
 *
 *   Part B. Write the checkout steps. Each returns a Result. None of them throws, and none returns null:
 *
 *     parseQuantity(text)       " 2 " -> Success(2)
 *                               "abc" -> Failure("not a number: 'abc'")
 *                               "0"   -> Failure("quantity must be at least 1")
 *     findProduct(sku)          a known SKU -> Success(product)
 *                               an unknown one -> Failure("unknown product: SKU-9")
 *     reserve(product, qty)     qty <= product.stock() -> Success(product)
 *                               otherwise -> Failure("only 3 left of Mouse")
 *     checkout(sku, qtyText)    chain the three steps with flatMap/map into a receipt line:
 *                               Success("2 x Keyboard = 2998.0")
 *                               The FIRST failure is passed through unchanged.
 *
 *   Don't change main or the CATALOG.
 *
 * EXPECTED OUTPUT
 *   map:        PASS
 *   flatMap:    PASS
 *   getOrElse:  PASS
 *   parse:      PASS
 *   find:       PASS
 *   checkout:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - Integer.parseInt throws NumberFormatException. Catch it inside parseQuantity and
 *     turn it into a Failure, so no exception escapes.
 *   - In checkout, you need both the product and the quantity at the end. One way:
 *       parseQuantity(qtyText).flatMap(qty ->
 *           findProduct(sku).flatMap(product -> reserve(product, qty))
 *                           .map(product -> ...receipt using product and qty...));
 *   - A Failure<T> can't just "return this" as a Result<R>: build a new Failure with the same error.
 *
 * Run: java solutions/Solution3_CheckoutResult.java
 */
public class Solution3_CheckoutResult {

    sealed interface Result<T> permits Success, Failure {
        static <T> Result<T> success(T value) { return new Success<>(value); }
        static <T> Result<T> failure(String error) { return new Failure<>(error); }

        <R> Result<R> map(Function<? super T, ? extends R> mapper);
        <R> Result<R> flatMap(Function<? super T, Result<R>> mapper);
        T getOrElse(T fallback);
        boolean isSuccess();
    }

    record Success<T>(T value) implements Result<T> {
        public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
            return Result.success(mapper.apply(value));
        }

        // mapper already returns a Result, so return it as is: wrapping it again
        // would give a Result<Result<R>>.
        public <R> Result<R> flatMap(Function<? super T, Result<R>> mapper) {
            return mapper.apply(value);
        }

        public T getOrElse(T fallback) {
            return value;
        }

        public boolean isSuccess() {
            return true;
        }
    }

    // A Failure skips every step and carries its error to the end. It can't just "return this":
    // this is a Result<T>, but the method must return a Result<R>. So it builds a new Failure<R>.
    record Failure<T>(String error) implements Result<T> {
        public <R> Result<R> map(Function<? super T, ? extends R> mapper) {
            return Result.failure(error);
        }

        public <R> Result<R> flatMap(Function<? super T, Result<R>> mapper) {
            return Result.failure(error);
        }

        public T getOrElse(T fallback) {
            return fallback;
        }

        public boolean isSuccess() {
            return false;
        }
    }

    record Product(String sku, String name, double price, int stock) {}

    static final Map<String, Product> CATALOG = Map.of(
            "SKU-1", new Product("SKU-1", "Keyboard", 1499.0, 10),
            "SKU-2", new Product("SKU-2", "Mouse", 599.0, 3));

    static Result<Integer> parseQuantity(String text) {
        int quantity;
        try {
            quantity = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return Result.failure("not a number: '" + text + "'");   // the exception stops here
        }
        if (quantity < 1) {
            return Result.failure("quantity must be at least 1");
        }
        return Result.success(quantity);
    }

    static Result<Product> findProduct(String sku) {
        Product product = CATALOG.get(sku);
        if (product == null) {
            return Result.failure("unknown product: " + sku);
        }
        return Result.success(product);
    }

    static Result<Product> reserve(Product product, int quantity) {
        if (quantity > product.stock()) {
            return Result.failure("only " + product.stock() + " left of " + product.name());
        }
        return Result.success(product);
    }

    // Each step runs only if the one before succeeded. The first failure flows to the end unchanged.
    // flatMap for steps that can fail (they return a Result); map for the last step, which can't.
    // The receipt is built inside the outer lambda, where both qty and product are in scope.
    static Result<String> checkout(String sku, String quantityText) {
        return parseQuantity(quantityText).flatMap(qty ->
                findProduct(sku)
                        .flatMap(product -> reserve(product, qty))
                        .map(product -> qty + " x " + product.name() + " = " + product.price() * qty));
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("map", () -> {
            Result<String> label = Result.success(42).map(n -> "qty " + n);
            Result<String> failed = Result.<Integer>failure("boom").map(n -> "qty " + n);
            return label.equals(Result.success("qty 42")) && failed.equals(Result.failure("boom"));
        });

        allPass &= check("flatMap", () -> {
            Function<Integer, Result<Integer>> half = n -> n % 2 == 0
                    ? Result.success(n / 2)
                    : Result.failure(n + " is odd");
            return Result.success(10).flatMap(half).equals(Result.success(5))
                    && Result.success(7).flatMap(half).equals(Result.failure("7 is odd"))
                    && Result.<Integer>failure("no input").flatMap(half).equals(Result.failure("no input"));
        });

        allPass &= check("getOrElse", () ->
                Result.success("A").getOrElse("Z").equals("A")
                        && Result.<String>failure("x").getOrElse("Z").equals("Z")
                        && Result.success(1).isSuccess()
                        && !Result.failure("x").isSuccess());

        allPass &= check("parse", () ->
                parseQuantity(" 2 ").equals(Result.success(2))
                        && parseQuantity("abc").equals(Result.failure("not a number: 'abc'"))
                        && parseQuantity("0").equals(Result.failure("quantity must be at least 1")));

        allPass &= check("find", () ->
                findProduct("SKU-2").map(Product::name).equals(Result.success("Mouse"))
                        && findProduct("SKU-9").equals(Result.failure("unknown product: SKU-9")));

        allPass &= check("checkout", () ->
                checkout("SKU-1", "2").equals(Result.success("2 x Keyboard = 2998.0"))
                        && checkout("SKU-2", "3").equals(Result.success("3 x Mouse = 1797.0"))
                        && checkout("SKU-2", "5").equals(Result.failure("only 3 left of Mouse"))
                        && checkout("SKU-9", "1").equals(Result.failure("unknown product: SKU-9"))
                        && checkout("SKU-9", "lots").equals(Result.failure("not a number: 'lots'")));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-11s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-11s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
