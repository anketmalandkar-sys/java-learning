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

    static boolean q1 = true;    // 100 is in the Integer cache (-128..127): same object
    static boolean q2 = false;   // 1000 isn't cached: two different objects
    static boolean q3 = true;    // equals compares values
    static boolean q4 = true;    // wrapper == primitive: e is unboxed, values compared
    static boolean q5 = false;   // 5 boxes to an Integer, and a Long never equals an Integer
    static boolean q6 = true;    // same type, same value
    static boolean q7 = true;    // long == Long: i is unboxed
    static boolean q8 = true;    // Characters 0..127 are cached

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
