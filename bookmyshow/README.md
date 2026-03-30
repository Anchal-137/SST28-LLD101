# BookMyShow - Movie Ticket Booking System

LLD for a movie ticket booking system in Java. Covers admin operations (add movies, theatres, shows), customer flows (browse, book, cancel), concurrency-safe seat locking, dynamic pricing, and payment handling.

## How to Run

```bash
cd bookmyshow/src
javac com/bookmyshow/**/*.java com/bookmyshow/App.java
java com.bookmyshow.App
```

## What's Implemented

- Add movies, theatres with multiple screens, shows (with overlap validation)
- Browse theatres/movies by city, view seat layout
- Book tickets with seat locking (5-min timeout), cancel with refund
- Concurrent booking handled via synchronized blocks on Show object
- Dynamic pricing: demand-based surge + weekend surcharge (price only goes up)
- Payment via UPI / Credit Card / Debit Card (Factory Pattern)
- Multi-threaded race condition test in main driver

## Design Patterns

- **Strategy** - `PricingStrategy` with `DemandPricingStrategy`, `WeekendPricingStrategy`, `CompositePricingStrategy`
- **Factory** - `PaymentGatewayFactory` picks the right gateway based on payment method
- **Locking** - `SeatLockManager` uses `synchronized(show)` to prevent double booking

## Class Diagram

