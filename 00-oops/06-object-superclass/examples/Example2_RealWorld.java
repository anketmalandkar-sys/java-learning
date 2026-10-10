import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Example 2: equals/hashCode in a cache, clone vs copy constructor, and AutoCloseable.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    // A cache key compared by content.
    static final class RouteKey {
        private final String from;
        private final String to;

        RouteKey(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof RouteKey k && from.equals(k.from) && to.equals(k.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to);
        }
    }

    static class Playlist implements Cloneable {
        String name;
        List<String> songs = new ArrayList<>();

        Playlist(String name) {
            this.name = name;
        }

        // Copy constructor: the preferred way to copy. Copies the list too.
        Playlist(Playlist other) {
            this.name = other.name;
            this.songs = new ArrayList<>(other.songs);
        }

        // clone() as Object provides it: SHALLOW. The list is shared.
        @Override
        public Playlist clone() {
            try {
                return (Playlist) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }

    // Replaces finalize(): close() runs at a known moment, via try-with-resources.
    static class ReportFile implements AutoCloseable {
        private final String name;

        ReportFile(String name) {
            this.name = name;
            System.out.println("  open " + name);
        }

        void write(String line) {
            System.out.println("  write " + line);
        }

        @Override
        public void close() {
            System.out.println("  close " + name);
        }
    }

    public static void main(String[] args) {
        Map<RouteKey, Integer> fareCache = new HashMap<>();
        fareCache.put(new RouteKey("Pune", "Mumbai"), 450);
        // A NEW key object with the same content finds the cached fare.
        System.out.println("cached fare: " + fareCache.get(new RouteKey("Pune", "Mumbai")));

        Playlist original = new Playlist("Road trip");
        original.songs.add("Intro");

        Playlist shallow = original.clone();
        Playlist copy = new Playlist(original);
        shallow.songs.add("Added via clone");
        copy.songs.add("Added via copy");
        System.out.println("original songs: " + original.songs);
        System.out.println("copy songs:     " + copy.songs);

        System.out.println("try-with-resources:");
        try (ReportFile report = new ReportFile("daily.csv")) {
            report.write("id,total");
        }
        System.out.println("done");
    }
}

/* Expected output:
cached fare: 450
original songs: [Intro, Added via clone]
copy songs:     [Intro, Added via copy]
try-with-resources:
  open daily.csv
  write id,total
  close daily.csv
done
*/
