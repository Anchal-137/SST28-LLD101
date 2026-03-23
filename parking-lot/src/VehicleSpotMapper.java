import java.util.List;

public class VehicleSpotMapper {

    public static List<SlotType> getAllowedSpots(VehicleType vehicleType) {
        switch (vehicleType) {
            case TWO_WHEELER: return List.of(SlotType.SMALL, SlotType.MEDIUM, SlotType.LARGE);
            case CAR:         return List.of(SlotType.MEDIUM, SlotType.LARGE);
            case BUS:         return List.of(SlotType.LARGE);
            default: throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }
    }
}
