import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;

/**
 * Example 1: the four kinds of nested classes, and shadowing.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // 1. Static nested class: no outer object needed.
    static class Point {
        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }
    }

    // A class with an inner class.
    static class Playlist {
        private final String name;
        private final String[] songs;

        Playlist(String name, String... songs) {
            this.name = name;
            this.songs = songs;
        }

        // 2. Inner class: each Cursor belongs to one Playlist and reads its private fields.
        class Cursor {
            private int index = 0;

            boolean hasNext() {
                return index < songs.length;
            }

            String next() {
                return name + ":" + songs[index++];
            }
        }
    }

    // Shadowing: three variables named x.
    static class Outer {
        int x = 1;

        class Inner {
            int x = 2;

            String show(int x) {
                return x + " " + this.x + " " + Outer.this.x;
            }
        }
    }

    public static void main(String[] args) {
        Point p = new Point(3, 4);
        System.out.println("static nested: " + p);

        Playlist playlist = new Playlist("Chill", "Rain", "Neon");
        Playlist.Cursor cursor = playlist.new Cursor();   // outer.new Inner()
        System.out.print("inner:");
        while (cursor.hasNext()) {
            System.out.print(" " + cursor.next());
        }
        System.out.println();

        // 3. Local class: declared inside the method, captures an effectively final local.
        String domain = "shop.com";
        class EmailRule {
            boolean accepts(String email) {
                return email.endsWith("@" + domain);
            }
        }
        EmailRule rule = new EmailRule();
        System.out.println("local: a@shop.com " + rule.accepts("a@shop.com") + ", b@x.org " + rule.accepts("b@x.org"));

        // 4. Anonymous class: declared and created in one expression. It has a field.
        Comparator<String> countingByLength = new Comparator<>() {
            int comparisons = 0;

            @Override
            public int compare(String a, String b) {
                comparisons++;
                return Integer.compare(a.length(), b.length());
            }

            @Override
            public String toString() {
                return "comparisons=" + comparisons;
            }
        };
        List<String> words = new ArrayList<>(List.of("kiwi", "fig", "banana"));
        words.sort(countingByLength);
        System.out.println("anonymous: " + words + " (" + countingByLength + ")");

        // The same comparator as a lambda: shorter, but no fields or extra methods.
        words.sort((a, b) -> Integer.compare(b.length(), a.length()));
        System.out.println("lambda: " + words);

        Outer outer = new Outer();
        Outer.Inner inner = outer.new Inner();
        System.out.println("shadowing (param, this.x, Outer.this.x): " + inner.show(3));
    }
}

/* Expected output:
static nested: (3,4)
inner: Chill:Rain Chill:Neon
local: a@shop.com true, b@x.org false
anonymous: [fig, kiwi, banana] (comparisons=3)
lambda: [banana, kiwi, fig]
shadowing (param, this.x, Outer.this.x): 3 2 1
*/
