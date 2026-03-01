package com.example.tickets;

import java.util.List;

/**
 * Service layer — no longer mutates tickets after creation.
 * "Updates" produce a new IncidentTicket via toBuilder().
 */
public class TicketService {

    public IncidentTicket createTicket(String id, String reporterEmail, String title) {
        return new IncidentTicket.Builder(id, reporterEmail, title)
                .priority("MEDIUM")
                .source("CLI")
                .customerVisible(false)
                .addTag("NEW")
                .build();
    }

    /**
     * Returns a brand-new ticket with CRITICAL priority and an ESCALATED tag.
     * The original ticket stays unchanged.
     */
    public IncidentTicket escalateToCritical(IncidentTicket t) {
        return t.toBuilder()
                .priority("CRITICAL")
                .addTag("ESCALATED")
                .build();
    }

    /**
     * Returns a brand-new ticket with the assignee set.
     */
    public IncidentTicket assign(IncidentTicket t, String assigneeEmail) {
        return t.toBuilder()
                .assigneeEmail(assigneeEmail)
                .build();
    }
}
