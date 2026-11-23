
package com.rupix.service;

import com.rupix.model.Merchant;
import com.rupix.util.FileStorage;
import com.rupix.exception.InvalidMerchantException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MerchantService {

    private static final String FILE_NAME = "merchants.txt";

    private final List<Merchant> merchants = new ArrayList<>();

    public MerchantService() {
        loadMerchants();
    }

    public synchronized Merchant registerMerchant(String name, String upiId) {

        if (name == null || name.trim().isEmpty()
                || name.contains("|") || name.contains("\n")) {
            throw new InvalidMerchantException("Invalid merchant name.");
        }

        if (upiId == null ||
                !upiId.matches("[a-zA-Z0-9._-]+@rupix")) {
            throw new InvalidMerchantException("Invalid UPI ID.");
        }

        if (findMerchantByUpiId(upiId) != null) {
            throw new InvalidMerchantException("Merchant already registered.");
        }

        String merchantId = UUID.randomUUID().toString();

        Merchant merchant = new Merchant(
                merchantId,
                name.trim(),
                upiId
        );

        merchants.add(merchant);

        FileStorage.appendLine(
                FILE_NAME,
                merchant.getMerchantId() + "|"
                        + merchant.getName() + "|"
                        + merchant.getUpiId() + "|"
                        + merchant.isActive()
        );

        return merchant;
    }

    public synchronized Merchant findMerchantById(String merchantId) {

        for (Merchant merchant : merchants) {
            if (merchant.getMerchantId().equals(merchantId)) {
                return merchant;
            }
        }

        return null;
    }

    public synchronized Merchant findMerchantByUpiId(String upiId) {

        for (Merchant merchant : merchants) {
            if (merchant.getUpiId().equalsIgnoreCase(upiId)) {
                return merchant;
            }
        }

        return null;
    }

    public synchronized List<Merchant> getAllMerchants() {
        return new ArrayList<>(merchants);
    }

    private void loadMerchants() {

        List<String> records = FileStorage.readLines(FILE_NAME);

        for (String record : records) {

            try {
                String[] data = record.split("\\|", -1);

                if (data.length != 4) {
                    continue;
                }

                Merchant merchant = new Merchant(
                        data[0],
                        data[1],
                        data[2]
                );

                merchant.setActive(Boolean.parseBoolean(data[3]));

                merchants.add(merchant);

            } catch (RuntimeException e) {
                System.out.println("Invalid merchant record skipped.");
            }
        }
    }
}
