import java.util.List;
import java.util.Optional;

public class ClosestSpotStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<ParkingSpot> allocateSpot(List<ParkingSpot> spots, VehicleType vehicleType, Gate entryGate) {
        List<SlotType> compatible = VehicleSpotMapper.getAllowedSpots(vehicleType);

        return spots.stream()
                .filter(s -> !s.isReserved())
                .filter(s -> compatible.contains(s.getType()))
                .min((a, b) -> {
                    int distA = Math.abs(a.getFloor() - entryGate.getFloor()) * 1000 + a.getSpotNumber();
                    int distB = Math.abs(b.getFloor() - entryGate.getFloor()) * 1000 + b.getSpotNumber();
                    return Integer.compare(distA, distB);
                });
    }
}
