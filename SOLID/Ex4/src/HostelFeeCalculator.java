import java.util.*;

public class HostelFeeCalculator {

    private final BookingRepo repo;

    public HostelFeeCalculator(BookingRepo repo) {
        this.repo = repo;
    }

    public void process(BookingRequest req) {

        Money monthly = calculateMonthly(req);
        Money deposit = new Money(5000.00);

        ReceiptPrinter.print(req, monthly, deposit);

        String bookingId = "H-" + (7000 + new Random(1).nextInt(1000));
        repo.save(bookingId, req, monthly, deposit);
    }

    private Money calculateMonthly(BookingRequest req) {

        RoomPricingPolicy roomPolicy =
                RoomPricingRegistry.get(req.roomType);

        Money total = roomPolicy.basePrice();

        for (AddOn addOn : req.addOns) {
            AddOnPolicy policy =
                    PricingAddOn.get(addOn);

            total = total.plus(policy.addOnPrice());
        }

        return total;
    }
}