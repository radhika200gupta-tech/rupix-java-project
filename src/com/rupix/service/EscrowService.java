
package com.rupix.service;

import com.rupix.model.EscrowProject;
import com.rupix.escrow.EscrowStatus;
import com.rupix.exception.InvalidEscrowException;
import com.rupix.util.InputValidator;
import com.rupix.util.FileStorage;

import java.util.ArrayList;
import java.util.List;

public class EscrowService {

    private static final String ESCROW_UPI = "escrow@rupix";
    private static final String FILE = "escrow_projects.txt";

    private final List<EscrowProject> projects = new ArrayList<>();

    private final WalletService walletService;
    private final PaymentService paymentService;

    public EscrowService(WalletService walletService,
                         PaymentService paymentService) {

        if (walletService == null || paymentService == null)
            throw new IllegalArgumentException("Services cannot be null");

        this.walletService = walletService;
        this.paymentService = paymentService;

        if (walletService.getUser(ESCROW_UPI) == null)
            walletService.registerUser("RUPIX Escrow", ESCROW_UPI);

        loadProjects();
    }

    private void loadProjects() {
        for (String line : FileStorage.readLines(FILE)) {
            String[] data = line.split("\\|");

            if (data.length == 5) {
                EscrowProject p = new EscrowProject(
                        Integer.parseInt(data[0]),
                        data[1],
                        data[2],
                        Long.parseLong(data[3])
                );

                p.setStatus(EscrowStatus.valueOf(data[4]));
                projects.add(p);
            }
        }
    }

    private void saveProjects() {
        List<String> records = new ArrayList<>();

        for (EscrowProject p : projects) {
            records.add(
                    p.getProjectId() + "|" +
                            p.getClientUpi() + "|" +
                            p.getFreelancerUpi() + "|" +
                            p.getTotalAmountPaise() + "|" +
                            p.getStatus()
            );
        }

        FileStorage.writeLines(FILE, records);
    }

    public synchronized void createProject(EscrowProject project)
            throws InvalidEscrowException {

        if (project == null ||
                !InputValidator.isValidUpi(project.getClientUpi()) ||
                !InputValidator.isValidUpi(project.getFreelancerUpi()) ||
                !InputValidator.isValidAmount(project.getTotalAmountPaise()))
            throw new InvalidEscrowException("Invalid project details");

        if (ESCROW_UPI.equalsIgnoreCase(project.getClientUpi()) ||
                ESCROW_UPI.equalsIgnoreCase(project.getFreelancerUpi()))
            throw new InvalidEscrowException("Invalid project participant");

        for (EscrowProject old : projects) {
            if (old.getProjectId() == project.getProjectId())
                throw new InvalidEscrowException("Project ID already exists");
        }

        if (walletService.getUser(project.getClientUpi()) == null ||
                walletService.getUser(project.getFreelancerUpi()) == null)
            throw new InvalidEscrowException("User is not registered");

        projects.add(project);

        try {
            saveProjects();
            System.out.println("Escrow project created successfully!");
        } catch (RuntimeException e) {
            projects.remove(project);
            throw new InvalidEscrowException(
                    "Unable to save project: " + e.getMessage());
        }
    }

    public synchronized void fundProject(int projectId)
            throws InvalidEscrowException {

        EscrowProject project = getProject(projectId);

        if (project.getStatus() != EscrowStatus.CREATED)
            throw new InvalidEscrowException("Project cannot be funded");

        try {
            paymentService.transfer(
                    project.getClientUpi(),
                    ESCROW_UPI,
                    project.getTotalAmountPaise()
            );
        } catch (RuntimeException e) {
            throw new InvalidEscrowException(
                    "Funding failed: " + e.getMessage());
        }

        project.setStatus(EscrowStatus.FUNDED);

        try {
            saveProjects();
            System.out.println("Escrow project funded successfully!");
        } catch (RuntimeException e) {
            project.setStatus(EscrowStatus.CREATED);

            try {
                paymentService.transfer(
                        ESCROW_UPI,
                        project.getClientUpi(),
                        project.getTotalAmountPaise()
                );
            } catch (RuntimeException rollbackError) {
                throw new InvalidEscrowException(
                        "Project save failed and refund also failed: "
                                + rollbackError.getMessage());
            }

            throw new InvalidEscrowException(
                    "Project save failed. Funding was reversed.");
        }
    }

    public synchronized EscrowProject getProject(int projectId)
            throws InvalidEscrowException {

        for (EscrowProject p : projects)
            if (p.getProjectId() == projectId)
                return p;

        throw new InvalidEscrowException("Project not found");
    }

    public synchronized List<EscrowProject> getAllProjects() {
        return new ArrayList<>(projects);
    }

    public synchronized void saveProjectChanges() {
        saveProjects();
    }

}
