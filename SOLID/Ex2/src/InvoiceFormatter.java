import java.util.*;

public class InvoiceFormatter {

    public String format(
            String invId,
            List<OrderLine> lines,
            Map<String, MenuItem> menu,
            PricingResult result) {

        StringBuilder out = new StringBuilder();
        out.append("Invoice# ").append(invId).append("\n");

        for (OrderLine l : lines) {
            MenuItem item = menu.get(l.itemId);
            double lineTotal = item.price * l.qty;
            out.append(String.format("- %s x%d = %.2f\n", item.name, l.qty, lineTotal));
        }

        out.append(String.format("Subtotal: %.2f\n", result.subtotal));
        out.append(String.format("Tax(%.0f%%): %.2f\n", result.taxPct, result.tax));
        out.append(String.format("Discount: -%.2f\n", result.discount));
        out.append(String.format("TOTAL: %.2f\n", result.total));

        return out.toString();
    }
}