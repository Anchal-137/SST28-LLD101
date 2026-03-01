import com.example.tickets.IncidentTicket;
import com.example.tickets.TicketService;

import java.util.List;

/**
 * Demo showing immutability in action.
 *
 * After refactor:
 * - No setters exist, so direct mutation won't compile.
 * - External list modifications don't affect the ticket.
 * - Service "updates" return new instances.
 */
public class TryIt {

    public static void main(String[] args) {
        TicketService service = new TicketService();

        IncidentTicket t = service.createTicket("TCK-1001", "reporter@example.com", "Payment failing on checkout");
        System.out.println("Created: " + t);

        // updates produce new tickets — original stays the same
        IncidentTicket assigned = service.assign(t, "agent@example.com");
        IncidentTicket escalated = service.escalateToCritical(assigned);
        System.out.println("\nAfter service operations (new instance): " + escalated);
        System.out.println("Original is unchanged: " + t);

        // tags list is unmodifiable — external mutation has no effect
        List<String> tags = escalated.getTags();
        try {
            tags.add("HACKED_FROM_OUTSIDE");
            System.out.println("BUG: should not reach here");
        } catch (UnsupportedOperationException e) {
            System.out.println("\nExternal tag mutation blocked (UnsupportedOperationException)");
        }

        System.out.println("Tags still safe: " + escalated.getTags());
    }
}
