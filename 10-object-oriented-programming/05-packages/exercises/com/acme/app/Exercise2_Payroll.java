package com.acme.app;

import com.acme.payroll.Employee;
import com.acme.payroll.PayrollService;
import com.acme.payroll.TaxTable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Exercise 2 (Medium): visibility across packages.
 *
 * TASK
 *   The payroll package (com.acme.payroll) exposes too much. The app (com.acme.app) bypasses
 *   PayrollService and edits salaries directly, skipping the 0..50% raise rule.
 *
 *   1. In payroll: make TaxTable and its method package-private (no "public").
 *   2. Make Employee's fields private (name can stay final). Add a public name() accessor.
 *      PayrollService is in the same package; change it to work without public fields.
 *      Hint: a package-private accessor annualRupees() and a package-private setter are
 *      visible to PayrollService but not to the app.
 *   3. In this file: remove the TaxTable import and the direct field access in
 *      oldStyleBonus(), so everything goes through PayrollService. Then delete the
 *      oldStyleBonus method (it can't work any more, and that's the point).
 *
 *   The checks use reflection to inspect the modifiers.
 *
 * EXPECTED OUTPUT
 *   TaxTable hidden:  PASS
 *   fields private:   PASS
 *   name() public:    PASS
 *   take-home:        PASS
 *   raise rule:       PASS
 *   ALL PASS
 *
 * Run (from the lesson folder):
 *   javac -d out/ex2 $(find exercises/com -name "*.java")
 *   java -cp out/ex2 com.acme.app.Exercise2_Payroll
 * Or press the green run button next to main in IntelliJ.
 */
public class Exercise2_Payroll {

    // TODO: delete this method once the fields are private
    static void oldStyleBonus(Employee e) {
        e.annualRupees += 1_000_000;                          // bypasses the raise rule!
        System.out.println("tax now " + TaxTable.taxPercentFor(e.annualRupees) + "%");
    }

    public static void main(String[] args) throws Exception {
        boolean allPass = true;

        Class<?> taxTable = Class.forName("com.acme.payroll.TaxTable");
        allPass &= check("TaxTable hidden:", !Modifier.isPublic(taxTable.getModifiers()));

        boolean allPrivate = true;
        for (Field f : Employee.class.getDeclaredFields()) {
            allPrivate &= Modifier.isPrivate(f.getModifiers());
        }
        allPass &= check("fields private:", allPrivate);

        boolean namePublic;
        try {
            namePublic = Modifier.isPublic(Employee.class.getDeclaredMethod("name").getModifiers());
        } catch (NoSuchMethodException e) {
            namePublic = false;
        }
        allPass &= check("name() public:", namePublic);

        PayrollService payroll = new PayrollService();
        Employee asha = new Employee("Asha", 1_200_000);
        allPass &= check("take-home:", payroll.monthlyTakeHome(asha) == 85_000);

        boolean ruleHolds;
        try {
            payroll.giveRaise(asha, 80);
            ruleHolds = false;
        } catch (IllegalArgumentException e) {
            payroll.giveRaise(asha, 10);
            ruleHolds = payroll.monthlyTakeHome(asha) == 93_500;
        }
        allPass &= check("raise rule:", ruleHolds);

        System.out.println(allPass ? "ALL PASS" : "SOME FAILED");
    }

    static boolean check(String name, boolean ok) {
        System.out.printf("%-17s %s%n", name, ok ? "PASS" : "FAIL");
        return ok;
    }
}
