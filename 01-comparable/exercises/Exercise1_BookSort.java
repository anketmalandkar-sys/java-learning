import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exercise 1 (Easy): sort books by title.
 *
 * TASK
 *   Make Book implement Comparable<Book> so that its natural order is by title,
 *   alphabetically (A to Z). Then sort the library in main.
 *
 * INPUT
 *   "The Pragmatic Programmer", "Clean Code", "Effective Java", "Head First Java"
 *
 * EXPECTED OUTPUT (titles after sorting)
 *   [Clean Code, Effective Java, Head First Java, The Pragmatic Programmer]
 *   PASS
 *
 * HINTS
 *   - Add "implements Comparable<Book>" to the class declaration.
 *   - String already implements Comparable, so you can delegate to it.
 *   - Collections.sort(list) uses compareTo automatically.
 *
 * Run: java exercises/Exercise1_BookSort.java
 */
public class Exercise1_BookSort {

    // TODO: implement Comparable<Book>
    static class Book implements Comparable<Book> {
        private final String title;
        private final String author;

        Book(String title, String author) {
            this.title = title;
            this.author = author;
        }

        String getTitle() {
            return title;
        }

        // TODO: add compareTo(Book other) that orders by title
        @Override
        public int compareTo(Book o) {
            return this.title.compareTo(o.title);
        }
        @Override
        public String toString() {
            return title;
        }
    }

    public static void main(String[] args) {
        List<Book> library = new ArrayList<>(List.of(
                new Book("The Pragmatic Programmer", "Hunt & Thomas"),
                new Book("Clean Code", "Robert C. Martin"),
                new Book("Effective Java", "Joshua Bloch"),
                new Book("Head First Java", "Sierra & Bates")
        ));

        // TODO: sort the library using its natural order
        Collections.sort(library);
        System.out.println(library);

        List<String> expected = List.of("Clean Code", "Effective Java", "Head First Java", "The Pragmatic Programmer");
        List<String> actual = new ArrayList<>();
        for (Book book : library) {
            actual.add(book.getTitle());
        }
        System.out.println(expected.equals(actual) ? "PASS" : "FAIL: expected " + expected);
    }
}
