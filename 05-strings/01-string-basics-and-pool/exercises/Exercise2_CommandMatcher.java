import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * Exercise 2 (Medium): comparing text that users actually type.
 *
 * TASK
 *   A chat bot reads commands and usernames typed by users. Their input is messy:
 *   extra spaces, random capitals, sometimes missing (null). And it's always built at
 *   runtime, so == never works on it. Implement:
 *
 *     isCommand(input, command)   true if input is the command, ignoring case and
 *                                 spaces at either end. A null input is never a command.
 *                                 isCommand("  HeLp ", "help") -> true
 *
 *     countCommands(inputs, command)   how many of the inputs are that command
 *                                      (reuse isCommand; inputs may contain nulls)
 *
 *     sameUser(a, b)              true if both usernames are present and match,
 *                                 ignoring case and spaces at either end.
 *                                 If either one is null, the answer is false.
 *
 *     canonicalCountry(code)      the code trimmed, upper-cased and INTERNED, so the result
 *                                 is the pooled object: canonicalCountry(" in ") == "IN" is true.
 *                                 Return null for a null code.
 *
 *   Don't change main. Notice that main never passes a literal straight in: every input is
 *   built at runtime, like real input would be.
 *
 * EXPECTED OUTPUT
 *   isCommand:        PASS
 *   countCommands:    PASS
 *   sameUser:         PASS
 *   canonicalCountry: PASS
 *   ALL PASS
 *
 * HINTS
 *   - strip() (or trim()) removes surrounding spaces. Strings are immutable: use the result.
 *   - equalsIgnoreCase compares without caring about capitals.
 *   - Check for null before calling any method on a value that might be null.
 *
 * Run: java exercises/Exercise2_CommandMatcher.java
 */
public class Exercise2_CommandMatcher {

    static boolean isCommand(String input, String command) {
        // TODO
        return false;
    }

    static int countCommands(List<String> inputs, String command) {
        // TODO
        return -1;
    }

    static boolean sameUser(String a, String b) {
        // TODO
        return false;
    }

    static String canonicalCountry(String code) {
        // TODO
        return "";
    }

    public static void main(String[] args) {
        // Simulated input: split() and new String() build every value at runtime.
        String[] typed = "help| HELP |Help me|  hElP|quit".split("\\|");
        List<String> chatLog = Arrays.asList(typed[0], null, typed[1], typed[2], typed[3], typed[4]);

        boolean allPass = true;

        allPass &= check("isCommand", () ->
                isCommand(typed[0], "help")
                        && isCommand(typed[1], "help")
                        && isCommand(typed[3], "help")
                        && !isCommand(typed[2], "help")    // "Help me" is not "help"
                        && !isCommand(null, "help"));

        allPass &= check("countCommands", () ->
                countCommands(chatLog, "help") == 3 && countCommands(chatLog, "quit") == 1);

        allPass &= check("sameUser", () ->
                sameUser(new String("  Meera"), new String("meera "))
                        && !sameUser(new String("Meera"), new String("Ravi"))
                        && !sameUser(null, new String("Meera"))
                        && !sameUser(new String("Meera"), null)
                        && !sameUser(null, null));

        allPass &= check("canonicalCountry", () ->
                canonicalCountry(new String(" in ")) == "IN"            // == on purpose: must be the pooled object
                        && canonicalCountry(new StringBuilder("us").toString()) == "US"
                        && canonicalCountry(null) == null);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, Supplier<Boolean> test) {
        boolean ok;
        try {
            ok = test.get();
        } catch (NullPointerException e) {
            System.out.printf("%-17s FAIL (NullPointerException: a missing null check?)%n", name + ":");
            return false;
        }
        System.out.printf("%-17s %s%n", name + ":", ok ? "PASS" : "FAIL");
        return ok;
    }
}
