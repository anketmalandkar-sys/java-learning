import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Solution 3: the fixed game leaderboard.
 *
 * BUG 1: Bela disappeared.
 *   compareTo looked only at score. Arjun and Bela both have 1200, so compareTo returned 0
 *   and the TreeSet treated Bela as a duplicate of Arjun and ignored add().
 *   FIX: when scores tie, compare names. Names are unique, so compareTo returns 0 only
 *   for the same player.
 *
 * BUG 2: Zara (3 billion) ranked near the bottom.
 *   (int) (other.score - this.score) with Zara vs Chen = (int) (950 - 3_000_000_000)
 *   = (int) -2_999_999_050. That doesn't fit in an int (max about 2.1 billion), so the
 *   cast keeps only the low 32 bits and the result is +1_294_968_246: the wrong sign.
 *   FIX: Long.compare(other.score, this.score). Never subtract, and never cast.
 *
 * BUG 3: Dina's points didn't change her rank.
 *   A TreeSet places each element when it's added. Changing score afterwards doesn't
 *   move the element, so the tree stays in the old order (and contains/remove can
 *   start failing too).
 *   FIX: remove the player, change the score, and add it back.
 *
 * Run: java solutions/Solution3_LeaderboardBug.java
 */
public class Solution3_LeaderboardBug {

    static class Player implements Comparable<Player> {
        private final String name;
        private long score;

        Player(String name, long score) {
            this.name = name;
            this.score = score;
        }

        String getName() {
            return name;
        }

        void addToScore(long points) {
            score += points;
        }

        @Override
        public int compareTo(Player other) {
            // Bug 2 fix: Long.compare can't overflow. Arguments are swapped for highest-first.
            int byScore = Long.compare(other.score, this.score);
            if (byScore != 0) {
                return byScore;
            }
            // Bug 1 fix: tie-breaker, so different players never compare as equal
            return this.name.compareTo(other.name);
        }

        @Override
        public String toString() {
            return name + " " + score;
        }
    }

    static class Leaderboard {
        private final TreeSet<Player> ranking = new TreeSet<>();

        void join(Player player) {
            ranking.add(player);
        }

        void addPoints(String playerName, long points) {
            for (Player player : ranking) {
                if (player.getName().equals(playerName)) {
                    // Bug 3 fix: take it out while its position is still correct, change it,
                    // then re-insert so the tree puts it in the right place.
                    // Returning right away matters: changing a TreeSet while looping over it
                    // and then continuing the loop throws ConcurrentModificationException.
                    ranking.remove(player);
                    player.addToScore(points);
                    ranking.add(player);
                    return;
                }
            }
        }

        List<Player> standings() {
            return new ArrayList<>(ranking);
        }
    }

    public static void main(String[] args) {
        Leaderboard board = new Leaderboard();
        board.join(new Player("Chen", 950));
        board.join(new Player("Arjun", 1200));
        board.join(new Player("Bela", 1200));
        board.join(new Player("Zara", 3_000_000_000L));
        board.join(new Player("Dina", 0));

        board.addPoints("Dina", 2000);

        List<Player> standings = board.standings();
        System.out.println("Leaderboard: " + standings);

        String expected = "[Zara 3000000000, Dina 2000, Arjun 1200, Bela 1200, Chen 950]";
        System.out.println(expected.equals(standings.toString()) ? "PASS" : "FAIL: expected " + expected);
    }
}

/* Expected output:
Leaderboard: [Zara 3000000000, Dina 2000, Arjun 1200, Bela 1200, Chen 950]
PASS
*/
