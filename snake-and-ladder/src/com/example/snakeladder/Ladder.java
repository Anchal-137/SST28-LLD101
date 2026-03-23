package com.example.snakeladder;

public class Ladder {
    private final int bottom;
    private final int top;

    public Ladder(int bottom, int top) {
        this.bottom = bottom;
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public int getTop() {
        return top;
    }

    @Override
    public String toString() {
        return "Ladder{" + bottom + " -> " + top + "}";
    }
}
