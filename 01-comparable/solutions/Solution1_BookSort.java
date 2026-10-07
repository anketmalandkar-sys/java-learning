import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Solution 1: sort books by title.
 *
 * Same as the exercise, plus an extra demo at the end. Because compareTo looks only
 * at the title, two different books with the same title are "duplicates" to a
 * TreeSet, so one of them is silently dropped.
 *
 * Run: java solutions/Solution1_BookSort.java
 */
public class Solution1_BookSort {

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

        @Override
        public int compareTo(Book other) {
            // String.compareTo is case-sensitive: "Zebra" sorts before "apple".
            // Use compareToIgnoreCase if titles may differ in case.
            return this.title.compareTo(other.title);
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

        Collections.sort(library);
        System.out.println(library);

        List<String> expected = List.of("Clean Code", "Effective Java", "Head First Java", "The Pragmatic Programmer");
        List<String> actual = new ArrayList<>();
        for (Book book : library) {
            actual.add(book.getTitle());
        }
        System.out.println(expected.equals(actual) ? "PASS" : "FAIL: expected " + expected);

        // Extra: two different books, same title
        Set<Book> shelf = new TreeSet<>();
        shelf.add(new Book("Java", "Author A"));
        shelf.add(new Book("Java", "Author B"));
        System.out.println("TreeSet size with two 'Java' books: " + shelf.size());
    }
}

/* Expected output:
[Clean Code, Effective Java, Head First Java, The Pragmatic Programmer]
PASS
TreeSet size with two 'Java' books: 1
*/
