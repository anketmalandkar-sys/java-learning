package com.example.shop.billing;

import com.example.shop.catalog.Product;   // a type from another package of the same app

import java.util.ArrayList;
import java.util.List;

/**
 * Part of Example 2. Public API of the billing package.
 */
public class Invoice {

    private final List<Product> products = new ArrayList<>();
    private final List<Integer> quantities = new ArrayList<>();

    public void add(Product product, int qty) {
        products.add(product);
        quantities.add(qty);
    }

    public long subtotalPaise() {
        long sum = 0;
        for (int i = 0; i < products.size(); i++) {
            sum += products.get(i).pricePaise() * quantities.get(i);
        }
        return sum;
    }

    // TaxRules is package-private, but Invoice is in the same package, so it can use it.
    public long taxPaise() {
        return TaxRules.taxOn(subtotalPaise());
    }

    public long totalPaise() {
        return subtotalPaise() + taxPaise();
    }

    // Package-private: for tests and helpers in this package only.
    int lineCount() {
        return products.size();
    }
}
