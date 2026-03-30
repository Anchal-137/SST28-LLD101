package com.elevator.controller;

import com.elevator.model.Direction;
import com.elevator.model.ElevatorCar;
import com.elevator.model.ElevatorRequest;
import com.elevator.model.RequestType;
import com.elevator.strategy.SchedulingStrategy;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Central dispatcher that receives external requests (from floor panels)
 * and assigns them to the best elevator using the configured strategy.
 *
 * Thread-safe: uses ConcurrentLinkedQueue and synchronized assignment
 * to prevent duplicate handling of the same request.
 */
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

    /**
     * Called by OutsidePanel when UP/DOWN is pressed on a floor.
     * Assigns exactly ONE elevator to handle this request.
     */
    public void handleExternalRequest(int floor, Direction direction) {
        ElevatorRequest request = new ElevatorRequest(floor, direction, RequestType.EXTERNAL);

        synchronized (assignmentLock) {
            ElevatorCar selected = strategy.selectElevator(elevators, floor, direction);
            if (selected == null) {
                System.out.println("[DISPATCHER] No elevator available for: " + request);
                pendingRequests.add(request);
                return;
            }
            selected.addStop(floor, direction);
            System.out.println("[DISPATCHER] Assigned " + selected.getCarId()
                    + " for " + request);
        }
    }

    /**
     * Retry any pending requests that couldn't be assigned earlier.
     */
    public void retryPendingRequests() {
        while (!pendingRequests.isEmpty()) {
            ElevatorRequest req = pendingRequests.peek();
            synchronized (assignmentLock) {
                ElevatorCar selected = strategy.selectElevator(
                        elevators, req.getFloor(), req.getDirection());
                if (selected != null) {
                    pendingRequests.poll();
                    selected.addStop(req.getFloor(), req.getDirection());
                    System.out.println("[DISPATCHER] Retry assigned " + selected.getCarId()
                            + " for " + req);
                } else {
                    break; // still no elevator available
                }
            }
        }
    }

    public List<ElevatorCar> getElevators() { return elevators; }
}
