package com.elevator.controller;

import com.elevator.model.ElevatorCar;
import com.elevator.panel.InsidePanel;
import com.elevator.panel.OutsidePanel;
import com.elevator.strategy.SchedulingStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        for (int i = 1; i <= numElevators; i++) {
            ElevatorCar car = new ElevatorCar("E" + i, 0, totalFloors - 1);
            elevators.add(car);
        }

        this.dispatcher = new ElevatorDispatcher(elevators, strategy);

        for (ElevatorCar car : elevators)
            insidePanels.put(car.getCarId(), new InsidePanel(car));

        for (int f = 0; f < totalFloors; f++)
            outsidePanels.put(f, new OutsidePanel(f, totalFloors - 1, dispatcher));

        System.out.println("[SYSTEM] " + totalFloors + " floors, " + numElevators
                + " elevators, " + strategy.getClass().getSimpleName());
    }

    public OutsidePanel getFloorPanel(int floor) { return outsidePanels.get(floor); }
    public InsidePanel getInsidePanel(String carId) { return insidePanels.get(carId); }
    public ElevatorDispatcher getDispatcher() { return dispatcher; }
    public List<ElevatorCar> getElevators() { return elevators; }

    public ElevatorCar getElevator(String carId) {
        for (ElevatorCar car : elevators)
            if (car.getCarId().equals(carId)) return car;
        return null;
    }

    public void processAllElevators() {
        dispatcher.retryPendingRequests();
        for (ElevatorCar car : elevators) car.processStops();
    }

    public void printStatus() {
        System.out.println("\n--- Elevator Status ---");
        for (ElevatorCar car : elevators) System.out.println("  " + car);
        System.out.println();
    }
}