```
    ┌───────────────┐    ┌────────────────┐    ┌───────────────────┐
    │  <<enum>>     │    │   <<enum>>     │    │    <<enum>>       │
    │  SeatType     │    │  SeatStatus    │    │  BookingStatus    │
    ├───────────────┤    ├────────────────┤    ├───────────────────┤
    │ SILVER        │    │ AVAILABLE      │    │ PENDING           │
    │ GOLD          │    │ LOCKED         │    │ CONFIRMED         │
    │ PLATINUM      │    │ BOOKED         │    │ CANCELLED         │
    └───────────────┘    └────────────────┘    └───────────────────┘

    ┌────────────────┐   ┌─────────────────┐
    │  <<enum>>      │   │   <<enum>>      │
    │ PaymentMethod  │   │ PaymentStatus   │
    ├────────────────┤   ├─────────────────┤
    │ UPI            │   │ PENDING         │
    │ CREDIT_CARD    │   │ SUCCESS         │
    │ DEBIT_CARD     │   │ FAILED          │
    └────────────────┘   │ REFUNDED        │
                         └─────────────────┘

    ┌─────────────────────┐      ┌─────────────────────────┐
    │        User          │      │         Movie            │
    ├─────────────────────┤      ├─────────────────────────┤
    │- userId: String      │      │- movieId: String         │
    │- name: String        │      │- title: String           │
    │- email: String       │      │- genre: String           │
    │- isAdmin: boolean    │      │- durationMinutes: int    │
    └─────────────────────┘      └─────────────────────────┘

    ┌───────────────────────┐      ┌──────────────────────────┐
    │        Seat            │      │        Screen             │
    ├───────────────────────┤      ├──────────────────────────┤
    │- seatId: String        │◄─────│- screenId: String        │
    │- rowNumber: int        │      │- name: String            │
    │- seatNumber: int       │      │- seats: List<Seat>       │
    │- seatType: SeatType    │      └──────────┬───────────────┘
    └───────────────────────┘                  │ has many
                                               ▼
    ┌──────────────────────────────────────────────────────┐
    │                     Theatre                           │
    ├──────────────────────────────────────────────────────┤
    │- theatreId: String                                    │
    │- name: String                                         │
    │- city: String                                         │
    │- screens: List<Screen>                                │
    ├──────────────────────────────────────────────────────┤
    │+ addScreen(Screen): void                              │
    └──────────────────────────────────────────────────────┘

    ┌────────────────────────────────────────────────────────┐
    │                        Show                             │
    ├────────────────────────────────────────────────────────┤
    │- showId: String                                         │
    │- movie: Movie                                           │
    │- screen: Screen                                         │
    │- theatre: Theatre                                       │
    │- startTime / endTime: LocalDateTime                     │
    │- seatStatusMap: ConcurrentHashMap<String, SeatStatus>   │
    ├────────────────────────────────────────────────────────┤
    │+ getSeatStatus(seatId): SeatStatus                      │
    │+ overlaps(start, end): boolean                          │
    └────────────────────────────────────────────────────────┘
            │                           │
            ▼                           ▼
    ┌─────────────────────────────┐  ┌────────────────────────────┐
    │        Booking              │  │        Payment              │
    ├─────────────────────────────┤  ├────────────────────────────┤
    │- bookingId: String          │  │- paymentId: String          │
    │- customer: User             │  │- amount: double             │
    │- show: Show                 │  │- method: PaymentMethod      │
    │- bookedSeats: List<Seat>    │  │- status: PaymentStatus      │
    │- status: BookingStatus      │  │- timestamp: LocalDateTime   │
    │- payment: Payment ──────────┼─▶└────────────────────────────┘
    │- createdAt: LocalDateTime   │
    └─────────────────────────────┘

    ┌───────────────────────────────────────┐
    │    <<interface>>                       │
    │    PricingStrategy                     │
    ├───────────────────────────────────────┤
    │+ calculatePrice(show, seat, base):    │
    │                            double     │
    └───────────────────────────────────────┘
               ▲           ▲           ▲
               │           │           │
    ┌──────────┴──┐ ┌──────┴──────┐ ┌──┴──────────────────┐
    │  Demand     │ │  Weekend    │ │  Composite          │
    │  Pricing    │ │  Pricing    │ │  PricingStrategy     │
    │  Strategy   │ │  Strategy   │ │                      │
    ├─────────────┤ ├─────────────┤ ├──────────────────────┤
    │ >75%: 1.5x  │ │ Sat/Sun     │ │ chains strategies    │
    │ >50%: 1.3x  │ │  -> 1.2x   │ │ takes MAX price      │
    │ >25%: 1.1x  │ └─────────────┘ └──────────────────────┘
    └─────────────┘

    ┌───────────────────────────────────────┐
    │    <<interface>>                       │
    │    PaymentGateway                      │
    ├───────────────────────────────────────┤
    │+ processPayment(Payment): boolean     │
    │+ processRefund(Payment): boolean      │
    └───────────────────────────────────────┘
               ▲           ▲           ▲
               │           │           │
    ┌──────────┴──┐ ┌──────┴──────┐ ┌──┴──────────┐
    │ UpiGateway  │ │ CreditCard  │ │ DebitCard    │
    │             │ │ Gateway     │ │ Gateway      │
    └─────────────┘ └─────────────┘ └──────────────┘
                         ▲
                         │ creates
    ┌────────────────────┴──────────────────┐
    │    PaymentGatewayFactory               │
    ├───────────────────────────────────────┤
    │+ getGateway(method): PaymentGateway   │
    └───────────────────────────────────────┘

    ┌────────────────────────────────────────────────────┐
    │              SeatLockManager                        │
    ├────────────────────────────────────────────────────┤
    │- lockMap: ConcurrentHashMap<String, LockInfo>      │
    │- LOCK_TIMEOUT_MINUTES: 5                           │
    ├────────────────────────────────────────────────────┤
    │+ lockSeats(show, seats, userId): boolean           │
    │+ confirmSeats(show, seats): void                   │
    │+ releaseSeats(show, seats): void                   │
    └────────────────────────────────────────────────────┘

    ┌────────────────────────────────────────────────────────┐
    │                   BookingService                        │
    ├────────────────────────────────────────────────────────┤
    │- lockManager: SeatLockManager                          │
    │- pricingStrategy: PricingStrategy                      │
    │- basePrices: Map<SeatType, Double>                     │
    │- bookingStore: ConcurrentHashMap<String, Booking>      │
    ├────────────────────────────────────────────────────────┤
    │+ bookTickets(user, show, seats, method): Booking       │
    │+ cancelBooking(bookingId): void                        │
    └────────────────────────────────────────────────────────┘
```

## Sequence - Booking Flow

```
Customer -> BookingService.bookTickets()
              -> SeatLockManager.lockSeats()        [synchronized on show]
              -> PricingStrategy.calculatePrice()    [for each seat]
              -> PaymentGatewayFactory.getGateway()
              -> gateway.processPayment()
              -> if fail: releaseSeats() + throw
              -> SeatLockManager.confirmSeats()      [LOCKED -> BOOKED]
              <- return Booking
```

## Assumptions

- In-memory storage (HashMaps), no real DB
- Payment gateways just simulate success
- Single JVM, not distributed
- Lock timeout = 5 minutes
