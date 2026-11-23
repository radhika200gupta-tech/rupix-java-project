
package com.rupix.service;

import com.rupix.model.Merchant;
import com.rupix.model.MerchantPayment;
import com.rupix.model.Settlement;
import com.rupix.util.FileStorage;
import com.rupix.exception.InvalidMerchantException;

import java.util.ArrayList;
import java.util.List;

public class SettlementService {

    private static final String PAYMENT_FILE = "merchant_payments.txt";
    private static final String SETTLEMENT_FILE = "settlements.txt";

    private static final double MDR_RATE = 0.02;

    private final MerchantService merchantService;
    private final List<MerchantPayment> payments = new ArrayList<>();
    private final List<Settlement> settlements = new ArrayList<>();

    public SettlementService(MerchantService merchantService) {
        this.merchantService = merchantService;
        loadPayments();
        loadSettlements();
    }

    public synchronized MerchantPayment recordPayment(
            int paymentId,
            String merchantId,
            String customerUpi,
            long amountPaise) {

        Merchant merchant = merchantService.findMerchantById(merchantId);

        if (merchant == null || !merchant.isActive()) {
            throw new InvalidMerchantException("Invalid or inactive merchant.");
        }

        if (amountPaise <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        for (MerchantPayment payment : payments) {
            if (payment.getPaymentId() == paymentId) {
                throw new IllegalArgumentException("Duplicate payment ID.");
            }
        }

        MerchantPayment payment = new MerchantPayment(
                paymentId, merchantId, customerUpi, amountPaise
        );

        payments.add(payment);

        FileStorage.appendLine(
                PAYMENT_FILE,
                payment.getPaymentId() + "|"
                        + payment.getMerchantId() + "|"
                        + payment.getCustomerUpi() + "|"
                        + payment.getAmountPaise() + "|"
                        + payment.getPaymentTime() + "|"
                        + payment.getStatus()
        );

        return payment;
    }

    public long calculateMDR(long amountPaise) {
        if (amountPaise <= 0) {
            throw new IllegalArgumentException("Invalid amount.");
        }

        return Math.round(amountPaise * MDR_RATE);
    }

    public synchronized Settlement processSettlement(int paymentId) {

        MerchantPayment payment = null;

        for (MerchantPayment p : payments) {
            if (p.getPaymentId() == paymentId) {
                payment = p;
                break;
            }
        }

        if (payment == null) {
            throw new IllegalArgumentException("Payment not found.");
        }

        if (!"SUCCESS".equals(payment.getStatus())) {
            throw new IllegalStateException("Payment already processed.");
        }

        long mdr = calculateMDR(payment.getAmountPaise());

        Settlement settlement = new Settlement(
                settlements.size() + 1,
                payment.getMerchantId(),
                payment.getAmountPaise(),
                mdr
        );

        settlements.add(settlement);
        payment.setStatus("SETTLEMENT_PENDING");

        saveSettlements();
        savePayments();

        return settlement;
    }

    public synchronized List<Settlement> getAllSettlements() {
        return new ArrayList<>(settlements);
    }

    public synchronized List<MerchantPayment> getAllPayments() {
        return new ArrayList<>(payments);
    }

    private void savePayments() {
        List<String> records = new ArrayList<>();

        for (MerchantPayment p : payments) {
            records.add(
                    p.getPaymentId() + "|"
                            + p.getMerchantId() + "|"
                            + p.getCustomerUpi() + "|"
                            + p.getAmountPaise() + "|"
                            + p.getPaymentTime() + "|"
                            + p.getStatus()
            );
        }

        FileStorage.writeLines(PAYMENT_FILE, records);
    }

    private void saveSettlements() {
        List<String> records = new ArrayList<>();

        for (Settlement s : settlements) {
            records.add(
                    s.getSettlementId() + "|"
                            + s.getMerchantId() + "|"
                            + s.getTotalAmountPaise() + "|"
                            + s.getMdrPaise() + "|"
                            + s.getNetAmountPaise() + "|"
                            + s.getSettlementTime() + "|"
                            + s.getStatus()
            );
        }

        FileStorage.writeLines(SETTLEMENT_FILE, records);
    }

    private void loadPayments() {
        for (String record : FileStorage.readLines(PAYMENT_FILE)) {
            try {
                String[] d = record.split("\\|", -1);

                if (d.length != 6) continue;

                MerchantPayment p = new MerchantPayment(
                        Integer.parseInt(d[0]), d[1], d[2],
                        Long.parseLong(d[3])
                );

                p.setStatus(d[5]);
                payments.add(p);

            } catch (RuntimeException e) {
                System.out.println("Invalid payment record skipped.");
            }
        }
    }

    private void loadSettlements() {
        for (String record : FileStorage.readLines(SETTLEMENT_FILE)) {
            try {
                String[] d = record.split("\\|", -1);

                if (d.length != 7) continue;

                Settlement s = new Settlement(
                        Integer.parseInt(d[0]), d[1],
                        Long.parseLong(d[2]),
                        Long.parseLong(d[3])
                );

                s.setStatus(d[6]);
                settlements.add(s);

            } catch (RuntimeException e) {
                System.out.println("Invalid settlement record skipped.");
            }
        }
    }
}
