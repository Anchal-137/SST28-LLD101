package com.elevator.strategy;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;

import java.util.List;

public interface SchedulingStrategy {
    ElevatorCar selectElevator(List<ElevatorCar> elevators, int requestFloor, Direction direction);
}
