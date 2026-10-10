import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): an arithmetic expression evaluator.
 *
 * TASK
 *   Expressions are trees of records under a sealed interface:
 *       Num(5)                     the number 5
 *       Add(left, right)           left + right
 *       Mul(left, right)           left * right
 *       Neg(inner)                 -inner
 *   so 2 * (3 + 4) is  new Mul(new Num(2), new Add(new Num(3), new Num(4))).
 *
 *   Implement three methods, each with a switch over Expr using RECORD PATTERNS and NO default
 *   (the sealed interface makes the switch exhaustive):
 *
 *     eval(expr)       the integer value.                        2 * (3 + 4) -> 14
 *
 *     show(expr)       a fully bracketed string:
 *                        Num        -> "5"
 *                        Add        -> "(left + right)"
 *                        Mul        -> "(left * right)"
 *                        Neg        -> "-" + show(inner)
 *                      2 * (3 + 4) -> "(2 * (3 + 4))"
 *
 *     simplify(expr)   simplify the children first, then apply these rules to the result:
 *                        0 + e  and  e + 0   ->  e
 *                        1 * e  and  e * 1   ->  e
 *                        0 * e  and  e * 0   ->  Num(0)
 *                        -(-e)               ->  e
 *                        -(Num n)            ->  Num(-n)
 *                      Anything else stays as it is.
 *
 *   Don't change the records or main.
 *
 * EXPECTED OUTPUT
 *   eval:     PASS
 *   show:     PASS
 *   simplify: PASS
 *   ALL PASS
 *
 * HINTS
 *   - Recursion: case Add(Expr left, Expr right) -> eval(left) + eval(right);
 *   - A record pattern can't contain a constant: Num(0) is not a valid pattern.
 *     Use a guard instead:  case Add(Num(int n), Expr right) when n == 0 -> right;
 *   - Nested patterns: case Neg(Neg(Expr inner)) -> inner;
 *   - In simplify, first build the expression with simplified children, e.g.
 *       Expr simpler = switch (expr) { case Add(var l, var r) -> new Add(simplify(l), simplify(r)); ... };
 *     then switch over simpler to apply the rules.
 *   - Guarded cases don't count towards exhaustiveness (the guard might be false). After the
 *     rules, add one plain case per type, e.g. case Add a -> a; so every Expr is still covered.
 *
 * Run: java exercises/Exercise2_ExpressionEvaluator.java
 */
public class Exercise2_ExpressionEvaluator {

    sealed interface Expr permits Num, Add, Mul, Neg {}
    record Num(int value) implements Expr {}
    record Add(Expr left, Expr right) implements Expr {}
    record Mul(Expr left, Expr right) implements Expr {}
    record Neg(Expr inner) implements Expr {}

    static int eval(Expr expr) {
        // TODO
        return 0;
    }

    static String show(Expr expr) {
        // TODO
        return "";
    }

    static Expr simplify(Expr expr) {
        // TODO
        return expr;
    }

    public static void main(String[] args) {
        Expr twoTimesSum = new Mul(new Num(2), new Add(new Num(3), new Num(4)));
        Expr negated = new Neg(new Add(new Num(10), new Neg(new Num(4))));

        boolean allPass = true;

        allPass &= check("eval", () ->
                eval(new Num(7)) == 7
                        && eval(twoTimesSum) == 14
                        && eval(negated) == -6);

        allPass &= check("show", () ->
                show(new Num(7)).equals("7")
                        && show(twoTimesSum).equals("(2 * (3 + 4))")
                        && show(negated).equals("-(10 + -4)"));

        allPass &= check("simplify", () -> {
            Expr x = new Mul(new Num(6), new Num(7));
            return simplify(new Add(new Num(0), x)).equals(x)
                    && simplify(new Mul(x, new Num(1))).equals(x)
                    && simplify(new Mul(new Num(0), twoTimesSum)).equals(new Num(0))
                    && simplify(new Neg(new Neg(x))).equals(x)
                    && simplify(new Neg(new Num(5))).equals(new Num(-5))
                    // children first: inside-out, 1 * (x + 0)  ->  1 * x  ->  x
                    && simplify(new Mul(new Num(1), new Add(x, new Num(0)))).equals(x)
                    && simplify(twoTimesSum).equals(twoTimesSum);   // nothing to simplify
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (RuntimeException e) {
            System.out.printf("%-9s FAIL (%s)%n", name + ":", e);
            return false;
        }
        System.out.printf("%-9s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
