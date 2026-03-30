package com.elevator.model;

/**
 * Represents a request to the elevator system.
 * Can be EXTERNAL (from a floor panel) or INTERNAL (from inside the elevator).
 */
public class ElevatorRequest {
    private final int floor;
    private final Direction direction;
    private final RequestType type;
    private final long timestamp;

    public ElevatorRequest(int floor, Direction direction, RequestType type) {
        this.floor = floor;
        this.direction = direction;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public int getFloor() { return floor; }
    public Direction getDirection() { return direction; }
    public RequestType getType() { return type; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return type + " request: floor " + floor + " " + direction;
    }
}
