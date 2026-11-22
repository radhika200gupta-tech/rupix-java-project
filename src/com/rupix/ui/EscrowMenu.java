
package com.rupix.ui;

import com.rupix.model.EscrowProject;
import com.rupix.model.Milestone;
import com.rupix.service.EscrowService;
import com.rupix.service.MilestoneService;
import com.rupix.exception.InvalidEscrowException;
import com.rupix.util.EscrowReport;

import java.util.List;
import java.util.Scanner;

public class EscrowMenu {

    private final EscrowService escrowService;
    private final MilestoneService milestoneService;

    public EscrowMenu(EscrowService escrowService,
                      MilestoneService milestoneService) {
        this.escrowService = escrowService;
        this.milestoneService = milestoneService;
    }

    public void show(Scanner sc) {

        while (true) {
            System.out.println("\n===== FREELANCER ESCROW =====");
            System.out.println("1. Create Project");
            System.out.println("2. Fund Project");
            System.out.println("3. Add Milestone");
            System.out.println("4. Approve Milestone");
            System.out.println("5. Release Milestone");
            System.out.println("6. View Project Report");
            System.out.println("7. View All Projects");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");

            int choice;

            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number!");
                continue;
            }

            try {
                switch (choice) {

                    case 1:
                        System.out.print("Project ID: ");
                        int projectId = Integer.parseInt(sc.nextLine());

                        System.out.print("Client UPI: ");
                        String client = sc.nextLine();

                        System.out.print("Freelancer UPI: ");
                        String freelancer = sc.nextLine();

                        System.out.print("Total amount (rupees): ");
                        long amount = Math.multiplyExact(
                                Long.parseLong(sc.nextLine()), 100);

                        escrowService.createProject(
                                new EscrowProject(
                                        projectId, client, freelancer, amount));
                        break;

                    case 2:
                        System.out.print("Project ID: ");
                        escrowService.fundProject(
                                Integer.parseInt(sc.nextLine()));
                        break;

                    case 3:
                        System.out.print("Project ID: ");
                        int pId = Integer.parseInt(sc.nextLine());

                        EscrowProject project =
                                escrowService.getProject(pId);

                        System.out.print("Milestone ID: ");
                        int mId = Integer.parseInt(sc.nextLine());

                        System.out.print("Description: ");
                        String description = sc.nextLine();

                        System.out.print("Amount (rupees): ");
                        long mAmount = Math.multiplyExact(
                                Long.parseLong(sc.nextLine()), 100);

                        milestoneService.addMilestone(
                                new Milestone(mId, pId, description, mAmount),
                                project);
                        break;

                    case 4:
                        System.out.print("Milestone ID: ");
                        milestoneService.approveMilestone(
                                Integer.parseInt(sc.nextLine()));
                        break;

                    case 5:
                        System.out.print("Milestone ID: ");
                        milestoneService.releaseMilestone(
                                Integer.parseInt(sc.nextLine()));
                        break;

                    case 6:
                        System.out.print("Project ID: ");
                        int reportId = Integer.parseInt(sc.nextLine());

                        EscrowProject reportProject =
                                escrowService.getProject(reportId);

                        List<Milestone> reportMilestones =
                                milestoneService.getMilestonesByProject(reportId);

                        EscrowReport.generateReport(
                                reportProject, reportMilestones);
                        break;

                    case 7:
                        List<EscrowProject> projects =
                                escrowService.getAllProjects();

                        if (projects.isEmpty()) {
                            System.out.println("No projects found.");
                        } else {
                            for (EscrowProject p : projects) {
                                System.out.println(
                                        "Project ID: " + p.getProjectId()
                                                + " | Client: " + p.getClientUpi()
                                                + " | Freelancer: " + p.getFreelancerUpi()
                                                + " | Status: " + p.getStatus());
                            }
                        }
                        break;

                    case 0:
                        return;

                    default:
                        System.out.println("Invalid choice!");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter valid numeric input.");
            } catch (ArithmeticException e) {
                System.out.println("Amount is too large.");
            } catch (InvalidEscrowException | RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
