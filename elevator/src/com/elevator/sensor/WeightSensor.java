package com.elevator.sensor;

public class WeightSensor {
    private static final double MAX_CAPACITY_KG = 700.0;
    private double currentWeight;

    public WeightSensor() {
        this.currentWeight = 0;
    }

    public synchronized void setWeight(double weightKg) { this.currentWeight = weightKg; }
    public synchronized double getCurrentWeight() { return currentWeight; }
    public synchronized boolean isOverloaded() { return currentWeight > MAX_CAPACITY_KG; }
    public double getMaxCapacity() { return MAX_CAPACITY_KG; }
}
