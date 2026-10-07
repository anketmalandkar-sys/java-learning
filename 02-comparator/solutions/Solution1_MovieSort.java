import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Solution 1 (Easy): sort movies three different ways.
 *
 * TASK
 *   Movie does NOT implement Comparable. Fill in the three Comparator constants
 *   below so that:
 *     BY_TITLE        sorts A to Z by title
 *     BY_YEAR_NEWEST  sorts by release year, newest first
 *     BY_RATING_BEST  sorts by rating, highest first
 *   Use Comparator factory methods (comparing, comparingInt, comparingDouble, reversed),
 *   not hand-written lambdas.
 *
 * INPUT
 *   Inception (2010, 8.8), Avatar (2009, 7.9), Dangal (2016, 8.3), Up (2009, 8.3), Interstellar (2014, 8.7)
 *
 * EXPECTED OUTPUT
 *   By title:       [Avatar, Dangal, Inception, Interstellar, Up]
 *   Newest first:   [Dangal, Interstellar, Inception, Avatar, Up]
 *   Best rated:     [Inception, Interstellar, Dangal, Up, Avatar]
 *   PASS
 *
 * HINTS
 *   - Comparator.comparing(Movie::getTitle) works because String is Comparable.
 *   - For int and double keys, prefer comparingInt / comparingDouble.
 *   - .reversed() flips a comparator.
 *   - Ties (Avatar/Up in 2009, Dangal/Up at 8.3) keep their order from the previous sort,
 *     because List.sort is stable. You don't need a tie-breaker for this exercise.
 *
 * Run: java exercises/Solution1_MovieSort.java
 */
public class Solution1_MovieSort {

    static class Movie {
        private final String title;
        private final int year;
        private final double rating;

        Movie(String title, int year, double rating) {
            this.title = title;
            this.year = year;
            this.rating = rating;
        }

        String getTitle() { return title; }
        int getYear() { return year; }
        double getRating() { return rating; }

        @Override
        public String toString() {
            return title;
        }
    }

    // Factory methods: the key extractor says WHAT to sort by, reversed() says which way.
    static final Comparator<Movie> BY_TITLE = Comparator.comparing(Movie::getTitle);
    static final Comparator<Movie> BY_YEAR_NEWEST = Comparator.comparingInt(Movie::getYear).reversed();
    static final Comparator<Movie> BY_RATING_BEST = Comparator.comparingDouble(Movie::getRating).reversed();

    public static void main(String[] args) {
        List<Movie> movies = new ArrayList<>(List.of(
                new Movie("Inception", 2010, 8.8),
                new Movie("Avatar", 2009, 7.9),
                new Movie("Dangal", 2016, 8.3),
                new Movie("Up", 2009, 8.3),
                new Movie("Interstellar", 2014, 8.7)
        ));

        if (BY_TITLE == null || BY_YEAR_NEWEST == null || BY_RATING_BEST == null) {
            System.out.println("FAIL: fill in all three comparators first");
            return;
        }

        // Each sort starts from the result of the previous one (this matters for ties).
        movies.sort(BY_TITLE);
        String byTitle = movies.toString();
        System.out.println("By title:       " + byTitle);

        movies.sort(BY_YEAR_NEWEST);
        String newest = movies.toString();
        System.out.println("Newest first:   " + newest);

        movies.sort(BY_RATING_BEST);
        String best = movies.toString();
        System.out.println("Best rated:     " + best);

        boolean pass = byTitle.equals("[Avatar, Dangal, Inception, Interstellar, Up]")
                && newest.equals("[Dangal, Interstellar, Inception, Avatar, Up]")
                && best.equals("[Inception, Interstellar, Dangal, Up, Avatar]");
        System.out.println(pass ? "PASS" : "FAIL");
    }
}
