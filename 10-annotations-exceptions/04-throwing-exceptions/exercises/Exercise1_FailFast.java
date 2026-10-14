import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Exercise 1 (Easy): fail fast with the right standard exception.
 *
 * TASK
 *   Add the checks to Playlist so bad calls fail immediately with these exceptions and EXACT
 *   messages:
 *
 *     new Playlist(null)           NullPointerException       "name"
 *     new Playlist("  ")           IllegalArgumentException   "name must not be blank"
 *     add(null)                    NullPointerException       "song"
 *     add(song) after lock()       IllegalStateException      "playlist <name> is locked"
 *     add(song) when 5 songs       IllegalStateException      "playlist <name> is full"
 *     songAt(i) out of range       IndexOutOfBoundsException  (any message)
 *     setRating(r), r not 1..5     IllegalArgumentException   "rating must be 1..5: <r>"
 *
 *   None of these are checked exceptions: they all report a mistake by the caller.
 *
 * EXPECTED OUTPUT
 *   constructor:  PASS
 *   add:          PASS
 *   songAt:       PASS
 *   rating:       PASS
 *   happy path:   PASS
 *   ALL PASS
 *
 * HINTS
 *   - Objects.requireNonNull(value, "message") throws NullPointerException with that message.
 *   - Objects.checkIndex(index, size) throws IndexOutOfBoundsException for you.
 *
 * Run: java exercises/Exercise1_FailFast.java
 */
public class Exercise1_FailFast {

    static class Playlist {
        static final int MAX_SONGS = 5;
        private final String name;
        private final List<String> songs = new ArrayList<>();
        private boolean locked;
        private int rating;

        Playlist(String name) {
            if (name == null) {
                throw new NullPointerException("name");
            }
            if (name.isBlank()) {
                throw new IllegalArgumentException("name must not be blank");
            }
            this.name = name;
        }

        void add(String song) {
            if (song == null) {
                throw new NullPointerException("song");
            }
            if (locked) {
                throw new IllegalStateException("playlist " + name + " is locked");
            }
            if (songs.size() >= MAX_SONGS) {
                throw new IllegalStateException("playlist " + name + " is full");
            }
            songs.add(song);
        }

        String songAt(int index) {
            Objects.checkIndex(index, songs.size());
            return songs.get(index);
        }

        void setRating(int rating) {
            if (rating < 1 || rating > 5) {
                throw new IllegalArgumentException("rating must be 1..5: " + rating);
            }
            this.rating = rating;
        }

        void lock() {
            locked = true;
        }

        int rating() {
            return rating;
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("constructor:", throwsWith(() -> new Playlist(null), NullPointerException.class, "name")
                && throwsWith(() -> new Playlist("  "), IllegalArgumentException.class, "name must not be blank"));

        Playlist chill = new Playlist("Chill");
        boolean addOk = throwsWith(() -> chill.add(null), NullPointerException.class, "song");
        for (int i = 1; i <= 5; i++) {
            chill.add("song " + i);
        }
        addOk &= throwsWith(() -> chill.add("song 6"), IllegalStateException.class, "playlist Chill is full");
        Playlist road = new Playlist("Road");
        road.lock();
        addOk &= throwsWith(() -> road.add("x"), IllegalStateException.class, "playlist Road is locked");
        allPass &= check("add:", addOk);

        allPass &= check("songAt:", throwsWith(() -> chill.songAt(5), IndexOutOfBoundsException.class, null)
                && throwsWith(() -> chill.songAt(-1), IndexOutOfBoundsException.class, null));
        allPass &= check("rating:", throwsWith(() -> chill.setRating(0), IllegalArgumentException.class, "rating must be 1..5: 0")
                && throwsWith(() -> chill.setRating(6), IllegalArgumentException.class, "rating must be 1..5: 6"));

        chill.setRating(4);
        allPass &= check("happy path:", "song 3".equals(chill.songAt(2)) && chill.rating() == 4);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // message == null means "any message".
    static boolean throwsWith(Runnable action, Class<? extends RuntimeException> type, String message) {
        try {
            action.run();
            return false;
        } catch (RuntimeException e) {
            return type.isInstance(e) && (message == null || Objects.equals(message, e.getMessage()));
        }
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-13s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
