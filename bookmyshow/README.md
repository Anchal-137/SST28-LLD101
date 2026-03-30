# BookMyShow - Movie Ticket Booking System (LLD)

A production-grade low-level design of a movie ticket booking system built in Java, demonstrating SOLID principles, design patterns, thread-safe concurrency, and clean architecture.

## Features

- Admin: Add movies, theatres (with screens), and shows with overlap validation
- Customer: Browse theatres/movies by city, view seat layout, book & cancel tickets
- Concurrency-safe seat booking using synchronized locking with 5-minute timeout
- Dynamic pricing (demand-based + weekend surcharge) - prices can ONLY increase
- Payment gateway support (UPI, Credit Card, Debit Card) via Factory Pattern
- Cancellation with automatic refund to original payment method
- In-memory data store (ConcurrentHashMaps) - extendable to real DB

## Project Structure

```
bookmyshow/src/com/bookmyshow/
|
|-- model/                    # Domain entities
|   |-- User.java             # Admin / Customer
|   |-- Movie.java            # Movie details
|   |-- Theatre.java          # Theatre with screens
|   |-- Screen.java           # Screen with seats
|   |-- Seat.java             # Individual seat
|   |-- Show.java             # Movie + Screen + Time + Seat status map
|   |-- Booking.java          # Booking record
|   |-- Payment.java          # Payment record
|   |-- SeatType.java         # SILVER, GOLD, PLATINUM
|   |-- SeatStatus.java       # AVAILABLE, LOCKED, BOOKED
|   |-- BookingStatus.java    # PENDING, CONFIRMED, CANCELLED
|   |-- PaymentMethod.java    # UPI, CREDIT_CARD, DEBIT_CARD
|   |-- PaymentStatus.java    # PENDING, SUCCESS, FAILED, REFUNDED
|
|-- service/                  # Business logic
|   |-- MovieService.java     # CRUD for movies
|   |-- TheatreService.java   # CRUD for theatres, search by city
|   |-- ShowService.java      # Add shows (overlap check), search, seat layout
|   |-- BookingService.java   # Lock -> Price -> Pay -> Confirm / Cancel flow
|
|-- pricing/                  # Strategy Pattern for dynamic pricing
|   |-- PricingStrategy.java          # Interface
|   |-- DemandPricingStrategy.java    # Price surge based on occupancy
|   |-- WeekendPricingStrategy.java   # Weekend surcharge (1.2x)
|   |-- CompositePricingStrategy.java # Chains strategies, takes max price
|
|-- payment/                  # Factory Pattern for payment gateways
|   |-- PaymentGateway.java           # Interface
|   |-- UpiGateway.java               # UPI processor
|   |-- CreditCardGateway.java        # Credit card processor
|   |-- DebitCardGateway.java         # Debit card processor
|   |-- PaymentGatewayFactory.java    # Factory to select gateway
|
|-- lock/                     # Concurrency control
|   |-- SeatLockManager.java  # Temporary seat locks with expiry
|
|-- exception/                # Custom exceptions
|   |-- SeatNotAvailableException.java
|   |-- ShowOverlapException.java
|   |-- PaymentFailedException.java
|
|-- App.java                  # Main driver - full simulation
```

## Class Diagram

