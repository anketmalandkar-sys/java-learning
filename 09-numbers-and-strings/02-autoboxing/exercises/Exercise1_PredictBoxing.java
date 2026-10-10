/**
 * Exercise 1 (Easy): predict == and equals on boxed values.
 *
 * TASK
 *   For each comparison, predict true or false, and set the matching answer. The program checks
 *   your predictions against what Java really does, and tells you which ones were wrong.
 *
 *     q1   Integer a = 100, b = 100;        a == b
 *     q2   Integer c = 1000, d = 1000;      c == d
 *     q3   Integer c = 1000, d = 1000;      c.equals(d)
 *     q4   Integer e = 1000; int f = 1000;  e == f
 *     q5   Long g = 5L;                     g.equals(5)
 *     q6   Long g = 5L;                     g.equals(5L)
 *     q7   Integer h = 5; Long i = 5L;      h.longValue() == i
 *     q8   Character j = 'a', k = 'a';      j == k
 *
 * EXPECTED OUTPUT
 *   8 / 8 correct
 *   ALL PASS
 *
 * HINTS
 *   - Two wrappers with ==: reference comparison. Which values are cached?
 *   - A wrapper and a primitive with ==: the wrapper is unboxed.
 *   - equals needs the same TYPE as well as the same value.
 *
 * Run: java exercises/Exercise1_PredictBoxing.java
 */
public class Exercise1_PredictBoxing {

    // TODO: your predictions
    static boolean q1 = false;
    static boolean q2 = false;
    static boolean q3 = false;
    static boolean q4 = false;
    static boolean q5 = false;
    static boolean q6 = false;
    static boolean q7 = false;
    static boolean q8 = false;

    public static void main(String[] args) {
        Integer a = 100, b = 100;
        Integer c = 1000, d = 1000;
        Integer e = 1000;
        int f = 1000;
        Long g = 5L;
        Integer h = 5;
        Long i = 5L;
        Character j = 'a', k = 'a';

        boolean[] actual = {
            a == b,
            c == d,
            c.equals(d),
            e == f,
            g.equals(5),
            g.equals(5L),
            h.longValue() == i,
            j == k,
        };
        boolean[] predicted = {q1, q2, q3, q4, q5, q6, q7, q8};

        int correct = 0;
        for (int n = 0; n < actual.length; n++) {
            if (actual[n] == predicted[n]) {
                correct++;
            } else {
                System.out.println("q" + (n + 1) + ": you said " + predicted[n] + ", Java says " + actual[n]);
            }
        }
        System.out.println(correct + " / " + actual.length + " correct");
        System.out.println(correct == actual.length ? "ALL PASS" : "SOME FAILED");
    }
}
