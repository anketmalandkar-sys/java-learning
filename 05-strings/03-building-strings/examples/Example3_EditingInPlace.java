/**
 * Example 3: StringBuilder length vs capacity, and editing a builder in place.
 *
 * Run: java examples/Example3_EditingInPlace.java
 */
public class Example3_EditingInPlace {

    public static void main(String[] args) {
        System.out.println("Length vs capacity:");
        show("new StringBuilder()", new StringBuilder());
        show("new StringBuilder(100)", new StringBuilder(100));
        show("new StringBuilder(\"Greetings\")", new StringBuilder("Greetings"));

        StringBuilder growing = new StringBuilder();
        growing.append("0123456789ABCDEFG");     // 17 chars: more than the 16 it started with
        show("after appending 17 chars", growing);
        growing.trimToSize();
        show("after trimToSize()", growing);

        System.out.println("Editing in place:");
        StringBuilder sb = new StringBuilder("Java is fun");
        step("start", sb);
        step("insert(0, \">> \")", sb.insert(0, ">> "));
        step("replace(3, 7, \"Kotlin\")", sb.replace(3, 7, "Kotlin"));
        step("delete(0, 3)", sb.delete(0, 3));
        step("deleteCharAt(last)", sb.deleteCharAt(sb.length() - 1));
        sb.setCharAt(0, 'k');
        step("setCharAt(0, 'k')", sb);
        step("reverse()", sb.reverse());
        System.out.println("  indexOf(\"si\") = " + sb.indexOf("si"));

        System.out.println("setLength:");
        StringBuilder title = new StringBuilder("Quarterly sales report");
        title.setLength(9);
        System.out.println("  setLength(9)  -> \"" + title + "\"");
        title.setLength(11);
        // The two new characters are null characters (code 0), not spaces. Show their codes.
        System.out.println("  setLength(11) -> codes at 9 and 10: " + (int) title.charAt(9) + ", " + (int) title.charAt(10));
        title.setLength(0);
        System.out.println("  setLength(0)  -> \"" + title + "\" (reused, capacity kept: " + title.capacity() + ")");

        System.out.println("Removing digits, looping backwards:");
        StringBuilder code = new StringBuilder("a1b22c3");
        for (int i = code.length() - 1; i >= 0; i--) {
            if (Character.isDigit(code.charAt(i))) {
                code.deleteCharAt(i);
            }
        }
        System.out.println("  " + code);
    }

    static void show(String label, StringBuilder sb) {
        System.out.printf("  %-32s length=%-3d capacity=%d%n", label, sb.length(), sb.capacity());
    }

    static void step(String label, StringBuilder sb) {
        System.out.printf("  %-24s \"%s\"%n", label, sb);
    }
}

/* Expected output:
Length vs capacity:
  new StringBuilder()              length=0   capacity=16
  new StringBuilder(100)           length=0   capacity=100
  new StringBuilder("Greetings")   length=9   capacity=25
  after appending 17 chars         length=17  capacity=34
  after trimToSize()               length=17  capacity=17
Editing in place:
  start                    "Java is fun"
  insert(0, ">> ")         ">> Java is fun"
  replace(3, 7, "Kotlin")  ">> Kotlin is fun"
  delete(0, 3)             "Kotlin is fun"
  deleteCharAt(last)       "Kotlin is fu"
  setCharAt(0, 'k')        "kotlin is fu"
  reverse()                "uf si niltok"
  indexOf("si") = 3
setLength:
  setLength(9)  -> "Quarterly"
  setLength(11) -> codes at 9 and 10: 0, 0
  setLength(0)  -> "" (reused, capacity kept: 38)
Removing digits, looping backwards:
  abc
*/
