import java.util.*;

public class PricingAddOn {

    private static final Map<AddOn, AddOnPolicy> policies = new HashMap<>();

    static {
        policies.put(AddOn.MESS, new MessAddOn());
        policies.put(AddOn.LAUNDRY, new LaundryAddOn());
        policies.put(AddOn.GYM, new GymAddOn());
    }

    public static AddOnPolicy get(AddOn addOn) {
        return policies.get(addOn);
    }
}