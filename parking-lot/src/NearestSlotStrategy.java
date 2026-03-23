import java.util.List;
import java.util.Optional;

public class NearestSlotStrategy implements SlotAssignmentStrategy {

    @Override
    public Optional<ParkingSlot> findSlot(List<ParkingSlot> slots, VehicleType vehicleType, Gate entryGate) {
        List<SlotType> compatible = VehicleSlotCompatibility.compatibleSlots(vehicleType);

        return slots.stream()
                .filter(s -> !s.isOccupied())
                .filter(s -> compatible.contains(s.getType()))
                .min((a, b) -> {
                    int distA = Math.abs(a.getFloor() - entryGate.getFloor()) * 1000 + a.getSlotNumber();
                    int distB = Math.abs(b.getFloor() - entryGate.getFloor()) * 1000 + b.getSlotNumber();
                    return Integer.compare(distA, distB);
                });
    }
}
