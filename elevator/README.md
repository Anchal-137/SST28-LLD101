# Multi-Elevator System - Low Level Design

A production-grade multi-elevator system built in Java with concurrent request handling, pluggable scheduling algorithms, safety sensors, and state management.

## Features

- Multiple elevator cars operating simultaneously
- Outside panels (UP/DOWN per floor) and inside panels (floor buttons, door, alarm)
- Pluggable scheduling: Shortest Distance and FCFS (Strategy Pattern)
- Thread-safe request dispatching (synchronized + ConcurrentLinkedQueue)
- Weight sensor (700kg limit) - blocks movement when overloaded
- Floor sensor - tracks current position
- Alarm system - stops elevator immediately
- Maintenance mode - removes elevator from dispatch pool
- Door safety - won't close when overloaded
- State management: IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN, MAINTENANCE

## Project Structure

```
elevator/src/com/elevator/
|
|-- model/                        # Domain entities
|   |-- Direction.java            # UP, DOWN, NONE
|   |-- RequestType.java          # EXTERNAL, INTERNAL
|   |-- ElevatorRequest.java      # Request with floor, direction, type
|   |-- ElevatorCar.java          # Core elevator with state, stops, sensors
|   |-- Door.java                 # Open/close with overload protection
|   |-- Display.java              # Floor + direction display
|
|-- state/                        # Elevator states
|   |-- ElevatorState.java        # IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN, MAINTENANCE
|
|-- sensor/                       # Safety sensors
|   |-- WeightSensor.java         # Max 700kg, overload detection
|   |-- FloorSensor.java          # Current floor tracking
|
|-- panel/                        # User interface panels
|   |-- Button.java               # Pressable, illuminatable button
|   |-- OutsidePanel.java         # Floor panel: UP/DOWN buttons -> dispatcher
|   |-- InsidePanel.java          # Elevator panel: floor buttons, door, alarm
|
|-- strategy/                     # Scheduling algorithms (Strategy Pattern)
|   |-- SchedulingStrategy.java   # Interface
|   |-- ShortestDistanceStrategy.java  # Nearest + direction-aware
|   |-- FCFSStrategy.java         # Round-robin first available
|
|-- controller/                   # System coordination
|   |-- ElevatorDispatcher.java   # Assigns requests to elevators (thread-safe)
|   |-- ElevatorSystem.java       # Top-level controller
|
|-- App.java                      # Main driver - full simulation
```

## Class Diagram

