package com.elevator.panel;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;

/**
 * Panel inside each elevator with floor buttons, open/close door,
 * and alarm button.
 * When a floor button is pressed, it adds an INTERNAL stop directly
 * to the elevator car.
 */
public class InsidePanel {
    private final ElevatorCar car;
    private final Button[] floorButtons;
    private final Button openDoorButton;
    private final Button closeDoorButton;
    private final Button alarmButton;

    public InsidePanel(ElevatorCar car) {
        this.car = car;
        int totalFloors = car.getMaxFloor() - car.getMinFloor() + 1;
        this.floorButtons = new Button[totalFloors];
        for (int i = 0; i < totalFloors; i++) {
            floorButtons[i] = new Button("F" + (car.getMinFloor() + i));
        }
        this.openDoorButton = new Button("OPEN");
        this.closeDoorButton = new Button("CLOSE");
        this.alarmButton = new Button("ALARM");
    }

    public void pressFloor(int floor) {
        int index = floor - car.getMinFloor();
        if (index < 0 || index >= floorButtons.length) return;

        floorButtons[index].press();
        Direction dir = floor > car.getCurrentFloor() ? Direction.UP :
                        floor < car.getCurrentFloor() ? Direction.DOWN : Direction.NONE;
        System.out.println("[" + car.getCarId() + " PANEL] Floor " + floor + " pressed");
        car.addStop(floor, dir);
    }

    public void pressOpenDoor() {
        openDoorButton.press();
        car.getDoor().openDoor();
    }

    public void pressCloseDoor() {
        closeDoorButton.press();
        car.getDoor().closeDoor(car.getWeightSensor().isOverloaded());
    }

    public void pressAlarm() {
        alarmButton.press();
        car.triggerAlarm();
    }
}
