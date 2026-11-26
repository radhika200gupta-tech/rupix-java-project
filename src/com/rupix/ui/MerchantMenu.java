
package com.rupix.ui;

import com.rupix.model.Merchant;
import com.rupix.model.MerchantPayment;
import com.rupix.service.MerchantService;
import com.rupix.service.SettlementService;
import com.rupix.util.MerchantReport;

import java.util.List;
import java.util.Scanner;

public class MerchantMenu {

    private final MerchantService merchantService;
    private final SettlementService settlementService;
    private final MerchantReport merchantReport;
    private final Scanner scanner;

    public MerchantMenu(
            MerchantService merchantService,
            SettlementService settlementService,
            Scanner scanner) {

        this.merchantService = merchantService;
        this.settlementService = settlementService;
        this.scanner = scanner;

        this.merchantReport = new MerchantReport();
    }

    public void showMenu() {

        while (true) {

            System.out.println("\n===== MERCHANT SETTLEMENT MENU =====");
            System.out.println("1. Register Merchant");
            System.out.println("2. View All Merchants");
            System.out.println("3. Record Merchant Payment");
            System.out.println("4. View All Payments");
            System.out.println("5. Process Settlement");
            System.out.println("6. Generate Merchant Report");
            System.out.println("7. Back to Main Menu");

            System.out.print("Enter choice: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {

                    case 1:
                        registerMerchant();
                        break;

                    case 2:
                        viewMerchants();
                        break;

                    case 3:
                        recordPayment();
                        break;

                    case 4:
                        viewPayments();
                        break;

                    case 5:
                        processSettlement();
                        break;

                    case 6:
                        generateReport();
                        break;

                    case 7:
                        return;

                    default:
                        System.out.println("Invalid choice.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");

            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void registerMerchant() {

        System.out.print("Enter merchant name: ");
        String name = scanner.nextLine();

        System.out.print("Enter merchant UPI ID: ");
        String upiId = scanner.nextLine();

        Merchant merchant =
                merchantService.registerMerchant(name, upiId);

        System.out.println("Merchant registered successfully!");
        System.out.println("Merchant ID: " + merchant.getMerchantId());
    }

    private void viewMerchants() {

        List<Merchant> merchants =
                merchantService.getAllMerchants();

        if (merchants.isEmpty()) {
            System.out.println("No merchants registered.");
            return;
        }

        for (Merchant merchant : merchants) {

            System.out.println("\nMerchant ID: "
                    + merchant.getMerchantId());

            System.out.println("Name: " + merchant.getName());
            System.out.println("UPI ID: " + merchant.getUpiId());
            System.out.println("Active: " + merchant.isActive());
        }
    }

    private void recordPayment() {

        System.out.print("Enter payment ID: ");
        int paymentId = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter merchant ID: ");
        String merchantId = scanner.nextLine();

        System.out.print("Enter customer UPI ID: ");
        String customerUpi = scanner.nextLine();

        System.out.print("Enter amount in rupees: ");
        double amount = Double.parseDouble(scanner.nextLine());

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        long amountPaise = Math.round(amount * 100);

        MerchantPayment payment =
                settlementService.recordPayment(
                        paymentId, merchantId, customerUpi, amountPaise);

        System.out.println("Payment recorded successfully!");
        System.out.println("Payment ID: " + payment.getPaymentId());
    }

    private void viewPayments() {

        List<MerchantPayment> payments =
                settlementService.getAllPayments();

        if (payments.isEmpty()) {
            System.out.println("No payments found.");
            return;
        }

        for (MerchantPayment payment : payments) {

            System.out.println("\nPayment ID: "
                    + payment.getPaymentId());

            System.out.println("Merchant ID: "
                    + payment.getMerchantId());

            System.out.println("Amount: Rs. "
                    + String.format("%.2f",
                    payment.getAmountPaise() / 100.0));

            System.out.println("Status: " + payment.getStatus());
        }
    }

    private void processSettlement() {

        System.out.print("Enter payment ID: ");
        int paymentId = Integer.parseInt(scanner.nextLine());

        settlementService.processSettlement(paymentId);

        System.out.println("Settlement processed successfully!");
    }

    private void generateReport() {

        System.out.print("Enter merchant ID: ");
        String merchantId = scanner.nextLine();

        String report = merchantReport.generateReport(
                merchantId, merchantService, settlementService);

        System.out.println(report);
    }
}
