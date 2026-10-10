package com.acme.payroll;

/**
 * Part of Exercise 2. The public face of the payroll package.
 */
public class PayrollService {

    public long monthlyTakeHome(Employee e) {
        long monthly = e.annualRupees / 12;
        int tax = TaxTable.taxPercentFor(e.annualRupees);
        return monthly - monthly * tax / 100;
    }

    public void giveRaise(Employee e, int percent) {
        if (percent < 0 || percent > 50) {
            throw new IllegalArgumentException("raise must be 0..50%");
        }
        e.annualRupees += e.annualRupees * percent / 100;
    }
}
