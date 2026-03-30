package com.bookmyshow.pricing;

import com.bookmyshow.model.Show;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.SeatStatus;

/**
 * Increases price based on seat occupancy (demand).
 * >75% occupied → 1.5x, >50% → 1.3x, >25% → 1.1x
 * Price can ONLY increase — never below base price.
 */
public class DemandPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Show show, Seat seat, double basePrice) {
        long totalSeats = show.getScreen().getSeats().size();
        long bookedSeats = show.getSeatStatusMap().values().stream()
                .filter(s -> s == SeatStatus.BOOKED || s == SeatStatus.LOCKED)
                .count();

        double occupancyRatio = (double) bookedSeats / totalSeats;
        double multiplier = 1.0;

        if (occupancyRatio > 0.75) {
            multiplier = 1.5;
        } else if (occupancyRatio > 0.50) {
            multiplier = 1.3;
        } else if (occupancyRatio > 0.25) {
            multiplier = 1.1;
        }

        return Math.max(basePrice, basePrice * multiplier);
    }
}
