
package com.rupix.settlement;

import com.rupix.model.Settlement;
import com.rupix.service.SettlementService;

import java.util.Queue;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class SettlementQueue {

    private final Queue<Integer> pendingPayments = new LinkedList<>();

    private final SettlementService settlementService;

    public SettlementQueue(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    public synchronized void addPayment(int paymentId) {
        pendingPayments.add(paymentId);
        System.out.println("Payment added to settlement queue: " + paymentId);
    }

    public void processQueue(int numberOfThreads) {

        if (numberOfThreads <= 0) {
            throw new IllegalArgumentException(
                    "Number of threads must be positive."
            );
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(numberOfThreads);

        List<Future<Settlement>> results = new ArrayList<>();

        try {
            Integer paymentId;

            while (true) {

                synchronized (this) {
                    paymentId = pendingPayments.poll();
                }

                if (paymentId == null) {
                    break;
                }

                final int id = paymentId;

                Future<Settlement> result = executor.submit(() -> {

                    Settlement settlement =
                            settlementService.processSettlement(id);

                    System.out.println(
                            Thread.currentThread().getName()
                                    + " processed settlement: "
                                    + settlement.getSettlementId()
                    );

                    return settlement;
                });

                results.add(result);
            }

            for (Future<Settlement> result : results) {
                try {
                    result.get();
                } catch (ExecutionException e) {
                    System.out.println(
                            "Settlement failed: " + e.getCause().getMessage()
                    );
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Settlement processing interrupted.");

        } finally {
            executor.shutdown();
        }
    }

    public synchronized int getQueueSize() {
        return pendingPayments.size();
    }
}
