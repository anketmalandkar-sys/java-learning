package com.acme.payroll;

/**
 * Part of Exercise 2.
 */
public class Employee {

    public String name;             // TODO
    public long annualRupees;       // TODO: nobody outside payroll should read or change it directly

    public Employee(String name, long annualRupees) {
        this.name = name;
        this.annualRupees = annualRupees;
    }

    // TODO: add a public name() accessor
}