```
    ┌───────────────┐       ┌────────────────┐       ┌───────────────────┐
    │  <<enum>>     │       │   <<enum>>     │       │    <<enum>>       │
    │  SeatType     │       │  SeatStatus    │       │  BookingStatus    │
    ├───────────────┤       ├────────────────┤       ├───────────────────┤
    │ SILVER        │       │ AVAILABLE      │       │ PENDING           │
    │ GOLD          │       │ LOCKED         │       │ CONFIRMED         │
    │ PLATINUM      │       │ BOOKED         │       │ CANCELLED         │
    └───────────────┘       └────────────────┘       └───────────────────┘

    ┌────────────────┐      ┌─────────────────┐
    │  <<enum>>      │      │   <<enum>>      │
    │ PaymentMethod  │      │ PaymentStatus   │
    ├────────────────┤      ├─────────────────┤
    │ UPI            │      │ PENDING         │
    │ CREDIT_CARD    │      │ SUCCESS         │
    │ DEBIT_CARD     │      │ FAILED          │
    └────────────────┘      │ REFUNDED        │
                            └─────────────────┘

    ┌─────────────────────┐
    │        User          │
    ├─────────────────────┤
    │- userId: String      │
    │- name: String        │
    │- email: String       │
    │- isAdmin: boolean    │
    ├─────────────────────┤
    │+ isAdmin(): boolean  │
    └─────────────────────┘

    ┌─────────────────────────┐
    │         Movie            │
    ├─────────────────────────┤
    │- movieId: String         │
    │- title: String           │
    │- genre: String           │
    │- durationMinutes: int    │
    └─────────────────────────┘

    ┌───────────────────────┐      ┌──────────────────────────┐
    │        Seat            │      │        Screen             │
    ├───────────────────────┤      ├──────────────────────────┤
    │- seatId: String        │◄─────│- seats: List<Seat>       │
    │- rowNumber: int        │      │- screenId: String        │
    │- seatNumber: int       │      │- name: String            │
    │- seatType: SeatType    │      └──────────┬───────────────┘
    └───────────────────────┘                  │
                                               │ has many
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
    │+ getTheatresByCity(city): List<Theatre>                │
    └──────────────────────────────────────────────────────┘

    ┌───────────────────────────────────────────────────────────┐
    │                        Show                                │
    ├───────────────────────────────────────────────────────────┤
    │- showId: String                                            │
    │- movie: Movie                                              │
    │- screen: Screen                                            │
    │- theatre: Theatre                                          │
    │- startTime: LocalDateTime                                  │
    │- endTime: LocalDateTime                                    │
    │- seatStatusMap: ConcurrentHashMap<String, SeatStatus>      │
    ├───────────────────────────────────────────────────────────┤
    │+ getSeatStatus(seatId): SeatStatus                         │
    │+ overlaps(start, end): boolean                             │
    └───────────────────────────────────────────────────────────┘
            │                           │
            │ referenced by             │ referenced by
            ▼                           ▼
    ┌─────────────────────────────┐  ┌────────────────────────────┐
    │        Booking              │  │        Payment              │
    ├─────────────────────────────┤  ├────────────────────────────┤
    │- bookingId: String          │  │- paymentId: String          │
    │- customer: User             │  │- amount: double             │
    │- show: Show                 │  │- method: PaymentMethod      │
    │- bookedSeats: List<Seat>    │  │- status: PaymentStatus      │
    │- status: BookingStatus      │  │- timestamp: LocalDateTime   │
    │- payment: Payment      ─────┼─▶└────────────────────────────┘
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
    │ occupancy   │ │ Sat/Sun     │ │ chains all strategies│
    │ >75%: 1.5x  │ │ -> 1.2x    │ │ takes MAX price      │
    │ >50%: 1.3x  │ └─────────────┘ │ (never decreases)    │
    │ >25%: 1.1x  │                 └──────────────────────┘
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
    │+ getGateway(PaymentMethod):           │
    │                     PaymentGateway    │  (static factory)
    └───────────────────────────────────────┘

    ┌────────────────────────────────────────────────────┐
    │              SeatLockManager                        │
    ├────────────────────────────────────────────────────┤
    │- lockMap: ConcurrentHashMap<String, LockInfo>      │
    │- LOCK_TIMEOUT_MINUTES: 5                           │
    ├────────────────────────────────────────────────────┤
    │+ lockSeats(show, seats, userId): boolean           │  synchronized
    │+ confirmSeats(show, seats): void                   │  synchronized
    │+ releaseSeats(show, seats): void                   │  synchronized
    │+ cleanExpiredLocks(show): void                     │  synchronized
    └────────────────────────────────────────────────────┘

    ┌────────────────────────────────────────────────────────────┐
    │                   BookingService                            │
    ├────────────────────────────────────────────────────────────┤
    │- lockManager: SeatLockManager                              │
    │- pricingStrategy: PricingStrategy                          │
    │- basePrices: Map<SeatType, Double>                         │
    │- bookingStore: ConcurrentHashMap<String, Booking>          │
    ├────────────────────────────────────────────────────────────┤
    │+ bookTickets(user, show, seats, method): Booking           │
    │  [Lock -> Price -> Pay -> Confirm OR Rollback]             │
    │+ cancelBooking(bookingId): void                            │
    │  [Release seats -> Refund to original method]              │
    └────────────────────────────────────────────────────────────┘
```

