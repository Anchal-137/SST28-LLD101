import java.util.Map;

public class DefaultPricingPolicy implements PricingPolicy {
    private final Map<SlotType, Integer> rates;

    public DefaultPricingPolicy(Map<SlotType, Integer> rates) {
        this.rates = rates;
    }

    @Override
    public int ratePerHour(SlotType slotType) {
        Integer rate = rates.get(slotType);
        if (rate == null) throw new IllegalArgumentException("No rate for slot type: " + slotType);
        return rate;
    }
}
