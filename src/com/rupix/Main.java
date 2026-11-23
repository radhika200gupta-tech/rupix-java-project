
package com.rupix;

import com.rupix.service.WalletService;
import com.rupix.service.PaymentService;
import com.rupix.service.MerchantService;
import com.rupix.service.SettlementService;
import com.rupix.service.EscrowService;
import com.rupix.service.MilestoneService;

import com.rupix.ui.PaymentMenu;
import com.rupix.ui.MerchantMenu;
import com.rupix.ui.EscrowMenu;

import com.rupix.util.FileStorage;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Initialize file storage
        FileStorage.initialize();

        // Create one shared Scanner
        Scanner scanner = new Scanner(System.in);

        // Initialize Payment Module
        WalletService walletService = new WalletService();
        PaymentService paymentService =
                new PaymentService(walletService);

        PaymentMenu paymentMenu =
                new PaymentMenu(walletService, paymentService);

        // Initialize Merchant Settlement Module
        MerchantService merchantService = new MerchantService();

        SettlementService settlementService =
                new SettlementService(merchantService);

        MerchantMenu merchantMenu =
                new MerchantMenu(
                        merchantService,
                        settlementService,
                        scanner
                );

        // Initialize Freelancer Escrow Module
        EscrowService escrowService =
                new EscrowService(walletService, paymentService);

        MilestoneService milestoneService =
                new MilestoneService(escrowService, paymentService);

        EscrowMenu escrowMenu =
                new EscrowMenu(escrowService, milestoneService);

        // Main Application Menu
        int choice = -1;

        while (choice != 4) {

            System.out.println("\n================================");
            System.out.println("          WELCOME TO RUPIX");
            System.out.println("================================");
            System.out.println("1. Payments");
            System.out.println("2. Merchant Settlements");
            System.out.println("3. Freelancer Escrow");
            System.out.println("4. Exit");
            System.out.println("================================");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {

                    case 1:
                        paymentMenu.show(scanner);
                        break;

                    case 2:
                        merchantMenu.showMenu();
                        break;

                    case 3:
                        escrowMenu.show(scanner);
                        break;

                    case 4:
                        System.out.println(
                                "Thank you for using RUPIX!"
                        );
                        break;

                    default:
                        System.out.println(
                                "Invalid choice. Please try again."
                        );
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Please enter a valid number."
                );
            }
        }

        scanner.close();
    }
}
