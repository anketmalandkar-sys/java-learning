package com.acme.payroll;

/**
 * Part of Exercise 2.
 */
public class Employee {

    private final String name;
    private long annualRupees;      // only payroll code can read or change it

    public Employee(String name, long annualRupees) {
        this.name = name;
        this.annualRupees = annualRupees;
    }

    public String name() {
        return name;
    }

    // Package-private: visible to PayrollService, not to com.acme.app.
    long annualRupees() {
        return annualRupees;
    }

    void setAnnualRupees(long annualRupees) {
        this.annualRupees = annualRupees;
    }
}
