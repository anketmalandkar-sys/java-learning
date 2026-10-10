package com.example.shop.app;

import com.example.shop.billing.Invoice;
import com.example.shop.catalog.Product;

import static java.lang.String.format;

/**
 * Example 2: a small app split across three packages.
 *
 *   com.example.shop.catalog   Product (public record)
 *   com.example.shop.billing   Invoice (public), TaxRules (package-private)
 *   com.example.shop.app       ShopApp (this class)
 *
 * Run from the lesson folder:
 *   javac -d out/ex $(find examples/com -name "*.java")
 *   java -cp out/ex com.example.shop.app.ShopApp
 * Or press the green run button next to main in IntelliJ.
 */
public class ShopApp {

    public static void main(String[] args) {
        Invoice invoice = new Invoice();
        invoice.add(new Product("PEN", "Pen", 1200), 10);
        invoice.add(new Product("BOOK", "Notebook", 4500), 2);

        System.out.println(format("subtotal Rs %d.%02d", invoice.subtotalPaise() / 100, invoice.subtotalPaise() % 100));
        System.out.println(format("tax      Rs %d.%02d", invoice.taxPaise() / 100, invoice.taxPaise() % 100));
        System.out.println(format("total    Rs %d.%02d", invoice.totalPaise() / 100, invoice.totalPaise() % 100));

        // invoice.lineCount();          // compile error: lineCount() is package-private in billing
        // TaxRules.taxOn(100);          // compile error: TaxRules isn't visible outside billing

        System.out.println("Invoice lives in " + Invoice.class.getPackageName());
        System.out.println("ShopApp lives in " + ShopApp.class.getName());
    }
}

/* Expected output:
subtotal Rs 210.00
tax      Rs 37.80
total    Rs 247.80
Invoice lives in com.example.shop.billing
ShopApp lives in com.example.shop.app.ShopApp
*/
