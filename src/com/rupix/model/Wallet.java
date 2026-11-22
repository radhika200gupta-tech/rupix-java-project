package com.rupix.model;

public class Wallet {
    private final String walletId;
    private final String upiId;
    private long balancePaise;

    public Wallet(String walletId, String upiId) {
        this.walletId = walletId;
        this.upiId = upiId;
        this.balancePaise = 0;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getUpiId() {
        return upiId;
    }

    public synchronized long getBalancePaise() {
        return balancePaise;
    }

    public synchronized void credit(long amountPaise) {
        if (amountPaise <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive."
            );
        }

        balancePaise = Math.addExact(balancePaise, amountPaise);
    }

    public synchronized void debit(long amountPaise) {
        if (amountPaise <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be positive."
            );
        }

        if (balancePaise < amountPaise) {
            throw new IllegalArgumentException(
                    "Insufficient wallet balance."
            );
        }

        balancePaise -= amountPaise;
    }

}
