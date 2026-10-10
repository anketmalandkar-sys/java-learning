package com.acme.payroll;

/**
 * Part of Exercise 2. An internal helper of the payroll package.
 */
public class TaxTable {   // TODO: should not be visible outside com.acme.payroll

    public static int taxPercentFor(long annualRupees) {   // TODO
        if (annualRupees <= 700_000) {
            return 0;
        }
        return annualRupees <= 1_500_000 ? 15 : 30;
    }
}
