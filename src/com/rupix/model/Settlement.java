
package com.rupix.model;

import java.time.LocalDateTime;

public class Settlement {

    private int settlementId;
    private String merchantId;
    private long totalAmountPaise;
    private long mdrPaise;
    private long netAmountPaise;
    private LocalDateTime settlementTime;
    private String status;

    public Settlement(int settlementId, String merchantId,
                      long totalAmountPaise, long mdrPaise) {

        this.settlementId = settlementId;
        this.merchantId = merchantId;
        this.totalAmountPaise = totalAmountPaise;
        this.mdrPaise = mdrPaise;
        this.netAmountPaise = totalAmountPaise - mdrPaise;
        this.settlementTime = LocalDateTime.now();
        this.status = "PENDING";
    }

    public int getSettlementId() {
        return settlementId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public long getTotalAmountPaise() {
        return totalAmountPaise;
    }

    public long getMdrPaise() {
        return mdrPaise;
    }

    public long getNetAmountPaise() {
        return netAmountPaise;
    }

    public LocalDateTime getSettlementTime() {
        return settlementTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
