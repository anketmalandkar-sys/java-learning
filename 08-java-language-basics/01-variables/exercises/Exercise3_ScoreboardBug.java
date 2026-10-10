/**
 * Exercise 3 (Hard): a shared-state bug, plus locals and a constant.
 *
 * TASK
 *   A quiz game tracks each player's score. A tester reports: "When Kiran scores, Zoya's score
 *   goes up too." Fix the bug in Player.
 *
 *   Then finish addRound(int[] answersCorrect):
 *     - Each correct answer (1 in the array) is worth 10 points; 0 means wrong.
 *     - A round with ALL answers correct earns a 20-point bonus.
 *     - A player's score can never go above MAX_SCORE (300). Add that constant.
 *     - Use local variables for the round's points; only touch the field once at the end.
 *
 *   Also track the HIGHEST score any player has reached, shared across all players,
 *   in a field read by Player.bestScoreSoFar().
 *
 * EXAMPLES
 *   addRound({1, 0, 1})       -> +20
 *   addRound({1, 1, 1, 1})    -> +40 +20 bonus = +60
 *   addRound({})              -> +0 (an empty round is not "all correct")
 *
 * EXPECTED OUTPUT
 *   scores are separate:  PASS
 *   round with bonus:     PASS
 *   empty round:          PASS
 *   score capped:         PASS
 *   best score:           PASS
 *   ALL PASS
 *
 * HINTS
 *   - Which variable should be one-per-player, and which one-for-the-whole-game?
 *   - A local used for counting must start at 0; the compiler won't do it for you.
 *
 * Run: java exercises/Exercise3_ScoreboardBug.java
 */
public class Exercise3_ScoreboardBug {

    static class Player {
        final String name;
        static int score;   // reported bug lives here

        // TODO: MAX_SCORE constant
        // TODO: shared field for the best score so far

        Player(String name) {
            this.name = name;
        }

        void addRound(int[] answersCorrect) {
            // TODO: count correct answers with a local, compute points, apply bonus and cap,
            //       update score once, and update the best score if beaten
        }

        int score() {
            return score;
        }

        static int bestScoreSoFar() {
            return 0; // TODO
        }
    }

    public static void main(String[] args) {
        Player kiran = new Player("Kiran");
        Player zoya = new Player("Zoya");

        boolean allPass = true;

        kiran.addRound(new int[] {1, 0, 1});
        allPass &= check("scores are separate:", kiran.score() == 20 && zoya.score() == 0);

        zoya.addRound(new int[] {1, 1, 1, 1});
        allPass &= check("round with bonus:", zoya.score() == 60 && kiran.score() == 20);

        kiran.addRound(new int[] {});
        allPass &= check("empty round:", kiran.score() == 20);

        for (int round = 0; round < 10; round++) {
            zoya.addRound(new int[] {1, 1, 1});   // +50 each time
        }
        allPass &= check("score capped:", zoya.score() == 300);

        allPass &= check("best score:", Player.bestScoreSoFar() == 300);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-21s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
