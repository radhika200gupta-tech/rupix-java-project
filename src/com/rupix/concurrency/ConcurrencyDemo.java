
package com.rupix.concurrency;

import com.rupix.service.PaymentService;
import com.rupix.service.WalletService;

import java.util.UUID;

public class ConcurrencyDemo {

    public static void runDemo() {

        WalletService walletService = new WalletService();
        PaymentService paymentService = new PaymentService(walletService);

        // Generate unique UPI IDs for every demo run
        String demoId = UUID.randomUUID().toString().substring(0, 8);

        String aliceUpi = "alice" + demoId + "@rupix";
        String bobUpi = "bob" + demoId + "@rupix";
        String charlieUpi = "charlie" + demoId + "@rupix";

        // Register demo users
        walletService.registerUser("Alice", aliceUpi);
        walletService.registerUser("Bob", bobUpi);
        walletService.registerUser("Charlie", charlieUpi);

        // Add Rs. 1000 to Alice's wallet
        walletService.addMoney(aliceUpi, 100000);

        // Record transactions before starting the threads
        int transactionsBefore =
                paymentService.getAllTransactions().size();

        System.out.println("\n===== RUPIX MULTITHREADING DEMO =====");

        System.out.println(
                "Alice Initial Balance: Rs. "
                        + walletService.getBalance(aliceUpi) / 100.0
        );

        // Both threads try to spend Rs. 1300 in total
        // Alice only has Rs. 1000

        Thread thread1 = new Thread(
                new PaymentTask(
                        paymentService,
                        aliceUpi,
                        bobUpi,
                        70000
                ),
                "Payment-Thread-1"
        );

        Thread thread2 = new Thread(
                new PaymentTask(
                        paymentService,
                        aliceUpi,
                        charlieUpi,
                        60000
                ),
                "Payment-Thread-2"
        );

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Demo interrupted.");
            return;
        }

        System.out.println("\n===== FINAL BALANCES =====");

        System.out.println(
                "Alice: Rs. "
                        + walletService.getBalance(aliceUpi) / 100.0
        );

        System.out.println(
                "Bob: Rs. "
                        + walletService.getBalance(bobUpi) / 100.0
        );

        System.out.println(
                "Charlie: Rs. "
                        + walletService.getBalance(charlieUpi) / 100.0
        );

        // Count only transactions created during this demo
        int transactionsAfter =
                paymentService.getAllTransactions().size();

        int successfulTransactions =
                transactionsAfter - transactionsBefore;

        System.out.println(
                "Successful Transactions: " + successfulTransactions
        );

        System.out.println("==================================");
    }

    public static void main(String[] args) {
        runDemo();
    }
}