## Sequence Diagram - Ticket Booking Flow

```
Customer          BookingService       SeatLockManager       PricingStrategy      PaymentGatewayFactory    PaymentGateway
  │                     │                    │                     │                      │                     │
  │  bookTickets()      │                    │                     │                      │                     │
  │────────────────────>│                    │                     │                      │                     │
  │                     │  lockSeats()       │                     │                      │                     │
  │                     │───────────────────>│                     │                      │                     │
  │                     │                    │──┐ synchronized     │                      │                     │
  │                     │                    │  │ check all seats  │                      │                     │
  │                     │                    │  │ AVAILABLE?       │                      │                     │
  │                     │                    │<─┘                  │                      │                     │
  │                     │   true/false       │                     │                      │                     │
  │                     │<───────────────────│                     │                      │                     │
  │                     │                    │                     │                      │                     │
  │                     │  [if false: throw SeatNotAvailableException]                    │                     │
  │                     │                    │                     │                      │                     │
  │                     │  calculatePrice()  │                     │                      │                     │
  │                     │────────────────────────────────────────>│                      │                     │
  │                     │                    │                     │──┐ demand + weekend  │                     │
  │                     │                    │                     │  │ take MAX price    │                     │
  │                     │   totalPrice       │                     │<─┘                  │                     │
  │                     │<────────────────────────────────────────│                      │                     │
  │                     │                    │                     │                      │                     │
  │                     │  getGateway()      │                     │                      │                     │
  │                     │────────────────────────────────────────────────────────────────>│                     │
  │                     │   gateway          │                     │                      │                     │
  │                     │<────────────────────────────────────────────────────────────────│                     │
  │                     │                    │                     │                      │                     │
  │                     │  processPayment()  │                     │                      │                     │
  │                     │─────────────────────────────────────────────────────────────────────────────────────>│
  │                     │   success/fail     │                     │                      │                     │
  │                     │<─────────────────────────────────────────────────────────────────────────────────────│
  │                     │                    │                     │                      │                     │
  │                     │  [if fail: releaseSeats() + throw PaymentFailedException]       │                     │
  │                     │                    │                     │                      │                     │
  │                     │  confirmSeats()    │                     │                      │                     │
  │                     │───────────────────>│                     │                      │                     │
  │                     │                    │──┐ LOCKED->BOOKED   │                      │                     │
  │                     │                    │<─┘                  │                      │                     │
  │                     │                    │                     │                      │                     │
  │   Booking           │                    │                     │                      │                     │
  │<────────────────────│                    │                     │                      │                     │
  │                     │                    │                     │                      │                     │
```

## Sequence Diagram - Cancellation & Refund Flow

```
Customer          BookingService       SeatLockManager       PaymentGatewayFactory    PaymentGateway
  │                     │                    │                      │                     │
  │  cancelBooking()    │                    │                      │                     │
  │────────────────────>│                    │                      │                     │
  │                     │  releaseSeats()    │                      │                     │
  │                     │───────────────────>│                      │                     │
  │                     │                    │──┐ BOOKED->AVAILABLE │                     │
  │                     │                    │<─┘                   │                     │
  │                     │                    │                      │                     │
  │                     │  getGateway(original method)              │                     │
  │                     │─────────────────────────────────────────>│                     │
  │                     │   gateway          │                      │                     │
  │                     │<─────────────────────────────────────────│                     │
  │                     │                    │                      │                     │
  │                     │  processRefund()   │                      │                     │
  │                     │────────────────────────────────────────────────────────────────>│
  │                     │   refunded         │                      │                     │
  │                     │<────────────────────────────────────────────────────────────────│
  │                     │                    │                      │                     │
  │                     │  status = CANCELLED│                      │                     │
  │   done              │                    │                      │                     │
  │<────────────────────│                    │                      │                     │
```

