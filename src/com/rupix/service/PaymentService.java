
package com.rupix.service;

import com.rupix.exception.InsufficientBalanceException;
import com.rupix.exception.InvalidAmountException;
import com.rupix.model.Transaction;
import com.rupix.model.Wallet;
import com.rupix.util.FileStorage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentService {

    private final WalletService walletService;

    private final ArrayList<Transaction> transactions =
            new ArrayList<>();

    public PaymentService(WalletService walletService) {

        if (walletService == null) {
            throw new IllegalArgumentException(
                    "WalletService cannot be null."
            );
        }

        this.walletService = walletService;

        loadTransactionData();
    }

    // 1. Transfer money between two wallets
    public Transaction transfer(
            String senderUpi,
            String receiverUpi,
            long amountPaise
    ) {

        if (amountPaise <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be greater than zero."
            );
        }

        if (senderUpi == null || receiverUpi == null) {
            throw new IllegalArgumentException(
                    "UPI IDs cannot be null."
            );
        }

        senderUpi = senderUpi.trim().toLowerCase();
        receiverUpi = receiverUpi.trim().toLowerCase();

        if (senderUpi.equals(receiverUpi)) {
            throw new IllegalArgumentException(
                    "Cannot transfer money to the same wallet."
            );
        }

        synchronized (walletService.getBalanceLock()) {

            Wallet sender = walletService.getWallet(senderUpi);
            Wallet receiver = walletService.getWallet(receiverUpi);

            if (sender.getBalancePaise() < amountPaise) {
                throw new InsufficientBalanceException(
                        "Insufficient wallet balance."
                );
            }

            // Check receiver balance overflow
            Math.addExact(
                    receiver.getBalancePaise(),
                    amountPaise
            );

            // Create a NEW transaction
            Transaction transaction = new Transaction(
                    senderUpi,
                    receiverUpi,
                    amountPaise,
                    "SUCCESS"
            );

            // Update wallet balances
            sender.debit(amountPaise);
            receiver.credit(amountPaise);

            // Add transaction to history
            transactions.add(transaction);

            try {

                // Save transaction history
                saveTransactionData();

                // Save updated wallet balances
                walletService.saveBalances();

            } catch (RuntimeException e) {

                // Roll back in-memory changes
                receiver.debit(amountPaise);
                sender.credit(amountPaise);
                transactions.remove(transaction);

                // Try restoring transaction file
                try {
                    saveTransactionData();
                } catch (RuntimeException restoreError) {
                    e.addSuppressed(restoreError);
                }

                throw new IllegalStateException(
                        "Transfer could not be saved. "
                                + "Please retry.",
                        e
                );
            }

            return transaction;
        }
    }

    // 2. Get transaction history for one UPI ID
    public List<Transaction> getTransactionHistory(
            String upiId
    ) {

        if (upiId == null) {
            throw new IllegalArgumentException(
                    "UPI ID cannot be null."
            );
        }

        String normalizedUpi =
                upiId.trim().toLowerCase();

        synchronized (walletService.getBalanceLock()) {

            List<Transaction> history =
                    new ArrayList<>();

            for (Transaction transaction : transactions) {

                if (transaction.getSenderUpi()
                        .equals(normalizedUpi)
                        || transaction.getReceiverUpi()
                        .equals(normalizedUpi)) {

                    history.add(transaction);
                }
            }

            return history;
        }
    }

    // 3. Get all transactions
    public List<Transaction> getAllTransactions() {

        synchronized (walletService.getBalanceLock()) {
            return new ArrayList<>(transactions);
        }
    }

    // 4. Save transactions to file
    private void saveTransactionData() {

        List<String> lines = new ArrayList<>();

        for (Transaction transaction : transactions) {

            String record =
                    transaction.getTransactionId() + "|"
                            + transaction.getSenderUpi() + "|"
                            + transaction.getReceiverUpi() + "|"
                            + transaction.getAmountPaise() + "|"
                            + transaction.getStatus() + "|"
                            + transaction.getTimestamp();

            lines.add(record);
        }

        FileStorage.writeLines(
                "transactions.txt",
                lines
        );
    }

    // 5. Load saved transactions from file
    private void loadTransactionData() {

        synchronized (walletService.getBalanceLock()) {

            List<String> lines =
                    FileStorage.readLines("transactions.txt");

            for (String line : lines) {

                String[] parts = line.split("\\|", -1);

                if (parts.length != 6) {
                    throw new IllegalStateException(
                            "Invalid saved transaction record."
                    );
                }

                String transactionId = parts[0];
                String senderUpi = parts[1];
                String receiverUpi = parts[2];
                String status = parts[4];

                long amountPaise;
                LocalDateTime timestamp;

                try {
                    amountPaise = Long.parseLong(parts[3]);
                    timestamp = LocalDateTime.parse(parts[5]);

                } catch (RuntimeException e) {
                    throw new IllegalStateException(
                            "Invalid saved transaction data.",
                            e
                    );
                }

                if (amountPaise <= 0) {
                    throw new IllegalStateException(
                            "Invalid saved transaction amount."
                    );
                }

                // Restore ORIGINAL ID and timestamp
                Transaction transaction = new Transaction(
                        transactionId,
                        senderUpi,
                        receiverUpi,
                        amountPaise,
                        status,
                        timestamp
                );

                transactions.add(transaction);
            }
        }
    }
}