```
    ┌──────────────┐   ┌───────────────┐   ┌─────────────────┐
    │  <<enum>>    │   │   <<enum>>    │   │    <<enum>>     │
    │  Direction   │   │  RequestType  │   │ ElevatorState   │
    ├──────────────┤   ├───────────────┤   ├─────────────────┤
    │ UP           │   │ EXTERNAL      │   │ IDLE            │
    │ DOWN         │   │ INTERNAL      │   │ MOVING_UP       │
    │ NONE         │   │               │   │ MOVING_DOWN     │
    └──────────────┘   └───────────────┘   │ DOOR_OPEN       │
                                           │ MAINTENANCE     │
                                           └─────────────────┘

    ┌──────────────────────────────┐
    │      ElevatorRequest         │
    ├──────────────────────────────┤
    │- floor: int                  │
    │- direction: Direction        │
    │- type: RequestType           │
    │- timestamp: long             │
    └──────────────────────────────┘

    ┌──────────────────┐      ┌──────────────────┐
    │   WeightSensor    │      │   FloorSensor    │
    ├──────────────────┤      ├──────────────────┤
    │- currentWeight    │      │- currentFloor    │
    │- MAX_CAPACITY:700 │      ├──────────────────┤
    ├──────────────────┤      │+ getCurrentFloor()│
    │+ isOverloaded()   │      │+ setCurrentFloor()│
    │+ setWeight()      │      └────────┬─────────┘
    └────────┬─────────┘                │
             │                          │
             │ has-a                    │ has-a
             ▼                          ▼
    ┌───────────────────────────────────────────────────────────┐
    │                      ElevatorCar                           │
    ├───────────────────────────────────────────────────────────┤
    │- carId: String                                             │
    │- minFloor / maxFloor: int                                  │
    │- door: Door                                                │
    │- display: Display                                          │
    │- weightSensor: WeightSensor                                │
    │- floorSensor: FloorSensor                                  │
    │- state: ElevatorState                                      │
    │- direction: Direction                                      │
    │- upStops: TreeSet<Integer>    (ascending order)             │
    │- downStops: TreeSet<Integer>  (descending traversal)       │
    │- alarmActive: boolean                                      │
    ├───────────────────────────────────────────────────────────┤
    │+ addStop(floor, dir): void          synchronized           │
    │+ processStops(): void               synchronized           │
    │+ triggerAlarm() / resetAlarm()      synchronized           │
    │+ setMaintenance(boolean)            synchronized           │
    │+ isAvailable(): boolean                                    │
    │+ getPendingStopCount(): int                                │
    └───────────────┬──────────────────────┬────────────────────┘
                    │                      │
                    │ has-a                │ has-a
                    ▼                      ▼
    ┌──────────────────────┐   ┌────────────────────────┐
    │        Door           │   │       Display          │
    ├──────────────────────┤   ├────────────────────────┤
    │- open: boolean        │   │- currentFloor: int     │
    │- elevatorId: String   │   │- direction: Direction  │
    ├──────────────────────┤   ├────────────────────────┤
    │+ openDoor()           │   │+ update(floor, dir)    │
    │+ closeDoor(overloaded)│   │+ show()                │
    └──────────────────────┘   └────────────────────────┘

    ┌──────────────────┐
    │     Button        │
    ├──────────────────┤
    │- label: String    │
    │- illuminated: bool│
    ├──────────────────┤
    │+ press() / reset()│
    └──────────┬───────┘
               │ used by
        ┌──────┴──────────────────────┐
        ▼                             ▼
    ┌──────────────────────┐   ┌──────────────────────────────┐
    │   OutsidePanel        │   │       InsidePanel             │
    ├──────────────────────┤   ├──────────────────────────────┤
    │- floor: int           │   │- car: ElevatorCar             │
    │- upButton: Button     │   │- floorButtons: Button[]       │
    │- downButton: Button   │   │- openDoorButton: Button       │
    │- dispatcher           │   │- closeDoorButton: Button      │
    ├──────────────────────┤   │- alarmButton: Button           │
    │+ pressUp()            │   ├──────────────────────────────┤
    │+ pressDown()          │   │+ pressFloor(int)              │
    │  -> dispatcher        │   │+ pressOpenDoor()              │
    │    .handleExternal()  │   │+ pressCloseDoor()             │
    └──────────────────────┘   │+ pressAlarm()                  │
                               └──────────────────────────────┘

    ┌────────────────────────────────────────────┐
    │       <<interface>>                         │
    │       SchedulingStrategy                    │
    ├────────────────────────────────────────────┤
    │+ selectElevator(elevators, floor, dir):    │
    │                            ElevatorCar     │
    └────────────────────────────────────────────┘
               ▲                    ▲
               │                    │
    ┌──────────┴──────────┐  ┌─────┴──────────────┐
    │ ShortestDistance     │  │   FCFSStrategy      │
    │ Strategy             │  │                     │
    ├─────────────────────┤  ├─────────────────────┤
    │ nearest + direction  │  │ round-robin first   │
    │ aware + tiebreak     │  │ available           │
    │ on pending stops     │  │                     │
    └─────────────────────┘  └─────────────────────┘

    ┌────────────────────────────────────────────────────┐
    │              ElevatorDispatcher                      │
    ├────────────────────────────────────────────────────┤
    │- elevators: List<ElevatorCar>                       │
    │- strategy: SchedulingStrategy                       │
    │- pendingRequests: ConcurrentLinkedQueue              │
    │- assignmentLock: Object                              │
    ├────────────────────────────────────────────────────┤
    │+ handleExternalRequest(floor, dir)  synchronized    │
    │+ retryPendingRequests()                              │
    │+ setStrategy(SchedulingStrategy)                     │
    └────────────────────────────────────────────────────┘
                         │
                         │ managed by
                         ▼
    ┌────────────────────────────────────────────────────┐
    │                ElevatorSystem                        │
    ├────────────────────────────────────────────────────┤
    │- elevators: List<ElevatorCar>                       │
    │- dispatcher: ElevatorDispatcher                     │
    │- insidePanels: Map<String, InsidePanel>              │
    │- outsidePanels: Map<Integer, OutsidePanel>           │
    ├────────────────────────────────────────────────────┤
    │+ getFloorPanel(floor): OutsidePanel                 │
    │+ getInsidePanel(carId): InsidePanel                 │
    │+ processAllElevators(): void                        │
    │+ printStatus(): void                                │
    └────────────────────────────────────────────────────┘
```