## Relationships Summary

| Relationship | Type |
|---|---|
| `PricingStrategy` <-- `DemandPricingStrategy` | **implements** (Strategy Pattern) |
| `PricingStrategy` <-- `WeekendPricingStrategy` | **implements** (Strategy Pattern) |
| `PricingStrategy` <-- `CompositePricingStrategy` | **implements** (chains strategies) |
| `PaymentGateway` <-- `UpiGateway` | **implements** (Factory Pattern) |
| `PaymentGateway` <-- `CreditCardGateway` | **implements** (Factory Pattern) |
| `PaymentGateway` <-- `DebitCardGateway` | **implements** (Factory Pattern) |
| `PaymentGatewayFactory` --> `PaymentGateway` | **creates** (Factory Pattern) |
| `Theatre` --> `Screen` | **aggregation** (has many) |
| `Screen` --> `Seat` | **aggregation** (has many) |
| `Show` --> `Movie`, `Screen`, `Theatre` | **association** |
| `Show` --> `SeatStatus` | **manages** (ConcurrentHashMap) |
| `Booking` --> `User`, `Show`, `Seat`, `Payment` | **association** |
| `BookingService` --> `SeatLockManager` | **composition** (concurrency) |
| `BookingService` --> `PricingStrategy` | **composition** (pricing) |
| `SeatLockManager` --> `Show` | **synchronizes on** (thread-safety) |

## Design Patterns Used

| Pattern | Where | Why |
|---|---|---|
| **Strategy** | `PricingStrategy` interface + `DemandPricingStrategy`, `WeekendPricingStrategy` | Extensible pricing without modifying core logic |
| **Composite** | `CompositePricingStrategy` | Chain multiple pricing rules, enforce price-only-increases |
| **Factory** | `PaymentGatewayFactory` | Select correct payment gateway by method at runtime |
| **Synchronized Locking** | `SeatLockManager` | Thread-safe seat reservation with 5-min expiry |

## Concurrency Handling

1. **Synchronized on Show object** - `SeatLockManager` uses `synchronized(show)` blocks ensuring only one thread can lock/confirm/release seats for a show at a time
2. **ConcurrentHashMap** - All in-memory stores use `ConcurrentHashMap` for thread-safe reads/writes
3. **AtomicInteger** - Booking and payment ID counters use `AtomicInteger` for lock-free increments
4. **Lock expiry** - Seats locked during payment have a 5-minute timeout; expired locks are auto-cleaned

## Pricing Rules

- Base prices: SILVER = Rs.150, GOLD = Rs.250, PLATINUM = Rs.400
- **Demand surge**: >25% occupied = 1.1x, >50% = 1.3x, >75% = 1.5x
- **Weekend surcharge**: Saturday/Sunday = 1.2x
- **Invariant**: Price can ONLY increase, never decrease below base

## Assumptions

- In-memory storage (no database) - easily replaceable with JPA/DB layer
- Payment gateways simulate success (no real API calls)
- Single JVM deployment (locking is JVM-level, not distributed)
- Show overlap check is per-screen per-theatre
- Lock timeout is 5 minutes (configurable in `SeatLockManager`)

## How to Run

```bash
cd bookmyshow/src
javac com/bookmyshow/**/*.java com/bookmyshow/App.java
java com.bookmyshow.App
```

## Future Improvements

- Replace in-memory stores with database (JPA/Hibernate)
- Distributed locking with Redis for multi-instance deployment
- REST API layer (Spring Boot)
- Event-driven architecture (Kafka) for booking notifications
- Seat selection UI with WebSocket for real-time availability
- Coupon/discount system as additional pricing strategies
- Rate limiting for booking API
