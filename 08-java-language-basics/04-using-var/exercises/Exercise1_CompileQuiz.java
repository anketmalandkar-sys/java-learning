/**
 * Exercise 1 (Easy): which var declarations compile?
 *
 * TASK
 *   For each snippet, decide whether it compiles. Set the matching answer to true (compiles)
 *   or false (compile error). Answer from the rules first; then you can test a snippet by
 *   pasting it into a method below and trying to run the file.
 *
 *     q1   var city = "Pune";                               (inside a method)
 *     q2   var total;                                       (inside a method)
 *     q3   class Cart { var items = 3; }
 *     q4   var nothing = null;                              (inside a method)
 *     q5   for (var i = 0; i < 3; i++) { }
 *     q6   void ship(var weight) { }
 *     q7   var nums = {1, 2, 3};                            (inside a method)
 *     q8   var nums = new int[] {1, 2, 3};                  (inside a method)
 *     q9   var a = 1, b = 2;                                (inside a method)
 *     q10  var x = 5;  x = 7.5;                             (inside a method)
 *
 * EXPECTED OUTPUT
 *   10 / 10 correct
 *   ALL PASS
 *
 *   If not all are correct, the program tells you HOW MANY are wrong, not which ones.
 *   Re-read the "Where it doesn't" section of the README.
 *
 * Run: java exercises/Exercise1_CompileQuiz.java
 */
public class Exercise1_CompileQuiz {

    // TODO: set each answer
    static boolean q1 = false;
    static boolean q2 = false;
    static boolean q3 = false;
    static boolean q4 = false;
    static boolean q5 = false;
    static boolean q6 = false;
    static boolean q7 = false;
    static boolean q8 = false;
    static boolean q9 = false;
    static boolean q10 = false;

    public static void main(String[] args) {
        boolean[] answers = {q1, q2, q3, q4, q5, q6, q7, q8, q9, q10};
        // The answer key, as bits: bit i is 1 if question i+1 compiles. Reading it gives the
        // answers away, so try the quiz first.
        int key = 0b0010010001;
        int correct = 0;
        for (int i = 0; i < answers.length; i++) {
            boolean compiles = (key >> i & 1) == 1;
            if (answers[i] == compiles) {
                correct++;
            }
        }
        System.out.println(correct + " / " + answers.length + " correct");
        System.out.println(correct == answers.length ? "ALL PASS" : "SOME FAILED");
    }
}
