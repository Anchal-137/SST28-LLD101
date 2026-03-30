# Multi-Elevator System

LLD for a multi-elevator building system in Java. Supports concurrent request handling, pluggable scheduling algorithms, safety sensors, alarm, maintenance mode, and proper state management.

## How to Run

```bash
cd elevator/src
javac com/elevator/**/*.java com/elevator/App.java
java com.elevator.App
```

## What's Implemented

- Multiple elevators working independently, coordinated by a central dispatcher
- Outside panels (UP/DOWN per floor) and inside panels (floor buttons, door, alarm)
- Two scheduling algorithms: Shortest Distance and FCFS, swappable at runtime
- Thread-safe dispatching (synchronized lock prevents duplicate assignment)
- Weight sensor (700kg max) - blocks movement + keeps door open when overloaded
- Alarm button - stops elevator, skipped by dispatcher until reset
- Maintenance mode - elevator removed from dispatch pool
- Door won't close if overloaded

## Design Patterns

- **Strategy** - `SchedulingStrategy` with `ShortestDistanceStrategy` and `FCFSStrategy`
- **State** - `ElevatorState` enum (IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPEN, MAINTENANCE)
- **Synchronized Locking** - dispatcher uses `assignmentLock` so only one elevator handles each request

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
             │ has-a                    │ has-a
             ▼                          ▼
    ┌───────────────────────────────────────────────────────┐
    │                      ElevatorCar                       │
    ├───────────────────────────────────────────────────────┤
    │- carId: String                                         │
    │- minFloor / maxFloor: int                              │
    │- door: Door                                            │
    │- display: Display                                      │
    │- weightSensor: WeightSensor                            │
    │- floorSensor: FloorSensor                              │
    │- state: ElevatorState                                  │
    │- direction: Direction                                  │
    │- upStops: TreeSet<Integer>                             │
    │- downStops: TreeSet<Integer>                           │
    │- alarmActive: boolean                                  │
    ├───────────────────────────────────────────────────────┤
    │+ addStop(floor, dir): void         synchronized        │
    │+ processStops(): void              synchronized        │
    │+ triggerAlarm() / resetAlarm()     synchronized        │
    │+ setMaintenance(boolean)           synchronized        │
    │+ isAvailable(): boolean                                │
    └───────────────┬──────────────────────┬────────────────┘
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
    └──────────────────────┘   │+ pressOpenDoor()              │
                               │+ pressCloseDoor()             │
                               │+ pressAlarm()                  │
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
    └────────────────────────────────────────────────────┘
```

## State Diagram

```
                    ┌─────────────────────┐
                    │       IDLE          │ <-- initial
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
                      │ arrived          │ arrived
                      ▼                  ▼
              ┌───────────────────────────────────┐
              │           DOOR_OPEN               │
              └───────────────┬───────────────────┘
                              │
                    ┌─────────┴───────────┐
                    │  more stops?        │
                    └─────┬─────────┬─────┘
                    yes   │         │   no
                          ▼         ▼
              ┌───────────────┐  ┌──────────┐
              │  MOVING_*     │  │   IDLE   │
              └───────────────┘  └──────────┘

    Special transitions:
    ANY STATE --alarm--> IDLE (alarmActive=true)
    ANY STATE --maint--> MAINTENANCE (stops cleared)
    MAINTENANCE --clear--> IDLE
```

## Sequence - External Request

```
User        OutsidePanel      ElevatorDispatcher      SchedulingStrategy      ElevatorCar
 │               │                    │                       │                    │
 │  pressUp()    │                    │                       │                    │
 │──────────────>│                    │                       │                    │
 │               │ handleExternal     │                       │                    │
 │               │ Request(floor,UP)  │                       │                    │
 │               │───────────────────>│                       │                    │
 │               │                    │  synchronized block   │                    │
 │               │                    │──┐ selectElevator()   │                    │
 │               │                    │  │───────────────────>│                    │
 │               │                    │  │   bestCar          │                    │
 │               │                    │  │<───────────────────│                    │
 │               │                    │  │ addStop(floor, UP) │                    │
 │               │                    │  │───────────────────────────────────────>│
 │               │                    │<─┘                    │                    │
 │               │                    │                       │  processStops()    │
 │               │                    │                       │  moveToFloor()     │
 │               │                    │                       │  openDoor()        │
 │               │                    │                       │  closeDoor()       │
 │               │                    │                       │                    │
```

## Sequence - Inside Panel Request

```
User        InsidePanel       ElevatorCar
 │               │                 │
 │ pressFloor(8) │                 │
 │──────────────>│                 │
 │               │ addStop(8, UP)  │
 │               │────────────────>│  synchronized
 │               │                 │  add to upStops
 │               │                 │
 │       [processAllElevators()]   │
 │               │                 │  processStops()
 │               │                 │  moveToFloor(8)
 │               │                 │  openDoor/closeDoor
 │               │                 │
```

## Assumptions

- Simulated movement (no Thread.sleep), batch processing
- Single JVM, in-process locking
- Floor numbering starts at 0
- Weight set externally to simulate sensor
