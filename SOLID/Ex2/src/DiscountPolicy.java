import java.util.List;

public class DiscountPolicy implements Discount {

    @Override
    public double discount(String customerType,
                           double subtotal,
                           List<OrderLine> lines) {

        int distinctLines = lines.size();

        if ("student".equalsIgnoreCase(customerType)) {
            if (subtotal >= 180.0) return 10.0;
            return 0.0;
        }

        if ("staff".equalsIgnoreCase(customerType)) {
            if (distinctLines >= 3) return 15.0;
            return 5.0;
        }

        return 0.0;
    }
}