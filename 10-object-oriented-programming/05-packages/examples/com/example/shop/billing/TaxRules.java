package com.example.shop.billing;

/**
 * Part of Example 2. Package-private (no "public"): only classes in com.example.shop.billing can
 * use it. The app package can't even see that it exists, so the tax logic can change freely.
 */
final class TaxRules {

    static final int GST_PERCENT = 18;

    private TaxRules() {
    }

    static long taxOn(long amountPaise) {
        return amountPaise * GST_PERCENT / 100;
    }
}