## State Diagram - Elevator Car

```
                    ┌─────────────────────┐
                    │       IDLE          │ <--- initial state
                    └─────────┬───────────┘
                              │
                    ┌─────────┴───────────┐
                    │  request received?  │
                    └─────┬─────────┬─────┘
                 UP req   │         │   DOWN req
                          ▼         ▼
              ┌───────────────┐  ┌────────────────┐
              │  MOVING_UP    │  │  MOVING_DOWN   │
              └───────┬───────┘  └───────┬────────┘
                      │                  │
                      │  arrived         │  arrived
                      ▼                  ▼
              ┌───────────────────────────────────┐
              │           DOOR_OPEN               │
              │  (open door, passengers in/out)    │
              └───────────────┬───────────────────┘
                              │
                    ┌─────────┴───────────┐
                    │  more stops?        │
                    └─────┬─────────┬─────┘
                    yes   │         │   no
                          ▼         ▼
              ┌───────────────┐  ┌──────────┐
              │  MOVING_*     │  │   IDLE   │
              │  (continue)   │  │          │
              └───────────────┘  └──────────┘

    Special transitions:
    ┌──────────────┐
    │ ANY STATE    │ --alarm--> IDLE (alarmActive=true, stops cleared)
    │              │ --maint--> MAINTENANCE (stops cleared)
    └──────────────┘

    ┌──────────────────┐
    │  MAINTENANCE     │  does NOT accept any requests
    │                  │  --clear--> IDLE
    └──────────────────┘
```

## Sequence Diagram - External Request (Floor Panel)

```
User        OutsidePanel      ElevatorDispatcher      SchedulingStrategy      ElevatorCar
 │               │                    │                       │                    │
 │  pressUp()    │                    │                       │                    │
 │──────────────>│                    │                       │                    │
 │               │ handleExternal     │                       │                    │
 │               │ Request(floor,UP)  │                       │                    │
 │               │───────────────────>│                       │                    │
 │               │                    │  synchronized block   │                    │
 │               │                    │──┐                    │                    │
 │               │                    │  │ selectElevator()   │                    │
 │               │                    │  │───────────────────>│                    │
 │               │                    │  │   bestCar          │                    │
 │               │                    │  │<───────────────────│                    │
 │               │                    │  │                    │                    │
 │               │                    │  │ addStop(floor, UP) │                    │
 │               │                    │  │───────────────────────────────────────>│
 │               │                    │<─┘                    │                    │
 │               │                    │                       │                    │
 │               │                    │  processAllElevators()│                    │
 │               │                    │                       │  processStops()    │
 │               │                    │───────────────────────────────────────────>│
 │               │                    │                       │    moveToFloor()   │
 │               │                    │                       │    openDoor()      │
 │               │                    │                       │    closeDoor()     │
 │               │                    │                       │                    │
```

## Sequence Diagram - Internal Request (Inside Panel)

```
User        InsidePanel       ElevatorCar
 │               │                 │
 │ pressFloor(8) │                 │
 │──────────────>│                 │
 │               │ addStop(8, UP)  │
 │               │────────────────>│
 │               │                 │──┐ synchronized
 │               │                 │  │ add to upStops
 │               │                 │<─┘
 │               │                 │
 │       [processAllElevators()]   │
 │               │                 │  processStops()
 │               │                 │──┐ moveToFloor(8)
 │               │                 │  │ openDoor()
 │               │                 │  │ closeDoor()
 │               │                 │<─┘
 │               │                 │
```

