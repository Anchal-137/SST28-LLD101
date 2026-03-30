package com.elevator.strategy;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;
import com.elevator.state.ElevatorState;

import java.util.List;

public class ShortestDistanceStrategy implements SchedulingStrategy {

    @Override
    public ElevatorCar selectElevator(List<ElevatorCar> elevators, int requestFloor, Direction direction) {
        ElevatorCar best = null;
        int bestScore = Integer.MAX_VALUE;

        for (ElevatorCar car : elevators) {
            if (!car.isAvailable()) continue;

            int distance = Math.abs(car.getCurrentFloor() - requestFloor);
            int score = distance;

            if (car.getState() == ElevatorState.IDLE) {
                score = distance;
            } else if (car.getDirection() == direction) {
                boolean onTheWay = (direction == Direction.UP && car.getCurrentFloor() <= requestFloor)
                        || (direction == Direction.DOWN && car.getCurrentFloor() >= requestFloor);
                score = onTheWay ? distance : distance + 1000;
            } else {
                score = distance + 2000;
            }

            score += car.getPendingStopCount() * 10;

            if (score < bestScore) {
                bestScore = score;
                best = car;
            }
        }
        return best;
    }
}
