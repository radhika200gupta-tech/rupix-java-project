
package com.rupix.concurrency;

import com.rupix.model.Transaction;
import com.rupix.service.PaymentService;

public class PaymentTask implements Runnable {

    private final PaymentService paymentService;
    private final String senderUpi;
    private final String receiverUpi;
    private final long amountPaise;

    public PaymentTask(
            PaymentService paymentService,
            String senderUpi,
            String receiverUpi,
            long amountPaise
    ) {
        this.paymentService = paymentService;
        this.senderUpi = senderUpi;
        this.receiverUpi = receiverUpi;
        this.amountPaise = amountPaise;
    }

    @Override
    public void run() {
        try {
            Transaction transaction = paymentService.transfer(
                    senderUpi,
                    receiverUpi,
                    amountPaise
            );

            System.out.println(
                    Thread.currentThread().getName()
                            + " SUCCESS: "
                            + transaction.getTransactionId()
            );

        } catch (RuntimeException e) {
            System.out.println(
                    Thread.currentThread().getName()
                            + " FAILED: "
                            + e.getMessage()
            );
        }
    }
}
