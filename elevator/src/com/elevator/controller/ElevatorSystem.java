package com.elevator.controller;

import com.elevator.model.ElevatorCar;
import com.elevator.panel.InsidePanel;
import com.elevator.panel.OutsidePanel;
import com.elevator.strategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Top-level system controller that manages all elevators,
 * panels, and the dispatcher.
 *
 * Acts as the entry point for the entire elevator system.
 */
public class ElevatorSystem {
    private final int totalFloors;
    private final List<ElevatorCar> elevators;
    private final ElevatorDispatcher dispatcher;
    private final Map<String, InsidePanel> insidePanels;
    private final Map<Integer, OutsidePanel> outsidePanels;

    public ElevatorSystem(int totalFloors, int numElevators, SchedulingStrategy strategy) {
        this.totalFloors = totalFloors;
        this.elevators = new ArrayList<>();
        this.insidePanels = new HashMap<>();
        this.outsidePanels = new HashMap<>();

        // Create elevator cars
        for (int i = 1; i <= numElevators; i++) {
            ElevatorCar car = new ElevatorCar("E" + i, 0, totalFloors - 1);
            elevators.add(car);
        }

        // Create dispatcher
        this.dispatcher = new ElevatorDispatcher(elevators, strategy);

        // Create inside panels (one per elevator)
        for (ElevatorCar car : elevators) {
            insidePanels.put(car.getCarId(), new InsidePanel(car));
        }

        // Create outside panels (one per floor)
        for (int f = 0; f < totalFloors; f++) {
            outsidePanels.put(f, new OutsidePanel(f, totalFloors - 1, dispatcher));
        }

        System.out.println("[SYSTEM] Initialized: " + totalFloors + " floors, "
                + numElevators + " elevators, strategy="
                + strategy.getClass().getSimpleName());
    }

    // ---- Public APIs ----

    public OutsidePanel getFloorPanel(int floor) {
        return outsidePanels.get(floor);
    }

    public InsidePanel getInsidePanel(String carId) {
        return insidePanels.get(carId);
    }

    public ElevatorDispatcher getDispatcher() {
        return dispatcher;
    }

    public List<ElevatorCar> getElevators() {
        return elevators;
    }

    public ElevatorCar getElevator(String carId) {
        for (ElevatorCar car : elevators) {
            if (car.getCarId().equals(carId)) return car;
        }
        return null;
    }

    /**
     * Process all pending stops for all elevators.
     * In a real system, each elevator would be a separate thread.
     * Here we simulate it step by step.
     */
    public void processAllElevators() {
        dispatcher.retryPendingRequests();
        for (ElevatorCar car : elevators) {
            car.processStops();
        }
    }

    public void printStatus() {
        System.out.println("\n--- Elevator Status ---");
        for (ElevatorCar car : elevators) {
            System.out.println("  " + car);
        }
        System.out.println();
    }
}
