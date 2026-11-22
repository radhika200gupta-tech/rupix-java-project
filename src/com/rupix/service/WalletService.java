
package com.rupix.service;

import com.rupix.exception.InvalidAmountException;
import com.rupix.model.User;
import com.rupix.model.Wallet;
import com.rupix.util.FileStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class WalletService {

    // Store users by UPI ID
    private final HashMap<String, User> users = new HashMap<>();

    // Store wallets by UPI ID
    private final HashMap<String, Wallet> wallets = new HashMap<>();

    // Track registered UPI IDs
    private final HashSet<String> registeredUpiIds = new HashSet<>();

    // Shared lock for wallet operations
    private final Object balanceLock = new Object();

    // Load saved wallets when service starts
    public WalletService() {
        loadWalletData();
    }

    // 1. Register user and create wallet
    public User registerUser(String name, String upiId) {

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name cannot be empty."
            );
        }

        if (upiId == null ||
                !upiId.trim().toLowerCase()
                        .matches("[a-z0-9._-]+@rupix")) {
            throw new IllegalArgumentException(
                    "Invalid UPI ID. Example: user@rupix"
            );
        }

        upiId = upiId.trim().toLowerCase();

        synchronized (balanceLock) {

            if (registeredUpiIds.contains(upiId)) {
                throw new IllegalArgumentException(
                        "UPI ID already registered."
                );
            }

            String userId = UUID.randomUUID().toString();
            String walletId = UUID.randomUUID().toString();

            User user = new User(
                    userId,
                    name.trim(),
                    upiId
            );

            Wallet wallet = new Wallet(
                    walletId,
                    upiId
            );

            users.put(upiId, user);
            wallets.put(upiId, wallet);
            registeredUpiIds.add(upiId);

            try {
                saveWalletData();
            } catch (RuntimeException e) {
                users.remove(upiId);
                wallets.remove(upiId);
                registeredUpiIds.remove(upiId);
                throw e;
            }

            return user;
        }
    }

    // 2. Find registered user
    public User getUser(String upiId) {
        synchronized (balanceLock) {
            return users.get(normalizeUpi(upiId));
        }
    }

    // 3. Find wallet by UPI ID
    public Wallet getWallet(String upiId) {

        synchronized (balanceLock) {

            Wallet wallet = wallets.get(
                    normalizeUpi(upiId)
            );

            if (wallet == null) {
                throw new IllegalArgumentException(
                        "Wallet not found for UPI ID: " + upiId
                );
            }

            return wallet;
        }
    }

    // 4. Check wallet balance
    public long getBalance(String upiId) {

        synchronized (balanceLock) {
            return getWallet(upiId).getBalancePaise();
        }
    }

    // 5. Add simulated money
    public void addMoney(String upiId, long amountPaise) {

        if (amountPaise <= 0) {
            throw new InvalidAmountException(
                    "Amount must be greater than zero."
            );
        }

        synchronized (balanceLock) {

            Wallet wallet = getWallet(upiId);

            wallet.credit(amountPaise);

            try {
                saveWalletData();
            } catch (RuntimeException e) {
                wallet.debit(amountPaise);
                throw e;
            }
        }
    }

    // 6. Shared lock for safe transfers
    public Object getBalanceLock() {
        return balanceLock;
    }

    // 7. Save wallet balances
    public void saveBalances() {

        synchronized (balanceLock) {
            saveWalletData();
        }
    }

    // 8. Load saved wallet records
    private void loadWalletData() {

        synchronized (balanceLock) {

            List<String> lines =
                    FileStorage.readLines("wallets.txt");

            for (String line : lines) {

                String[] parts = line.split("\\|", -1);

                if (parts.length != 5) {
                    throw new IllegalStateException(
                            "Invalid wallet record."
                    );
                }

                String userId = parts[0];
                String name = parts[1];
                String upiId = parts[2];
                String walletId = parts[3];

                long balance;

                try {
                    balance = Long.parseLong(parts[4]);
                } catch (NumberFormatException e) {
                    throw new IllegalStateException(
                            "Invalid saved wallet balance.", e
                    );
                }

                if (balance < 0 ||
                        wallets.containsKey(upiId)) {
                    throw new IllegalStateException(
                            "Invalid or duplicate wallet record."
                    );
                }

                User user = new User(
                        userId,
                        name,
                        upiId
                );

                Wallet wallet = new Wallet(
                        walletId,
                        upiId
                );

                if (balance > 0) {
                    wallet.credit(balance);
                }

                users.put(upiId, user);
                wallets.put(upiId, wallet);
                registeredUpiIds.add(upiId);
            }
        }
    }

    // 9. Write wallet records to text file
    private void saveWalletData() {

        List<String> lines = new ArrayList<>();

        for (String upiId : wallets.keySet()) {

            User user = users.get(upiId);
            Wallet wallet = wallets.get(upiId);

            String record =
                    user.getUserId() + "|"
                            + user.getName() + "|"
                            + user.getUpiId() + "|"
                            + wallet.getWalletId() + "|"
                            + wallet.getBalancePaise();

            lines.add(record);
        }

        FileStorage.writeLines(
                "wallets.txt",
                lines
        );
    }

    // 10. Normalize UPI ID
    private String normalizeUpi(String upiId) {

        if (upiId == null) {
            throw new IllegalArgumentException(
                    "UPI ID cannot be null."
            );
        }

        return upiId.trim().toLowerCase();
    }
}
