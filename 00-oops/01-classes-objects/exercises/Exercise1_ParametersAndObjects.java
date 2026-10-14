package oops.exercises;

import java.util.Arrays;

/**
 * Exercise 1 (Easy): varargs, pass-by-value, shadowing, private constructors.
 * Goes with basics/ParametersAndObjects.java.
 *
 * TASK
 *   1. average(first, rest...): the average of one or more numbers. The signature forces
 *      at least one argument.                         average(4) -> 4.0, average(1, 2, 3, 6) -> 3.0
 *
 *   2. rotateRight(arr): move every element one place right, the last one to the front,
 *      IN THE CALLER'S ARRAY (it's an object, so changes are visible).  [1,2,3] -> [3,1,2]
 *
 *   3. Customer.rename(name) has a shadowing bug. Fix it.
 *
 *   4. Discount: make the constructor private and add static Discount of(int percent):
 *        - 0..90 allowed, otherwise IllegalArgumentException
 *        - of(0), of(10) and of(50) must return the SAME cached object each time
 *          (store them in static final fields); other values get a new object.
 *      Then change the two "new Discount(...)" calls in main to Discount.of(...).
 *
 * EXPECTED OUTPUT
 *   average:  PASS
 *   rotate:   PASS
 *   rename:   PASS
 *   discount: PASS
 *   cached:   PASS
 *   ALL PASS
 *
 * Run: press the green run button next to main in IntelliJ, or from the repo root:
 *   javac -d out $(find 00-oops/01-classes-objects -name "*.java") && java -cp out oops.exercises.Exercise1_ParametersAndObjects
 */
public class Exercise1_ParametersAndObjects {

    static double average(double first, double... rest) {
        double sum = first;
        for (double value : rest) {
            sum = Double.sum(sum, value);
        }
        return sum / (rest.length + 1);
    }

    static void rotateRight(int[] arr) {
        if (arr.length == 0) {
            return;
        }
        int last = arr[arr.length - 1];
        for (int i = arr.length - 1; i > 0; i--) {
            arr[i] = arr[i - 1];
        }
        arr[0] = last;
    }

    static class Customer {
        String name;

        Customer(String name) {
            this.name = name;
        }

        void rename(String name) {
            this.name = name.strip();
        }
    }

    static class Discount {
        private static final Discount NONE = new Discount(0);
        private static final Discount TEN = new Discount(10);
        private static final Discount HALF = new Discount(50);

        final int percent;

        private Discount(int percent) {
            this.percent = percent;
        }

        static Discount of(int percent) {
            if (percent < 0 || percent > 90) {
                throw new IllegalArgumentException("percent must be 0..90: " + percent);
            }
            return switch (percent) {
                case 0 -> NONE;
                case 10 -> TEN;
                case 50 -> HALF;
                default -> new Discount(percent);
            };
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("average:", average(4) == 4.0 && average(1, 2, 3, 6) == 3.0);

        int[] data = {1, 2, 3, 4};
        rotateRight(data);
        int[] single = {7};
        rotateRight(single);
        allPass &= check("rotate:", Arrays.equals(data, new int[] {4, 1, 2, 3}) && single[0] == 7);

        Customer c = new Customer("Asha");
        c.rename("  Asha Rao ");
        allPass &= check("rename:", "Asha Rao".equals(c.name));

        Discount d25 = Discount.of(25);
        Discount d95 = null;
        boolean rejected;
        try {
            d95 = Discount.of(95);
            rejected = false;
        } catch (IllegalArgumentException e) {
            rejected = true;
        }
        allPass &= check("discount:", d25.percent == 25 && rejected && d95 == null);
        allPass &= check("cached:", Discount.of(10) != null && Discount.of(10) == Discount.of(10)
                && Discount.of(0) == Discount.of(0) && Discount.of(25) != Discount.of(25));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-9s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
