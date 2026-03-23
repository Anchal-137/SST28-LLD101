import java.util.List;
import java.util.Optional;

public interface SlotAssignmentStrategy {
    Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, VehicleType vehicleType, Gate entryGate);
}
