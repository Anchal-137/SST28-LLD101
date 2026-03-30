package com.elevator.strategy;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;

import java.util.List;

/**
 * Strategy Pattern: interface for elevator scheduling algorithms.
 * New algorithms can be added by implementing this interface.
 */
public interface SchedulingStrategy {
    ElevatorCar selectElevator(List<ElevatorCar> elevators, int requestFloor, Direction direction);
}
