package com.elevator.model;

public class Display {
    private int currentFloor;
    private Direction direction;
    private final String location;

    public Display(String location) {
        this.location = location;
        this.currentFloor = 0;
        this.direction = Direction.NONE;
    }

    public void update(int floor, Direction direction) {
        this.currentFloor = floor;
        this.direction = direction;
    }

    public void show() {
        String arrow = direction == Direction.UP ? "^" : direction == Direction.DOWN ? "v" : "-";
        System.out.println("    [DISPLAY " + location + "] Floor: " + currentFloor + " " + arrow);
    }

    public int getCurrentFloor() { return currentFloor; }
    public Direction getDirection() { return direction; }
}
