/**
 * Example 2: a loyalty-points rule, written three ways.
 *
 * The rule: a customer earns a bonus if they are a member AND spent at least 1000,
 * OR if it's their birthday month. Bonus points are (spend / 100) * 2 + 50.
 *
 * Version 1 relies on precedence and is correct, but hard to check.
 * Version 2 adds parentheses: same result, easy to read.
 * Version 3 names the parts: easiest to read and to change.
 *
 * Run: java examples/Example2_RealWorld.java
 */
public class Example2_RealWorld {

    public static void main(String[] args) {
        int[][] customers = {
            // isMember (1/0), spend, birthdayMonth (1/0)
            {1, 1500, 0},
            {0, 2000, 0},
            {0, 300, 1},
            {1, 900, 0},
        };

        for (int[] row : customers) {
            boolean member = row[0] == 1;
            int spend = row[1];
            boolean birthday = row[2] == 1;

            int v1 = member && spend >= 1000 || birthday ? spend / 100 * 2 + 50 : 0;
            int v2 = ((member && spend >= 1000) || birthday) ? ((spend / 100) * 2 + 50) : 0;
            int v3 = bonusPoints(member, spend, birthday);

            System.out.printf("member=%-5b spend=%-4d birthday=%-5b -> %d %d %d%n",
                    member, spend, birthday, v1, v2, v3);
        }

        // A tempting "shortcut" that's wrong: + binds tighter than the shift.
        int level = 3;
        int wrong = 1 << level + 1;        // 1 << 4 = 16
        int right = (1 << level) + 1;      // 8 + 1 = 9
        System.out.println("2^level + 1: wrong=" + wrong + " right=" + right);
    }

    static int bonusPoints(boolean member, int spend, boolean birthday) {
        boolean bigSpender = member && spend >= 1000;
        boolean qualifies = bigSpender || birthday;
        if (!qualifies) {
            return 0;
        }
        int pointsPerHundred = 2;
        return (spend / 100) * pointsPerHundred + 50;
    }
}

/* Expected output:
member=true  spend=1500 birthday=false -> 80 80 80
member=false spend=2000 birthday=false -> 0 0 0
member=false spend=300  birthday=true  -> 56 56 56
member=true  spend=900  birthday=false -> 0 0 0
2^level + 1: wrong=16 right=9
*/