## Relationships Summary

| Relationship | Type |
|---|---|
| `SchedulingStrategy` <-- `ShortestDistanceStrategy` | **implements** (Strategy Pattern) |
| `SchedulingStrategy` <-- `FCFSStrategy` | **implements** (Strategy Pattern) |
| `ElevatorSystem` --> `ElevatorDispatcher` | **composition** |
| `ElevatorSystem` --> `ElevatorCar` | **aggregation** (has many) |
| `ElevatorSystem` --> `InsidePanel`, `OutsidePanel` | **composition** |
| `ElevatorDispatcher` --> `SchedulingStrategy` | **composition** (pluggable) |
| `ElevatorDispatcher` --> `ElevatorCar` | **selects from** |
| `ElevatorCar` --> `Door`, `Display` | **composition** |
| `ElevatorCar` --> `WeightSensor`, `FloorSensor` | **composition** |
| `OutsidePanel` --> `ElevatorDispatcher` | **delegates to** |
| `InsidePanel` --> `ElevatorCar` | **delegates to** |
| `OutsidePanel`, `InsidePanel` --> `Button` | **has many** |

## Design Patterns Used

| Pattern | Where | Why |
|---|---|---|
| **Strategy** | `SchedulingStrategy` + implementations | Pluggable dispatch algorithms without modifying core |
| **State** | `ElevatorState` enum + state-driven behavior in `ElevatorCar` | Clean state transitions (IDLE/MOVING/DOOR_OPEN/MAINTENANCE) |
| **Synchronized Locking** | `ElevatorDispatcher.assignmentLock`, `ElevatorCar` methods | Thread-safe request assignment, prevents duplicate dispatch |

## Concurrency Handling

1. **Synchronized assignment** - `ElevatorDispatcher` uses `synchronized(assignmentLock)` so only ONE elevator gets assigned per request even with concurrent button presses
2. **Synchronized ElevatorCar** - `addStop()`, `processStops()`, `triggerAlarm()`, `setMaintenance()` are all `synchronized` on the car instance
3. **ConcurrentLinkedQueue** - Pending requests that couldn't be assigned are queued and retried
4. **TreeSet** for stops - Ensures floor-order traversal (ascending for UP, descending for DOWN)

## Assumptions

- Simulated movement (no real timing delays) - easily extendable with `Thread.sleep()`
- Single JVM (locking is in-process, not distributed)
- Each elevator processes all its stops in one `processStops()` call (batch simulation)
- Floor numbering starts at 0
- Weight is set externally (simulating the physical sensor reading)

## How to Run

```bash
cd elevator/src
javac com/elevator/**/*.java com/elevator/App.java
java com.elevator.App
```

## Example Scenarios Simulated

| # | Scenario | What it tests |
|---|---|---|
| 1 | Floor 5 presses UP | Basic external dispatch |
| 2 | Floor 3 presses DOWN | Direction-aware assignment |
| 3 | Inside E1, press floor 8 | Internal request handling |
| 4 | 3 simultaneous requests | Multi-elevator distribution |
| 5 | 3 concurrent threads | Thread-safety of dispatcher |
| 6 | Weight overload (750kg) | Safety sensor + door behavior |
| 7 | Alarm in E3 | Alarm stops elevator, skipped by dispatch |
| 8 | E1 maintenance mode | Removed from dispatch pool |
| 9 | Strategy swap to FCFS | Runtime algorithm change |
| 10 | Door open/close | Inside panel door controls |

## Future Improvements

- Real-time simulation with `Thread.sleep()` per floor
- Each elevator as its own thread with event queue
- Distributed locking (Redis) for multi-instance
- VIP/emergency priority requests
- REST API layer (Spring Boot)
- Real-time display via WebSocket
- AI-based predictive scheduling (learn traffic patterns)
- Energy-efficient scheduling (minimize total travel distance)
