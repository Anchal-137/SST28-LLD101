# Pen - Low Level Design

A pen simulation built in Java demonstrating the Strategy Pattern and SOLID principles.

## Features

- Different pen mechanisms (Cap-based, Click-based)
- Swappable refill cartridges with different ink colors
- Strategy Pattern for pen behavior
- Clean separation of concerns

## Class Diagram

```
    ┌──────────────────────────┐
    │        <<enum>>          │
    │        InkColor          │
    ├──────────────────────────┤
    │  RED                     │
    │  BLUE                    │
    │  BLACK                   │
    │  GREEN                   │
    └──────────┬───────────────┘
               │
               │ uses
               ▼
    ┌──────────────────────────────┐
    │          Refill              │
    ├──────────────────────────────┤
    │- inkColor: InkColor          │
    ├──────────────────────────────┤
    │+ getInkColor(): InkColor     │
    └──────────┬───────────────────┘
               │
               │ has-a
               │
    ┌────────────────────────────┐       ┌────────────────────────────┐
    │           Pen              │       │      <<interface>>         │
    ├────────────────────────────┤       │      PenMechanism          │
    │- mechanism: PenMechanism   │       ├────────────────────────────┤
    │- cartridge: Refill         │──────▶│+ open(): void              │
    ├────────────────────────────┤       │+ retract(): void           │
    │+ write(text: String)       │       └────────────────────────────┘
    │+ swapRefill(Refill)        │                    ▲
    │+ getInkColor(): InkColor   │                    │ implements
    └────────────────────────────┘          ┌─────────┴──────────┐
                                            │                    │
                                   ┌────────────────┐  ┌──────────────────┐
                                   │ CapMechanism   │  │ ClickMechanism   │
                                   ├────────────────┤  ├──────────────────┤
                                   │+ open()        │  │+ open()          │
                                   │+ retract()     │  │+ retract()       │
                                   └────────────────┘  └──────────────────┘
                                    "Taking off        "Pressing click
                                     cap..."            to deploy..."
                                    "Replacing          "Pressing click
                                     cap."               to retract."
```

## Relationships Summary

| Relationship | Type |
|---|---|
| `PenMechanism` <-- `CapMechanism` | **implements** (interface) |
| `PenMechanism` <-- `ClickMechanism` | **implements** (interface) |
| `Pen` --> `PenMechanism` | **composition** (has-a) |
| `Pen` --> `Refill` | **association** (has-a, replaceable) |
| `Refill` --> `InkColor` | **uses** (enum) |

## Design Patterns Used

- **Strategy Pattern** - `PenMechanism` interface with `CapMechanism` and `ClickMechanism` implementations, allowing different pen mechanisms without modifying the `Pen` class
- **Dependency Injection** - Mechanism and Refill are injected into Pen via constructor
- **Open/Closed Principle** - New pen mechanisms can be added (e.g., `TwistMechanism`) without modifying existing code
- **Single Responsibility Principle** - Each class has one job: `Pen` writes, `Refill` holds ink color, mechanisms handle open/retract

## How to Run

```bash
cd pen/src
javac *.java
java Main
```

## Sample Output

```
=== Cap Pen ===
  Taking off cap...
  Writing 'Hello' in BLUE
  Replacing cap.
  Swapping refill: BLUE -> RED
  Taking off cap...
  Writing 'World' in RED
  Replacing cap.

=== Click Pen ===
  Pressing click to deploy...
  Writing 'Design Patterns' in BLACK
  Pressing click to retract.
  Swapping refill: BLACK -> GREEN
  Pressing click to deploy...
  Writing 'are fun' in GREEN
  Pressing click to retract.
```
