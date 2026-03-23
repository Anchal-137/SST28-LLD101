import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        RateStrategy pricing = new StandardRateStrategy(Map.of(
                SlotType.SMALL, 10,
                SlotType.MEDIUM, 20,
                SlotType.LARGE, 50
        ));

        InvoiceGenerator invoiceGenerator = new InvoiceGenerator(pricing);
        SpotAllocationStrategy strategy = new ClosestSpotStrategy();

        List<ParkingSpot> spots = List.of(
                new ParkingSpot(1, SlotType.SMALL, 0),
                new ParkingSpot(2, SlotType.SMALL, 0),
                new ParkingSpot(3, SlotType.MEDIUM, 0),
                new ParkingSpot(4, SlotType.MEDIUM, 0),
                new ParkingSpot(5, SlotType.LARGE, 0),
                new ParkingSpot(6, SlotType.SMALL, 1),
                new ParkingSpot(7, SlotType.MEDIUM, 1),
                new ParkingSpot(8, SlotType.LARGE, 1)
        );

        ParkingLot parkingLot = new ParkingLot(spots, strategy, invoiceGenerator);

        Gate gateA = new Gate(1, 0);
        Gate gateB = new Gate(2, 1);

        // Bike enters from ground floor
        Vehicle bike = new Vehicle("KA-01-1234", VehicleType.TWO_WHEELER);
        ParkingPass p1 = parkingLot.parkVehicle(bike, gateA, LocalDateTime.of(2026, 3, 23, 10, 0));
        System.out.println("Bike parked -> Pass: " + p1.getPassId()
                + ", Spot: " + p1.getSpot().getSpotNumber()
                + " (" + p1.getSpot().getType() + ")");

        // Car enters from first floor
        Vehicle car = new Vehicle("KA-02-5678", VehicleType.CAR);
        ParkingPass p2 = parkingLot.parkVehicle(car, gateB, LocalDateTime.of(2026, 3, 23, 10, 15));
        System.out.println("Car parked  -> Pass: " + p2.getPassId()
                + ", Spot: " + p2.getSpot().getSpotNumber()
                + " (" + p2.getSpot().getType() + ")");

        // Bus enters from ground floor
        Vehicle bus = new Vehicle("KA-03-9999", VehicleType.BUS);
        ParkingPass p3 = parkingLot.parkVehicle(bus, gateA, LocalDateTime.of(2026, 3, 23, 11, 0));
        System.out.println("Bus parked  -> Pass: " + p3.getPassId()
                + ", Spot: " + p3.getSpot().getSpotNumber()
                + " (" + p3.getSpot().getType() + ")");

        // Exits
        Invoice inv1 = parkingLot.releaseVehicle(p1.getPassId(), LocalDateTime.of(2026, 3, 23, 12, 30));
        System.out.println("\nBike invoice -> Hours: " + inv1.getDuration()
                + ", SpotType: " + inv1.getPass().getSpot().getType()
                + ", Charge: Rs." + inv1.getCharge());

        Invoice inv2 = parkingLot.releaseVehicle(p2.getPassId(), LocalDateTime.of(2026, 3, 23, 14, 15));
        System.out.println("Car invoice  -> Hours: " + inv2.getDuration()
                + ", SpotType: " + inv2.getPass().getSpot().getType()
                + ", Charge: Rs." + inv2.getCharge());

        Invoice inv3 = parkingLot.releaseVehicle(p3.getPassId(), LocalDateTime.of(2026, 3, 23, 12, 0));
        System.out.println("Bus invoice  -> Hours: " + inv3.getDuration()
                + ", SpotType: " + inv3.getPass().getSpot().getType()
                + ", Charge: Rs." + inv3.getCharge());

        // Bike overflow to MEDIUM spot
        Vehicle bike2 = new Vehicle("KA-04-1111", VehicleType.TWO_WHEELER);
        Vehicle bike3 = new Vehicle("KA-04-2222", VehicleType.TWO_WHEELER);
        parkingLot.parkVehicle(bike2, gateA, LocalDateTime.of(2026, 3, 23, 13, 0));
        parkingLot.parkVehicle(bike3, gateA, LocalDateTime.of(2026, 3, 23, 13, 0));

        Vehicle bike4 = new Vehicle("KA-04-3333", VehicleType.TWO_WHEELER);
        ParkingPass p4 = parkingLot.parkVehicle(bike4, gateA, LocalDateTime.of(2026, 3, 23, 13, 5));
        System.out.println("\nBike4 (overflow) -> Spot: " + p4.getSpot().getSpotNumber()
                + " (" + p4.getSpot().getType() + ") — charged at MEDIUM rate");
    }
}
