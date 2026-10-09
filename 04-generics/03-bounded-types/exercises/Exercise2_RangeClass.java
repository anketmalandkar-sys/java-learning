import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): a bounded generic class, plus multiple bounds.
 *
 * TASK
 *   Part A. Range<T extends Comparable<T>> is an inclusive range [low, high]
 *   that works for any comparable type: ages, prices, names, app versions.
 *   Implement:
 *     constructor       throw IllegalArgumentException if low is greater than high
 *     contains(value)   true if low <= value <= high
 *     overlaps(other)   true if the two ranges share at least one value
 *                       ([1, 5] and [5, 9] overlap; [1, 4] and [5, 9] don't)
 *
 *   Part B. widest(ranges) returns the range with the biggest (high - low).
 *   It only makes sense for numbers, so its type parameter needs TWO bounds:
 *     N must be a Number   (to work out high - low with doubleValue())
 *     N must be a Comparable<N>   (Range<N> requires it)
 *   widest() already has the Comparable bound (Range<N> wouldn't compile without it).
 *   Add the Number bound to it, then implement it.
 *
 *   Version is a small Comparable record, so Range<Version> works too.
 *   Don't change main.
 *
 * EXPECTED OUTPUT
 *   constructor: PASS
 *   contains:    PASS
 *   overlaps:    PASS
 *   widest:      PASS
 *   ALL PASS
 *
 * HINTS
 *   - a.compareTo(b) <= 0 means "a is at most b".
 *   - Two ranges overlap unless one ends before the other starts.
 *   - Multiple bounds: <N extends A & B>. The class (Number) must come first.
 *
 * Run: java exercises/Exercise2_RangeClass.java
 */
public class Exercise2_RangeClass {

    static class Range<T extends Comparable<T>> {
        private final T low;
        private final T high;

        Range(T low, T high) {
            // TODO: validatez
            if (low.compareTo(high) > 0) {
                throw new IllegalArgumentException("low " + low + " is greater than high " + high);
            }
            this.low = low;
            this.high = high;
        }

        T low() { return low; }
        T high() { return high; }

        boolean contains(T value) {
            // TODO
            return low.compareTo(value) <= 0 && value.compareTo(high) <= 0;
        }

        boolean overlaps(Range<T> other) {
            boolean thisEndsFirst = high.compareTo(other.low) < 0;
            boolean otherEndsFirst = other.high.compareTo(low) < 0;
            return !thisEndsFirst && !otherEndsFirst;
        }

        @Override
        public String toString() {
            return "[" + low + ", " + high + "]";
        }
    }

    // TODO: add the Number bound
    static <N extends Number & Comparable<N>> Range<N> widest(List<Range<N>> ranges) {
        Range<N> widest = ranges.get(0);
        for (Range<N> range : ranges) {
            if (width(range) > width(widest)) {
                widest = range;
            }
        }
        return widest;
    }

    private static <N extends Number & Comparable<N>> double width(Range<N> range) {
        return range.high().doubleValue() - range.low().doubleValue();
    }

    record Version(int major, int minor) implements Comparable<Version> {
        @Override
        public int compareTo(Version other) {
            if (major != other.major) {
                return Integer.compare(major, other.major);
            }
            return Integer.compare(minor, other.minor);
        }

        @Override
        public String toString() {
            return major + "." + minor;
        }
    }

    public static void main(String[] args) {
        boolean allPass = true;

        allPass &= check("constructor", () -> {
            try {
                new Range<>(10, 1);
                return false;   // should have thrown
            } catch (IllegalArgumentException expected) {
                return new Range<>(5, 5).contains(5);   // a one-value range is fine
            }
        });

        allPass &= check("contains", () -> {
            Range<Integer> workingAge = new Range<>(18, 65);
            Range<String> aToM = new Range<>("A", "M");
            Range<Version> supported = new Range<>(new Version(2, 1), new Version(3, 4));
            return workingAge.contains(18) && workingAge.contains(65) && !workingAge.contains(70)
                    && aToM.contains("Kavya") && !aToM.contains("Ravi")
                    && supported.contains(new Version(2, 9)) && !supported.contains(new Version(3, 5));
        });

        allPass &= check("overlaps", () -> {
            Range<Integer> morning = new Range<>(8, 12);
            return morning.overlaps(new Range<>(12, 17))       // touch at 12
                    && morning.overlaps(new Range<>(10, 11))   // inside
                    && new Range<>(10, 11).overlaps(morning)   // either direction
                    && !morning.overlaps(new Range<>(13, 17))
                    && !morning.overlaps(new Range<>(1, 7));
        });

        allPass &= check("widest", () -> {
            List<Range<Integer>> shifts = List.of(new Range<>(9, 17), new Range<>(6, 20), new Range<>(10, 14));
            List<Range<Double>> bands = List.of(new Range<>(0.5, 1.0), new Range<>(1.0, 3.25));
            Range<Integer> longestShift = widest(shifts);    // must return Range<Integer>
            return longestShift.toString().equals("[6, 20]")
                    && widest(bands).toString().equals("[1.0, 3.25]");
        });

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    // Runs one check. An unfinished TODO returns null, which shows up as a NullPointerException.
    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-12s FAIL (still returning null?)%n", name + ":");
            return false;
        }
        System.out.printf("%-12s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
