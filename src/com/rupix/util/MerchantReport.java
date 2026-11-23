
package com.rupix.util;

import com.rupix.model.Merchant;
import com.rupix.model.MerchantPayment;
import com.rupix.model.Settlement;
import com.rupix.service.MerchantService;
import com.rupix.service.SettlementService;

public class MerchantReport {

    public String generateReport(
            String merchantId,
            MerchantService merchantService,
            SettlementService settlementService) {

        Merchant merchant =
                merchantService.findMerchantById(merchantId);

        if (merchant == null) {
            return "Merchant not found.";
        }

        long totalPayments = 0;
        long totalSettled = 0;
        long totalMDR = 0;

        StringBuilder report = new StringBuilder();

        report.append("\n===== MERCHANT REPORT =====\n");
        report.append("Merchant Name: ")
                .append(merchant.getName()).append("\n");
        report.append("Merchant UPI: ")
                .append(merchant.getUpiId()).append("\n");

        report.append("\n--- Payment History ---\n");

        for (MerchantPayment payment :
                settlementService.getAllPayments()) {

            if (payment.getMerchantId().equals(merchantId)) {

                totalPayments += payment.getAmountPaise();

                report.append("Payment ID: ")
                        .append(payment.getPaymentId())
                        .append(" | Amount: Rs. ")
                        .append(String.format("%.2f",
                                payment.getAmountPaise() / 100.0))
                        .append(" | Status: ")
                        .append(payment.getStatus())
                        .append("\n");
            }
        }

        report.append("\n--- Settlement History ---\n");

        for (Settlement settlement :
                settlementService.getAllSettlements()) {

            if (settlement.getMerchantId().equals(merchantId)) {

                totalSettled += settlement.getNetAmountPaise();
                totalMDR += settlement.getMdrPaise();

                report.append("Settlement ID: ")
                        .append(settlement.getSettlementId())
                        .append(" | Net Amount: Rs. ")
                        .append(String.format("%.2f",
                                settlement.getNetAmountPaise() / 100.0))
                        .append(" | Status: ")
                        .append(settlement.getStatus())
                        .append("\n");
            }
        }

        report.append("\n--- Summary ---\n");

        report.append("Total Payments: Rs. ")
                .append(String.format("%.2f",
                        totalPayments / 100.0))
                .append("\n");

        report.append("Total MDR: Rs. ")
                .append(String.format("%.2f",
                        totalMDR / 100.0))
                .append("\n");

        report.append("Total Net Settled: Rs. ")
                .append(String.format("%.2f",
                        totalSettled / 100.0))
                .append("\n");

        report.append("===========================\n");

        return report.toString();
    }
}
