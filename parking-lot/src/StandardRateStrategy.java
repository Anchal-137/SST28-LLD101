import java.util.Map;

public class StandardRateStrategy implements RateStrategy {
    private final Map<SlotType, Integer> priceMap;

    public StandardRateStrategy(Map<SlotType, Integer> priceMap) {
        this.priceMap = priceMap;
    }

    @Override
    public int getHourlyRate(SlotType slotType) {
        Integer rate = priceMap.get(slotType);
        if (rate == null) throw new IllegalArgumentException("No rate for slot type: " + slotType);
        return rate;
    }
}
