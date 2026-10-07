import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Exercise 3 (Hard): fix the broken game leaderboard.
 *
 * STORY
 *   A game keeps its leaderboard in a TreeSet<Player>, ranked by score (highest first).
 *   Players are reporting three problems:
 *     1. "Bela joined, but she's not on the leaderboard!"
 *     2. "Zara has 3 billion points but shows up near the BOTTOM."
 *     3. "Dina just scored 2000 points, but her rank didn't change."
 *
 * TASK
 *   There are 3 bugs. Find and fix all of them. Don't change main.
 *   Required ranking: score descending; players with equal scores sorted by name A to Z.
 *
 * EXPECTED OUTPUT
 *   Leaderboard: [Zara 3000000000, Dina 2000, Arjun 1200, Bela 1200, Chen 950]
 *   PASS
 *
 * HINTS
 *   - Bug 1: what does a TreeSet do when compareTo returns 0?
 *   - Bug 2: what does casting a long to int do when the value doesn't fit in an int?
 *   - Bug 3: a TreeSet places each element when it's ADDED. What happens if a field
 *            used by compareTo changes after that?
 *   - Debug by printing compareTo results for specific pairs, e.g. zara.compareTo(chen).
 *
 * Run: java exercises/Exercise3_LeaderboardBug.java
 */
public class Exercise3_LeaderboardBug {

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
            // Highest score first
            int res = Long.compare(other.score, this.score);
            if (res != 0) {
                return res;
            }
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
