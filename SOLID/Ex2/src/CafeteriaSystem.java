import java.util.*;

public class CafeteriaSystem {

    private final Map<String, MenuItem> menu = new LinkedHashMap<>();
    private final InvoiceRepo repository;
    private final PricingService pricingService;
    private final Tax taxPolicy;
    private final DiscountPolicy discountPolicy;
    private final InvoiceFormatter formatter;

    private int invoiceSeq = 1000;

    public CafeteriaSystem(
            InvoiceRepo repository,
            PricingService pricingService,
            Tax taxPolicy,
            DiscountPolicy discountPolicy,
            InvoiceFormatter formatter) {

        this.repository = repository;
        this.pricingService = pricingService;
        this.taxPolicy = taxPolicy;
        this.discountPolicy = discountPolicy;
        this.formatter = formatter;
    }

    public void addToMenu(MenuItem i) {
        menu.put(i.id, i);
    }

    public void checkout(String customerType, List<OrderLine> lines) {

        String invId = "INV-" + (++invoiceSeq);

        PricingResult result = pricingService.calculate(
                customerType, lines, menu, taxPolicy, discountPolicy);

        String printable = formatter.format(invId, lines, menu, result);

        System.out.print(printable);

        repository.save(invId, printable);

        System.out.println("Saved invoice: " + invId +
                " (lines=" + repository.countLines(invId) + ")");
    }
}