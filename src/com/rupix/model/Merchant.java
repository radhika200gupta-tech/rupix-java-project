
package com.rupix.model;

public class Merchant {

    private String merchantId;
    private String name;
    private String upiId;
    private boolean active;

    public Merchant(String merchantId, String name, String upiId) {
        this.merchantId = merchantId;
        this.name = name;
        this.upiId = upiId;
        this.active = true;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getName() {
        return name;
    }

    public String getUpiId() {
        return upiId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
