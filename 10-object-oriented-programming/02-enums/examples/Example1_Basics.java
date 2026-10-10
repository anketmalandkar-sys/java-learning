import java.util.Arrays;

/**
 * Example 1: declaring an enum, its built-in methods, switch, and constant-specific bodies.
 *
 * Run: java examples/Example1_Basics.java
 */
public class Example1_Basics {

    enum Size { SMALL, MEDIUM, LARGE }

    enum Operation {
        ADD("+") {
            @Override
            int apply(int a, int b) {
                return a + b;
            }
        },
        MULTIPLY("*") {
            @Override
            int apply(int a, int b) {
                return a * b;
            }
        };

        private final String symbol;

        Operation(String symbol) {
            this.symbol = symbol;
        }

        String symbol() {
            return symbol;
        }

        // Every constant must implement this in its own body.
        abstract int apply(int a, int b);
    }

    public static void main(String[] args) {
        Size s = Size.MEDIUM;
        System.out.println("name=" + s.name() + " ordinal=" + s.ordinal() + " toString=" + s);
        System.out.println("values() = " + Arrays.toString(Size.values()));
        System.out.println("valueOf(\"LARGE\") == Size.LARGE ? " + (Size.valueOf("LARGE") == Size.LARGE));
        System.out.println("MEDIUM.compareTo(LARGE) < 0 ? " + (s.compareTo(Size.LARGE) < 0));

        try {
            Size.valueOf("small");
        } catch (IllegalArgumentException e) {
            System.out.println("valueOf(\"small\") -> IllegalArgumentException (names are case-sensitive)");
        }

        // A switch expression over an enum: every constant listed, no default needed.
        for (Size size : Size.values()) {
            int ml = switch (size) {
                case SMALL -> 250;
                case MEDIUM -> 350;
                case LARGE -> 500;
            };
            System.out.println("  " + size + " cup = " + ml + " ml");
        }

        for (Operation op : Operation.values()) {
            System.out.println("6 " + op.symbol() + " 7 = " + op.apply(6, 7));
        }

        // Enums implicitly extend java.lang.Enum.
        System.out.println("superclass of Size: " + Size.class.getSuperclass().getSimpleName());
    }
}

/* Expected output:
name=MEDIUM ordinal=1 toString=MEDIUM
values() = [SMALL, MEDIUM, LARGE]
valueOf("LARGE") == Size.LARGE ? true
MEDIUM.compareTo(LARGE) < 0 ? true
valueOf("small") -> IllegalArgumentException (names are case-sensitive)
  SMALL cup = 250 ml
  MEDIUM cup = 350 ml
  LARGE cup = 500 ml
6 + 7 = 13
6 * 7 = 42
superclass of Size: Enum
*/
