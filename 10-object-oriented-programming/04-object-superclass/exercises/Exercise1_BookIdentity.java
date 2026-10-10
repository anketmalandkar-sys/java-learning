import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Exercise 1 (Easy): toString, equals and hashCode.
 *
 * TASK
 *   A library has several physical copies of the same edition. Two Book objects are EQUAL when
 *   they have the same isbn, ignoring the copyNumber and the shelf.
 *
 *   Override in Book:
 *     toString()   "Book[isbn=978-1, copy=2]"
 *     equals()     by isbn only, using the standard recipe (same object? instanceof? compare)
 *     hashCode()   consistent with equals
 *
 * EXPECTED OUTPUT
 *   toString: PASS
 *   equals:   PASS
 *   hashCode: PASS
 *   set:      PASS
 *   map:      PASS
 *   ALL PASS
 *
 * HINTS
 *   - Use @Override on all three: it catches equals(Book) mistakes.
 *   - Objects.hash(isbn) or isbn.hashCode().
 *
 * Run: java exercises/Exercise1_BookIdentity.java
 */
public class Exercise1_BookIdentity {

    static class Book {
        final String isbn;
        final int copyNumber;
        String shelf;

        Book(String isbn, int copyNumber, String shelf) {
            this.isbn = isbn;
            this.copyNumber = copyNumber;
            this.shelf = shelf;
        }

        // TODO: toString, equals, hashCode
    }

    public static void main(String[] args) {
        Book a = new Book("978-1", 1, "A3");
        Book b = new Book("978-1", 2, "B7");
        Book c = new Book("978-2", 1, "A3");

        boolean allPass = true;
        allPass &= check("toString:", "Book[isbn=978-1, copy=2]".equals(b.toString()));
        allPass &= check("equals:", a.equals(b) && b.equals(a) && !a.equals(c) && a.equals(a)
                && !a.equals(null) && !a.equals("978-1"));
        allPass &= check("hashCode:", a.hashCode() == b.hashCode());

        Set<Book> editions = new HashSet<>(List.of(a, b, c));
        allPass &= check("set:", editions.size() == 2 && editions.contains(new Book("978-2", 9, "Z1")));

        Map<Book, Integer> loans = new HashMap<>();
        loans.merge(a, 1, Integer::sum);
        loans.merge(b, 1, Integer::sum);
        allPass &= check("map:", Integer.valueOf(2).equals(loans.get(new Book("978-1", 5, "C1"))));

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-9s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
