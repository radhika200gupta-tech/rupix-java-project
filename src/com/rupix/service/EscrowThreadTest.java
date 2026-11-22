
package com.rupix.service;

import com.rupix.model.Milestone;
import com.rupix.model.EscrowProject;
import com.rupix.escrow.EscrowStatus;
import com.rupix.exception.InvalidEscrowException;
import com.rupix.model.Wallet;

public class EscrowThreadTest {

    public static void main(String[] args)
            throws InterruptedException, InvalidEscrowException {

        // Create payment services
        WalletService walletService = new WalletService();
        PaymentService paymentService = new PaymentService(walletService);

        // Register users if they do not exist
        if (walletService.getUser("client@rupix") == null)
            walletService.registerUser("Client", "client@rupix");

        if (walletService.getUser("freelancer@rupix") == null)
            walletService.registerUser("Freelancer", "freelancer@rupix");

        // Add money to client wallet
        if (walletService.getBalance("client@rupix") < 500000)
            walletService.addMoney("client@rupix",
                    500000 - walletService.getBalance("client@rupix"));

        // Create services
        EscrowService escrowService =
                new EscrowService(walletService, paymentService);

        MilestoneService service =
                new MilestoneService(escrowService, paymentService);

        // Create and fund project
        EscrowProject project = new EscrowProject(
                101, "client@rupix", "freelancer@rupix", 500000
        );

        escrowService.createProject(project);
        escrowService.fundProject(101);

        // Create milestone
        Milestone milestone = new Milestone(
                1, 101, "Website Design", 500000
        );

        service.addMilestone(milestone, project);
        service.approveMilestone(1);

        // Both threads attempt to release the same milestone
        Runnable task = () -> {
            try {
                service.releaseMilestone(1);
            } catch (InvalidEscrowException e) {
                System.out.println(
                        Thread.currentThread().getName()
                                + ": " + e.getMessage()
                );
            }
        };

        Thread t1 = new Thread(task, "Thread 1");
        Thread t2 = new Thread(task, "Thread 2");

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println(
                "Final Milestone Status: " + milestone.getStatus()
        );

        System.out.println(
                "Freelancer Balance: "
                        + walletService.getBalance("freelancer@rupix")
                        / 100.0
        );
    }
}
