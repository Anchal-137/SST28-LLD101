import java.util.HashMap;
import java.util.Map;

public class RoomPricingRegistry {

    private static final Map<Integer, RoomPricingPolicy> policies = new HashMap<>();

    static {
        policies.put(LegacyRoomTypes.SINGLE, new PricingSingleRoom());
        policies.put(LegacyRoomTypes.DOUBLE, new PricingDoubleRoom());
        policies.put(LegacyRoomTypes.TRIPLE, new PricingTripleRoom());
        policies.put(LegacyRoomTypes.DELUXE, new PricingDoubleRoom());
    }

    public static RoomPricingPolicy get(int roomType) {
        return policies.get(roomType);
    }
}