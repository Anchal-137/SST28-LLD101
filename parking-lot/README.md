# Parking Lot - Low Level Design

A parking lot management system built in Java demonstrating SOLID principles and common design patterns.

## Features

- Multi-floor parking with different spot types (SMALL, MEDIUM, LARGE)
- Vehicle types: TWO_WHEELER, CAR, BUS
- Automatic spot allocation using closest-spot strategy
- Vehicle-to-spot compatibility with overflow support (e.g., bike can park in MEDIUM if SMALL is full)
- Hourly invoicing with configurable pricing per spot type
- Entry/exit via gates with pass-based tracking

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                            PARKING LOT - CLASS DIAGRAM                          │
└─────────────────────────────────────────────────────────────────────────────────┘

    ┌──────────────────────┐          ┌──────────────────────┐
    │    <<enum>>          │          │     <<enum>>         │
    │    VehicleType       │          │     SlotType         │
    ├──────────────────────┤          ├──────────────────────┤
    │  TWO_WHEELER         │          │  SMALL               │
    │  CAR                 │          │  MEDIUM              │
    │  BUS                 │          │  LARGE               │
    └──────────┬───────────┘          └───────┬──────────────┘
               │                              │
               │ uses                         │ uses
               ▼                              ▼
    ┌──────────────────────┐       ┌──────────────────────────┐
    │      Vehicle         │       │      ParkingSpot         │
    ├──────────────────────┤       ├──────────────────────────┤
    │- numberPlate: String │       │- spotNumber: int         │
    │- type: VehicleType   │       │- type: SlotType          │
    ├──────────────────────┤       │- floor: int              │
    │+ getNumberPlate()    │       │- reserved: boolean       │
    │+ getType()           │       ├──────────────────────────┤
    └──────────┬───────────┘       │+ reserve()              │
               │                   │+ release()              │
               │                   │+ isReserved()           │
               │                   │+ getSpotNumber()        │
               │                   │+ getType()              │
               │                   │+ getFloor()             │
               │                   └────────┬─────────────────┘
               │                            │
               │         ┌──────────────────┘
               ▼         ▼
    ┌──────────────────────────────┐       ┌──────────────────────┐
    │        ParkingPass           │       │        Gate          │
    ├──────────────────────────────┤       ├──────────────────────┤
    │- passId: String              │       │- gateId: int         │
    │- vehicle: Vehicle            │◄──┐   │- floor: int          │
    │- spot: ParkingSpot           │   │   ├──────────────────────┤
    │- entryTime: LocalDateTime    │   │   │+ getGateId()         │
    ├──────────────────────────────┤   │   │+ getFloor()          │
    │+ getPassId()                 │   │   └──────────┬───────────┘
    │+ getVehicle()                │   │              │
    │+ getSpot()                   │   │              │
    │+ getEntryTime()              │   │              │
    └──────────────┬───────────────┘   │              │
                   │                   │              │
                   ▼                   │              │
    ┌──────────────────────────────┐   │              │
    │         Invoice              │   │              │
    ├──────────────────────────────┤   │              │
    │- pass: ParkingPass           │   │              │
    │- exitTime: LocalDateTime     │   │              │
    │- duration: long              │   │              │
    │- charge: int                 │   │              │
    ├──────────────────────────────┤   │              │
    │+ getPass()                   │   │              │
    │+ getExitTime()               │   │              │
    │+ getDuration()               │   │              │
    │+ getCharge()                 │   │              │
    └──────────────────────────────┘   │              │
                   ▲                   │              │
                   │ creates           │              │
                   │                   │              │
    ┌──────────────────────────────┐   │              │
    │     InvoiceGenerator         │   │              │
    ├──────────────────────────────┤   │              │
    │- rateStrategy: RateStrategy  │   │              │
    ├──────────────────────────────┤   │              │
    │+ createInvoice(pass, exit)   │   │              │
    └──────────┬───────────────────┘   │              │
               │                       │              │
               │ uses                  │              │
               ▼                       │              │
    ┌─────────────────────────┐        │              │
    │  <<interface>>          │        │              │
    │  RateStrategy           │        │              │
    ├─────────────────────────┤        │              │
    │+ getHourlyRate(SlotType)│        │              │
    └─────────────────────────┘        │              │
               ▲                       │              │
               │ implements            │              │
               │                       │              │
    ┌──────────────────────────────┐   │              │
    │  StandardRateStrategy        │   │              │
    ├──────────────────────────────┤   │              │
    │- priceMap: Map<SlotType,Int> │   │              │
    ├──────────────────────────────┤   │              │
    │+ getHourlyRate(SlotType)     │   │              │
    └──────────────────────────────┘   │              │
                                       │              │
    ┌─────────────────────────────────┐│              │
    │  <<interface>>                   ││              │
    │  SpotAllocationStrategy          ││              │
    ├─────────────────────────────────┤│              │
    │+ allocateSpot(spots, vehicleType,│              │
    │               entryGate)        ││              │
    └─────────────────────────────────┘│              │
               ▲                       │              │
               │ implements            │              │
               │                       │              │
    ┌──────────────────────────────┐   │              │
    │  ClosestSpotStrategy         │   │              │
    ├──────────────────────────────┤   │              │
    │+ allocateSpot(spots,         │   │              │
    │    vehicleType, entryGate)   │   │              │
    └──────────────────────────────┘   │              │
                                       │              │
    ┌──────────────────────────────────┘              │
    │                                                 │
    ▼                                                 ▼
    ┌───────────────────────────────────────────────────────┐
    │                    ParkingLot                          │
    ├───────────────────────────────────────────────────────┤
    │- spots: List<ParkingSpot>                             │
    │- allocationStrategy: SpotAllocationStrategy           │
    │- invoiceGenerator: InvoiceGenerator                   │
    │- activePasses: Map<String, ParkingPass>               │
    │- passCounter: int                                     │
    ├───────────────────────────────────────────────────────┤
    │+ parkVehicle(vehicle, gate, time): ParkingPass        │
    │+ releaseVehicle(passId, exitTime): Invoice            │
    └───────────────────────────────────────────────────────┘
                         │
                         │ uses
                         ▼
    ┌──────────────────────────────────────┐
    │       VehicleSpotMapper              │
    ├──────────────────────────────────────┤
    │+ getAllowedSpots(VehicleType): List  │  (static utility)
    └──────────────────────────────────────┘
