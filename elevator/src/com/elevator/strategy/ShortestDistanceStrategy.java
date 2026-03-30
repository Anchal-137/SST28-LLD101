package com.elevator.strategy;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;
import com.elevator.state.ElevatorState;

import java.util.List;

/**
 * Selects the nearest available elevator to the requesting floor.
 * Prefers idle elevators, then elevators already moving toward the floor.
 */
public class ShortestDistanceStrategy implements SchedulingStrategy {

    @Override
    public ElevatorCar selectElevator(List<ElevatorCar> elevators, int requestFloor, Direction direction) {
        ElevatorCar best = null;
        int bestScore = Integer.MAX_VALUE;

        for (ElevatorCar car : elevators) {
            if (!car.isAvailable()) continue;

            int distance = Math.abs(car.getCurrentFloor() - requestFloor);
            int score = distance;

            // Prefer idle elevators (lower score)
            if (car.getState() == ElevatorState.IDLE) {
                score = distance;
            }
            // Elevator moving toward request floor in same direction is ideal
            else if (car.getDirection() == direction) {
                if (direction == Direction.UP && car.getCurrentFloor() <= requestFloor) {
                    score = distance; // on the way
                } else if (direction == Direction.DOWN && car.getCurrentFloor() >= requestFloor) {
                    score = distance; // on the way
                } else {
                    score = distance + 1000; // needs to reverse
                }
            } else {
                score = distance + 2000; // moving opposite direction
            }

            // Tiebreak: fewer pending stops is better
            score += car.getPendingStopCount() * 10;

            if (score < bestScore) {
                bestScore = score;
                best = car;
            }
        }
        return best;
    }
}
