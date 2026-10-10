package com.example.shop.catalog;

/**
 * Part of Example 2. A public type: other packages may use it.
 */
public record Product(String sku, String name, long pricePaise) {

    public Product {
        if (pricePaise < 0) {
            throw new IllegalArgumentException("negative price for " + sku);
        }
    }
}
