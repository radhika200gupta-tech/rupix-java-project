
package com.rupix.model;

import com.rupix.escrow.EscrowStatus;

public class Milestone {

    private int milestoneId;
    private int projectId;
    private String description;
    private long amountPaise;
    private EscrowStatus status;

    public Milestone(int milestoneId, int projectId,
                     String description, long amountPaise) {

        this.milestoneId = milestoneId;
        this.projectId = projectId;
        this.description = description;
        this.amountPaise = amountPaise;
        this.status = EscrowStatus.PENDING;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getDescription() {
        return description;
    }

    public long getAmountPaise() {
        return amountPaise;
    }

    public EscrowStatus getStatus() {
        return status;
    }

    public void setStatus(EscrowStatus status) {
        this.status = status;
    }
}
