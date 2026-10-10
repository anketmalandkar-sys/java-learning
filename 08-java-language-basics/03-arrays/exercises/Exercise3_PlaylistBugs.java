import java.util.Arrays;

/**
 * Exercise 3 (Hard): find and fix three array bugs.
 *
 * TASK
 *   A music app stores a playlist as a String[]. Each method below has ONE bug. Find and fix it.
 *   Don't change the method signatures.
 *
 *     shuffledPreview(playlist)  must return a reordered COPY. The caller's array must not change.
 *     sameOrder(a, b)            must be true when both playlists hold the same songs in the
 *                                same order, even if they are different array objects.
 *     rotateLeft(playlist)       moves every song one place to the left, and the first song to
 *                                the end, IN PLACE:  [A, B, C, D] -> [B, C, D, A]
 *
 *   Then add one more method:
 *     withSong(playlist, song)   returns a NEW array one longer, with song at the end.
 *                                The original is unchanged.
 *
 * EXPECTED OUTPUT
 *   preview is a copy:  PASS
 *   sameOrder:          PASS
 *   rotateLeft:         PASS
 *   withSong:           PASS
 *   ALL PASS
 *
 * HINTS
 *   - Does "String[] copy = playlist;" create a new array?
 *   - What do == and .equals check for arrays?
 *   - Run rotateLeft on paper with 4 songs. What happens at the last index?
 *   - Arrays.copyOf can make an array longer.
 *
 * Run: java exercises/Exercise3_PlaylistBugs.java
 */
public class Exercise3_PlaylistBugs {

    static String[] shuffledPreview(String[] playlist) {
        String[] copy = playlist;
        // "Shuffle" by reversing: simple and predictable for the checks.
        for (int i = 0; i < copy.length / 2; i++) {
            String temp = copy[i];
            copy[i] = copy[copy.length - 1 - i];
            copy[copy.length - 1 - i] = temp;
        }
        return copy;
    }

    static boolean sameOrder(String[] a, String[] b) {
        return a.equals(b);
    }

    static void rotateLeft(String[] playlist) {
        if (playlist.length == 0) {
            return;
        }
        String first = playlist[0];
        for (int i = 0; i < playlist.length - 1; i++) {
            playlist[i] = playlist[i + 1];
        }
        playlist[0] = first;
    }

    static String[] withSong(String[] playlist, String song) {
        return playlist; // TODO
    }

    public static void main(String[] args) {
        boolean allPass = true;

        String[] mine = {"Intro", "Rain", "Neon", "Outro"};
        String[] preview = shuffledPreview(mine);
        allPass &= check("preview is a copy:",
                Arrays.equals(preview, new String[] {"Outro", "Neon", "Rain", "Intro"})
                        && Arrays.equals(mine, new String[] {"Intro", "Rain", "Neon", "Outro"}));

        String[] yours = {"Intro", "Rain", "Neon", "Outro"};
        allPass &= check("sameOrder:", sameOrder(mine, yours) && !sameOrder(mine, preview));

        String[] queue = {"A", "B", "C", "D"};
        rotateLeft(queue);
        String[] single = {"Solo"};
        rotateLeft(single);
        allPass &= check("rotateLeft:", Arrays.equals(queue, new String[] {"B", "C", "D", "A"})
                && Arrays.equals(single, new String[] {"Solo"}));

        String[] longer = withSong(mine, "Encore");
        allPass &= check("withSong:", longer.length == 5 && "Encore".equals(longer[4])
                && "Outro".equals(longer[3]) && mine.length == 4);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-19s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
