package com.elevator;

import com.elevator.controller.ElevatorSystem;
import com.elevator.model.ElevatorCar;
import com.elevator.panel.InsidePanel;
import com.elevator.panel.OutsidePanel;
import com.elevator.strategy.FCFSStrategy;
import com.elevator.strategy.ShortestDistanceStrategy;

public class App {
    public static void main(String[] args) {
        System.out.println("==========================================================");
        System.out.println("         Multi-Elevator System - LLD Simulation            ");
        System.out.println("==========================================================\n");

        // ================================================
        //  1. Initialize system: 10 floors, 3 elevators
        // ================================================
        ElevatorSystem system = new ElevatorSystem(10, 3, new ShortestDistanceStrategy());
        system.printStatus();

        // ================================================
        //  2. External request: Person on floor 5 presses UP
        // ================================================
        System.out.println("--- Scenario 1: Floor 5 presses UP ---");
        OutsidePanel floor5 = system.getFloorPanel(5);
        floor5.pressUp();
        system.processAllElevators();
        system.printStatus();

        // ================================================
        //  3. External request: Floor 3 presses DOWN
        // ================================================
        System.out.println("--- Scenario 2: Floor 3 presses DOWN ---");
        OutsidePanel floor3 = system.getFloorPanel(3);
        floor3.pressDown();
        system.processAllElevators();
        system.printStatus();

        // ================================================
        //  4. Internal request: Person inside E1 presses floor 8
        // ================================================
        System.out.println("--- Scenario 3: Inside E1, press floor 8 ---");
        InsidePanel e1Panel = system.getInsidePanel("E1");
        e1Panel.pressFloor(8);
        system.processAllElevators();
        system.printStatus();

        // ================================================
        //  5. Multiple simultaneous external requests
        // ================================================
        System.out.println("--- Scenario 4: Multiple requests at once ---");
        System.out.println("  Floor 0 UP, Floor 9 DOWN, Floor 4 UP (all at once)");
        OutsidePanel floor0 = system.getFloorPanel(0);
        OutsidePanel floor9 = system.getFloorPanel(9);
        OutsidePanel floor4 = system.getFloorPanel(4);
        floor0.pressUp();
        floor9.pressDown();
        floor4.pressUp();
        system.processAllElevators();
        system.printStatus();

        // ================================================
        //  6. Multi-threaded concurrent requests
        // ================================================
        System.out.println("--- Scenario 5: Concurrent requests (multi-threaded) ---");

        Thread t1 = new Thread(() -> {
            OutsidePanel f7 = system.getFloorPanel(7);
            f7.pressUp();
        }, "User-Floor7");

        Thread t2 = new Thread(() -> {
            OutsidePanel f2 = system.getFloorPanel(2);
            f2.pressDown();
        }, "User-Floor2");

        Thread t3 = new Thread(() -> {
            OutsidePanel f6 = system.getFloorPanel(6);
            f6.pressUp();
        }, "User-Floor6");

        t1.start();
        t2.start();
        t3.start();
        try { t1.join(); t2.join(); t3.join(); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        system.processAllElevators();
        system.printStatus();

        // ================================================
        //  7. Weight sensor overload test
        // ================================================
        System.out.println("--- Scenario 6: Weight overload test ---");
        ElevatorCar e2 = system.getElevator("E2");
        e2.getWeightSensor().setWeight(750); // exceeds 700kg
        System.out.println("  E2 weight set to 750kg (max: 700kg)");
        InsidePanel e2Panel = system.getInsidePanel("E2");
        e2Panel.pressFloor(7);
        system.processAllElevators();

        // Reset weight
        e2.getWeightSensor().setWeight(400);
        System.out.println("  E2 weight reset to 400kg");
        system.printStatus();

        // ================================================
        //  8. Alarm test
        // ================================================
        System.out.println("--- Scenario 7: Alarm triggered in E3 ---");
        InsidePanel e3Panel = system.getInsidePanel("E3");
        e3Panel.pressAlarm();
        system.printStatus();

        // Try to assign request - E3 should be skipped
        System.out.println("  Floor 1 presses UP (E3 should be skipped):");
        OutsidePanel floor1 = system.getFloorPanel(1);
        floor1.pressUp();
        system.processAllElevators();

        // Reset alarm
        ElevatorCar e3 = system.getElevator("E3");
        e3.resetAlarm();
        system.printStatus();

        // ================================================
        //  9. Maintenance mode test
        // ================================================
        System.out.println("--- Scenario 8: E1 set to maintenance ---");
        ElevatorCar e1 = system.getElevator("E1");
        e1.setMaintenance(true);
        system.printStatus();

        System.out.println("  Floor 8 presses DOWN (E1 in maintenance, should skip):");
        OutsidePanel floor8 = system.getFloorPanel(8);
        floor8.pressDown();
        system.processAllElevators();
        system.printStatus();

        // Clear maintenance
        e1.setMaintenance(false);
        system.printStatus();

        // ================================================
        //  10. Strategy swap: switch to FCFS
        // ================================================
        System.out.println("--- Scenario 9: Switch to FCFS strategy ---");
        system.getDispatcher().setStrategy(new FCFSStrategy());

        OutsidePanel floor5b = system.getFloorPanel(5);
        floor5b.pressUp();
        OutsidePanel floor2b = system.getFloorPanel(2);
        floor2b.pressDown();
        system.processAllElevators();
        system.printStatus();

        // ================================================
        //  11. Inside panel: open/close door
        // ================================================
        System.out.println("--- Scenario 10: Door open/close ---");
        InsidePanel e1PanelFinal = system.getInsidePanel("E1");
        e1PanelFinal.pressOpenDoor();
        e1PanelFinal.pressCloseDoor();

        System.out.println("\n==========================================================");
        System.out.println("                   Simulation Complete                     ");
        System.out.println("==========================================================");
    }
}
