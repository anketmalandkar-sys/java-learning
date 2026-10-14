import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Exercise 3 (Hard): a stack calculator driven by switch expressions.
 *
 * TASK
 *   Reverse Polish Notation (RPN) writes the operator AFTER its operands, so no parentheses
 *   are needed:  "3 4 + 2 *"  means (3 + 4) * 2 = 14.
 *
 *   Write evaluate(expression) that returns the result as a long:
 *     - Split the expression on spaces into tokens.
 *     - For a number, push it on the stack.
 *     - For an operator, pop b (top), then a, and push the result of "a op b".
 *     - At the end, exactly one value must remain: return it.
 *
 *   Operators (compute the result with ONE switch expression over the token):
 *     +  -  *  /     the usual (integer division)
 *     %              remainder
 *     ^              a to the power b (b >= 0); write the loop inside a yield block, no Math.pow
 *     max            the larger of a and b
 *
 *   Errors: throw IllegalArgumentException with exactly these messages:
 *     division (or %) by zero      "division by zero"
 *     not enough values for an op  "not enough operands"
 *     a token that's not a number and not an operator      "unknown token: <token>"
 *     more or less than one value left at the end          "invalid expression"
 *
 * EXPECTED OUTPUT
 *   basic:       PASS
 *   precedence:  PASS
 *   power/max:   PASS
 *   errors:      PASS
 *   ALL PASS
 *
 * HINTS
 *   - Deque<Long> stack = new ArrayDeque<>(); push(), pop(), size().
 *   - Decide "operator or number" with a switch too, or try Long.parseLong and catch
 *     NumberFormatException for the unknown-token error.
 *   - case "/", "%" -> { ... yield ... }  can share the zero check.
 *
 * Run: java exercises/Exercise3_RpnCalculator.java
 */
public class Exercise3_RpnCalculator {

    static long evaluate(String expression) {
        Deque<Long> stack = new ArrayDeque<>();
        for (String token : expression.trim().split(" +")) {
            boolean isOperator = switch (token) {
                case "+", "-", "*", "/", "%", "^", "max" -> true;
                default -> false;
            };
            if (!isOperator) {
                stack.push(parseNumber(token));
                continue;
            }
            if (stack.size() < 2) {
                throw new IllegalArgumentException("not enough operands");
            }
            long b = stack.pop();
            long a = stack.pop();
            long result = switch (token) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/", "%" -> {
                    if (b == 0) {
                        throw new IllegalArgumentException("division by zero");
                    }
                    yield token.equals("/") ? a / b : a % b;
                }
                case "^" -> {
                    long power = 1;
                    for (long i = 0; i < b; i++) {
                        power *= a;
                    }
                    yield power;
                }
                case "max" -> Math.max(a, b);
                default -> throw new IllegalStateException("not an operator: " + token);
            };
            stack.push(result);
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("invalid expression");
        }
        return stack.pop();
    }

    static long parseNumber(String token) {
        try {
            return Long.parseLong(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("unknown token: " + token);
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;
        allPass &= check("basic:", evaluate("3 4 +") == 7 && evaluate("10 3 -") == 7
                && evaluate("6 7 *") == 42 && evaluate("7 2 /") == 3 && evaluate("7 2 %") == 1
                && evaluate("42") == 42);
        allPass &= check("precedence:", evaluate("3 4 + 2 *") == 14 && evaluate("3 4 2 * +") == 11
                && evaluate("5 1 2 + 4 * + 3 -") == 14);
        allPass &= check("power/max:", evaluate("2 10 ^") == 1024 && evaluate("5 0 ^") == 1
                && evaluate("3 9 max 4 max") == 9);
        allPass &= check("errors:", throwsWith("1 0 /", "division by zero")
                && throwsWith("1 0 %", "division by zero")
                && throwsWith("1 +", "not enough operands")
                && throwsWith("1 2 ?", "unknown token: ?")
                && throwsWith("1 2", "invalid expression"));
        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean throwsWith(String expression, String message) {
        try {
            evaluate(expression);
            return false;
        } catch (IllegalArgumentException e) {
            return message.equals(e.getMessage());
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-12s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
