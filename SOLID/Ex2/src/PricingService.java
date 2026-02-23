import java.util.*;

public class PricingService {

    public PricingResult calculate(
            String customerType,
            List<OrderLine> lines,
            Map<String, MenuItem> menu,
            Tax taxPolicy,
            DiscountPolicy discountPolicy) {

        double subtotal = 0.0;

        for (OrderLine l : lines) {
            MenuItem item = menu.get(l.itemId);
            subtotal += item.price * l.qty;
        }

        double taxPct = taxPolicy.taxPercent(customerType);
        double tax = subtotal * (taxPct / 100.0);

        double discount = discountPolicy.discount(customerType, subtotal, lines);
        double total = subtotal + tax - discount;

        return new PricingResult(subtotal, taxPct, tax, discount, total);
    }
}