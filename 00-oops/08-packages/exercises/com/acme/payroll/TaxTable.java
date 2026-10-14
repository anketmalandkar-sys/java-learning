package com.acme.payroll;

/**
 * Part of Exercise 2. An internal helper of the payroll package.
 */
class TaxTable {   // package-private: invisible outside com.acme.payroll

    static int taxPercentFor(long annualRupees) {
        if (annualRupees <= 700_000) {
            return 0;
        }
        return annualRupees <= 1_500_000 ? 15 : 30;
    }
}
