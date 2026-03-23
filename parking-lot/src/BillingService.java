import java.time.Duration;
import java.time.LocalDateTime;

public class BillingService {
    private final PricingPolicy pricingPolicy;

    public BillingService(PricingPolicy pricingPolicy) {
        this.pricingPolicy = pricingPolicy;
    }

    public Bill generateBill(ParkingTicket ticket, LocalDateTime exitTime) {
        long minutes = Duration.between(ticket.getEntryTime(), exitTime).toMinutes();
        long hours = (minutes + 59) / 60;
        if (hours == 0) hours = 1;

        int rate = pricingPolicy.ratePerHour(ticket.getSlot().getType());
        int totalAmount = (int) (hours * rate);

        return new Bill(ticket, exitTime, hours, totalAmount);
    }
}
