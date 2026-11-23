
package com.rupix.model;

import java.time.LocalDateTime;

public class MerchantPayment {

    private int paymentId;
    private String merchantId;
    private String customerUpi;
    private long amountPaise;
    private LocalDateTime paymentTime;
    private String status;

    public MerchantPayment(int paymentId, String merchantId,
                           String customerUpi, long amountPaise) {

        this.paymentId = paymentId;
        this.merchantId = merchantId;
        this.customerUpi = customerUpi;
        this.amountPaise = amountPaise;
        this.paymentTime = LocalDateTime.now();
        this.status = "SUCCESS";
    }

    public int getPaymentId() {
        return paymentId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public String getCustomerUpi() {
        return customerUpi;
    }

    public long getAmountPaise() {
        return amountPaise;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
