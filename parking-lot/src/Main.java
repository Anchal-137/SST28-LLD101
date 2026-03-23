import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        PricingPolicy pricing = new DefaultPricingPolicy(Map.of(
                SlotType.SMALL, 10,
                SlotType.MEDIUM, 20,
                SlotType.LARGE, 50
        ));

        BillingService billingService = new BillingService(pricing);
        SlotAssignmentStrategy strategy = new NearestSlotStrategy();

        List<ParkingSlot> slots = List.of(
                new ParkingSlot(1, SlotType.SMALL, 0),
                new ParkingSlot(2, SlotType.SMALL, 0),
                new ParkingSlot(3, SlotType.MEDIUM, 0),
                new ParkingSlot(4, SlotType.MEDIUM, 0),
                new ParkingSlot(5, SlotType.LARGE, 0),
                new ParkingSlot(6, SlotType.SMALL, 1),
                new ParkingSlot(7, SlotType.MEDIUM, 1),
                new ParkingSlot(8, SlotType.LARGE, 1)
        );

        ParkingLot parkingLot = new ParkingLot(slots, strategy, billingService);

        Gate gateA = new Gate(1, 0);
        Gate gateB = new Gate(2, 1);

        // Bike enters from ground floor
        Vehicle bike = new Vehicle("KA-01-1234", VehicleType.TWO_WHEELER);
        ParkingTicket t1 = parkingLot.entry(bike, gateA, LocalDateTime.of(2026, 3, 23, 10, 0));
        System.out.println("Bike parked -> Ticket: " + t1.getTicketId()
                + ", Slot: " + t1.getSlot().getSlotNumber()
                + " (" + t1.getSlot().getType() + ")");

        // Car enters from first floor
        Vehicle car = new Vehicle("KA-02-5678", VehicleType.CAR);
        ParkingTicket t2 = parkingLot.entry(car, gateB, LocalDateTime.of(2026, 3, 23, 10, 15));
        System.out.println("Car parked  -> Ticket: " + t2.getTicketId()
                + ", Slot: " + t2.getSlot().getSlotNumber()
                + " (" + t2.getSlot().getType() + ")");

        // Bus enters from ground floor
        Vehicle bus = new Vehicle("KA-03-9999", VehicleType.BUS);
        ParkingTicket t3 = parkingLot.entry(bus, gateA, LocalDateTime.of(2026, 3, 23, 11, 0));
        System.out.println("Bus parked  -> Ticket: " + t3.getTicketId()
                + ", Slot: " + t3.getSlot().getSlotNumber()
                + " (" + t3.getSlot().getType() + ")");

        // Exits
        Bill bill1 = parkingLot.exit(t1.getTicketId(), LocalDateTime.of(2026, 3, 23, 12, 30));
        System.out.println("\nBike bill -> Hours: " + bill1.getHoursParked()
                + ", SlotType: " + bill1.getTicket().getSlot().getType()
                + ", Amount: Rs." + bill1.getTotalAmount());

        Bill bill2 = parkingLot.exit(t2.getTicketId(), LocalDateTime.of(2026, 3, 23, 14, 15));
        System.out.println("Car bill  -> Hours: " + bill2.getHoursParked()
                + ", SlotType: " + bill2.getTicket().getSlot().getType()
                + ", Amount: Rs." + bill2.getTotalAmount());

        Bill bill3 = parkingLot.exit(t3.getTicketId(), LocalDateTime.of(2026, 3, 23, 12, 0));
        System.out.println("Bus bill  -> Hours: " + bill3.getHoursParked()
                + ", SlotType: " + bill3.getTicket().getSlot().getType()
                + ", Amount: Rs." + bill3.getTotalAmount());

        // Bike overflow to MEDIUM slot
        Vehicle bike2 = new Vehicle("KA-04-1111", VehicleType.TWO_WHEELER);
        Vehicle bike3 = new Vehicle("KA-04-2222", VehicleType.TWO_WHEELER);
        parkingLot.entry(bike2, gateA, LocalDateTime.of(2026, 3, 23, 13, 0));
        parkingLot.entry(bike3, gateA, LocalDateTime.of(2026, 3, 23, 13, 0));

        Vehicle bike4 = new Vehicle("KA-04-3333", VehicleType.TWO_WHEELER);
        ParkingTicket t4 = parkingLot.entry(bike4, gateA, LocalDateTime.of(2026, 3, 23, 13, 5));
        System.out.println("\nBike4 (overflow) -> Slot: " + t4.getSlot().getSlotNumber()
                + " (" + t4.getSlot().getType() + ") — billed at MEDIUM rate");
    }
}
