
package com.rupix.ui;

import com.rupix.concurrency.ConcurrencyDemo;
import com.rupix.model.Transaction;
import com.rupix.model.User;
import com.rupix.service.PaymentService;
import com.rupix.service.WalletService;

import java.util.List;
import java.util.Scanner;

public class PaymentMenu {

    private final WalletService walletService;
    private final PaymentService paymentService;

    public PaymentMenu(
            WalletService walletService,
            PaymentService paymentService
    ) {
        this.walletService = walletService;
        this.paymentService = paymentService;
    }

    public void show(Scanner scanner) {

        while (true) {

            System.out.println("\n========== RUPIX UPI MENU ==========");
            System.out.println("1. Register User");
            System.out.println("2. Add Money");
            System.out.println("3. Check Balance");
            System.out.println("4. Transfer Money");
            System.out.println("5. Transaction History");
            System.out.println("6. Multithreading Demo");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter choice: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {

                    case "1":
                        System.out.print("Enter name: ");
                        String name = scanner.nextLine();

                        System.out.print("Enter UPI ID: ");
                        String upiId = scanner.nextLine();

                        User user = walletService.registerUser(
                                name, upiId
                        );

                        System.out.println(
                                "User registered successfully!"
                        );
                        System.out.println(
                                "UPI ID: " + user.getUpiId()
                        );
                        break;

                    case "2":
                        System.out.print("Enter UPI ID: ");
                        String addUpi = scanner.nextLine();

                        long addAmount = readAmount(scanner);

                        walletService.addMoney(addUpi, addAmount);

                        System.out.println(
                                "Money added successfully!"
                        );
                        break;

                    case "3":
                        System.out.print("Enter UPI ID: ");
                        String balanceUpi = scanner.nextLine();

                        long balance =
                                walletService.getBalance(balanceUpi);

                        System.out.printf(
                                "Available Balance: Rs. %.2f%n",
                                balance / 100.0
                        );
                        break;

                    case "4":
                        System.out.print("Sender UPI ID: ");
                        String sender = scanner.nextLine();

                        System.out.print("Receiver UPI ID: ");
                        String receiver = scanner.nextLine();

                        long transferAmount = readAmount(scanner);

                        Transaction transaction =
                                paymentService.transfer(
                                        sender,
                                        receiver,
                                        transferAmount
                                );

                        System.out.println(
                                "Payment successful!"
                        );
                        System.out.println(
                                "Transaction ID: "
                                        + transaction.getTransactionId()
                        );
                        break;

                    case "5":
                        System.out.print("Enter UPI ID: ");
                        String historyUpi = scanner.nextLine();

                        List<Transaction> history =
                                paymentService.getTransactionHistory(
                                        historyUpi
                                );

                        if (history.isEmpty()) {
                            System.out.println(
                                    "No transactions found."
                            );
                        } else {
                            for (Transaction t : history) {
                                System.out.println(t);
                            }
                        }
                        break;

                    case "6":
                        ConcurrencyDemo.runDemo();
                        break;

                    case "0":
                        return;

                    default:
                        System.out.println(
                                "Invalid choice. Try again."
                        );
                }

            } catch (RuntimeException e) {
                System.out.println(
                        "Error: " + e.getMessage()
                );
            }
        }
    }

    private long readAmount(Scanner scanner) {

        System.out.print("Enter amount in Rs.: ");
        String input = scanner.nextLine().trim();

        try {
            java.math.BigDecimal rupees =
                    new java.math.BigDecimal(input);

            return rupees.movePointRight(2)
                    .longValueExact();

        } catch (NumberFormatException
                 | ArithmeticException e) {
            throw new IllegalArgumentException(
                    "Invalid amount. Enter a valid amount "
                            + "with at most 2 decimal places."
            );
        }
    }
}
