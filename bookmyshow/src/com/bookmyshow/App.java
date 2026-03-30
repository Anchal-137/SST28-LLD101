package com.bookmyshow;

import com.bookmyshow.exception.SeatNotAvailableException;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.*;
import com.bookmyshow.pricing.*;
import com.bookmyshow.service.*;

import java.time.LocalDateTime;
import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("           BookMyShow - Movie Ticket Booking System         ");
        System.out.println("==========================================================\n");

        // --- Initialize services ---
        SeatLockManager lockManager = new SeatLockManager();

        PricingStrategy pricing = new CompositePricingStrategy(List.of(
                new DemandPricingStrategy(),
                new WeekendPricingStrategy()
        ));

        Map<SeatType, Double> basePrices = Map.of(
                SeatType.SILVER, 150.0,
                SeatType.GOLD, 250.0,
                SeatType.PLATINUM, 400.0
        );

        MovieService movieService = new MovieService();
        TheatreService theatreService = new TheatreService();
        ShowService showService = new ShowService();
        BookingService bookingService = new BookingService(lockManager, pricing, basePrices);

        // --- Users ---
        User admin = new User("U1", "Admin Raj", "raj@admin.com", true);
        User alice = new User("U2", "Alice", "alice@gmail.com", false);
        User bob = new User("U3", "Bob", "bob@gmail.com", false);

        // ================================================
        //  1. ADMIN: Add Movies
        // ================================================
        System.out.println("--- 1. Admin adds movies ---");
        Movie m1 = new Movie("MOV1", "Inception", "Sci-Fi", 148);
        Movie m2 = new Movie("MOV2", "The Dark Knight", "Action", 152);
        movieService.addMovie(m1);
        movieService.addMovie(m2);

        // ================================================
        //  2. ADMIN: Add Theatre with Screens & Seats
        // ================================================
        System.out.println("\n--- 2. Admin adds theatre ---");
        List<Seat> screen1Seats = new ArrayList<>();
        screen1Seats.add(new Seat("S1-R1-1", 1, 1, SeatType.SILVER));
        screen1Seats.add(new Seat("S1-R1-2", 1, 2, SeatType.SILVER));
        screen1Seats.add(new Seat("S1-R2-1", 2, 1, SeatType.GOLD));
        screen1Seats.add(new Seat("S1-R2-2", 2, 2, SeatType.GOLD));
        screen1Seats.add(new Seat("S1-R3-1", 3, 1, SeatType.PLATINUM));
        screen1Seats.add(new Seat("S1-R3-2", 3, 2, SeatType.PLATINUM));

        Screen screen1 = new Screen("SCR1", "Screen 1", screen1Seats);

        List<Seat> screen2Seats = new ArrayList<>();
        screen2Seats.add(new Seat("S2-R1-1", 1, 1, SeatType.SILVER));
        screen2Seats.add(new Seat("S2-R1-2", 1, 2, SeatType.SILVER));
        screen2Seats.add(new Seat("S2-R2-1", 2, 1, SeatType.GOLD));
        screen2Seats.add(new Seat("S2-R2-2", 2, 2, SeatType.GOLD));

        Screen screen2 = new Screen("SCR2", "Screen 2", screen2Seats);

        Theatre theatre = new Theatre("TH1", "PVR Cinemas", "Bangalore",
                new ArrayList<>(List.of(screen1, screen2)));
        theatreService.addTheatre(theatre);

        // ================================================
        //  3. ADMIN: Add Shows (with overlap validation)
        // ================================================
        System.out.println("\n--- 3. Admin adds shows ---");
        Show show1 = showService.addShow("SH1", m1, screen1, theatre,
                LocalDateTime.of(2026, 4, 5, 14, 0));  // Saturday

        Show show2 = showService.addShow("SH2", m2, screen1, theatre,
                LocalDateTime.of(2026, 4, 5, 18, 0));  // Saturday evening

        Show show3 = showService.addShow("SH3", m1, screen2, theatre,
                LocalDateTime.of(2026, 4, 5, 14, 0));  // Same time, different screen -- OK

        // Test overlap detection
        System.out.println("\n--- 3b. Overlap test ---");
        try {
            showService.addShow("SH-BAD", m2, screen1, theatre,
                    LocalDateTime.of(2026, 4, 5, 15, 0));  // Overlaps with SH1
            System.out.println("ERROR: Should have thrown overlap exception!");
        } catch (Exception e) {
            System.out.println("[OK] Overlap detected: " + e.getMessage());
        }

        // ================================================
        //  4. CUSTOMER: Browse theatres & movies by city
        // ================================================
        System.out.println("\n--- 4. Customer browses ---");
        System.out.println("Theatres in Bangalore: " + theatreService.getTheatresByCity("Bangalore"));
        System.out.println("Movies in Bangalore: " + showService.getMoviesByCity("Bangalore"));
        System.out.println("Shows for Inception: " + showService.getShowsForMovie("MOV1"));

        // ================================================
        //  5. CUSTOMER: View Seat Layout
        // ================================================
        System.out.println("\n--- 5. Seat layout for Show SH1 ---");
        Map<String, SeatStatus> layout = showService.getSeatLayout("SH1");
        for (Seat seat : screen1Seats) {
            System.out.println("  " + seat + " -> " + layout.get(seat.getSeatId()));
        }

        // ================================================
        //  6. CUSTOMER: Book Tickets (Alice books 2 Gold seats)
        // ================================================
        System.out.println("\n--- 6. Alice books 2 Gold seats via UPI ---");
        List<Seat> aliceSeats = List.of(screen1Seats.get(2), screen1Seats.get(3)); // Gold seats
        Booking aliceBooking = bookingService.bookTickets(alice, show1, aliceSeats, PaymentMethod.UPI);

        // ================================================
        //  7. CONCURRENCY: Bob tries to book same seats
        // ================================================
        System.out.println("\n--- 7. Bob tries same Gold seats (concurrency test) ---");
        try {
            bookingService.bookTickets(bob, show1, aliceSeats, PaymentMethod.CREDIT_CARD);
            System.out.println("ERROR: Should have thrown SeatNotAvailableException!");
        } catch (SeatNotAvailableException e) {
            System.out.println("[OK] Blocked: " + e.getMessage());
        }

        // ================================================
        //  8. Bob books different seats successfully
        // ================================================
        System.out.println("\n--- 8. Bob books 2 Platinum seats via Credit Card ---");
        List<Seat> bobSeats = List.of(screen1Seats.get(4), screen1Seats.get(5)); // Platinum seats
        Booking bobBooking = bookingService.bookTickets(bob, show1, bobSeats, PaymentMethod.CREDIT_CARD);

        // ================================================
        //  9. Updated seat layout after bookings
        // ================================================
        System.out.println("\n--- 9. Updated seat layout for Show SH1 ---");
        layout = showService.getSeatLayout("SH1");
        for (Seat seat : screen1Seats) {
            System.out.println("  " + seat + " -> " + layout.get(seat.getSeatId()));
        }

        // ================================================
        //  10. CANCEL: Alice cancels -> refund to UPI
        // ================================================
        System.out.println("\n--- 10. Alice cancels her booking ---");
        bookingService.cancelBooking(aliceBooking.getBookingId());

        // Seats should be available again
        System.out.println("\n--- Seat layout after cancellation ---");
        layout = showService.getSeatLayout("SH1");
        for (Seat seat : screen1Seats) {
            System.out.println("  " + seat + " -> " + layout.get(seat.getSeatId()));
        }

        // ================================================
        //  11. CONCURRENCY: Multi-threaded booking test
        // ================================================
        System.out.println("\n--- 11. Multi-threaded concurrency test ---");
        List<Seat> contestedSeats = List.of(screen1Seats.get(0), screen1Seats.get(1)); // Silver seats

        Thread t1 = new Thread(() -> {
            try {
                Booking b = bookingService.bookTickets(alice, show1, contestedSeats, PaymentMethod.UPI);
                System.out.println("  >> Alice WON the race: " + b.getBookingId());
            } catch (SeatNotAvailableException e) {
                System.out.println("  >> Alice LOST the race: " + e.getMessage());
            }
        }, "Alice-Thread");

        Thread t2 = new Thread(() -> {
            try {
                Booking b = bookingService.bookTickets(bob, show1, contestedSeats, PaymentMethod.DEBIT_CARD);
                System.out.println("  >> Bob WON the race: " + b.getBookingId());
            } catch (SeatNotAvailableException e) {
                System.out.println("  >> Bob LOST the race: " + e.getMessage());
            }
        }, "Bob-Thread");

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Final layout
        System.out.println("\n--- Final seat layout for Show SH1 ---");
        layout = showService.getSeatLayout("SH1");
        for (Seat seat : screen1Seats) {
            System.out.println("  " + seat + " -> " + layout.get(seat.getSeatId()));
        }

        System.out.println("\n==========================================================");
        System.out.println("                    Simulation Complete                     ");
        System.out.println("==========================================================");
    }
}