```

## Relationships Summary

| Relationship | Type |
|---|---|
| `RateStrategy` <-- `StandardRateStrategy` | **implements** (interface) |
| `SpotAllocationStrategy` <-- `ClosestSpotStrategy` | **implements** (interface) |
| `ParkingLot` --> `SpotAllocationStrategy` | **composition** (has-a) |
| `ParkingLot` --> `InvoiceGenerator` | **composition** (has-a) |
| `ParkingLot` --> `ParkingSpot` | **aggregation** (has many) |
| `ParkingLot` --> `ParkingPass` | **manages** (activePasses map) |
| `ParkingPass` --> `Vehicle`, `ParkingSpot` | **association** (references) |
| `Invoice` --> `ParkingPass` | **association** (references) |
| `InvoiceGenerator` --> `RateStrategy` | **composition** (has-a) |
| `Vehicle` --> `VehicleType` | **uses** (enum) |
| `ParkingSpot` --> `SlotType` | **uses** (enum) |
| `VehicleSpotMapper` | **static utility** (maps VehicleType -> SlotType) |

## Design Patterns Used

- **Strategy Pattern** - `SpotAllocationStrategy` interface with `ClosestSpotStrategy` implementation, allowing easy swap of spot allocation logic
- **Dependency Injection** - `RateStrategy` injected into `InvoiceGenerator`, `SpotAllocationStrategy` and `InvoiceGenerator` injected into `ParkingLot`
- **Single Responsibility Principle** - Each class has one clear job (invoicing, spot allocation, compatibility mapping, etc.)
- **Open/Closed Principle** - New rate strategies or spot allocation strategies can be added without modifying existing code

## Vehicle-Spot Compatibility

| Vehicle Type | Compatible Spot Types |
|---|---|
| TWO_WHEELER | SMALL, MEDIUM, LARGE |
| CAR | MEDIUM, LARGE |
| BUS | LARGE |

If the preferred spot type is full, vehicles overflow to the next compatible larger spot.

## Pricing

Configured per spot type (default rates in `Main.java`):

| Spot Type | Rate per Hour |
|---|---|
| SMALL | Rs. 10 |
| MEDIUM | Rs. 20 |
| LARGE | Rs. 50 |

## How to Run

```bash
cd parking-lot/src
javac *.java
java Main
```
