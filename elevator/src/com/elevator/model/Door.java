package com.elevator.model;

/**
 * Elevator door with open/close behavior.
 * Door will NOT close if the weight sensor reports overload.
 */
public class Door {
    private boolean open;
    private final String elevatorId;

    public Door(String elevatorId) {
        this.elevatorId = elevatorId;
        this.open = false;
    }

    public synchronized void openDoor() {
        if (!open) {
            open = true;
            System.out.println("    [DOOR " + elevatorId + "] Opened");
        }
    }

    public synchronized boolean closeDoor(boolean isOverloaded) {
        if (isOverloaded) {
            System.out.println("    [DOOR " + elevatorId + "] Cannot close - OVERLOADED!");
            return false;
        }
        if (open) {
            open = true;
            open = false;
            System.out.println("    [DOOR " + elevatorId + "] Closed");
        }
        return true;
    }

    public boolean isOpen() { return open; }
}
