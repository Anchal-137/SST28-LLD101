import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParkingLot {
    private final List<ParkingSlot> slots;
    private final SlotAssignmentStrategy assignmentStrategy;
    private final BillingService billingService;
    private final Map<String, ParkingTicket> activeTickets;
    private int ticketCounter;

    public ParkingLot(List<ParkingSlot> slots, SlotAssignmentStrategy assignmentStrategy, BillingService billingService) {
        this.slots = new ArrayList<>(slots);
        this.assignmentStrategy = assignmentStrategy;
        this.billingService = billingService;
        this.activeTickets = new HashMap<>();
        this.ticketCounter = 0;
    }

    public ParkingTicket entry(Vehicle vehicle, Gate entryGate, LocalDateTime entryTime) {
        Optional<ParkingSlot> slotOpt = assignmentStrategy.findSlot(slots, vehicle.getType(), entryGate);
        if (slotOpt.isEmpty()) {
            throw new IllegalStateException("No available slot for vehicle: " + vehicle.getLicensePlate());
        }

        ParkingSlot slot = slotOpt.get();
        slot.occupy();

        String ticketId = "T-" + (++ticketCounter);
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, slot, entryTime);
        activeTickets.put(ticketId, ticket);
        return ticket;
    }

    public Bill exit(String ticketId, LocalDateTime exitTime) {
        ParkingTicket ticket = activeTickets.remove(ticketId);
        if (ticket == null) {
            throw new IllegalArgumentException("Invalid ticket: " + ticketId);
        }

        ticket.getSlot().free();
        return billingService.generateBill(ticket, exitTime);
    }
}
