package com.elevator.model;

import com.elevator.sensor.FloorSensor;
import com.elevator.sensor.WeightSensor;
import com.elevator.state.ElevatorState;

import java.util.TreeSet;

/**
 * Represents a single elevator car with its own door, display,
 * sensors, state, and request queues.
 *
 * The elevator processes requests using two sorted sets:
 * - upStops: floors to visit while going UP (ascending order)
 * - downStops: floors to visit while going DOWN (descending order)
 *
 * Thread-safe: all state mutations are synchronized.
 */
public class ElevatorCar {
    private final String carId;
    private final int minFloor;
    private final int maxFloor;
    private final Door door;
    private final Display display;
    private final WeightSensor weightSensor;
    private final FloorSensor floorSensor;

    private ElevatorState state;
    private Direction direction;

    // Sorted sets for efficient floor-order traversal
    private final TreeSet<Integer> upStops;
    private final TreeSet<Integer> downStops;

    private boolean alarmActive;

    public ElevatorCar(String carId, int minFloor, int maxFloor) {
        this.carId = carId;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.door = new Door(carId);
        this.display = new Display(carId);
        this.weightSensor = new WeightSensor();
        this.floorSensor = new FloorSensor(minFloor);
        this.state = ElevatorState.IDLE;
        this.direction = Direction.NONE;
        this.upStops = new TreeSet<>();
        this.downStops = new TreeSet<>();
        this.alarmActive = false;
    }

    // ---- Getters ----
    public String getCarId() { return carId; }
    public int getCurrentFloor() { return floorSensor.getCurrentFloor(); }
    public ElevatorState getState() { return state; }
    public Direction getDirection() { return direction; }
    public Door getDoor() { return door; }
    public Display getDisplay() { return display; }
    public WeightSensor getWeightSensor() { return weightSensor; }
    public int getMinFloor() { return minFloor; }
    public int getMaxFloor() { return maxFloor; }
    public boolean isAlarmActive() { return alarmActive; }

    public synchronized boolean isAvailable() {
        return state != ElevatorState.MAINTENANCE && !alarmActive;
    }

    public synchronized int getPendingStopCount() {
        return upStops.size() + downStops.size();
    }

    // ---- Request handling ----
    public synchronized void addStop(int floor, Direction requestDir) {
        if (state == ElevatorState.MAINTENANCE || alarmActive) {
            System.out.println("  [" + carId + "] Cannot accept - " +
                    (alarmActive ? "ALARM ACTIVE" : "UNDER MAINTENANCE"));
            return;
        }
        if (floor < minFloor || floor > maxFloor) return;

        if (requestDir == Direction.UP || (requestDir == Direction.NONE && floor >= getCurrentFloor())) {
            upStops.add(floor);
        } else {
            downStops.add(floor);
        }
        System.out.println("  [" + carId + "] Stop added: floor " + floor + " (" + requestDir + ")");
    }

    /**
     * Process all pending stops. This simulates the elevator movement.
     * Called by the ElevatorSystem's processing loop.
     */
    public synchronized void processStops() {
        if (state == ElevatorState.MAINTENANCE || alarmActive) return;
        if (upStops.isEmpty() && downStops.isEmpty()) {
            if (state != ElevatorState.IDLE) {
                state = ElevatorState.IDLE;
                direction = Direction.NONE;
                display.update(getCurrentFloor(), Direction.NONE);
                System.out.println("  [" + carId + "] Now IDLE at floor " + getCurrentFloor());
            }
            return;
        }

        // Decide direction based on current state
        if (direction == Direction.NONE || direction == Direction.UP) {
            processUpStops();
            if (!downStops.isEmpty()) {
                processDownStops();
            }
        } else {
            processDownStops();
            if (!upStops.isEmpty()) {
                processUpStops();
            }
        }

        // After processing all, go idle
        if (upStops.isEmpty() && downStops.isEmpty()) {
            state = ElevatorState.IDLE;
            direction = Direction.NONE;
            display.update(getCurrentFloor(), Direction.NONE);
            System.out.println("  [" + carId + "] Now IDLE at floor " + getCurrentFloor());
        }
    }

    private void processUpStops() {
        direction = Direction.UP;
        state = ElevatorState.MOVING_UP;

        while (!upStops.isEmpty()) {
            int nextFloor = upStops.pollFirst();
            moveToFloor(nextFloor);
        }
    }

    private void processDownStops() {
        direction = Direction.DOWN;
        state = ElevatorState.MOVING_DOWN;

        while (!downStops.isEmpty()) {
            int nextFloor = downStops.pollLast();
            moveToFloor(nextFloor);
        }
    }

    private void moveToFloor(int targetFloor) {
        int current = getCurrentFloor();
        if (current == targetFloor) {
            arriveAtFloor(targetFloor);
            return;
        }

        Direction moveDir = targetFloor > current ? Direction.UP : Direction.DOWN;
        state = moveDir == Direction.UP ? ElevatorState.MOVING_UP : ElevatorState.MOVING_DOWN;
        direction = moveDir;
        display.update(current, direction);

        System.out.println("  [" + carId + "] Moving " + moveDir + ": " + current + " -> " + targetFloor);

        // Simulate floor-by-floor movement
        int step = moveDir == Direction.UP ? 1 : -1;
        for (int f = current + step; ; f += step) {
            floorSensor.setCurrentFloor(f);
            if (f == targetFloor) break;
        }

        arriveAtFloor(targetFloor);
    }

    private void arriveAtFloor(int floor) {
        floorSensor.setCurrentFloor(floor);
        display.update(floor, direction);

        // Check weight before opening door
        if (weightSensor.isOverloaded()) {
            System.out.println("  [" + carId + "] WARNING: Overloaded at floor " + floor
                    + " (" + weightSensor.getCurrentWeight() + "kg > "
                    + weightSensor.getMaxCapacity() + "kg) - NOT moving further!");
            door.openDoor();
            return;
        }

        // Open door, simulate stop, close door
        state = ElevatorState.DOOR_OPEN;
        door.openDoor();
        System.out.println("  [" + carId + "] Arrived at floor " + floor);
        door.closeDoor(weightSensor.isOverloaded());
    }

    // ---- Alarm ----
    public synchronized void triggerAlarm() {
        alarmActive = true;
        state = ElevatorState.IDLE;
        direction = Direction.NONE;
        System.out.println("  [" + carId + "] !!! ALARM TRIGGERED - Elevator STOPPED !!!");
    }

    public synchronized void resetAlarm() {
        alarmActive = false;
        System.out.println("  [" + carId + "] Alarm reset - Elevator operational");
    }

    // ---- Maintenance ----
    public synchronized void setMaintenance(boolean maintenance) {
        if (maintenance) {
            state = ElevatorState.MAINTENANCE;
            direction = Direction.NONE;
            upStops.clear();
            downStops.clear();
            System.out.println("  [" + carId + "] Set to MAINTENANCE mode");
        } else {
            state = ElevatorState.IDLE;
            System.out.println("  [" + carId + "] Maintenance cleared - now IDLE");
        }
    }

    @Override
    public String toString() {
        return carId + " [floor=" + getCurrentFloor() + ", state=" + state
                + ", dir=" + direction + ", stops=" + getPendingStopCount() + "]";
    }
}
