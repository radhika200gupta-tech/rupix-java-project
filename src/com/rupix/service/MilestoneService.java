
package com.rupix.service;

import com.rupix.model.Milestone;
import com.rupix.model.EscrowProject;
import com.rupix.escrow.EscrowStatus;
import com.rupix.exception.InvalidEscrowException;
import com.rupix.util.FileStorage;
import com.rupix.util.InputValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class MilestoneService {

    private static final String FILE = "milestones.txt";
    private static final String ESCROW_UPI = "escrow@rupix";

    private final List<Milestone> milestones = new ArrayList<>();
    private final EscrowService escrowService;
    private final PaymentService paymentService;

    public MilestoneService(EscrowService escrowService,
                            PaymentService paymentService) {
        this.escrowService = escrowService;
        this.paymentService = paymentService;
        loadMilestones();
    }

    private void loadMilestones() {
        for (String line : FileStorage.readLines(FILE)) {
            try {
                String[] d = line.split("\\|", 5);

                Milestone m = new Milestone(
                        Integer.parseInt(d[0]),
                        Integer.parseInt(d[1]),
                        new String(Base64.getDecoder().decode(d[2]),
                                StandardCharsets.UTF_8),
                        Long.parseLong(d[3])
                );

                m.setStatus(EscrowStatus.valueOf(d[4]));
                milestones.add(m);

            } catch (Exception e) {
                System.out.println("Invalid milestone record skipped.");
            }
        }
    }

    private void saveMilestones() {
        List<String> lines = new ArrayList<>();

        for (Milestone m : milestones) {
            String description = Base64.getEncoder().encodeToString(
                    m.getDescription().getBytes(StandardCharsets.UTF_8));

            lines.add(m.getMilestoneId() + "|" +
                    m.getProjectId() + "|" + description + "|" +
                    m.getAmountPaise() + "|" + m.getStatus());
        }

        FileStorage.writeLines(FILE, lines);
    }

    public synchronized void addMilestone(Milestone m, EscrowProject p)
            throws InvalidEscrowException {

        if (m == null || p == null ||
                (p.getStatus() != EscrowStatus.FUNDED &&
                        p.getStatus() != EscrowStatus.IN_PROGRESS))
            throw new InvalidEscrowException("Invalid project or milestone");

        if (m.getProjectId() != p.getProjectId() ||
                !InputValidator.isValidAmount(m.getAmountPaise()) ||
                !InputValidator.isValidDescription(m.getDescription()))
            throw new InvalidEscrowException("Invalid milestone details");

        long total = m.getAmountPaise();

        for (Milestone old : milestones) {
            if (old.getMilestoneId() == m.getMilestoneId())
                throw new InvalidEscrowException("Duplicate milestone ID");

            if (old.getProjectId() == p.getProjectId())
                total += old.getAmountPaise();
        }

        if (total > p.getTotalAmountPaise())
            throw new InvalidEscrowException("Milestone budget exceeded");

        milestones.add(m);
        p.setStatus(EscrowStatus.IN_PROGRESS);

        saveMilestones();
        escrowService.saveProjectChanges();

        System.out.println("Milestone added successfully!");
    }

    public synchronized void approveMilestone(int id)
            throws InvalidEscrowException {

        Milestone m = getMilestone(id);

        if (m.getStatus() != EscrowStatus.PENDING)
            throw new InvalidEscrowException("Milestone cannot be approved");

        m.setStatus(EscrowStatus.APPROVED);
        saveMilestones();

        System.out.println("Milestone approved successfully!");
    }

    public synchronized void releaseMilestone(int id)
            throws InvalidEscrowException {

        Milestone m = getMilestone(id);

        if (m.getStatus() != EscrowStatus.APPROVED)
            throw new InvalidEscrowException(
                    "Milestone must be approved before release");

        EscrowProject p = escrowService.getProject(m.getProjectId());

        if (p.getStatus() != EscrowStatus.IN_PROGRESS)
            throw new InvalidEscrowException("Project is not in progress");

        try {
            paymentService.transfer(
                    ESCROW_UPI, p.getFreelancerUpi(), m.getAmountPaise());

            m.setStatus(EscrowStatus.RELEASED);

            boolean completed = true;

            for (Milestone item : milestones) {
                if (item.getProjectId() == p.getProjectId() &&
                        item.getStatus() != EscrowStatus.RELEASED) {
                    completed = false;
                    break;
                }
            }

            if (completed)
                p.setStatus(EscrowStatus.COMPLETED);

            saveMilestones();
            escrowService.saveProjectChanges();

            System.out.println("Milestone released successfully!");

        } catch (RuntimeException e) {
            throw new InvalidEscrowException("Payment or saving failed: "
                    + e.getMessage());
        }
    }

    public synchronized Milestone getMilestone(int id)
            throws InvalidEscrowException {
        for (Milestone m : milestones)
            if (m.getMilestoneId() == id) return m;

        throw new InvalidEscrowException("Milestone not found");
    }

    public synchronized List<Milestone> getMilestonesByProject(int id) {
        List<Milestone> result = new ArrayList<>();

        for (Milestone m : milestones)
            if (m.getProjectId() == id) result.add(m);

        return result;
    }
}
