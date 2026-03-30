package com.elevator.state;

/**
 * State Pattern: Each elevator can be in one of these states.
 * Transitions are managed by the ElevatorCar.
 */
public enum ElevatorState {
    IDLE,
    MOVING_UP,
    MOVING_DOWN,
    DOOR_OPEN,
    MAINTENANCE
}
