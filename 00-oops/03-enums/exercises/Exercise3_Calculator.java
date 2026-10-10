import java.util.EnumMap;
import java.util.Map;

/**
 * Exercise 3 (Hard): constant-specific methods and EnumMap.
 *
 * TASK
 *   1. Op currently uses a switch inside apply(). Replace it with CONSTANT-SPECIFIC bodies:
 *      make apply abstract and give each constant its own implementation. Keep the symbols.
 *      DIVIDE must throw ArithmeticException("division by zero") for b == 0.
 *      Then add a new constant MOD("%") and make sure the compiler forces you to implement it.
 *
 *   2. static Op fromSymbol(String symbol): the Op with that symbol, or throw
 *      IllegalArgumentException("unknown operator: " + symbol).
 *
 *   3. Calculator.evaluate(a, symbol, b) applies the op and counts how often each op was used
 *      in an EnumMap<Op, Integer>. usage() returns that map. Failed evaluations (unknown op,
 *      division by zero) must NOT be counted.
 *
 * EXPECTED OUTPUT
 *   apply:      PASS
 *   mod:        PASS
 *   fromSymbol: PASS
 *   usage:      PASS
 *   ALL PASS
 *
 * HINTS
 *   - Each constant's body: ADD("+") { @Override long apply(long a, long b) { return a + b; } },
 *   - Count only after apply() returned.
 *
 * Run: java exercises/Exercise3_Calculator.java
 */
public class Exercise3_Calculator {

    enum Op {
        ADD("+"), SUBTRACT("-"), MULTIPLY("*"), DIVIDE("/");

        private final String symbol;

        Op(String symbol) {
            this.symbol = symbol;
        }

        String symbol() {
            return symbol;
        }

        // TODO: make this abstract and move each case into its constant's body
        long apply(long a, long b) {
            return switch (this) {
                case ADD -> a + b;
                case SUBTRACT -> a - b;
                case MULTIPLY -> a * b;
                case DIVIDE -> a / b;
            };
        }

        static Op fromSymbol(String symbol) {
            return null; // TODO
        }
    }

    static class Calculator {
        private final Map<Op, Integer> usage = new EnumMap<>(Op.class);

        long evaluate(long a, String symbol, long b) {
            return 0; // TODO
        }

        Map<Op, Integer> usage() {
            return usage;
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("apply:", Op.ADD.apply(6, 7) == 13 && Op.SUBTRACT.apply(6, 7) == -1
                && Op.MULTIPLY.apply(6, 7) == 42 && Op.DIVIDE.apply(42, 5) == 8
                && throwsWith(() -> Op.DIVIDE.apply(1, 0), ArithmeticException.class, "division by zero"));

        Op mod = null;
        for (Op op : Op.values()) {
            if (op.name().equals("MOD")) {
                mod = op;
            }
        }
        allPass &= check("mod:", mod != null && "%".equals(mod.symbol()) && mod.apply(17, 5) == 2);

        allPass &= check("fromSymbol:", Op.fromSymbol("*") == Op.MULTIPLY
                && throwsWith(() -> Op.fromSymbol("^"), IllegalArgumentException.class, "unknown operator: ^"));

        Calculator calc = new Calculator();
        calc.evaluate(2, "+", 3);
        calc.evaluate(10, "+", 1);
        calc.evaluate(10, "/", 2);
        try { calc.evaluate(1, "/", 0); } catch (ArithmeticException e) { /* not counted */ }
        try { calc.evaluate(1, "^", 2); } catch (IllegalArgumentException e) { /* not counted */ }
        allPass &= check("usage:", calc.usage().equals(Map.of(Op.ADD, 2, Op.DIVIDE, 1)));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean throwsWith(Runnable action, Class<? extends RuntimeException> type, String message) {
        try {
            action.run();
            return false;
        } catch (RuntimeException e) {
            return type.isInstance(e) && message.equals(e.getMessage());
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-11s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
