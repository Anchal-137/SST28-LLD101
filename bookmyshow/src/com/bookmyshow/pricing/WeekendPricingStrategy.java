package com.bookmyshow.pricing;

import com.bookmyshow.model.Show;
import com.bookmyshow.model.Seat;

import java.time.DayOfWeek;

/**
 * Increases price on weekends (Saturday/Sunday) by 1.2x.
 * Price can ONLY increase — never below base price.
 */
public class WeekendPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Show show, Seat seat, double basePrice) {
        DayOfWeek day = show.getStartTime().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return Math.max(basePrice, basePrice * 1.2);
        }
        return basePrice;
    }
}
