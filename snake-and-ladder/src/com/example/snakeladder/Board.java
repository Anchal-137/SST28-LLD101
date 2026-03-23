package com.example.snakeladder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private final int size;
    private final int maxCell;
    private final Map<Integer, Integer> snakePositions;
    private final Map<Integer, Integer> ladderPositions;

    public Board(int size, List<Snake> snakes, List<Ladder> ladders) {
        this.size = size;
        this.maxCell = size * size;
        this.snakePositions = new HashMap<>();
        this.ladderPositions = new HashMap<>();

        for (Snake snake : snakes) {
            snakePositions.put(snake.getStart(), snake.getEnd());
        }
        for (Ladder ladder : ladders) {
            ladderPositions.put(ladder.getBottom(), ladder.getTop());
        }
    }

    public int getMaxCell() {
        return maxCell;
    }

    public int resolveMove(int currentPosition, int diceValue) {
        int newPosition = currentPosition + diceValue;

        if (newPosition > maxCell) {
            return currentPosition;
        }

        if (snakePositions.containsKey(newPosition)) {
            int snakeTail = snakePositions.get(newPosition);
            System.out.println("  Hit by snake at " + newPosition + "! Sliding down to " + snakeTail);
            newPosition = snakeTail;
        } else if (ladderPositions.containsKey(newPosition)) {
            int ladderTop = ladderPositions.get(newPosition);
            System.out.println("  Found ladder at " + newPosition + "! Climbing up to " + ladderTop);
            newPosition = ladderTop;
        }

        return newPosition;
    }

    public boolean isCellTaken(int cell) {
        return snakePositions.containsKey(cell) || ladderPositions.containsKey(cell)
                || snakePositions.containsValue(cell) || ladderPositions.containsValue(cell);
    }

    public void displayBoard() {
        System.out.println("Board size: " + size + "x" + size + " (cells 1 to " + maxCell + ")");
        System.out.println("Snakes: " + snakePositions);
        System.out.println("Ladders: " + ladderPositions);
    }
}
