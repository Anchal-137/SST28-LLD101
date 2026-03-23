package com.example.snakeladder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {
    private final int size;
    private final int maxCell;
    private final Map<Integer, Integer> snakeMap;
    private final Map<Integer, Integer> ladderMap;

    public Board(int size, List<Snake> snakes, List<Ladder> ladders) {
        this.size = size;
        this.maxCell = size * size;
        this.snakeMap = new HashMap<>();
        this.ladderMap = new HashMap<>();

        for (Snake snake : snakes) {
            snakeMap.put(snake.getHead(), snake.getTail());
        }
        for (Ladder ladder : ladders) {
            ladderMap.put(ladder.getStart(), ladder.getEnd());
        }
    }

    public int getMaxCell() {
        return maxCell;
    }

    public int movePlayer(int currentPosition, int diceValue) {
        int newPosition = currentPosition + diceValue;

        if (newPosition > maxCell) {
            return currentPosition;
        }

        if (snakeMap.containsKey(newPosition)) {
            int snakeTail = snakeMap.get(newPosition);
            System.out.println("  Bitten by snake at " + newPosition + "! Sliding down to " + snakeTail);
            newPosition = snakeTail;
        } else if (ladderMap.containsKey(newPosition)) {
            int ladderEnd = ladderMap.get(newPosition);
            System.out.println("  Climbed ladder at " + newPosition + "! Moving up to " + ladderEnd);
            newPosition = ladderEnd;
        }

        return newPosition;
    }

    public boolean isOccupied(int cell) {
        return snakeMap.containsKey(cell) || ladderMap.containsKey(cell)
                || snakeMap.containsValue(cell) || ladderMap.containsValue(cell);
    }

    public void printBoard() {
        System.out.println("Board size: " + size + "x" + size + " (cells 1 to " + maxCell + ")");
        System.out.println("Snakes: " + snakeMap);
        System.out.println("Ladders: " + ladderMap);
    }
}
