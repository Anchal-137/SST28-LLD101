package com.elevator.panel;

public class Button {
    private final String label;
    private boolean illuminated;

    public Button(String label) {
        this.label = label;
        this.illuminated = false;
    }

    public void press() {
        illuminated = true;
    }

    public void reset() {
        illuminated = false;
    }

    public boolean isIlluminated() { return illuminated; }
    public String getLabel() { return label; }
}
