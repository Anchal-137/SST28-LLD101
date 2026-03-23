import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ParkingLot {
    private final List<ParkingSpot> spots;
    private final SpotAllocationStrategy allocationStrategy;
    private final InvoiceGenerator invoiceGenerator;
    private final Map<String, ParkingPass> activePasses;
    private int passCounter;

    public ParkingLot(List<ParkingSpot> spots, SpotAllocationStrategy allocationStrategy, InvoiceGenerator invoiceGenerator) {
        this.spots = new ArrayList<>(spots);
        this.allocationStrategy = allocationStrategy;
        this.invoiceGenerator = invoiceGenerator;
        this.activePasses = new HashMap<>();
        this.passCounter = 0;
    }

    public ParkingPass parkVehicle(Vehicle vehicle, Gate entryGate, LocalDateTime entryTime) {
        Optional<ParkingSpot> spotOpt = allocationStrategy.allocateSpot(spots, vehicle.getType(), entryGate);
        if (spotOpt.isEmpty()) {
            throw new IllegalStateException("No available spot for vehicle: " + vehicle.getNumberPlate());
        }

        ParkingSpot spot = spotOpt.get();
        spot.reserve();

        String passId = "P-" + (++passCounter);
        ParkingPass pass = new ParkingPass(passId, vehicle, spot, entryTime);
        activePasses.put(passId, pass);
        return pass;
    }

    public Invoice releaseVehicle(String passId, LocalDateTime exitTime) {
        ParkingPass pass = activePasses.remove(passId);
        if (pass == null) {
            throw new IllegalArgumentException("Invalid pass: " + passId);
        }

        pass.getSpot().release();
        return invoiceGenerator.createInvoice(pass, exitTime);
    }
}
