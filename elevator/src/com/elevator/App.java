package com.elevator;

import com.elevator.controller.ElevatorSystem;
import com.elevator.model.ElevatorCar;
import com.elevator.panel.InsidePanel;
import com.elevator.strategy.FCFSStrategy;
import com.elevator.strategy.ShortestDistanceStrategy;

public class App {
    public static void main(String[] args) {
        System.out.println("========== Multi-Elevator System ==========\n");

        // 10 floors, 3 elevators, shortest distance scheduling
        ElevatorSystem system = new ElevatorSystem(10, 3, new ShortestDistanceStrategy());
        system.printStatus();

        // someone on floor 5 presses UP
        System.out.println("--- Floor 5 UP ---");
        system.getFloorPanel(5).pressUp();
        system.processAllElevators();
        system.printStatus();

        // floor 3 presses DOWN
        System.out.println("--- Floor 3 DOWN ---");
        system.getFloorPanel(3).pressDown();
        system.processAllElevators();
        system.printStatus();

        // person inside E1 presses floor 8
        System.out.println("--- Inside E1, press floor 8 ---");
        system.getInsidePanel("E1").pressFloor(8);
        system.processAllElevators();
        system.printStatus();

        // multiple requests at once
        System.out.println("--- Multiple requests ---");
        system.getFloorPanel(0).pressUp();
        system.getFloorPanel(9).pressDown();
        system.getFloorPanel(4).pressUp();
        system.processAllElevators();
        system.printStatus();

        // concurrent requests from different threads
        System.out.println("--- Concurrent requests ---");
        Thread t1 = new Thread(() -> system.getFloorPanel(7).pressUp());
        Thread t2 = new Thread(() -> system.getFloorPanel(2).pressDown());
        Thread t3 = new Thread(() -> system.getFloorPanel(6).pressUp());
        t1.start(); t2.start(); t3.start();
        try { t1.join(); t2.join(); t3.join(); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        system.processAllElevators();
        system.printStatus();

        // weight overload
        System.out.println("--- Weight overload test ---");
        ElevatorCar e2 = system.getElevator("E2");
        e2.getWeightSensor().setWeight(750);
        System.out.println("  E2 weight = 750kg (max 700)");
        system.getInsidePanel("E2").pressFloor(7);
        system.processAllElevators();
        e2.getWeightSensor().setWeight(400);
        system.printStatus();

        // alarm
        System.out.println("--- Alarm in E3 ---");
        system.getInsidePanel("E3").pressAlarm();
        system.getFloorPanel(1).pressUp();  // E3 should be skipped
        system.processAllElevators();
        system.getElevator("E3").resetAlarm();
        system.printStatus();

        // maintenance
        System.out.println("--- E1 maintenance ---");
        system.getElevator("E1").setMaintenance(true);
        system.getFloorPanel(8).pressDown();  // should skip E1
        system.processAllElevators();
        system.getElevator("E1").setMaintenance(false);
        system.printStatus();

        // swap to FCFS strategy
        System.out.println("--- Switch to FCFS ---");
        system.getDispatcher().setStrategy(new FCFSStrategy());
        system.getFloorPanel(5).pressUp();
        system.getFloorPanel(2).pressDown();
        system.processAllElevators();
        system.printStatus();

        // door controls
        System.out.println("--- Door test ---");
        InsidePanel p = system.getInsidePanel("E1");
        p.pressOpenDoor();
        p.pressCloseDoor();

        System.out.println("\n========== Done ==========");
    }
}
