package com.elevator.controller;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;
import com.elevator.model.ElevatorRequest;
import com.elevator.model.RequestType;
import com.elevator.strategy.SchedulingStrategy;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ElevatorDispatcher {
    private final List<ElevatorCar> elevators;
    private SchedulingStrategy strategy;
    private final ConcurrentLinkedQueue<ElevatorRequest> pendingRequests;
    private final Object assignmentLock = new Object();

    public ElevatorDispatcher(List<ElevatorCar> elevators, SchedulingStrategy strategy) {
        this.elevators = elevators;
        this.strategy = strategy;
        this.pendingRequests = new ConcurrentLinkedQueue<>();
    }

    public void setStrategy(SchedulingStrategy strategy) {
        this.strategy = strategy;
        System.out.println("[DISPATCHER] Strategy changed to: " + strategy.getClass().getSimpleName());
    }

    public void handleExternalRequest(int floor, Direction direction) {
        ElevatorRequest request = new ElevatorRequest(floor, direction, RequestType.EXTERNAL);

        synchronized (assignmentLock) {
            ElevatorCar selected = strategy.selectElevator(elevators, floor, direction);
            if (selected == null) {
                System.out.println("[DISPATCHER] No elevator available, queuing request");
                pendingRequests.add(request);
                return;
            }
            selected.addStop(floor, direction);
            System.out.println("[DISPATCHER] Assigned " + selected.getCarId() + " for " + request);
        }
    }

    public void retryPendingRequests() {
        while (!pendingRequests.isEmpty()) {
            ElevatorRequest req = pendingRequests.peek();
            synchronized (assignmentLock) {
                ElevatorCar selected = strategy.selectElevator(elevators, req.getFloor(), req.getDirection());
                if (selected != null) {
                    pendingRequests.poll();
                    selected.addStop(req.getFloor(), req.getDirection());
                } else {
                    break;
                }
            }
        }
    }

    public List<ElevatorCar> getElevators() { return elevators; }
}
