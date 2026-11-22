
package com.rupix.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private final String transactionId;
    private final String senderUpi;
    private final String receiverUpi;
    private final long amountPaise;
    private final String status;
    private final LocalDateTime timestamp;

    // Constructor for NEW transactions
    public Transaction(
            String senderUpi,
            String receiverUpi,
            long amountPaise,
            String status
    ) {
        this(
                UUID.randomUUID().toString(),
                senderUpi,
                receiverUpi,
                amountPaise,
                status,
                LocalDateTime.now()
        );
    }

    // Constructor for transactions loaded from a file
    public Transaction(
            String transactionId,
            String senderUpi,
            String receiverUpi,
            long amountPaise,
            String status,
            LocalDateTime timestamp
    ) {
        this.transactionId = transactionId;
        this.senderUpi = senderUpi;
        this.receiverUpi = receiverUpi;
        this.amountPaise = amountPaise;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getSenderUpi() {
        return senderUpi;
    }

    public String getReceiverUpi() {
        return receiverUpi;
    }

    public long getAmountPaise() {
        return amountPaise;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId
                + " | From: " + senderUpi
                + " | To: " + receiverUpi
                + " | Amount: Rs. " + (amountPaise / 100.0)
                + " | Status: " + status
                + " | Time: " + timestamp;
    }
}
