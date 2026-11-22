package com.rupix.util;

import com.rupix.model.EscrowProject;
import com.rupix.model.Milestone;

import java.util.List;

public class EscrowReport {

    public static void generateReport(EscrowProject project,
                                      List<Milestone> milestones) {

        System.out.println("\n========== ESCROW REPORT ==========");

        System.out.println("Project ID: " + project.getProjectId());
        System.out.println("Client UPI: " + project.getClientUpi());
        System.out.println("Freelancer UPI: " + project.getFreelancerUpi());
        System.out.println("Total Amount: Rs. " +
                project.getTotalAmountPaise() / 100.0);
        System.out.println("Project Status: " + project.getStatus());

        System.out.println("\n--- Milestones ---");

        for (Milestone milestone : milestones) {
            System.out.println("Milestone ID: " + milestone.getMilestoneId());
            System.out.println("Description: " + milestone.getDescription());
            System.out.println("Amount: Rs. " +
                    milestone.getAmountPaise() / 100.0);
            System.out.println("Status: " + milestone.getStatus());
            System.out.println("----------------------------");
        }

        System.out.println("===================================");
    }
}