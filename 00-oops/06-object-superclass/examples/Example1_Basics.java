import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Example 1: Object's methods with and without overriding.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    // No overrides: inherits Object's identity-based behaviour.
    static class PlainBook {
        final String isbn;

        PlainBook(String isbn) {
            this.isbn = isbn;
        }
    }

    // Overrides toString, equals and hashCode, all based on isbn.
    static class Book {
        final String isbn;
        final String title;

        Book(String isbn, String title) {
            this.isbn = isbn;
            this.title = title;
        }

        @Override
        public String toString() {
            return "Book[isbn=" + isbn + ", title=" + title + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Book other)) {
                return false;
            }
            return isbn.equals(other.isbn);      // two copies of the same edition are equal
        }

        @Override
        public int hashCode() {
            return Objects.hash(isbn);           // same field as equals
        }
    }

    public static void main(String[] args) {
        PlainBook p1 = new PlainBook("978-0134685991");
        PlainBook p2 = new PlainBook("978-0134685991");
        System.out.println("PlainBook toString starts with class name? " + p1.toString().contains("PlainBook@"));
        System.out.println("PlainBook equals (same isbn)? " + p1.equals(p2));

        Book b1 = new Book("978-0134685991", "Effective Java");
        Book b2 = new Book("978-0134685991", "Effective Java (2nd copy)");
        System.out.println(b1);
        System.out.println("Book equals (same isbn)? " + b1.equals(b2) + ", same hashCode? " + (b1.hashCode() == b2.hashCode()));
        System.out.println("b1.equals(null)? " + b1.equals(null));

        Set<Book> shelf = new HashSet<>();
        shelf.add(b1);
        shelf.add(b2);                             // a duplicate by equals/hashCode
        System.out.println("shelf size after adding both: " + shelf.size());

        Set<PlainBook> plainShelf = new HashSet<>();
        plainShelf.add(p1);
        plainShelf.add(p2);
        System.out.println("plain shelf size after adding both: " + plainShelf.size());

        // getClass: runtime type information.
        Object o = b1;
        Class<?> c = o.getClass();
        System.out.println("getClass: " + c.getSimpleName() + ", superclass: " + c.getSuperclass().getSimpleName()
                + ", interfaces of String: " + Arrays.toString(Arrays.stream(String.class.getInterfaces())
                        .map(Class::getSimpleName).sorted().toArray()));
        System.out.println("isEnum? " + c.isEnum() + ", isInterface? " + c.isInterface() + ", isRecord? " + c.isRecord());
        System.out.println("Override.isAnnotation()? " + Override.class.isAnnotation());

        // getFields/getMethods: PUBLIC members only, inherited ones included.
        // getDeclaredFields/getDeclaredMethods: everything declared in the class itself.
        System.out.println("Book public fields: " + c.getFields().length
                + ", declared fields: " + Arrays.toString(Arrays.stream(c.getDeclaredFields())
                        .map(f -> f.getName()).sorted().toArray()));
        System.out.println("Book public methods include equals/hashCode/toString/getClass? "
                + Arrays.stream(c.getMethods()).map(m -> m.getName()).toList()
                        .containsAll(java.util.List.of("equals", "hashCode", "toString", "getClass")));
    }
}

/* Expected output:
PlainBook toString starts with class name? true
PlainBook equals (same isbn)? false
Book[isbn=978-0134685991, title=Effective Java]
Book equals (same isbn)? true, same hashCode? true
b1.equals(null)? false
shelf size after adding both: 1
plain shelf size after adding both: 2
getClass: Book, superclass: Object, interfaces of String: [CharSequence, Comparable, Constable, ConstantDesc, Serializable]
isEnum? false, isInterface? false, isRecord? false
Override.isAnnotation()? true
Book public fields: 0, declared fields: [isbn, title]
Book public methods include equals/hashCode/toString/getClass? true
*/
