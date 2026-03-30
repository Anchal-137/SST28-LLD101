package com.elevator.sensor;

/**
 * Detects the current floor of the elevator.
 * Updated as the elevator moves between floors.
 */
public class FloorSensor {
    private int currentFloor;

    public FloorSensor(int initialFloor) {
        this.currentFloor = initialFloor;
    }

    public synchronized int getCurrentFloor() {
        return currentFloor;
    }

    public synchronized void setCurrentFloor(int floor) {
        this.currentFloor = floor;
    }
}
