package com.elevator.panel;

import com.elevator.controller.ElevatorDispatcher;
import com.elevator.model.Direction;

public class OutsidePanel {
    private final int floor;
    private final Button upButton;
    private final Button downButton;
    private final ElevatorDispatcher dispatcher;

    public OutsidePanel(int floor, int maxFloor, ElevatorDispatcher dispatcher) {
        this.floor = floor;
        this.upButton = (floor < maxFloor) ? new Button("UP-" + floor) : null;
        this.downButton = (floor > 0) ? new Button("DOWN-" + floor) : null;
        this.dispatcher = dispatcher;
    }

    public void pressUp() {
        if (upButton != null) {
            upButton.press();
            System.out.println("[FLOOR " + floor + "] UP button pressed");
            dispatcher.handleExternalRequest(floor, Direction.UP);
        }
    }

    public void pressDown() {
        if (downButton != null) {
            downButton.press();
            System.out.println("[FLOOR " + floor + "] DOWN button pressed");
            dispatcher.handleExternalRequest(floor, Direction.DOWN);
        }
    }

    public int getFloor() { return floor; }
}
