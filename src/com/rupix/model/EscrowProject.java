
package com.rupix.model;

import com.rupix.escrow.EscrowStatus;

public class EscrowProject {

    private int projectId;
    private String clientUpi;
    private String freelancerUpi;
    private long totalAmountPaise;
    private EscrowStatus status;

    public EscrowProject(int projectId, String clientUpi,
                         String freelancerUpi, long totalAmountPaise) {

        this.projectId = projectId;
        this.clientUpi = clientUpi;
        this.freelancerUpi = freelancerUpi;
        this.totalAmountPaise = totalAmountPaise;
        this.status = EscrowStatus.CREATED;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getClientUpi() {
        return clientUpi;
    }

    public String getFreelancerUpi() {
        return freelancerUpi;
    }

    public long getTotalAmountPaise() {
        return totalAmountPaise;
    }

    public EscrowStatus getStatus() {
        return status;
    }

    public void setStatus(EscrowStatus status) {
        this.status = status;
    }
}
