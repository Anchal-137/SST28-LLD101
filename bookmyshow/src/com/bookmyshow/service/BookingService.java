package com.bookmyshow.service;

import com.bookmyshow.exception.PaymentFailedException;
import com.bookmyshow.exception.SeatNotAvailableException;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.*;
import com.bookmyshow.payment.PaymentGateway;
import com.bookmyshow.payment.PaymentGatewayFactory;
import com.bookmyshow.pricing.PricingStrategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core booking service that handles:
 * 1. Seat locking (concurrency-safe)
 * 2. Price calculation via strategy
 * 3. Payment via factory-selected gateway
 * 4. Booking confirmation / rollback
 * 5. Cancellation with refund to original payment method
 */
public class BookingService {
    private final SeatLockManager lockManager;
    private final PricingStrategy pricingStrategy;
    private final Map<String, Booking> bookingStore = new ConcurrentHashMap<>();
    private final Map<SeatType, Double> basePrices;
    private final AtomicInteger bookingCounter = new AtomicInteger(0);
    private final AtomicInteger paymentCounter = new AtomicInteger(0);

    public BookingService(SeatLockManager lockManager, PricingStrategy pricingStrategy,
                          Map<SeatType, Double> basePrices) {
        this.lockManager = lockManager;
        this.pricingStrategy = pricingStrategy;
        this.basePrices = basePrices;
    }

    /**
     * Full booking flow:
     * 1. Lock seats → 2. Calculate price → 3. Process payment → 4. Confirm booking
     * If payment fails → release seats and throw exception.
     */
    public Booking bookTickets(User customer, Show show, List<Seat> seats,
                               PaymentMethod paymentMethod) {
        // Step 1: Lock seats
        boolean locked = lockManager.lockSeats(show, seats, customer.getUserId());
        if (!locked) {
            throw new SeatNotAvailableException(
                    "One or more seats are not available for show: " + show.getShowId());
        }
        System.out.println("  [LOCK] Seats locked for " + customer.getName());

        // Step 2: Calculate total price
        double totalPrice = 0;
        for (Seat seat : seats) {
            double base = basePrices.getOrDefault(seat.getSeatType(), 200.0);
            totalPrice += pricingStrategy.calculatePrice(show, seat, base);
        }
        System.out.println("  [PRICE] Total: Rs." + String.format("%.2f", totalPrice));

        // Step 3: Process payment
        String paymentId = "PAY-" + paymentCounter.incrementAndGet();
        Payment payment = new Payment(paymentId, totalPrice, paymentMethod);

        PaymentGateway gateway = PaymentGatewayFactory.getGateway(paymentMethod);
        boolean paymentSuccess = gateway.processPayment(payment);

        if (!paymentSuccess) {
            lockManager.releaseSeats(show, seats);
            throw new PaymentFailedException("Payment failed for booking attempt");
        }

        // Step 4: Confirm booking
        String bookingId = "BKG-" + bookingCounter.incrementAndGet();
        Booking booking = new Booking(bookingId, customer, show, seats);
        booking.setPayment(payment);
        booking.setStatus(BookingStatus.CONFIRMED);
        lockManager.confirmSeats(show, seats);
        bookingStore.put(bookingId, booking);

        System.out.println("  [BOOKING] Confirmed: " + booking);
        return booking;
    }

    /**
     * Cancel booking:
     * 1. Release seats → 2. Refund to original payment method → 3. Mark cancelled
     */
    public void cancelBooking(String bookingId) {
        Booking booking = bookingStore.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("  [CANCEL] Booking already cancelled: " + bookingId);
            return;
        }

        // Release seats
        lockManager.releaseSeats(booking.getShow(), booking.getBookedSeats());

        // Refund to original payment method
        Payment payment = booking.getPayment();
        PaymentGateway gateway = PaymentGatewayFactory.getGateway(payment.getMethod());
        gateway.processRefund(payment);

        booking.setStatus(BookingStatus.CANCELLED);
        System.out.println("  [CANCEL] Booking cancelled: " + bookingId);
    }

    public Booking getBooking(String bookingId) {
        return bookingStore.get(bookingId);
    }
}
