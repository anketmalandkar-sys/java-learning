/**
 * Exercise 1 (Easy): predict the pool.
 *
 * TASK
 *   Below are 10 string comparisons. For each one, predict whether it prints true or false.
 *   Write your predictions in MY_ANSWERS, in order. Do it on paper first: DON'T run the code
 *   until all 10 are filled in.
 *
 *   Then run it. For each question it shows your answer, the real answer, and why.
 *   Aim for 10/10. For any you miss, re-read section 3 of the README and work out why.
 *
 *   Q1   "Java" == "Java"
 *   Q2   "Java" == new String("Java")
 *   Q3   new String("Java").equals("Java")
 *   Q4   "Ja" + "va" == "Java"
 *   Q5   ja + "va" == "Java"              where  String ja = "Ja";
 *   Q6   finalJa + "va" == "Java"         where  final String finalJa = "Ja";
 *   Q7   (ja + "va").intern() == "Java"
 *   Q8   "Java".toUpperCase() == "JAVA"
 *   Q9   java.concat("") == java          where  String java = "Java";
 *                                         (hint: read the Javadoc of String.concat)
 *   Q10  "java".equals("Java")
 *
 * EXPECTED OUTPUT
 *   Ten lines like:  Q1  you: true   actual: true   OK    (why...)
 *   then:            Score: 10/10
 *
 * Run: java exercises/Exercise1_PredictThePool.java
 */
public class Exercise1_PredictThePool {

    // TODO: replace each false with your prediction (true or false). Q1 is first.
    static final boolean[] MY_ANSWERS = {
            false, false, false, false, false,
            false, false, false, false, false
    };

    public static void main(String[] args) {
        String ja = "Ja";
        final String finalJa = "Ja";
        String java = "Java";

        boolean[] actual = {
                "Java" == "Java",
                "Java" == new String("Java"),
                new String("Java").equals("Java"),
                "Ja" + "va" == "Java",
                ja + "va" == "Java",
                finalJa + "va" == "Java",
                (ja + "va").intern() == "Java",
                "Java".toUpperCase() == "JAVA",
                java.concat("") == java,
                "java".equals("Java")
        };

        String[] why = {
                "both literals: one pooled object",
                "new String always makes a new object",
                "equals compares the characters",
                "both parts are literals: the compiler joins them into the pooled \"Java\"",
                "ja is a variable: the result is built at runtime, a new object",
                "final + literal is a compile-time constant: pooled",
                "intern() returns the pooled copy",
                "toUpperCase built a new string at runtime; the literal \"JAVA\" is a different object",
                "concat(\"\") is documented to return this same object",
                "equals is case-sensitive (equalsIgnoreCase would be true)"
        };

        int score = 0;
        for (int i = 0; i < actual.length; i++) {
            boolean ok = MY_ANSWERS[i] == actual[i];
            if (ok) {
                score++;
            }
            System.out.printf("Q%-3d you: %-5s  actual: %-5s  %-5s (%s)%n",
                    i + 1, MY_ANSWERS[i], actual[i], ok ? "OK" : "MISS", why[i]);
        }
        System.out.println("Score: " + score + "/" + actual.length);
    }
}
