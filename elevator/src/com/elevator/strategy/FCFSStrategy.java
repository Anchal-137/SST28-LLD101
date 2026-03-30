package com.elevator.strategy;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;

import java.util.List;

public class FCFSStrategy implements SchedulingStrategy {
    private int lastAssigned = -1;

    @Override
    public ElevatorCar selectElevator(List<ElevatorCar> elevators, int requestFloor, Direction direction) {
        int size = elevators.size();
        for (int i = 0; i < size; i++) {
            int index = (lastAssigned + 1 + i) % size;
            ElevatorCar car = elevators.get(index);
            if (car.isAvailable()) {
                lastAssigned = index;
                return car;
            }
        }
        return null;
    }
}
